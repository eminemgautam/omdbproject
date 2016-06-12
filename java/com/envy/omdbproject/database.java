package com.envy.omdbproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class database extends SQLiteOpenHelper {

    private String TABLE_NAME = "MOVIE";
    private String IMDBTAG = "IMDBTAG";
    private String TITLE = "TITLE";

    public database(Context context,String name) {
        super(context,name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE MOVIE (IMDBTAG TEXT NOT NULL,TITLE TEXT NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void putIntoDatabase(String tag,String title) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMDBTAG, tag);
        values.put(TITLE,title);
        database.insert(TABLE_NAME, null, values);
        Log.d("Value added to database",tag + title);
        database.close();
    }

    public ArrayList<String> getTitles() {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("SELECT %s FROM %s", TITLE,TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<String> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            ids.add(id);
        }
        database.close();
        cursor.close();
        return ids;
    }

    public String getImdbTag(int position){
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("SELECT %s FROM %s",IMDBTAG,TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToPosition(position);
        String Tag = cursor.getString(0);
        database.close();
        cursor.close();
        return Tag;
    }

    public void deleteDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,"",null);
    }
}
