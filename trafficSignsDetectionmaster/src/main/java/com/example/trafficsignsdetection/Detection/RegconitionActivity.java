package com.example.trafficsignsdetection.Detection;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.trafficsignsdetection.R;

public class RegconitionActivity extends Activity{
	private ArrayList<Sign> listSign;
	private itemAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_row);
		listSign = getIntent().getParcelableArrayListExtra("key");
		adapter = new itemAdapter(listSign,RegconitionActivity.this);
		ListView list = (ListView)findViewById(R.id.lstDetectedSigns);
		list.setAdapter(adapter);
	}

		
}
