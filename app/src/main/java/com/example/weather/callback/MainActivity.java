package com.example.weather.callback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.SettingsActivity;

import java.util.Date;
import java.util.Map;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.Storage;
import ru.mail.weather.lib.Topics;

public class MainActivity extends AppCompatActivity implements  ServiceHelper.NewsResultListener {

    private int mRequestId;
    private  String categoryName = Topics.AUTO;

    private final View.OnClickListener onSettingsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener onNotUpdateInBackground = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private final View.OnClickListener onUpdateInBackground = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private final View.OnClickListener onUpdateNewsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mRequestId == 0) {
                startProgress();
                mRequestId = ServiceHelper.getInstance().updateNews(MainActivity.this, categoryName, MainActivity.this);
            } else {
                Toast.makeText(MainActivity.this, "There is pending request", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_settings).setOnClickListener(onSettingsClick);
        findViewById(R.id.btn_update_now).setOnClickListener(onUpdateNewsClick);
        findViewById(R.id.btn_update_in_background).setOnClickListener(onUpdateInBackground);
        findViewById(R.id.btn_not_update_in_background).setOnClickListener(onNotUpdateInBackground);
        findViewById(R.id.text_news_title).setOnClickListener(onNotUpdateInBackground);

        Storage storage = Storage.getInstance(MainActivity.this);
        News news = storage.getLastSavedNews();

        if(news != null) {
            ((TextView)findViewById(R.id.text_news_title)).setText(news.getTitle());
            ((TextView)findViewById(R.id.text_news_content)).setText(news.getBody());
            ((TextView)findViewById(R.id.text_news_date)).setText((new Date(news.getDate() * 1000)).toString());
            String topic = storage.loadCurrentTopic();
            if(topic != null)
                categoryName = topic;
        }

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getStringExtra(NewsIntentService.EXTRA_NEWS_CATEGORY) != null ) {
                startProgress();
                categoryName = intent.getStringExtra(NewsIntentService.EXTRA_NEWS_CATEGORY);
                intent.removeExtra(NewsIntentService.EXTRA_NEWS_CATEGORY);
                mRequestId = ServiceHelper.getInstance().updateNews(MainActivity.this, categoryName, MainActivity.this);
                return;
            }
        }
    }


    @Override
    public void onNewsResult(boolean success, Map<String, String> result) {
        mRequestId = 0;
        stopProgress();
        if (success && result != null) {

            String date = (new Date(Long.parseLong(result.get(NewsIntentService.EXTRA_NEWS_DATE)) * 1000)).toString();

            ((TextView)findViewById(R.id.text_news_title)).setText(result.get(NewsIntentService.EXTRA_NEWS_TITLE));
            ((TextView)findViewById(R.id.text_news_content)).setText(result.get(NewsIntentService.EXTRA_NEWS_CONTENT));
            ((TextView)findViewById(R.id.text_news_date))
                    .setText(date);
        }
    }

    private void startProgress() {
        findViewById(R.id.progress_news).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_update_now).setVisibility(View.INVISIBLE);
    }

    private void stopProgress() {
        findViewById(R.id.progress_news).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_update_now).setVisibility(View.VISIBLE);
    }


}
