package com.example.weather.callback;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import ru.mail.weather.lib.News;

/**
 * Created by admin on 07.03.17.
 */

public class ServiceHelper {
    private int mIdCounter = 1;
    private final Map<Integer, NewsResultReceiver> mResultReceivers = new Hashtable<>();

    private static ServiceHelper instance;

    private ServiceHelper() {
    }

    synchronized static ServiceHelper getInstance() {
        if (instance == null) {
            instance = new ServiceHelper();
        }
        return instance;
    }

    int updateNews(final Context context, final String category, final NewsResultListener listener) {
        final NewsResultReceiver receiver = new NewsResultReceiver(mIdCounter, new Handler());
        receiver.setListener(listener);
        mResultReceivers.put(mIdCounter, receiver);

        Intent intent = new Intent(context, NewsIntentService.class);
        intent.setAction(NewsIntentService.ACTION_NEWS);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_CATEGORY, category);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_RESULT_RECEIVER, receiver);
        context.startService(intent);

        return mIdCounter++;
    }

    void removeListener(final int id) {
        NewsResultReceiver receiver = mResultReceivers.remove(id);
        if (receiver != null) {
            receiver.setListener(null);
        }
    }

    interface NewsResultListener {
        void onNewsResult(final boolean success, final Map<String, String> result);
    }
}
