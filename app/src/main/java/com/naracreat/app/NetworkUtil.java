package com.naracreat.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkUtil {
    public static boolean isOnline(Context c) {
        try {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            Network n = cm.getActiveNetwork();
            if (n == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(n);
            return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } catch (Exception e) {
            return false;
        }
    }
}
