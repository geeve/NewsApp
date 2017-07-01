package com.example.android.newsapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;

public class NewWebActivity extends AppCompatActivity {


    private String webUrl,title;

    private WebView webView;

    /***
     * 当点击ActionBar上的返回按钮时触发此事件，使用finish结束当前Activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * 设置返回上一页，如果不设置就会自动关闭
     * @param keyCode int
     * @param event  KeyEvent
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        //如果按手机上的返回按钮，退回上一个Activity
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_web);

        getParam();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);

        webView = (WebView) findViewById(R.id.wv_news);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(webUrl);
    }

    /***
     * 获取从Intent传过来的参数
     */
    private void getParam(){
        Bundle bundle = getIntent().getExtras();

        if(bundle == null){
            webUrl = null;
            return;
        }

        webUrl = bundle.getString(Constant.NEWS_PARAM);
        title = bundle.getString(Constant.NEWS_TITLE);
    }
}
