package com.mycoin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.AddBudget;
import com.mycoin.data.AddBudgetUser;
import com.mycoin.data.InterfaceAdapter;
import com.mycoin.util.ToastUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 添加预算
public class BudgetActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mTbBudget;
    private ImageButton mBtnClear;
    private ImageButton mBtnDone;
    private MaterialEditText mEdtBudget;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Retrofit retrofit;
    InterfaceAdapter interfaceAdapter;

    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        initView();
        initNet();
    }

    private void initView() {
        mTbBudget = (Toolbar) findViewById(R.id.toolbar_budget);
        mEdtBudget = (MaterialEditText) findViewById(R.id.edt_budget);
        mBtnClear = (ImageButton) findViewById(R.id.btn_clear);
        mBtnDone = (ImageButton) findViewById(R.id.btn_done);
        mBtnClear.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);

        setSupportActionBar(mTbBudget);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        switch (v.getId()) {
            case R.id.btn_clear :
                Intent intent = new Intent(BudgetActivity.this, CoinMainActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                break;
            case R.id.btn_done :
                if (!mEdtBudget.getText().toString().equals("")) {
                    Application.storedUserBudget = Integer.parseInt(mEdtBudget.getText().toString());
                    setBudget();
                }
                Intent intent1 = new Intent(BudgetActivity.this, CoinMainActivity.class);
                intent1.putExtra("id", 1);
                startActivity(intent1);
                break;
        }
    }

    private void setBudget() {
        AddBudgetUser addBudgetUser = new AddBudgetUser(Application.storedUserBudget, month);
        Call<AddBudget> call = interfaceAdapter.getAddBudget(addBudgetUser, Application.storedUserToken);
        call.enqueue(new Callback<AddBudget>() {
            @Override
            public void onResponse(Call<AddBudget> call, Response<AddBudget> response) {
                AddBudget addBudget = response.body();

                if (response.code() == 200) {
                    try {
                        Application.storedUserBudget = addBudget.getBudget().getBudget();
                        ToastUtils.showShort(BudgetActivity.this, R.string.save_successfully_local);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(BudgetActivity.this, CoinMainActivity.class);
                    intent.putExtra("id", 1);
                    startActivity(intent);
                }

            }
            @Override
            public void onFailure(Call<AddBudget> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

