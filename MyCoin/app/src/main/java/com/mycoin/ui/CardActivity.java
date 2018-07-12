package com.mycoin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycoin.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CardActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mTbCard;
    private ImageButton mBtnCardBack;
    private MaterialEditText mEdtStuNo, mEdtStuPwd, mEdtDate;
    private LinearLayout mLlCalender;
    private MaterialCalendarView mCalender;
    private TextView mTxvDate;
    private Button mBtnSelect, mBtnDateCancel, mBtnDateConfirm;
    private WebView mWvCard;

    private String mStuNo;
    private String mDate;
    // private String mStart;
    // private String mEnd;
    private String mSelected[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        initView();
    }

    private void initView() {
        mTbCard = (Toolbar) findViewById(R.id.toolbar_card) ;
        mBtnCardBack = (ImageButton) findViewById(R.id.btn_card_back);
        mBtnSelect = (Button) findViewById(R.id.btn_select);
        mEdtStuNo = (MaterialEditText) findViewById(R.id.edt_stu_no);
        mEdtStuPwd = (MaterialEditText) findViewById(R.id.edt_stu_pwd);
        mEdtDate = (MaterialEditText) findViewById(R.id.edt_date);
        // mEdtStart = (MaterialEditText) findViewById(R.id.edt_start);
        // mEdtEnd = (MaterialEditText) findViewById(R.id.edt_end);
        mLlCalender = (LinearLayout) findViewById(R.id.ll_calendar);
        mCalender = (MaterialCalendarView) findViewById(R.id.calendar);
        mTxvDate = (TextView) findViewById(R.id.txv_date);
        mBtnDateCancel = (Button) findViewById(R.id.btn_date_cancel);
        mBtnDateConfirm = (Button) findViewById(R.id.btn_date_confirm);
        mWvCard = (WebView) findViewById(R.id.wv_card);

        mBtnCardBack.setOnClickListener(this);
        mBtnSelect.setOnClickListener(this);
        mEdtDate.setOnClickListener(this);
        // mEdtStart.setOnClickListener(this);
        // mEdtEnd.setOnClickListener(this);
        mBtnDateConfirm.setOnClickListener(this);
        mBtnDateCancel.setOnClickListener(this);

        mEdtStuPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

        setSupportActionBar(mTbCard);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn_card_back :
               Intent intent = new Intent(CardActivity.this, CoinMainActivity.class);
               intent.putExtra("id", 3);
               startActivity(intent);
               break;
           case R.id.btn_select :
               initWebView();
               mWvCard.setVisibility(View.VISIBLE);
               mBtnSelect.setVisibility(View.GONE);
               break;
           case R.id.edt_date :
               initCalendar();
               mBtnSelect.setVisibility(View.GONE);
               mLlCalender.setVisibility(View.VISIBLE);
               mLlCalender.bringChildToFront(mLlCalender);
               break;
           // case R.id.edt_start :
           //     initCalendarStart();
           //     mBtnSelect.setVisibility(View.GONE);
           //     mLlCalender.setVisibility(View.VISIBLE);
           //     mLlCalender.bringChildToFront(mLlCalender);
           //     break;
           // case R.id.edt_end :
           //     initCalendarEnd();
           //     mBtnSelect.setVisibility(View.GONE);
           //     mLlCalender.setVisibility(View.VISIBLE);
           //     mLlCalender.bringChildToFront(mLlCalender);
           //     break;
           case R.id.btn_date_cancel :
               mLlCalender.setVisibility(View.GONE);
               break;
           case R.id.btn_date_confirm :
               mBtnSelect.setVisibility(View.VISIBLE);
               mLlCalender.setVisibility(View.GONE);
               break;
       }
    }

    public void initCalendar() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        mTxvDate.setText(currentDate);
        mCalender.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay select, boolean selected) {
                mSelected = select.toString().split("\\{|\\}");
                String temp = mSelected[1]; //2018-7-6
                String s[] = temp.split("-");
                int tempM = Integer.parseInt(s[1]);
                int tempD = Integer.parseInt(s[2]);
                String month;
                String day;
                if (tempM + 1 < 10) {
                    month = "0" + Integer.toString(tempM + 1);
                } else {
                    month = Integer.toString(tempM + 1);
                }
                if (tempD < 10) {
                    day = "0" + s[2];
                } else {
                    day = s[2];
                }
                mDate = s[0] + "-" + month + "-" + day;
                mEdtDate.setText(mDate);
            }
        });
    }

    private void initWebView() {
        if (!mEdtStuNo.getText().toString().equals("")) {
            mStuNo = mEdtStuNo.getText().toString();
        }
        mWvCard.getSettings().setJavaScriptEnabled(true);
        mWvCard.setWebViewClient(new WebViewClient());
        // http://112.74.88.136:4600/card?user=2016210773&time=2018-07-05
        mWvCard.loadUrl("http://112.74.88.136:4600/card?user=" + mStuNo + "&time=" + mDate);
    }

    // public void initCalendarStart() {
    //     String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    //     mTxvDate.setText(currentDate);
    //     mCalender.setOnDateChangedListener(new OnDateSelectedListener() {
    //         @Override
    //         public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay select, boolean selected) {
    //             mSelected = select.toString().split("\\{|\\}");
    //             String temp = mSelected[1]; //2018-7-6
    //             String s[] = temp.split("-");
    //             int tempM = Integer.parseInt(s[1]);
    //             String month = Integer.toString(tempM + 1);
    //             mStart = s[0] + "-" + month + "-" + s[2];
    //             mEdtStart.setText(mStart);
    //         }
    //     });
    // }
//
    // public void initCalendarEnd() {
    //     String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    //     mTxvDate.setText(currentDate);
    //     mCalender.setOnDateChangedListener(new OnDateSelectedListener() {
    //         @Override
    //         public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay select, boolean selected) {
    //             mSelected = select.toString().split("\\{|\\}");
    //             String temp = mSelected[1]; //2018-7-6
    //             String s[] = temp.split("-");
    //             int tempM = Integer.parseInt(s[1]);
    //             String month = Integer.toString(tempM + 1);
    //             mEnd = s[0] + "-" + month + "-" + s[2];
    //             mEdtEnd.setText(mEnd);
    //         }
    //     });
    // }

}
