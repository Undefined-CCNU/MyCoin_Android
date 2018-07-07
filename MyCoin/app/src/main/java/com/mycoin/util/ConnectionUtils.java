package com.mycoin.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.mycoin.R;

public class ConnectionUtils {

    private Context context;

    public ConnectionUtils(Context context){
        this.context = context;

    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWork = connectivityManager.getActiveNetworkInfo();
        if (activeNetWork != null) {
            if(activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    public static void makeSnackBar(View view, Context context) {
        ConnectionUtils connectionUtils = new ConnectionUtils(context);
        boolean isConnected = connectionUtils.isConnectingToInternet();
        if (!isConnected) {
            Snackbar.make(view, R.string.check_connection, Snackbar.LENGTH_INDEFINITE).
                    setAction(R.string.go_to_check, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }
    }
}
