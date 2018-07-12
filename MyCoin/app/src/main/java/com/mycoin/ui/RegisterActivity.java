package com.mycoin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.Check;
import com.mycoin.data.CheckUser;
import com.mycoin.data.CodeUser;
import com.mycoin.data.InterfaceAdapter;
import com.mycoin.util.ConnectionUtils;
import com.mycoin.util.ToastUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener {

    private LinearLayout mLlRegister;
    private MaterialEditText mEdtRegisterName;
    private MaterialEditText mEdtRegisterPassword;
    private MaterialEditText mEdtRegisterEmail;
    private MaterialEditText mEdtRegisterCode;
    private TextView mTxvGoToLogin;
    private Button mBtnRegister, mBtnCode;

    private String mUserName;
    private String mUserPassword;
    private String mUserEmail;
    private String mUserCode;
    private TimeCount mCount;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Retrofit retrofit;
    InterfaceAdapter interfaceAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initNet();

    }

    private void initView() {
        mCount = new TimeCount(60000, 1000);
        mLlRegister = (LinearLayout) findViewById(R.id.ll_register);
        mEdtRegisterName = (MaterialEditText) findViewById(R.id.edt_register_name);
        mEdtRegisterPassword = (MaterialEditText) findViewById(R.id.edt_register_password);
        mEdtRegisterEmail = (MaterialEditText) findViewById(R.id.edt_register_email);
        mEdtRegisterCode = (MaterialEditText) findViewById(R.id.edt_register_code);
        mTxvGoToLogin = (TextView) findViewById(R.id.txv_go_to_login);
        mBtnCode = (Button) findViewById(R.id.btn_code);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mTxvGoToLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mBtnCode.setOnClickListener(this);

        mEdtRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void initNet() {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://112.74.88.136:5488/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        interfaceAdapter = retrofit.create(InterfaceAdapter.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txv_go_to_login:
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_code:
                mUserName = mEdtRegisterName.getText().toString();
                mUserPassword = mEdtRegisterPassword.getText().toString();
                mUserEmail = mEdtRegisterEmail.getText().toString();

                ConnectionUtils.makeSnackBar(mLlRegister, getApplicationContext());
                if (mUserName.length() < 4 || mUserName.length() >= 20) {
                    ToastUtils.showShort(RegisterActivity.this, R.string.tip_please_input_4_20_name);
                } else if (mUserPassword.length() < 6 || mUserPassword.length() >= 32) {
                    ToastUtils.showShort(RegisterActivity.this, R.string.tip_please_input_6_32_password);
                } else if (mEdtRegisterEmail.getText().toString().equals("") || !isEmail(mEdtRegisterEmail.getText().toString())) {
                    ToastUtils.showShort(RegisterActivity.this, R.string.email_error);
                } else {
                    mCount.start();
                    checkAndEmail();
                }

                // if (!mEdtRegisterEmail.getText().toString().equals("") && isEmail(mEdtRegisterEmail.getText().toString())) {
                //     mCount.start();
                //     checkAndEmail();
                // } else {
                //     ToastUtils.showShort(RegisterActivity.this, R.string.email_error);
                // }
                break;
            case R.id.btn_register:
                mUserCode = mEdtRegisterCode.getText().toString();
                register();
                storeMessage();

                // ConnectionUtils.makeSnackBar(mLlRegister, getApplicationContext());
                // if (mUserName.length() >= 4 && mUserName.length() < 20) {
                //     if ((mUserPassword.length() >= 6 && mUserPassword.length() < 32)) {
                //         register();
                //     }else {
                //         Snackbar.make(mLlRegister, R.string.tip_please_input_6_32_password, Snackbar.LENGTH_INDEFINITE).
                //                 setAction(R.string.input_again, new View.OnClickListener() {
                //                     @Override
                //                     public void onClick(View v) {
                //                         mEdtRegisterPassword.setText("");
                //                     }
                //                 }).show();
                //     }
                // }else{
                //     Snackbar.make(mLlRegister, R.string.tip_please_input_4_20_name, Snackbar.LENGTH_INDEFINITE).
                //             setAction(R.string.input_again, new View.OnClickListener() {
                //                 @Override
                //                 public void onClick(View v) {
                //                     mEdtRegisterName.setText("");
                //                 }
                //             }).show();
                // }
                break;
        }
    }

    // 查重并发邮件
    private void checkAndEmail() {
        CheckUser checkUser = new CheckUser(mUserEmail, mUserName);
        Call<Check> call = interfaceAdapter.getCheckMsg(checkUser);
        call.enqueue(new Callback<Check>() {
            @Override
            public void onResponse(Call<Check> call, Response<Check> response) {
                Check bean = response.body();
                if (response.code() == 200) {
                    // "msg": "已发送邮件！"
                    ToastUtils.showShort(RegisterActivity.this, "已发送邮件！");
                    // ToastUtils.showShort(RegisterActivity.this, bean.getMsg());
                } else if (response.code() == 403) {
                    // "msg": "邮箱已被注册！"
                    // "msg": "用户名已被注册！"
                    ToastUtils.showShort(RegisterActivity.this, "用户名或邮箱已被注册！");
                    // if (bean != null) {
                    //     ToastUtils.showShort(RegisterActivity.this, bean.getMsg());
                    // }
                }
            }

            @Override
            public void onFailure(Call<Check> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // 验证码注册
    private void register() {
        CodeUser codeUser = new CodeUser(mUserEmail, mUserName, mUserCode, mUserPassword);
        Call<Check> call = interfaceAdapter.getCodeMsg(codeUser);
        call.enqueue(new Callback<Check>() {
            @Override
            public void onResponse(Call<Check> call, Response<Check> response) {
                Check bean = response.body();
                if (response.code() == 200) {
                    Application.storedUsername = mUserName;
                    Application.storedUserPassword = mUserPassword;
                    ToastUtils.showShort(RegisterActivity.this, R.string.register_successfully);
                    Intent intent = new Intent(RegisterActivity.this, CoinMainActivity.class);
                    startActivity(intent);
                }else if(response.code() == 401) {
                    ToastUtils.showShort(RegisterActivity.this, R.string.code_error);
                }
            }

            @Override
            public void onFailure(Call<Check> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void storeMessage() {
        if (Application.storedUsername != "" && Application.storedUserPassword != "") {
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", Application.storedUsername);
            editor.putString("password", Application.storedUserPassword);
            editor.commit();
        }
    }


    // 邮箱匹配
    private boolean isEmail(String email) {
        String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(regex, email);
    }

    // 重发验证码60s倒计时
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtnCode.setClickable(false);
            mBtnCode.setBackgroundColor(getResources().getColor(R.color.gray));
            mBtnCode.setText(millisUntilFinished / 1000 + " 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            mBtnCode.setClickable(true);
            mBtnCode.setBackgroundColor(getResources().getColor(R.color.purple));
            mBtnCode.setText("重新获取");

        }
    }

}