package com.example.trafficsignsdetection.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by helde on 19/05/2017.
 */

public class DBModel extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "community.db";
    private static final String TABLE = "signs";
    private static final String ID = "_id";
    private static final String ORIENTATION = "orientation";
    private static final String SIGNNAME = "signName";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TYPE = "type";
    private static final int VERSION = 1;

    public DBModel(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " ( "
                + getID() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + getSIGNNAME() + " VARCHAR2(45),"
                + getORIENTATION() + " VARCHAR2(10),"
                + getLATITUDE() + " VARCHAR2(15),"
                + getLONGITUDE() + " VARCHAR2(15),"
                + getTYPE() + " VARCHAR2(20)"
                +" ); ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public static String getTable() {
        return TABLE;
    }

    public static String getID() {
        return ID;
    }

    public static String getSIGNNAME() {
        return SIGNNAME;
    }

    public static String getORIENTATION() { return ORIENTATION; }

    public static String getLATITUDE() {
        return LATITUDE;
    }

    public static String getLONGITUDE() {
        return LONGITUDE;
    }

    public static String getTYPE() { return  TYPE; }

}