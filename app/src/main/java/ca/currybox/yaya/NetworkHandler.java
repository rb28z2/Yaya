package ca.currybox.yaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.*;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;


/**
 * Created by write_only_mem on 2016-02-14.
 */
public class NetworkHandler {

    private Context ctx;
    private String synopsis;
    private TextView obj;

    private int id;
    private final malClient client = new malClient();

    public void setSynopsis(String title, int id, TextView elem, Context context) {
        ctx = context;
        obj = elem;
        this.id = id;
        search search = new search();
        search.execute(title);
    }

    public void setImage(String title, int id, ImageView elem, Context context, RelativeLayout main) {
        ctx = context;
        final ImageView imgobj = elem;
        final RelativeLayout rLay = main;
        Boolean contains = false;

        //gets all the files in the files folder
        File fileDir[] = ctx.getFilesDir().listFiles();
        File theXML = new File("temp.tmp");
        int index = 0;

        for(File file : fileDir){
//            System.out.println(file.getName());
//            System.out.println("Checking:" + (String.valueOf(id) + ".xml")+ " = " + (fileDir[index].getName()));
            if(!((String.valueOf(id) + ".xml").equals(fileDir[index].getName()))){

            }
            else {
                contains = true;
                theXML = file;
                //System.out.println("Size:... " + file.length());
            }
            index++;
        }

        if(!contains){
            System.out.println("NANIIIIIIIII");
            //try and download xml or give up
        }
        else{
            XMLParser parser = new XMLParser();
            String s = parser.read(theXML.getName(), ctx);
//            System.out.println("sssssssE: \""  + s + "\"");

            String syn = s.substring(s.indexOf("<id>" + id + "</id>"));

            syn = syn.substring(syn.indexOf("<image>") + 7, syn.indexOf("</image>"));
            final int animeID = id;

//            System.out.println("LINKKKK: " + syn);
            client.getFile(syn, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try{
                        FileOutputStream fos = ctx.openFileOutput(String.valueOf(animeID) + ".jpg", 775);
                        fos.write(responseBody);

                        File image = ctx.getFileStreamPath(String.valueOf(animeID) + ".jpg");

                        Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());

                        imgobj.setImageBitmap(myBitmap);
                        rLay.addView(imgobj);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("Status Failed Code", String.valueOf(statusCode));
                    Log.i("Code", String.valueOf(statusCode));
                    Log.i("Body", String.valueOf(responseBody));
                    Log.i("Reason", String.valueOf(error));
                }
            });

            //http://stackoverflow.com/questions/11004744/android-displaying-jpg-image-from-sdcard-using-imageview
        }
    }

    public void downloadXML(String title, int id, final Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //preferences object

        //get credentials to use basic auth in http request
        String userName = prefs.getString("pref_mal_username", "a");
        String password = prefs.getString("pref_mal_password", "a");
        final int animeID = id;
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
                    try{
                        FileOutputStream fos = context.openFileOutput(String.valueOf(animeID) + ".xml", context.MODE_PRIVATE);
                        fos.write(responseBody);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("Status Failed Code", String.valueOf(statusCode));
                    Log.i("Code", String.valueOf(statusCode));
                    Log.i("Body", String.valueOf(responseBody));
                    Log.i("Reason", String.valueOf(error));
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
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
