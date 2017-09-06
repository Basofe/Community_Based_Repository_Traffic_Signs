package com.example.trafficsignsdetection.Communication;

import java.util.ArrayList;

/**
 * Created by helde on 28/08/2017.
 */

public class SignsData {
    private ArrayList<SignData> signsdata;

    public SignsData(){
        this.signsdata = new ArrayList<>();
    }

    public SignsData(ArrayList<SignData> signsdata){
        this.signsdata = signsdata;
    }

    public void add(SignData data){
        this.signsdata.add(data);
    }

    public void addArray(ArrayList<SignData> data){
        for(SignData s : data){
            this.signsdata.add(s);
        }
    }

    /**
     * @return the signsdata
     */
    public ArrayList<SignData> getSignsdata() {
        ArrayList<SignData> aux = new ArrayList<>();

        for(SignData s : this.signsdata){
            aux.add(s);
        }

        return aux;
    }
}
