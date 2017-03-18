package com.example.trafficsignsdetection.Detection;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Sign implements Parcelable{
	public static final Map<String, Bitmap> myMap = new HashMap<String, Bitmap>() {{
	    
	}};
	
	private String Name;
	private String image;
	
	public String getImage() {
		return image;
	}
	public Sign(String name, String image) {
		super();
		Name = name;
		this.image = image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Sign() {
		
	}
	public Sign(Parcel source) {
        Name = source.readString();
        //byteArray = source.createByteArray();
        //byteArray = source.readParcelable(null);
        image = source.readString();
    }
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(Name);
		//dest.writeByteArray(byteArray);
		dest.writeString(image);
	}
	 public static final Parcelable.Creator CREATOR
     = new Parcelable.Creator() {
	 public Sign createFromParcel(Parcel in) {
	     return new Sign(in);
	 }
	
	 public Sign[] newArray(int size) {
	     	return new Sign[size];
	 	}
	 };
}
