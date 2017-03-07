package com.example.weather.callback;

/**
 * Created by admin on 07.03.17.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.example.weather.NewsProcessor;

import java.io.IOException;

import ru.mail.weather.lib.News;

public class NewsIntentService extends IntentService {
    public final static String ACTION_NEWS = "action.NEWS";

    public final static String EXTRA_NEWS_CATEGORY = "extra.EXTRA_NEWS_CATEGORY";
    public final static String EXTRA_NEWS_RESULT_RECEIVER = "extra.EXTRA_NEWS_RESULT_RECEIVER";

    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_ERROR = 2;
    public final static String EXTRA_NEWS_RESULT = "extra.EXTRA_NEWS_RESULT";

    public NewsIntentService() {
        super("NewsIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWS.equals(action)) {
                final String category = intent.getStringExtra(EXTRA_NEWS_CATEGORY);
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_NEWS_RESULT_RECEIVER);
                handleActionNews(category, receiver);
            }
        }
    }

    private void handleActionNews(final String category, final ResultReceiver receiver) {
        final Bundle data = new Bundle();

        try {
            final News result = NewsProcessor.processNews(this, category);
            if(receiver == null)
                return;
            if (result != null ) {
                receiver.send(RESULT_SUCCESS, data);
            } else {
                data.putString(EXTRA_NEWS_RESULT, "result is null");
                receiver.send(RESULT_ERROR, data);
            }
        } catch (IOException ex) {
            data.putString(EXTRA_NEWS_RESULT, ex.getMessage());
            receiver.send(RESULT_ERROR, data);
        }
    }

}