package com.example.android.newsapp;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NewEntity>>{

    private NewsAdapter mNewsAdapter;

    private SearchView mSearchView;

    private Button mSearchBt;

    private ProgressBar mProgressBar;

    private TextView mEmptyView;

    private String mRequestKey;

    private static String mRequestUrl = "https://content.guardianapis.com/search";

    //是不是第一次加载Loader，如果是则为true，不是为false
    private boolean firstLoad = true;

    private int mCurrentPage = 0;   //当前页
    private int mTotalPage = 0;     //总页数

    private ListView mList;

    private ArrayList<NewEntity> mNewEntitys = new ArrayList<NewEntity>();

    private static final int REQUEST_CODE = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化界面
        initDisplay();

        //启动异步线程Load数据
        //getSupportLoaderManager().initLoader(1,null,this).forceLoad();

    }

    /**
     * 判断网络连接是否存在
     * @return boolean
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * 初始化界面
     * */
    public void initDisplay(){

        //隐藏原生的ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        mEmptyView = (TextView) findViewById(R.id.tv_empty_display);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_progress);
        mProgressBar.setVisibility(View.INVISIBLE);
        mSearchView = (SearchView) findViewById(R.id.sv_key);


        mSearchBt = (Button) findViewById(R.id.bt_search);
        mSearchBt.setClickable(false);

        //加载适配器
        mNewsAdapter = new NewsAdapter(this,0,new ArrayList<NewEntity>());
        mList = (ListView)findViewById(R.id.lv_new_list);
        mList.setAdapter(mNewsAdapter);
        mList.setEmptyView(mEmptyView);
        //设置搜索按钮事件
        mSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果网络连接不存在则提示
                if(!isOnline()){
                    mNewsAdapter.setNews(new ArrayList<NewEntity>());
                    mEmptyView.setText("There is no network!Please Check");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);
                //如果是第一次加载就启动Loader，反之重启Loader
                if(firstLoad) {
                    getSupportLoaderManager().initLoader(1, null, MainActivity.this).forceLoad();
                }else{
                    //重新搜索后要把页数都清空
                    mCurrentPage = 0;
                    mTotalPage = 0;
                    getSupportLoaderManager().restartLoader(1,null,MainActivity.this).forceLoad();
                }
            }
        });

        //设置搜索框事件
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mRequestKey = s;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s != null) {
                    mRequestKey = s;
                    //当搜索关键字改变了后要停止上一次未完成的Loader
                    if(getSupportLoaderManager().hasRunningLoaders()) {
                        getSupportLoaderManager().getLoader(1).stopLoading();
                    }
                }
                return false;
            }
        });



        //当下拉到listview底部时，自动加载下一页内容
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

//                if(i == 0 && mList.getLastVisiblePosition() == (mCurrentPage * 10 -1)){
//                    //Toast.makeText(MainActivity.this,"马上加载第"+Integer.toString(mCurrentPage+1) + "页，总共" + Integer.toString(mTotalPage) + "页。",Toast.LENGTH_LONG);
//                    mBottomView.setText("马上加载第"+Integer.toString(mCurrentPage+1) + "页，总共" + Integer.toString(mTotalPage) + "页。");
//                    mBottomView.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                //获得最后一个显示的Item在Adapter中的位置，从0起；
                int lastItemPosition = mList.getLastVisiblePosition();
                //如果当前显示的最后一项是本页的最后一项并且本页不是最后一页就加载下一页
                if(lastItemPosition == mCurrentPage * 10 -1 && mCurrentPage < mTotalPage){
                    getSupportLoaderManager().restartLoader(1,null,MainActivity.this).forceLoad();
                }

//                if(mCurrentPage == mTotalPage && !firstLoad){
//                    //Toast.makeText(MainActivity.this,"已经是最后一页！",Toast.LENGTH_SHORT);
//                    mBottomView.setText("已经是最后一页！");
//                    mBottomView.setVisibility(View.VISIBLE);
//                }
            }
        });

        //为LIstView中的每个Item添加点击事件
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,NewWebActivity.class);

                String newUrl = mNewEntitys.get(i).getmNewsWebUrl();
                String newTitle = mNewEntitys.get(i).getmNewsTitle();

                intent.putExtra(Constant.NEWS_PARAM,newUrl);
                intent.putExtra(Constant.NEWS_TITLE,newTitle);

                startActivityForResult(intent,REQUEST_CODE);

            }
        });

    }

    @Override
    public Loader<ArrayList<NewEntity>> onCreateLoader(int id, Bundle args) {

        String requestUrl = makeRequestUrl(mRequestKey,mCurrentPage + 1);


        return new NewsAsyncTaskLoader(MainActivity.this,requestUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<NewEntity>> loader, ArrayList<NewEntity> data) {
        //加载完成后进度条消失
        mProgressBar.setVisibility(View.GONE);
        if(data != null){

            //如果是第一页就清空之前的数据，不是就不用清空直接加载
            if(mCurrentPage == 0) {
                mNewsAdapter.setNews(data);
            }else{
                mNewsAdapter.addNews(data);
            }
            firstLoad = false;
            //加载完成后更新当前页及总页数
            mCurrentPage = NewsListUtil.currentPage;
            mTotalPage = NewsListUtil.totalPage;
            //如果是第一页就滚动到头部
            if(mCurrentPage == 1){
                mList.smoothScrollToPosition(0);
            }

            mNewEntitys = mNewsAdapter.getNews();
        }else {
            mEmptyView.setText("No Data!");
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<NewEntity>> loader) {
            mNewsAdapter.setNews(new ArrayList<NewEntity>());
    }

    /***
     * 利用搜索关键字生成查询链接，使用Uri和URLEncoder对链接中的字符进行转换
     * @param key
     * @param pageNum 要获取的页数
     * @return
     */
    private String makeRequestUrl(String key,int pageNum){
        String url="";

        //如果key没有值，就返回null
        if(TextUtils.isEmpty(key)){
            return null;
        }
        Uri baseUri = Uri.parse(mRequestUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //增加链接中药查询的内容
        try {
            //要把查询中所有的其他字符转换成utf-8字符
            uriBuilder.appendQueryParameter("q", URLEncoder.encode(key,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getSimpleName(),"Query Key error!",e);
        }
        uriBuilder.appendQueryParameter("format","json");
        uriBuilder.appendQueryParameter("show-fields","starRating,headline,thumbnail,body,publication");
        uriBuilder.appendQueryParameter("order-by","relevance");
        uriBuilder.appendQueryParameter("page",Integer.toString(pageNum));
        uriBuilder.appendQueryParameter("api-key",Constant.API_KEY);


        url = uriBuilder.toString();

        return url;
    }
}
