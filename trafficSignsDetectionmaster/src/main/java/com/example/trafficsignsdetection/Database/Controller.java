package com.example.trafficsignsdetection.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by helde on 19/05/2017.
 */

public class Controller {

    private SQLiteDatabase db;
    private DBModel model;

    public Controller() {}

    public Controller(Context context){
        model = new DBModel(context);
    }

    public Coordinate generateCoordinates() {
        //p = A + u*AB + v*AD

        double lngUpLeft = -8.871957;
        double lngUpRight = -6.548937;
        double lngLowerRight = -7.462716;
        double lngLowerLeft = -8.898944;

        double latUpLeft = 41.858960;
        double latUpRight = 41.816070;
        double latLowerRight = 37.229239;
        double latLowerLeft = 37.096151;

        double u = Math.random();
        double v = Math.random();

        double pointLat = latUpLeft + u * (latUpLeft - latUpRight) - v * (latUpLeft - latLowerLeft);
        double pointLng = lngUpLeft + u * Math.abs((lngUpLeft - lngUpRight)) - v * Math.abs((lngUpLeft - lngLowerLeft));

        Coordinate coord = new Coordinate(pointLat, pointLng);

        return coord;
    }

    public ArrayList<Coordinate> getFromToSigns(int N, int iterations) {

        Cursor result;
        ArrayList<Coordinate> coords = new ArrayList<>();
        db = model.getWritableDatabase();
        int i = 0;
        int from = 0;
        int howMany = N;

        while(i < iterations){
            result = db.rawQuery("select * from 'signs' limit "+from+ ", " + howMany+"", null);
            while (result.moveToNext()) {
                Coordinate c = new Coordinate();
                c.setLatitude(Double.parseDouble(result.getString(3)));
                c.setLongitude(Double.parseDouble(result.getString(4)));
                coords.add(c);
            }
            i++;
            from+=N;
        }

        return coords;
    }

    public HashMap<Character, ArrayList<Coordinate>> getSignsHashArray(int N, int iterations) {

        Cursor result;
        HashMap<Character, ArrayList<Coordinate>> collection = new HashMap<>();
        ArrayList<Coordinate> coords;
        db = model.getWritableDatabase();
        int i = 0;
        int from = 0;
        int howMany = N;

        while(i < iterations){
            result = db.rawQuery("select * from 'signs' limit "+from+ ", " + howMany+"", null);
            while (result.moveToNext()) {
                Coordinate c = new Coordinate();
                //c.setSignName(result.getString(1));
                c.setLatitude(Double.parseDouble(result.getString(3)));
                c.setLongitude(Double.parseDouble(result.getString(4)));

                char key = result.getString(2).charAt(0);

                coords = collection.get(key);
                if(coords == null) {
                    coords = new ArrayList<>();
                    coords.add(c);
                    collection.put(key, coords);
                } else {
                    coords.add(c);
                }
            }
            i++;
            from+=N;
        }

        return collection;
    }

    public HashMap<Integer, ArrayList<Sign>> getSignsHashArrayOptimized() {

        Cursor result;
        HashMap<Integer, ArrayList<Sign>> collection = new HashMap<>();
        ArrayList<Sign> coords;
        db = model.getWritableDatabase();

        result = db.rawQuery("select * from 'signs'", null);
        while (result.moveToNext()) {
            Sign c = new Sign();
            c.setSignName(result.getString(1));
            c.setLatitude(result.getString(3));
            c.setLongitude(result.getString(4));
            c.setType(result.getString(5));

            char index = result.getString(2).charAt(0);
            int key = Character.getNumericValue(index);

            coords = collection.get(key);
            if(coords != null) {
                coords.add(c);
            } else {
                coords = new ArrayList<>();
                coords.add(c);
                collection.put(key, coords);
            }
        }

        return collection;
    }

    public ArrayList<Sign> getAllSigns() {

        Cursor result;
        ArrayList<Sign> signs = new ArrayList<>();
        db = model.getWritableDatabase();

        result = db.rawQuery("select * from 'signs'", null);
        while (result.moveToNext()) {
            Sign sign = new Sign();
            sign.setSignName(result.getString(1));
            sign.setOrientation(result.getString(2));
            sign.setLatitude(result.getString(3));
            sign.setLongitude(result.getString(4));
            sign.setType(result.getString(5));
            signs.add(sign);
        }

        return signs;
    }

    public String insertSign(String signName, String orientation, String latitude, String longitude, String type){
        ContentValues values;
        long result;

        db = model.getWritableDatabase();

        values = new ContentValues();
        values.put(DBModel.getSIGNNAME(), signName);
        values.put(DBModel.getORIENTATION(), orientation);
        values.put(DBModel.getLATITUDE(), latitude);
        values.put(DBModel.getLONGITUDE(), longitude);
        values.put(DBModel.getTYPE(), type);

        result = db.insert(DBModel.getTable(), null, values);
        db.close();

        if (result ==-1)
            return "Error entering record";
        else
            return "Sign inserted with success!";

    }

    public void insertSigns(List<Sign> list) {
        SQLiteDatabase db = model.getWritableDatabase();
        model.onUpgrade(db,1,1);
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Sign sign : list) {
                values.put(DBModel.getSIGNNAME(), sign.getSignName());
                values.put(DBModel.getORIENTATION(), sign.getOrientation());
                values.put(DBModel.getLATITUDE(), sign.getLatitude());
                values.put(DBModel.getLONGITUDE(), sign.getLongitude());
                values.put(DBModel.getTYPE(), sign.getType());
                db.insert(DBModel.getTable(), null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
