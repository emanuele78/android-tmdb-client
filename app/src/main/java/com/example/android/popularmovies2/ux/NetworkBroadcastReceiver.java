package com.example.android.popularmovies2.ux;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.popularmovies2.network.NetworkUtils;

/**
 * Created by Emanuele on 05/03/2018.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private OnNetworkStatusChange listener;
    private Boolean isConnected;

    public NetworkBroadcastReceiver(OnNetworkStatusChange listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        if (isConnected == null) {
            isConnected = NetworkUtils.isDeviceConnected(context);
            listener.onNetworkChange(isConnected);
        } else {
            if (isConnected != NetworkUtils.isDeviceConnected(context)) {
                isConnected = NetworkUtils.isDeviceConnected(context);
                listener.onNetworkChange(isConnected);
            }
        }
    }

    public interface OnNetworkStatusChange {

        void onNetworkChange(boolean isConnected);
    }
}
