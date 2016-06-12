package com.envy.omdbproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class history extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ;
        final database db = new database(history.this, "movie_database.db");
        final ArrayList<String> mylist = db.getTitles();
        Collections.reverse(mylist);

        if(mylist.size() == 0){
            mylist.add(0,"No History Found. Go search!");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(history.this, android.R.layout.simple_list_item_1, mylist);
        ListView listView = (ListView) findViewById(R.id.history_list);
        if (listView != null) {
            listView.setAdapter(adapter);
        }

        if (listView != null && !(mylist.get(0).equals("No History Found. Go search!"))){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isNetworkAvailable()) {
                        int pos = mylist.size() - position;
                        pos--;
                        final String tag = db.getImdbTag(pos);
                        Toast.makeText(history.this, "Loading", Toast.LENGTH_SHORT).show();
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                decodeJson(s);
                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    if (tag != null) {
                                        String s = "http://www.omdbapi.com/?" + "i=" + tag;
                                        Log.d("URL searching:", s);
                                        URL omdb = new URL(s);
                                        InputStream is = omdb.openStream();
                                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                        String line;
                                        String json_code = "";
                                        while ((line = br.readLine()) != null) {
                                            json_code = json_code + line;
                                        }
                                        return json_code;
                                    } else {
                                        Log.d("TAG IS EMPTY BABY", "yes");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
                    } else {
                        Toast.makeText(history.this, "Internet Connection not found", Toast.LENGTH_LONG).show();
                    }
                }
            });
            Button b = (Button) findViewById(R.id.clear_history);

            if (b != null) {
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.deleteDatabase();
                        Log.d("Database deleted", "");
                        Toast.makeText(history.this, "History Deleted ;-)", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
            db.close();
        }else{
            Button b = (Button) findViewById(R.id.clear_history);
            if (b != null) {
                b.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void decodeJson(String a) {
        try {
            Intent i = new Intent("android.intent.action.search_result");
            i.putExtra("html", a);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}