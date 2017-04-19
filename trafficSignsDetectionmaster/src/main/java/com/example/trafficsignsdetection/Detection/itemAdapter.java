package com.example.trafficsignsdetection.Detection;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trafficsignsdetection.R;

public class itemAdapter extends BaseAdapter{

	private List<Sign> listSign;
	private LayoutInflater inflater;
	private Activity activity;
	
	public itemAdapter(List<Sign> listSign, Activity activity) {
		super();
		this.listSign = listSign;
		this.activity = activity;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = view;
		if(view == null){
			v = inflater.inflate(R.layout.list_item, null);
		}
		Sign item;
		item = listSign.get(position);
		ImageView iv = (ImageView)v.findViewById(R.id.list_image);
		TextView tvTitle = (TextView)v.findViewById(R.id.list_item_title);
		iv.setImageBitmap(Sign.myMap.get(item.getImage()));
		tvTitle.setText(item.getName());
		
		return v;
	}
	 
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listSign.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	

}
