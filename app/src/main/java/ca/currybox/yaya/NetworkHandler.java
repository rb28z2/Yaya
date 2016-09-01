package ca.currybox.yaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;


/**
 * Created by write_only_mem on 2016-02-14.
 */
public class NetworkHandler {

    private Context ctx;
    private String synopsis;
    private TextView obj;

    private int id;
    private malClient client = new malClient();
    private String xData;

    public void setSynopsis(String title, int id, TextView elem, Context context) {
        ctx = context;
        obj = elem;
        this.id = id;
        search search = new search();
        search.execute(title);
    }

    public void setImage(String xmlData, String title, int id, ImageView elem, Context context, RelativeLayout main) {
        ctx = context;
        final ImageView imgobj = elem;
        final RelativeLayout rLay = main;

        //TODO xml data stored in variable in network class soon
        //System.out.println("madman: " + xmlData);
        //String syn = getXMLData(title, context);
        //System.out.println("Bsa: " + syn);
        System.out.println("Asb: " + xmlData);
        System.out.println("ccd:" + xData);
//            syn = xmlData.substring(xmlData.indexOf("<id>" + id + "</id>"));
//            System.out.println("sadsad: " + syn);
//
//            syn = syn.substring(syn.indexOf("<image>") + 7, syn.indexOf("</image>"));
//            final int animeID = id;

//         System.out.println("LINKKKK: " + syn);
//        client.getFile(syn, null, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                try{
//                    FileOutputStream fos = ctx.openFileOutput(String.valueOf(animeID) + ".jpg", 775);
//                    fos.write(responseBody);
//
//                    File image = ctx.getFileStreamPath(String.valueOf(animeID) + ".jpg");
//
//
//                    Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
//
//                    imgobj.setImageBitmap(myBitmap);
//                    rLay.addView(imgobj);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.i("Status Failed Code", String.valueOf(statusCode));
//                Log.i("Code", String.valueOf(statusCode));
//                Log.i("Body", String.valueOf(responseBody));
//                Log.i("Reason", String.valueOf(error));
//            }
//        });
        }

    public String getXMLData(String title, Context cntx){
        ctx = cntx;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //preferences object

        //get credentials to use basic auth in http request
        String userName = prefs.getString("pref_mal_username", "a");
        String password = prefs.getString("pref_mal_password", "a");

        try {
            //encode so no url BS
            String encode = URLEncoder.encode(title, "utf-8");

            //make a new client to talk to mal
            client.setCreds(userName, password);
            //request the .xml for the title in question

            client.get(encode, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("Status Success Code", String.valueOf(statusCode));
                    //TODO save xml data in sting instead
                    xData = new String(responseBody);
                    client.setData(xData);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("Status Failed Code", String.valueOf(statusCode));
                    Log.i("Code", String.valueOf(statusCode));
                    Log.i("Body", String.valueOf(responseBody));
                    Log.i("Reason", String.valueOf(error));
                }

            });
            //bs inner class workaround
            //xmlData = client.getData();
            //Log.d("client data: ", client.getData());
            return client.getData();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
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
