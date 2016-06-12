package com.envy.omdbproject;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button advSearch  = (Button) findViewById(R.id.advancedSearch);
        Button qrcodescanner = (Button) findViewById(R.id.qrscanner) ;

        if (qrcodescanner != null) {
            qrcodescanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(ACTION_SCAN);
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        startActivityForResult(intent, 0);
                    } catch (ActivityNotFoundException anfe) {
                        Log.d("Activity not found"," ");
                        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(main.this);
                        downloadDialog.setTitle("No Scanner Found!");
                        downloadDialog.setMessage("Download a trusted scanner from Play Store?");
                        downloadDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException anfe) {
                                    anfe.printStackTrace();
                                }
                            }
                        });
                        downloadDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }

                        });
                        downloadDialog.show();
                    }
                }
            });
        }

        Button history = (Button) findViewById(R.id.history);
        if (history != null) {
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.history");
                    startActivity(intent);
                }
            });
        }
        if (advSearch != null) {
            advSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent b = new Intent("android.intent.action.advanced_search");
                    startActivity(b);
                }
            });
        }

        Button exit  = (Button) findViewById(R.id.exit);
            if (exit != null) {
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

    }

}
