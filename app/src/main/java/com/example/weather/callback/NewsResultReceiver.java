package com.example.weather.callback;

/**
 * Created by admin on 07.03.17.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

import ru.mail.weather.lib.News;

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

            Map<String, String> result = new HashMap<>();
            result.put(NewsIntentService.EXTRA_NEWS_DATE, new Long(resultData.getLong(NewsIntentService.EXTRA_NEWS_DATE)).toString());
            result.put(NewsIntentService.EXTRA_NEWS_CONTENT, resultData.getString(NewsIntentService.EXTRA_NEWS_CONTENT));
            result.put(NewsIntentService.EXTRA_NEWS_CATEGORY, resultData.getString(NewsIntentService.EXTRA_NEWS_CATEGORY));
            result.put(NewsIntentService.EXTRA_NEWS_TITLE, resultData.getString(NewsIntentService.EXTRA_NEWS_TITLE));

            mListener.onNewsResult(success, result);
        }
        ServiceHelper.getInstance().removeListener(requestId);
    }
}
