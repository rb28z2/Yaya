package ca.currybox.yaya;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class main extends AppCompatActivity {
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestion permissions for android 6.0
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //should we show an explanation
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
            }
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_appbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        SharedPreferences firstLaunch = getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once
        drawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, shownPrefs);
        //Todo: Possibly change this to see if MAL username is non-default

        Intent i = getIntent();

        /**
         * Launch correct fragment based on calling intent.
         * IE: Launch the file match fragment if a video file is played,
         * otherwise launch the main app view
         */

        if (i.getAction().equals("android.intent.action.VIEW")) {
            Bundle bundle = new Bundle();
            bundle.putString("uri", i.getDataString());

            playMatch play = new playMatch();
            play.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.body, play).commit();
            Log.i("file to play", i.getDataString());
        } else {
            Log.i("mainview", "returning here");
            getSupportFragmentManager().beginTransaction().replace(R.id.body, new mainFragment()).commit();

        }
        Log.i("INTENT", i.getAction());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, MyPreferenceActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
