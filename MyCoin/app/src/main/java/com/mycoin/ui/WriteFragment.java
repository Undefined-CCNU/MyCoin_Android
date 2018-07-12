package com.mycoin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.Accounting;
import com.mycoin.data.AccountingUser;
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

public class WriteFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageButton mBtnClearCost, mBtnDoneCost;
    private ImageView mImvTag;
    private TextView mTxvTag;
    private MaterialEditText mEdtMoney;
    private ImageButton  mBtnHung, mBtnEducation, mBtnEat, mBtnGeneral, mBtnCloth, mBtnPlay;

    private AccountingUser accountingUser;

    private String mMoney;
    private String flag;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Retrofit retrofit;
    InterfaceAdapter interfaceAdapter;

    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;
    int day = c.get(Calendar.DAY_OF_MONTH);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_cost);
        mBtnClearCost = (ImageButton) view.findViewById(R.id.btn_clear_cost);
        mBtnDoneCost = (ImageButton) view.findViewById(R.id.btn_done_cost);
        mImvTag = (ImageView) view.findViewById(R.id.imv_tag);
        mTxvTag = (TextView) view.findViewById(R.id.txv_tag);
        mEdtMoney= (MaterialEditText) view.findViewById(R.id.edt_money);
        mBtnEat = (ImageButton) view.findViewById(R.id.btn_eat);
        mBtnHung = (ImageButton) view.findViewById(R.id.btn_hung);
        mBtnCloth = (ImageButton) view.findViewById(R.id.btn_cloth);
        mBtnPlay = (ImageButton) view.findViewById(R.id.btn_play);
        mBtnEducation = (ImageButton) view.findViewById(R.id.btn_education);
        mBtnGeneral = (ImageButton) view.findViewById(R.id.btn_general);
        mBtnClearCost.setOnClickListener(this);
        mBtnDoneCost.setOnClickListener(this);
        mBtnEat.setOnClickListener(this);
        mBtnHung.setOnClickListener(this);
        mBtnCloth.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mBtnEducation.setOnClickListener(this);
        mBtnGeneral.setOnClickListener(this);

        initNet();

        return view;
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
        switch(v.getId()) {
            case R.id.btn_clear_cost :
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.data_fragment, new DataFragment(), null)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btn_hung :
                mImvTag.setImageResource(R.drawable.ic_hung);
                mTxvTag.setText(R.string.tag_hung);
                mTxvTag.setTextColor(getResources().getColor(R.color.ic_hung));
                break;
            case R.id.btn_education :
                mImvTag.setImageResource(R.drawable.ic_education);
                mTxvTag.setText(R.string.tag_education);
                mTxvTag.setTextColor(getResources().getColor(R.color.ic_education));
                break;
            case R.id.btn_eat :
                mImvTag.setImageResource(R.drawable.ic_eat);
                mTxvTag.setText(R.string.tag_eat);
                mTxvTag.setTextColor(getResources().getColor(R.color.ic_eat));
                break;
            case R.id.btn_general :
                mImvTag.setImageResource(R.drawable.ic_general);
                mTxvTag.setText(R.string.tag_general);
                mTxvTag.setTextColor(getResources().getColor(R.color.ic_general));
                break;
            case R.id.btn_cloth :
                mImvTag.setImageResource(R.drawable.ic_cloth);
                mTxvTag.setText(R.string.tag_cloth);
                mTxvTag.setTextColor(getResources().getColor(R.color.ic_cloth));
                break;
            case R.id.btn_play :
                mImvTag.setImageResource(R.drawable.ic_play);
                mTxvTag.setText(R.string.tag_play);
                mTxvTag.setTextColor(getResources().getColor(R.color.ic_play));
                break;
            case R.id.btn_done_cost :
                write();
                Intent intent = new Intent(getContext(), CoinMainActivity.class);
                intent.putExtra("id", 2);
                startActivity(intent);
                //getActivity().getSupportFragmentManager()
                //        .beginTransaction()
                //        .replace(R.id.data_fragment, new DataFragment(), null)
                //        .addToBackStack(null)
                //        .commit();
                break;
        }
    }

    private void write() {
        if(!mEdtMoney.getText().toString().equals("")){
            mMoney = mEdtMoney.getText().toString();
        } else {
            mMoney = "0";
        }
        if (!mTxvTag.getText().toString().equals("")) {
            flag = mTxvTag.getText().toString();
        }
        int money = Integer.parseInt(mMoney);
        if (flag.equals("出行")) {
            accountingUser = new AccountingUser(money, 0, 0, 0, 0, 0, month, day);
        } else if (flag.equals("教育")) {
            accountingUser = new AccountingUser(0, money, 0, 0, 0, 0, month, day);
        } else if (flag.equals("饮食")) {
            accountingUser = new AccountingUser(0, 0, money, 0, 0, 0, month, day);
        } else if (flag.equals("一般")) {
            accountingUser = new AccountingUser(0, 0, 0, money, 0, 0, month, day);
        } else if (flag.equals("服装")) {
            accountingUser = new AccountingUser(0, 0, 0, 0, money, 0, month, day);
        } else if (flag.equals("娱乐")) {
            accountingUser = new AccountingUser(0, 0, 0, 0, 0, money, month, day);
        }
        Call<Accounting> call = interfaceAdapter.getAccounting(accountingUser, Application.storedUserToken);
        call.enqueue(new Callback<Accounting>() {
            @Override
            public void onResponse(Call<Accounting> call, Response<Accounting> response) {
                Accounting bean = response.body();
                if (response.code() == 200) {
                    ToastUtils.showShort(getContext(), "记录成功");
                }
            }

            @Override
            public void onFailure(Call<Accounting> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

