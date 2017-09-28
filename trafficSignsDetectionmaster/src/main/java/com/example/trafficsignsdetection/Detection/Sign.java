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
	private long expirationTime;
	
	public String getImage() {
		return image;
	}

	public Sign(String name, String image, long time) {
		super();
		Name = name;
		this.image = image;
		this.expirationTime = time;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public Sign(String signRecognized, String s, String valueOf) {
		
	}
	public Sign(Parcel source) {
        Name = source.readString();
        image = source.readString();
		expirationTime = source.readLong();
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
		dest.writeString(image);
		dest.writeLong(getExpirationTime());
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

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
}
