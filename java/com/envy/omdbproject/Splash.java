package com.envy.omdbproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
            try{
                if(isNetworkAvailable()){
                    final AsyncTask<Void,Void,Void> mytask = new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean t = true;
                            try {
                                sleep(3000);
                            } catch (InterruptedException e) {
                                t= false;
                                finish();
                            }
                            finally {
                                if(t) {
                                    Intent p = new Intent("android.intent.action.menu");
                                    startActivity(p);
                                }
                                }
                            return null;
                        }
                    };
                    mytask.execute();
                }
                else{
                    showalertdailog();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void showalertdailog(){
        AlertDialog.Builder blr = new AlertDialog.Builder(this);
        blr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        blr.setTitle("Internet Connection not found");
        blr.setMessage("Please connect to the internet to use this app");
        blr.create().show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}