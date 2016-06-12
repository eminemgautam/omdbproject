package com.envy.omdbproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class search_result extends AppCompatActivity {
    AlertDialog.Builder builder;
    AlertDialog mydialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        builder = new AlertDialog.Builder(this);
        mydialog = builder.create();
        LinearLayout linearLayout = new LinearLayout(search_result.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = new TextView(search_result.this);
        textView.setText("Loading Content");
        textView.setPadding(20,20,20,20);
        ProgressBar progressBar = new ProgressBar(search_result.this);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.addView(textView);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(progressBar);
        mydialog.setView(linearLayout);
        mydialog.show();
        String s = getIntent().getStringExtra("html");
        if (s == null) {
            Log.e("STRING NULL!!!!!!!!!!", " ");
            finish();
        }
        new DownloadImage().execute(s);
    }

    public class DownloadImage extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            try {
                String s = params[0];
                JSONObject jsonObject = new JSONObject(s);
                URL url = new URL(jsonObject.getString("Poster"));
                InputStream content = (InputStream) url.getContent();
                return Drawable.createFromStream(content, "src");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable aVoid) {
            super.onPostExecute(aVoid);
            setDisplay(aVoid);
        }
    }

    private void setDisplay(Drawable d) {
        setContentView(R.layout.activity_search_result);
        String s = getIntent().getStringExtra("html");
        TextView t = (TextView) findViewById(R.id.result_content);
        TextView title = (TextView) findViewById(R.id.result_title);
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (t != null && title != null && jsonObject.getString("Response").equals("True")) {
                t.setText("");
                title.setText("");
                title.append(jsonObject.getString("Title"));
                t.append("Genre " + jsonObject.getString("Genre") + "\n");
                t.append("IMDB: " + jsonObject.getString("imdbRating") + "\n");
                t.append("Released Year: " + jsonObject.getString("Released") + "\n");
                t.append("Actors: " + jsonObject.getString("Actors") + "\n");
                t.append("Plot: " + jsonObject.getString("Plot"));
            } else {
                if (t != null && title != null) {
                    t.setText("");
                    title.setText("Movie Not Found!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView imageview = (ImageView) findViewById(R.id.image);
        if (imageview != null) {
            imageview.setImageDrawable(d);
        }
        Button b1 = (Button) findViewById(R.id.imdb);
        try {
            final JSONObject jsonObject = new JSONObject(s);
            if (b1 != null) {
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String a = "http://imdb.com/title/" + jsonObject.getString("imdbID");
                            Intent t = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
                            startActivity(t);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScrollView scrollView = (ScrollView) findViewById(R.id.search_result_scrollview);
        BitmapDrawable bitmapDrawable =  (BitmapDrawable) d;
        if(bitmapDrawable !=   null && android.os.Build.VERSION.SDK_INT >16){
        Bitmap bitmap = (bitmapDrawable).getBitmap();
        BlurBuilder blurBuilder = new BlurBuilder();
        Bitmap b = blurBuilder.blur(search_result.this, bitmap);
        Drawable drawable = new BitmapDrawable(getResources(), b);
        if (scrollView != null) {
            scrollView.setBackground(drawable);
        }
        }else{
            Toast.makeText(search_result.this,"Image Unavailable", Toast.LENGTH_SHORT).show();
        }
        mydialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }

    public class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 7.5f;

        public Bitmap blur(Context context, Bitmap image) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
                return outputBitmap;
            }
            return null;
        }
    }
}
