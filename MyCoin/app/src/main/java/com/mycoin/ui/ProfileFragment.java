package com.mycoin.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycoin.Application;
import com.mycoin.R;
import com.mycoin.data.Avatar;
import com.mycoin.data.InterfaceAdapter;
import com.mycoin.data.Profile;
import com.mycoin.util.CameraUtils;
import com.mycoin.util.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private CircleImageView mImvAvatar;
    private TextView mTxvProfileName;
    private Button mBtnCard;
    private Button mBtnExit;
    private AlertDialog mAlertDialog;

    private Uri mImageUri;
    private String mTempUrl;
    private boolean isGranted = false;
    private boolean isCanceled = true;
    private int mTime = 10;
    private static final int PERMISSION_GRANTED = 200;

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
        mImvAvatar.setImageResource(R.drawable.user_avatar);

        initNet();
        requestPermission();

        if (Application.storedUserAvatarUrl != "") {
            Picasso.get().load(Application.storedUserAvatarUrl).into(mImvAvatar);
        } else {
            mImvAvatar.setImageResource(R.drawable.user_avatar);
            // Picasso.get().load("http://ohr9krjig.bkt.clouddn.com/user_avatar.png").into(mImvAvatar);
        }
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
                Application.storedUserAvatarUrl = "";
                cleanMessage();
            }
        });

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
                            Application.storedUserAvatarUrl = profile.getAvatar();
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

    private class OnAlertClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.txv_album:
                    mAlertDialog.dismiss();
                    mImageUri = CameraUtils.getImageUri(getActivity());
                    openSystemAlbum();
                    break;
                case R.id.txv_camera:
                    mAlertDialog.dismiss();
                    mImageUri = CameraUtils.getImageUri(getActivity());
                    openSystemCamera();
                    break;
            }
        }
    }

    private void openSystemAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CameraUtils.OPEN_ALBUM);
    }

    private void openSystemCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, CameraUtils.TAKE_PHOTO);
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_GRANTED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_GRANTED: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isGranted = true;
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CameraUtils.TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        CameraUtils.display(getActivity(), mImageUri, mImvAvatar);
                        Bitmap bitmap = ((BitmapDrawable) mImvAvatar.getDrawable()).getBitmap();
                        File file = createCompressedBitmapFile(9000000, bitmap);
                        uploadPic(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CameraUtils.OPEN_ALBUM:
                if (resultCode != RESULT_CANCELED) {
                    if (resultCode != RESULT_CANCELED && isGranted) {
                        String imagePath = CameraUtils.handlImageOnKitKat(getActivity(), data);
                        CameraUtils.display(imagePath, mImvAvatar);
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        File file = null;
                        try {
                            file = createCompressedBitmapFile(getFileSize(imagePath), bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            uploadPic(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        timer.start();
                        break;
                    }
                }
        }
    }

    private int getFileSize(String imagePath) throws IOException {
        File file = new File(imagePath);
        FileInputStream stream = new FileInputStream(file);
        return stream.available();
    }

    private File createCompressedBitmapFile(int fileSize,Bitmap bitmap) throws IOException {
        File file = File.createTempFile("mycoin",".jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int quality = 0;
        if (fileSize > 8000000) {
            quality = 30;
        } else if (fileSize >= 4000000 && fileSize < 8000000) {
            quality = 40;
        } else if (fileSize < 4000000) {
            quality = 70;
        } else if (fileSize < 2000000) {
            quality = 90;
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(b);
        fileOutputStream.close();

        return file;
    }

    private void uploadPic(File file) throws IOException {
        if(file == null){
            ToastUtils.showShort(getActivity(), R.string.path_error);
            return;
        }
        UploadManager uploadManager = new UploadManager();
        String key = "mycoin.jpg";
        String token = "vGsefz5Y1J-RwGLB8gA89bTxqp02ETVGpfrA7mcu:YAzBZ4K3TZZGI7JYWvEFRhar_ac=:eyJzY29wZSI6InBpY3R1cmVzIiwiZGVhZGxpbmUiOjE1MzE1MTY3NDN9";
        uploadManager.put(file, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                if(info.isOK()) {
                    Log.d("qiniu", "Upload Success");
                } else {
                    Log.d("qiniu", "Upload Fail");
                }
                String s = key + ", " + info + ", " + res;
                Log.i("qiniutest", s);
            }
        }, null);

        changeAvatar(key);
    }

    private void changeAvatar(String key) {
        mTempUrl = "http://ohr9krjig.bkt.clouddn.com/" + key;
        Avatar avatar = new Avatar(mTempUrl);
        Call<Avatar> call = interfaceAdapter.getAvatar(avatar, Application.storedUserToken);
        call.enqueue(new Callback<Avatar>() {
            @Override
            public void onResponse(Call<Avatar> call, Response<Avatar> response) {
                Avatar bean = response.body();
                if (response.code() == 200) {
                    Application.storedUserAvatarUrl = mTempUrl;
                }
            }

            @Override
            public void onFailure(Call<Avatar> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void cleanMessage() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("token", "");
        editor.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private CountDownTimer timer = new CountDownTimer(1000,10000) {
        @Override
        public void onTick(long l) {
            mTime--;
        }

        @Override
        public void onFinish() {
            mTime = 10;
            if (!isCanceled) {
                ToastUtils.showShort(getActivity(), R.string.time_out);
            }
            isCanceled = true;
        }
    };
}

