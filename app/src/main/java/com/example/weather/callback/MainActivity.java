package com.example.weather.callback;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.SettingsActivity;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.Scheduler;
import ru.mail.weather.lib.Storage;
import ru.mail.weather.lib.Topics;

public class MainActivity extends AppCompatActivity implements  ServiceHelper.NewsResultListener {

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    private AtomicInteger requestId = new AtomicInteger();
    private  String categoryName = Topics.AUTO;
    private static Scheduler scheduler = Scheduler.getInstance();
    private static boolean background = false;

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
            if(background) {
                background = false;
                Scheduler.getInstance().unschedule(MainActivity.this, intentForBackGround());
            }
        }
    };

    private final View.OnClickListener onUpdateInBackground = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!background) {
                background = true;
                requestId.incrementAndGet();
                MainActivity.scheduler.schedule(MainActivity.this, intentForBackGround(), 6000);
            }
        }
    };

    private final View.OnClickListener onUpdateNewsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(requestId.get() == 0) {
                startProgress();
                requestId.set(ServiceHelper.getInstance().updateNews(MainActivity.this, categoryName, MainActivity.this));
            } else {
                Toast.makeText(MainActivity.this, "There is pending request", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        Storage storage = Storage.getInstance(MainActivity.this);

        Intent intent = getIntent();
        if(intent != null) {
            startProgress();
            categoryName = intent.getStringExtra(NewsIntentService.EXTRA_NEWS_CATEGORY);
            if(categoryName == null) {
                this.categoryName = storage.loadCurrentTopic() == null ? Topics.AUTO :
                                                                        storage.loadCurrentTopic();
            }
            requestId.set(ServiceHelper.getInstance().updateNews(MainActivity.this, categoryName, MainActivity.this));

        }

        News news = storage.getLastSavedNews();

        if(news != null) {
            ((TextView)findViewById(R.id.text_news_title)).setText(news.getTitle());
            ((TextView)findViewById(R.id.text_news_content)).setText(news.getBody());
            ((TextView)findViewById(R.id.text_news_date)).setText((new Date(news.getDate() * 1000)).toString());
            String topic = storage.loadCurrentTopic();
            if(topic != null) {
                categoryName = topic;
            }
        }
    }

    private void initializeUI() {
        findViewById(R.id.btn_settings).setOnClickListener(onSettingsClick);
        findViewById(R.id.btn_update_now).setOnClickListener(onUpdateNewsClick);
        findViewById(R.id.btn_update_in_background).setOnClickListener(onUpdateInBackground);
        findViewById(R.id.btn_not_update_in_background).setOnClickListener(onNotUpdateInBackground);
    }

    @Override
    protected void onStop() {
        ServiceHelper.getInstance().removeListener(requestId.get());
        super.onStop();
    }

    @Override
    public void onNewsResult(boolean success) {
        requestId.set(0);
        stopProgress();
        if (success) {

            Storage storage = Storage.getInstance(this);
            ((TextView)findViewById(R.id.text_news_title)).setText(storage.getLastSavedNews().getTitle());
            ((TextView)findViewById(R.id.text_news_content)).setText(storage.getLastSavedNews().getBody());
            ((TextView)findViewById(R.id.text_news_date))
                    .setText(new Date(storage.getLastSavedNews().getDate()).toString());
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


    private Intent intentForBackGround() {
        Intent intent = new Intent(MainActivity.this, NewsIntentService.class);
        intent.setAction(NewsIntentService.ACTION_NEWS);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_CATEGORY, MainActivity.this.categoryName);
        return intent;
    }


}
