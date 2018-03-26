package com.example.edp19.calchulator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.Scanner;

/**
 * Created by edp19 on 3/22/18.
 */

public class OsrsDB extends SQLiteOpenHelper {

    interface OnDBReadyListener {
        void onDBReady(SQLiteDatabase db);
    }

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "osrs.db";

    public static String SQL_CREATE_DB = "";
    private static String SQL_DESTROY_DB = "DROP TABLE IF EXISTS Item;";

    private static OsrsDB db;
    private static Context context;

    private OsrsDB(Context context) {
        super(context.getApplicationContext(),DATABASE_NAME,null,DATABASE_VERSION);

        OsrsDB.context = context;
    }

    public static synchronized OsrsDB getInstance(Context context) {
        if (db == null) {
            db = new OsrsDB(context);
        }

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Initializing Database...");

        try{
            Scanner s = new Scanner(context.getAssets().open("setup.sql"));

            //load SQL script into string
            while(s.hasNext()){
                SQL_CREATE_DB += s.nextLine() + "\n";
            }

            //tokenize SQL script. Can only execute one SQL statement at a time
            for(String cmd: SQL_CREATE_DB.split(";")){

                //only execute non-empty commands.
                if(cmd.trim().length() > 0){
                    db.execSQL(cmd + ";");
                }
            }

        } catch (Exception e){
            System.out.println("Failed to read file!");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DESTROY_DB);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void getWritableDatabase(OnDBReadyListener listener) {
        new OpenDbAsyncTask().execute(listener);
    }

    private static class OpenDbAsyncTask extends AsyncTask<OnDBReadyListener,Void,SQLiteDatabase> {
        OnDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(OnDBReadyListener... params){
            listener = params[0];
            return OsrsDB.db.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            listener.onDBReady(db);
        }
    }
}