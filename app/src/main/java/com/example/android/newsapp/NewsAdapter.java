package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/6/25 0025.
 * com.example.android.newsapp,NewsApp
 */

public class NewsAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<NewEntity> mNews;
    private NewsViewHolder newsHolder;

    public NewsAdapter(Context context, int resource, ArrayList<NewEntity> news) {
        super(context, resource, news);

        this.mContext = context;
        this.mNews = news;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        newsHolder = new NewsViewHolder();

        View currentView = convertView;
        if(convertView == null){
            currentView = LayoutInflater.from(mContext).inflate(R.layout.news_list_item,parent,false);

            newsHolder.newsDetail = (TextView)currentView.findViewById(R.id.list_item_news_detail);
            newsHolder.newsPublisher = (TextView)currentView.findViewById(R.id.list_item_publisher);
            newsHolder.newsRating = (RatingBar) currentView.findViewById(R.id.list_item_news_rating);
            newsHolder.newsTitle = (TextView) currentView.findViewById(R.id.list_item_news_title);

            currentView.setTag(newsHolder);
        }else{
            newsHolder = (NewsViewHolder) currentView.getTag();
        }

        NewEntity n = mNews.get(position);
        newsHolder.newsTitle.setText(n.getmNewsTitle());
        newsHolder.newsPublisher.setText(n.getmNewsPulbisher());
        newsHolder.newsDetail.setText(NewsListUtil.stripHtml(n.getmNewsDetail().substring(0,150)) + "...");
        newsHolder.newsRating.setNumStars(5);
        newsHolder.newsRating.setRating(Float.parseFloat(n.getmNewsRating()));

        return currentView;
    }


    /***
     * 设定ArrayList中的数据，可以通过setNews（“”）清空数据
     * @param news ArrayList类型
     */
    public void setNews(ArrayList<NewEntity> news){
        //之前的数据都清空后再添加
        if(mNews.size()>0){
            mNews.clear();
        }

        mNews.addAll(news);
        notifyDataSetChanged();
    }

    /***
     * 当不是第一页时，不用删除之前的数据
     * @param news
     */
    public void addNews(ArrayList<NewEntity> news){
        mNews.addAll(news);
        notifyDataSetChanged();
    }

    /***
     * 获得数据
     * @return
     */
    public ArrayList<NewEntity> getNews(){
        return mNews;
    }
}
