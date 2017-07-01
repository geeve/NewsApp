package com.example.android.newsapp;

/**
 * Created by Administrator on 2017/6/25 0025.
 * com.example.android.newsapp,NewsApp
 */

public class NewEntity {
    private String mNewsTitle;
    private String mNewsDetail;
    private String mNewsPulbisher;
    private String mNewsRating;
    private String mNewsWebUrl;

    public NewEntity(String newsTitle,String newsDetail,String newsPublisher,String newsRating,String newsWebUrl) {
        this.mNewsDetail = newsDetail;
        this.mNewsTitle = newsTitle;
        this.mNewsPulbisher = newsPublisher;
        this.mNewsRating = newsRating;
        this.mNewsWebUrl = newsWebUrl;
    }

    public String getmNewsTitle() {
        return mNewsTitle;
    }

    public String getmNewsDetail() {
        return mNewsDetail;
    }

    public String getmNewsPulbisher() {
        return mNewsPulbisher;
    }

    public String getmNewsRating() {
        return mNewsRating;
    }

    public String getmNewsWebUrl() {
        return mNewsWebUrl;
    }
}
