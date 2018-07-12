package com.mycoin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.InterfaceAdapter;
import com.mycoin.data.Login;
import com.mycoin.data.LoginUser;
import com.mycoin.util.ConnectionUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mLlLogin;
    private MaterialEditText mEdtLoginName;
    private MaterialEditText mEdtLoginPassword;
    private TextView mTxvGoToRegister;
    private Button mBtnLogin;

    private String mUserName;
    private String mUserPassword;
    private String mUserToken;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Retrofit retrofit;
    InterfaceAdapter interfaceAdapter;

    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;
    int day = c.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initNet();
        checkLogin();
    }

    private void initView() {
        mLlLogin = (LinearLayout) findViewById(R.id.ll_login);
        mEdtLoginName = (MaterialEditText) findViewById(R.id.edt_login_name);
        mEdtLoginPassword = (MaterialEditText) findViewById(R.id.edt_login_password);
        mTxvGoToRegister = (TextView) findViewById(R.id.txv_go_to_register);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mTxvGoToRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

        mEdtLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
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

    private void checkLogin() {
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
            mUserName = sharedPreferences.getString("username", "");
            mUserPassword = sharedPreferences.getString("password", "");
            mUserToken = sharedPreferences.getString("token", "");
            if (mUserName != "" && mUserPassword != "") {
                Application.storedUsername = mUserName;
                Application.storedUserPassword = mUserPassword;
                Application.storedUserToken = mUserToken;
                Intent intent = new Intent(MainActivity.this, CoinMainActivity.class);
                startActivity(intent);
                // login();
            }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_go_to_register:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                mUserName = mEdtLoginName.getText().toString();
                mUserPassword = mEdtLoginPassword.getText().toString();
                ConnectionUtils.makeSnackBar(mLlLogin, getApplicationContext());
                Application.storedUsername = mUserName;
                Application.storedUserPassword = mUserPassword;
                login();
                break;
        }
    }

    private void login() {
        LoginUser loginUser = new LoginUser(mUserName, mUserPassword, month, day);
        Call<Login> call = interfaceAdapter.getUserToken(loginUser);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Login bean = response.body();
                if (response.code() == 200) {
                    Application.storedUserToken = bean.getToken();
                    storeMessage();
                    Intent intent = new Intent(MainActivity.this, CoinMainActivity.class);
                    startActivity(intent);
                    // try {
                    //     Application.storedUserToken = bean.getToken();
                    // } catch(Exception e) {
                    //     e.printStackTrace();
                    // }
                    // ToastUtils.showShort(MainActivity.this, R.string.login_successfully);
                } else if (response.code() == 401) {
                    Snackbar.make(mLlLogin, R.string.login_error, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.input_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mEdtLoginPassword.setText("");
                                }
                            }).show();
                } else if(response.code() == 403) {
                    Snackbar.make(mLlLogin, R.string.login_error,Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.input_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mEdtLoginPassword.setText("");
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void storeMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", Application.storedUsername);
        editor.putString("password", Application.storedUserPassword);
        editor.putString("token", Application.storedUserToken);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
