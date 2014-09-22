package ca.currybox.yaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Kanchana on 9/10/2014.
 */
public class SettingsActivity extends PreferenceActivity {
    //@Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
        if (prefs.getBoolean("dark_pref", false)) //checks if settings checkbox is true to set app into dark mode
        {
            //setTheme(R.style.AppTheme_Dark);
            //Broken as of now
        }
        //Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        SharedPreferences firstLaunch = getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = firstLaunch.edit();

        editor.putBoolean("HaveShownPrefs", true);
        editor.commit();


    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Load the preferences from the XML resource
            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onResume() {
            super.onResume();

            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);

            initSummary();
        }

        @Override
        public void onPause() {
            super.onPause();

            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefsSummary(sharedPreferences, findPreference(key));
        }

        /**
         * Update summary
         *
         * @param sharedPreferences
         * @param pref
         */
        protected void updatePrefsSummary(SharedPreferences sharedPreferences, Preference pref) {

            if (pref == null)
                return;

            if (pref instanceof EditTextPreference) {
                // EditPreference
                EditTextPreference editTextPref = (EditTextPreference) pref;
                editTextPref.setSummary(editTextPref.getText());
            } else if (pref instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPref = (CheckBoxPreference) pref;
                if (checkBoxPref.isChecked()) {
                    //setTheme();
                }
            }
        }

        /*
        Initialize summary for all items
         */
        protected void initSummary() {
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initPrefsSummary(getPreferenceManager().getSharedPreferences(),
                        getPreferenceScreen().getPreference(i));
            }
        }

        /*
        Initialize summary for single elements
         */
        protected void initPrefsSummary(SharedPreferences sharedPreferences, Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pCat = (PreferenceCategory) p;
                for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                    initPrefsSummary(sharedPreferences, pCat.getPreference(i));
                }
            } else {
                updatePrefsSummary(sharedPreferences, p);
            }
        }

    }

}


