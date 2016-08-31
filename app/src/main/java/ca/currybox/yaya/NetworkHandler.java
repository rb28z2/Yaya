package ca.currybox.yaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
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
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;


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

    public void downloadXML(String title){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //preferences object

        //get credentials to use basic auth in http request
        String userName = prefs.getString("pref_mal_username", "a");
        String password = prefs.getString("pref_mal_password", "a");

        try {
            //encode so no url BS
            String encode = URLEncoder.encode(title, "utf-8");

            //make a new client to talk to mal
            malClient client = new malClient();
            client.setCreds(userName, password);

            //request the .xml for the title in question
            client.post(encode, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("Status Success Code", String.valueOf(statusCode));
                    System.out.println("Body:" + responseBody.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error ) {
                    Log.i("Status Failed Code", String.valueOf(statusCode));
                    Log.i("Code", String.valueOf(statusCode));
                    Log.i("Body", String.valueOf(responseBody.length));
                    Log.i("Reason", String.valueOf(error));
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setImage(String title, int id, ImageView elem, Context context) {
        ctx = context;
        imgobj = elem;

    }

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
