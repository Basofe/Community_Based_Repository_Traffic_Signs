package com.example.trafficsignsdetection.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by helde on 19/05/2017.
 */

public class Controller {

    private SQLiteDatabase db;
    private DBModel model;

    public Controller(Context context){
        model = new DBModel(context);
    }

    public String insertSign(String signName, String orientation, String latitude, String longitude){
        ContentValues values;
        long result;

        db = model.getWritableDatabase();
        values = new ContentValues();
        values.put(DBModel.getSIGNNAME(), signName);
        values.put(DBModel.getORIENTATION(), orientation);
        values.put(DBModel.getLATITUDE(), latitude);
        values.put(DBModel.getLONGITUDE(), longitude);

        result = db.insert(DBModel.getTable(), null, values);
        db.close();

        if (result ==-1)
            return "Error entering record";
        else
            return "Sign inserted with success!";

    }
}
