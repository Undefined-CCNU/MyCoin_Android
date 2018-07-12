package com.mycoin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.AddBudget;
import com.mycoin.data.BudgetUser;
import com.mycoin.data.Cost;
import com.mycoin.data.InterfaceAdapter;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataFragment extends Fragment implements View.OnClickListener {

    private TextView mTxvBudget, mTxvCost;
    private ImageButton mBtnReport;
    private WebView mWvTime;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Retrofit retrofit;
    InterfaceAdapter interfaceAdapter;

    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        mTxvBudget = (TextView) view.findViewById(R.id.txv_budget);
        mTxvCost = (TextView) view.findViewById(R.id.txv_cost);
        mBtnReport = (ImageButton) view.findViewById(R.id.btn_report);
        mWvTime = (WebView) view.findViewById(R.id.wv_time);
        mTxvBudget.setOnClickListener(this);
        mTxvCost.setOnClickListener(this);
        mBtnReport.setOnClickListener(this);

        initWebView();
        initNet();

        getUserBudget();
        getUserCost();

        return view;
    }

    private void initWebView() {
        mWvTime.getSettings().setJavaScriptEnabled(true);
        mWvTime.setWebViewClient(new WebViewClient());
        // http://112.74.88.136:4600/<token>/list
        // http://112.74.88.136:4600/eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MX0.xZSQGI9tmyi6Nsz-bLxnCq-JQr0BeDSuhZBgJmiPYGU/list
        mWvTime.loadUrl("http://112.74.88.136:4600/" + Application.storedUserToken + "/list");
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
            case R.id.txv_budget :
                Intent intent = new Intent();
                intent.setClass(getActivity(), BudgetActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.txv_cost :
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), getActivity().getClass());
                getActivity().startActivity(intent1);
                break;
            case R.id.btn_report :
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), ReportActivity.class);
                getActivity().startActivity(intent2);
                break;
        }
    }

    private void getUserBudget() {
        BudgetUser budgetUser = new BudgetUser(month);
        Call<AddBudget> call = interfaceAdapter.getUserBudget(budgetUser, Application.storedUserToken);
        call.enqueue(new Callback<AddBudget>() {
            @Override
            public void onResponse(Call<AddBudget> call, Response<AddBudget> response) {
                AddBudget budget = response.body();
                if (response.code() == 200) {
                    try {
                        Application.storedUserBudget = budget.getBudget().getBudget();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    mTxvBudget.setText(String.valueOf(Application.storedUserBudget));
                }
            }
            @Override
            public void onFailure(Call<AddBudget> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getUserCost() {
        Call<Cost> call = interfaceAdapter.getCost(month, Application.storedUserToken);
        call.enqueue(new Callback<Cost>() {
            @Override
            public void onResponse(Call<Cost> call, Response<Cost> response) {
                Cost cost = response.body();
                if (response.code() == 200) {
                    try {
                        mTxvCost.setText(String.valueOf(cost.getSum()));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Cost> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
