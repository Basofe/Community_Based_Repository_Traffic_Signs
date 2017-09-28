package com.example.trafficsignsdetection.Detection;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trafficsignsdetection.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by helde on 12/09/2017.
 */

public class CountDownAdapter extends ArrayAdapter<Sign> {

    private LayoutInflater lf;
    private List<Sign> listSigns;
    private List<ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public CountDownAdapter(Context context, List<Sign> objects) {
        super(context, 0, objects);
        lf = LayoutInflater.from(context);
        lstHolders = new ArrayList<>();
        listSigns = objects;
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View v = convertView;
        if(convertView == null){
            v = lf.inflate(R.layout.list_item, null);
        }
        holder = new ViewHolder();
        holder.tvName = (TextView) v.findViewById(R.id.list_item_title);
        holder.tvImage = (ImageView) v.findViewById(R.id.list_image);
        v.setTag(holder);
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }

        holder.setData(getItem(position));

        return v;
    }


    private class ViewHolder {
        TextView tvName;
        TextView tvTimeRemaining;
        ImageView tvImage;
        Sign mSign;

        public void setData(Sign item) {
            mSign = item;
            tvName.setText(item.getName());
            tvImage.setImageBitmap(Sign.myMap.get(item.getImage()));
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {
            long timeDiff = mSign.getExpirationTime() - currentTime;
            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                tvName.setText(hours + " hrs " + minutes + " mins " + seconds + " sec");
            } else {
                tvName.setText("Expired!!");
                //System.out.println("TAMANHO: " + listSigns.size());
                int size = listSigns.size();
                if(size != 0){
                    //System.out.println("ENTREI!");
                    listSigns.remove(getPosition(mSign));
                    notifyDataSetChanged();
                }
            }
        }
    }
}
