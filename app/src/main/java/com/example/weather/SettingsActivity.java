package com.example.weather;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.weather.callback.MainActivity;
import com.example.weather.callback.NewsIntentService;

import ru.mail.weather.lib.Topics;

public class SettingsActivity extends AppCompatActivity {

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    private final View.OnClickListener onSettingsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra(NewsIntentService.EXTRA_NEWS_CATEGORY, ((Button)view).getText());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((TextView)findViewById(R.id.btn_category_1)).setText(Topics.AUTO);
        ((TextView)findViewById(R.id.btn_category_2)).setText(Topics.HEALTH);
        ((TextView)findViewById(R.id.btn_category_3)).setText(Topics.IT);

        findViewById(R.id.btn_category_1).setOnClickListener(onSettingsClick);
        findViewById(R.id.btn_category_2).setOnClickListener(onSettingsClick);
        findViewById(R.id.btn_category_3).setOnClickListener(onSettingsClick);

    }
}
