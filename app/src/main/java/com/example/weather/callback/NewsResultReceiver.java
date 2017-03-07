package com.example.weather.callback;

/**
 * Created by admin on 07.03.17.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class NewsResultReceiver extends ResultReceiver {
    private final int requestId;
    private ServiceHelper.NewsResultListener mListener;

    public NewsResultReceiver(int requestId, final Handler handler) {
        super(handler);
        this.requestId = requestId;
    }

    void setListener(final ServiceHelper.NewsResultListener listener) {
        mListener = listener;
    }

    @Override
    protected void onReceiveResult(final int resultCode, final Bundle resultData) {
        if (mListener != null) {
            final boolean success = (resultCode == NewsIntentService.RESULT_SUCCESS);
            mListener.onNewsResult(success);
        }
        ServiceHelper.getInstance().removeListener(requestId);
    }
}
