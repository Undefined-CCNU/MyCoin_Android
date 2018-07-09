package com.mycoin.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.mycoin.R;

public class ReportActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ImageButton mBtnBack;
    private Button mBtnLately;
    private Button mBtnMonthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initView();
        initViewPager();
    }

    private void initView() {
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnLately = (Button) findViewById(R.id.btn_lately);
        mBtnMonthly = (Button) findViewById(R.id.btn_monthly);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_report);

        mBtnBack.setOnClickListener(this);
        mBtnLately.setOnClickListener(this);
        mBtnMonthly.setOnClickListener(this);

        mBtnLately.setTextColor(getResources().getColor(R.color.purple));
        mBtnMonthly.setTextColor(Color.BLACK);

        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp_choose);
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(
                getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new LatelyFragment();
                        break;
                    case 1:
                        fragment = new MonthlyFragment();
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBtnLately.setTextColor(getResources().getColor(R.color.purple));
                        mBtnMonthly.setTextColor(Color.BLACK);
                        break;
                    case 1:
                        mBtnLately.setTextColor(Color.BLACK);
                        mBtnMonthly.setTextColor(getResources().getColor(R.color.purple));
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lately:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.btn_monthly:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.btn_back:
                Intent intent = new Intent(ReportActivity.this, CoinMainActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                break;
        }
    }

}

