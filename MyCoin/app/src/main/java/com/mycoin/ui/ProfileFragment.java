package com.mycoin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.InterfaceAdapter;
import com.mycoin.data.Profile;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    private CircleImageView mImvAvatar;
    private TextView mTxvProfileName;
    private Button mBtnCard;
    private Button mBtnExit;
    private AlertDialog mAlertDialog;
    private String mImageUri;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Retrofit retrofit;
    InterfaceAdapter interfaceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mImvAvatar = (CircleImageView) view.findViewById(R.id.imv_avatar);
        mTxvProfileName = (TextView) view.findViewById(R.id.txv_profile_name);
        mBtnCard = (Button) view.findViewById(R.id.btn_card);
        mBtnExit = (Button) view.findViewById(R.id.btn_exit);

        /*
        mImvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View alertView = inflater.inflate(R.layout.alert_avatar, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(alertView);
                mAlertDialog = builder.create();
                mAlertDialog.show();
                TextView mTxvAlbum = (TextView) mAlertDialog.findViewById(R.id.txv_album);
                TextView mTxvCamera = (TextView) mAlertDialog.findViewById(R.id.txv_camera);
                mTxvAlbum.setOnClickListener(new OnAlertClickListener());
                mTxvCamera.setOnClickListener(new OnAlertClickListener());
            }
        });
        */

        mBtnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardActivity.class);
                startActivity(intent);
            }
        });
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                Application.storedUsername = "";
                Application.storedUserPassword = "";
                Application.storedUserToken = "";
                Application.storedUserBudget = 0;
                cleanMessage();
            }
        });

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
        getUserProfile();
    }

    private void getUserProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<Profile> call = interfaceAdapter.getProfile(Application.storedUserToken);
                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Profile profile = response.body();
                        try {
                            mTxvProfileName.setText(profile.getUsername());
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }).start();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void cleanMessage() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("token", "");
        editor.commit();
    }

    /*
    private class OnAlertClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.txv_album:
                    mAlertDialog.dismiss();
                    //mImageUri = CameraUtils.getImageUri(getActivity());
                    //openSystemAlbum();
                    break;
                case R.id.txv_camera:
                    mAlertDialog.dismiss();
                    //mImageUri = CameraUtils.getImageUri(getActivity());
                    //openSystemCamera();
                    break;
            }
        }
    }
    */
}

