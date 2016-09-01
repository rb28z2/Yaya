package ca.currybox.yaya;

import android.util.Log;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Avinash on 2016-08-30.
 */

public class malClient extends AsyncHttpClient{
    private static String BASE_URL = "myanimelist.net/api/anime/search.xml?q=";
    //private static String UA_URL = "http://headers.cloxy.net/request.php";
    private static String USERNAME;
    private static String PASSWORD;
    private static String data = "";

    private static AsyncHttpClient malClient = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        malClient.setUserAgent(new ApiKey().getKey());
        malClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getFile(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        malClient.setUserAgent(new ApiKey().getKey());
        malClient.get(url, params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        malClient.setUserAgent(new ApiKey().getKey());
        malClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return "https://" + USERNAME + ":" + PASSWORD + "@" + BASE_URL + relativeUrl;
        //return BASE_URL + relativeUrl;
    }

    public void setCreds(String userName, String password){
        USERNAME = userName;
        PASSWORD = password;
    }

    public void setData(String d){
        this.data = d;
    }

    public String getData(){
        return this.data;
    }
}