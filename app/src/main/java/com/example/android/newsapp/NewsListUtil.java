package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.R.id.input;


/**
 * Created by Administrator on 2017/6/25 0025.
 * com.example.android.newsapp,NewsApp
 */

public class NewsListUtil {

    private static final String LOG_TAG = NewsListUtil.class.getSimpleName();

    public static int currentPage;
    public static int totalPage;

    public static ArrayList<NewEntity> fetchNewsData(String requesetUrl){
        URL url = createUrl(requesetUrl);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Get JsonResponse error!",e);
        }

        ArrayList<NewEntity> news = extractNewsFromJson(jsonResponse);

        return  news;
    }

    /***
     * return the Collection of the News
     * @param jsonResponse String
     * @return
     */
    private static ArrayList<NewEntity> extractNewsFromJson(String jsonResponse) {
        if(TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        ArrayList<NewEntity> news = new ArrayList<>();

        try {
            JSONObject base = new JSONObject(jsonResponse);
            JSONObject response = base.getJSONObject(Constant.JsonKey.response);
            JSONArray results = response.getJSONArray(Constant.JsonKey.results);

            //获取当前JSON中的当前页及总页数
            currentPage = response.getInt(Constant.JsonKey.currentPage);
            totalPage = response.getInt(Constant.JsonKey.totalPage);

            if(results.length() > 0){
                for(int i=0;i<results.length();i++){

                    String title,url,publication,starRating,body;

                    JSONObject firstItem = results.getJSONObject(i);
                    if(firstItem.has(Constant.JsonKey.webTitle)) {
                        title = firstItem.getString(Constant.JsonKey.webTitle);
                    }else{
                        return null;
                    }
                    if(firstItem.has(Constant.JsonKey.webUrl)) {
                        url = firstItem.getString(Constant.JsonKey.webUrl);
                    }else{
                        return null;
                    }

                    JSONObject field = firstItem.getJSONObject(Constant.JsonKey.fields);
                    if(field.has(Constant.JsonKey.publication)){
                        publication = field.getString(Constant.JsonKey.publication);
                    }else{
                        publication = Constant.JsonKey.noPublication;
                    }
                    if(field.has(Constant.JsonKey.starRating)){
                        starRating = field.getString(Constant.JsonKey.starRating);
                    }else{
                        starRating = Constant.JsonKey.noRating;
                    }

                    if(field.has(Constant.JsonKey.body)){
                        body = field.getString(Constant.JsonKey.body);
                    }else {
                        body = title;
                    }

                    news.add(new NewEntity(title,body,publication,starRating,url));
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG,"Read from json error!",e);
        }

        return news;
    }

    /***
     * make http request from web
     * @param url URL
     * @return
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;

        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(10000);
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if(httpConnection.getResponseCode() == 200){
                inputStream = httpConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG,"HTTP ERROR:"+httpConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG,"ReadFromInputStream Error!",e);
        }finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line =bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    /***
     * Return the URL
     * @param requesetUrl String
     * @return URL
     */
    private static URL createUrl(String requesetUrl) {

        URL url = null;

        try {
            url = new URL(requesetUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Create URL error!",e);
        }

        return url;
    }

    //
    //去掉字符串里面的html代码
    // 要求数据要规范，比如大于小于号要配套,否则会被集体误杀。
    public static String stripHtml(String content) {
//        // <p>段落替换为换行
//        content = content.replaceAll("<p .*?>", "\r\n");
//        // <br><br/>替换为换行
//        content = content.replaceAll("<br\\s*/?>", "\r\n");
//        // 去掉其它的<>之间的东西
//        content = content.replaceAll("\\<.*?>", "");
//        // 还原HTML
//        // content = HTMLDecoder.decode(content);
//        return content;
        if (content == null || content.trim().equals("")) {
            return "";
        }
        // 去掉所有html元素,
        String str = content.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
                "<[^>]*>", "");
        str = str.replaceAll("[(/>)<]", "");
        int len = str.length();
        if (len <= content.length()) {
            return str;
        } else {
            str = str.substring(0, content.length());
            str += "......";
        }
        return str;
    }
}
