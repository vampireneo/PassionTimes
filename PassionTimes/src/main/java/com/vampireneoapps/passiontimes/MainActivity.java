package com.vampireneoapps.passiontimes;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Theme_Sherlock_Light_DarkActionBar
        setTheme(R.style.Theme_Sherlock_Light); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isonline = isOnline(this);
        if (isonline) {
            //new DownloadModelList().execute("http://test.crown-motors.com/model.js");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Save")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("Search")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Refresh")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    private class DownloadModelList extends AsyncTask<String, Void, ArrayList<VehicleObject>> {
        @Override
        protected ArrayList<VehicleObject> doInBackground(String... params) {
            try {
                return downloadUrl((String)params[0]);
            } catch (IOException e) {
                //return "Unable to retrieve web page. URL may be invalid.";
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<VehicleObject> l) {
            AddVehicleFromList(l);
            ((ModelListFragment) getSupportFragmentManager().findFragmentById(R.id.model_list)).updateList();
        }

        private ArrayList<VehicleObject> downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            FileOutputStream fos = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                byte[] buffer = new byte[4096];
                fos = openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
                int n = is.read(buffer, 0, buffer.length);
                while (n >= 0) {
                    fos.write(buffer, 0, n);
                    n = is.read(buffer, 0, buffer.length);
                }
                FileInputStream inputStream = openFileInput(DATA_FILE_NAME);
                return AddVehicleFromInputStream(inputStream);
            }
            finally {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }
}
