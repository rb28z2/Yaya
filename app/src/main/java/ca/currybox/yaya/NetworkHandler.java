package ca.currybox.yaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.*;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.auth.AuthScope;

/**
 * Created by write_only_mem on 2016-02-14.
 */
public class NetworkHandler {

    private Context ctx;
    private String synopsis;
    private TextView obj;
    private ImageView imgobj;
    private int id;

    public void setSynopsis(String title, int id, TextView elem, Context context) {
        ctx = context;
        obj = elem;
        this.id = id;
        search search = new search();
        search.execute(title);
    }

    public void setImage(String title, int id, ImageView elem, Context context){
        ctx = context;
        imgobj = elem;
        this.id = id;
        myAnimeListClientUsage client = new myAnimeListClientUsage();
        client.searchXML(title);
        client.downloadImages(title);
    }

    protected class myAnimeListClient extends AsyncHttpClient{
        protected static final String BASE_URL = "https://myanimelist.net/api/anime/search.xml?q=";

        private AsyncHttpClient client = new AsyncHttpClient();

        public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        private String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }
    }

    private class myAnimeListClientUsage {
        myAnimeListClient malClient = new myAnimeListClient();

        private void searchXML(String title) {
            Log.i("Penis", title);
            RequestParams params = new RequestParams();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //preferences object
            String userName = prefs.getString("pref_mal_username", "a");
            String password = prefs.getString("pref_mal_password", "a"); //set credentials
            malClient.setBasicAuth(userName, password, new AuthScope("myanimelist.net", 80, AuthScope.ANY_REALM));
            params.put("username", userName);
            params.put("password", password);
            params.put("User-Agent", new ApiKey().getKey());
            System.out.println("Paramsssss: " + params);
            malClient.setBasicAuth(userName, password);
            //gets the XML file in a byte array
            malClient.get(malClient.BASE_URL + title, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.out.println("StatusCode1: " + statusCode);
                    Log.i("aaaaaaaaaa", "bbbbbbbbb");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.i(responseString, "penis vagina");
                }
            });
        }

        private void downloadImages(String title) {
            RequestParams params = new RequestParams();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //preferences object
            String userName = prefs.getString("pref_mal_username", "a");
            String password = prefs.getString("pref_mal_password", "a"); //set credentials
            malClient.setBasicAuth(userName, password, new AuthScope("myanimelist.net", 80, AuthScope.ANY_REALM));
            params.put("username", userName);
            params.put("password", password);
            params.put("User-Agent", new ApiKey().getKey());
            System.out.println("Paramsssss: " + params);
            malClient.get(malClient.BASE_URL + title, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.out.println("StatusCode2: " + statusCode);
                    Log.i("cccccccc", "dddddddddd");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.i(responseString, "dkifosjdkf");
                }
            });
        }
    }
//        String fileName = animeID + "jpg";
//        File file = new File(ctx.getFilesDir(), fileName);
//        InputStream is;
//        try {
//            URL url = new URL(imageURL);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//            // make input streams
//            is = urlConnection.getInputStream();
//
//            OutputStream os = new FileOutputStream(file);
//            byte[] data = new byte[is.available()];
//            is.read(data);
//            os.write(data);
//            is.close();
//            os.close();
//        } catch (java.net.MalformedURLException e) {
//            //synopsis.setText("BAD URL Contact the dev");
//        } catch (IOException e) {
//            //synopsis.setText("IO Error. Check if your network is working");
//            Log.e("IOException", e.getMessage());
//            e.printStackTrace();
//        }

    private class search extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... title) {
            StringBuilder summary = new StringBuilder();
            try {
                String searchUrl = "http://myanimelist.net/api/anime/search.xml?q=";
                searchUrl = searchUrl + URLEncoder.encode(title[0], "UTF-8");
                Log.i("searchURL", searchUrl);
                //String searchUrl = "http://android.com";
                URL url = new URL(searchUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //preferences object
                String creds = prefs.getString("pref_mal_username", "a") + ":" + prefs.getString("pref_mal_password", "a"); //set credentials
                final String basicAuth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("User-Agent", new ApiKey().getKey());
                urlConnection.setRequestProperty("Authorization", basicAuth);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = r.readLine()) != null) {
                    summary.append(line);
                }
                urlConnection.disconnect();
            } catch (java.net.MalformedURLException e) {
                //synopsis.setText("BAD URL Contact the dev");
            } catch (IOException e) {
                //synopsis.setText("IO Error. Check if your network is working");
                Log.e("IOException", e.getMessage());
                e.printStackTrace();

            }
            synopsis = summary.toString();
            return null;
        }

        //I assume this is where you actually set set the obj to what you got
        @Override
        protected void onPostExecute(Void data) {

            String syn = synopsis.substring(synopsis.indexOf("<id>" + id + "</id>"));

            syn = syn.substring(syn.indexOf("<synopsis>") + 10, syn.indexOf("</synopsis>"));

            syn = Jsoup.parse(syn).text();
            syn = syn.replaceAll("<br />", "\n");
            syn = syn.replaceAll("\\[[^]]+\\]", "");
            syn = Parser.unescapeEntities(syn, false);
            obj.setText(syn);
            Log.i("synopsis", syn);
        }
    }

}
