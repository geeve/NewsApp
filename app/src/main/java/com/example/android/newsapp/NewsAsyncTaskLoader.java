package com.example.android.newsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/26 0026.
 * com.example.android.newsapp,NewsApp
 */

public class NewsAsyncTaskLoader extends AsyncTaskLoader<ArrayList<NewEntity>> {

    private String mUrl;

    public NewsAsyncTaskLoader(Context context,String url) {
        super(context);

        this.mUrl = url;
    }

    @Override
    public ArrayList<NewEntity> loadInBackground() {
        if(mUrl == null){
            return  null;
        }

        return NewsListUtil.fetchNewsData(mUrl);
    }
}
