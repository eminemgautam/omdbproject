package com.envy.omdbproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class advanced_search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);
        //
        Button b = (Button) findViewById(R.id.advanced_search_button);
        final EditText E = (EditText) findViewById(R.id.keywords);
        E.requestFocus();
        //
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(E, InputMethodManager.SHOW_IMPLICIT);

        if (b != null) {
            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (isNetworkAvailable()) {

                        try{
                            String str = E.getText().toString();
                            str = str.replace(" ","+");
                            Log.d("STR",str);
                            if(str.endsWith("+")){
                                str= str.substring(0,str.length()-1);
                            }
                            Log.d("STR",str);
                            final String title = str;
                            final String type = getType();

                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.advanced_progressbar);
                            progressBar.setVisibility(View.VISIBLE);

                            Toast.makeText(advanced_search.this, "Searching", Toast.LENGTH_SHORT).show();

                            new AsyncTask<Void, Void, Void>() {
                                protected Void  doInBackground(Void... params) {

                                    String s = "http://www.omdbapi.com/?" + "s=" + title;

                                    if(type!= null){
                                        s = s +"&type=" +type;
                                    }
                                    try {
                                        Log.d("URL searching...:",s);
                                        URL omdb = new URL(s);
                                        InputStream is = omdb.openStream();
                                        BufferedReader br =  new BufferedReader(new InputStreamReader(is));
                                        Log.d("URL searching...:",s);
                                        String line;
                                        String json_code = "";
                                        while((line = br.readLine())!=null){
                                            json_code =  json_code + line;
                                        }
                                        Log.d("URL searching...:",s);
                                        decodeJson(json_code);

                                    } catch (Exception e) {
                                        Looper.prepare();
                                        Toast.makeText(advanced_search.this,"An Error Occured",Toast.LENGTH_LONG).show();
                                    }
                                    return null;
                                }
                            }.execute();
                        }catch (Exception e){e.printStackTrace();}
                    } else {
                        Toast.makeText(advanced_search.this, "Internet Connection not found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private String getType(){
        RadioGroup rg =  (RadioGroup) findViewById(R.id.advanced_radiogroup);
        int a = rg.getCheckedRadioButtonId();
        if(a == R.id.Amovie){
            return "movie";
        }
        if(a == R.id.Aseries){
            return "series";
        }
        return null;
    }


    private void decodeJson(String a){
        try {
            Intent i = new Intent("android.intent.action.advanced_search_result");
            i.putExtra("html",a);
            startActivity(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.advanced_progressbar);
        progressBar.setVisibility(View.INVISIBLE);

    }
}