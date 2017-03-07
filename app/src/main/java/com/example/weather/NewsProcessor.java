package com.example.weather;


import android.content.Context;

import java.io.IOException;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.NewsLoader;
import ru.mail.weather.lib.Storage;

/**
 * Created by admin on 07.03.17.
 */

public class NewsProcessor {

    public static News processNews(Context context, final String text) throws IOException {

        NewsLoader newsLoader = new NewsLoader();
        News news = null;
        try {
            news = newsLoader.loadNews(text);

        } catch (IOException e) {

        }

        if(news != null) {
            Storage storage = Storage.getInstance(context);
            storage.saveCurrentTopic(text);
            storage.saveNews(news);
        }

        return news;
    }

}
