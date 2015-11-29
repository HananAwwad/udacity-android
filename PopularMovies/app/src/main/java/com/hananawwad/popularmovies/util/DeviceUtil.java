package com.hananawwad.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by user on 11/26/2015.
 */
public class DeviceUtil {
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected());
    }
}
