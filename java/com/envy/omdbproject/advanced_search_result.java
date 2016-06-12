package com.envy.omdbproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class advanced_search_result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_result);


        String webpage = getIntent().getStringExtra("html");

        try {
            Log.d("HTML", webpage);
            JSONObject collection = new JSONObject(webpage);
            if (collection.getString("Response").equals("True")) {
                int results = collection.getInt("totalResults");
                setResultsFound(results);
                Log.d("Results ", Integer.toString(results));
                final JSONArray listItems = collection.getJSONArray("Search");
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < listItems.length(); i++) {
                    JSONObject item = listItems.getJSONObject(i);
                    list.add(i, item.getString("Title"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
                ListView mylist = (ListView) findViewById(R.id.list);
                mylist.setAdapter(adapter);


                mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            final JSONObject selection = listItems.getJSONObject(position);
                            if (isNetworkAvailable()) {
                                final database db = new database(advanced_search_result.this, "movie_database.db");
                                try {
                                    String s2 = selection.getString("imdbID");
                                    String s1 = selection.getString("Title");
                                    db.putIntoDatabase(s2, s1);
                                    db.close();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new AsyncTask<Void, Void, Void>() {

                                    protected Void doInBackground(Void... params) {

                                        try {
                                            String s = "http://www.omdbapi.com/?" + "i=" + selection.getString("imdbID");
                                            Log.d("URL searching:", s);
                                            URL omdb = new URL(s);
                                            InputStream is = omdb.openStream();
                                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                            String line;
                                            String json_code = "";
                                            while ((line = br.readLine()) != null) {
                                                json_code = json_code + line;
                                            }

                                            decodeJson(json_code);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                }.execute();
                            } else {
                                Toast.makeText(advanced_search_result.this, "Internet Connection not found", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                setResultsFound(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setResultsFound(int t) {
        String s = " ";
        TextView v = (TextView) findViewById(R.id.results);
        v.setText(s);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
}
