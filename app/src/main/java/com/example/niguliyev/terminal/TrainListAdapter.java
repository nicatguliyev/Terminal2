package com.example.niguliyev.terminal;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.niguliyev.terminal.Model.TrainModel;

import org.json.JSONException;

import java.net.ConnectException;
import java.util.ArrayList;

public class TrainListAdapter extends BaseAdapter {

    Context context;
    ArrayList<TrainModel> trains;
    LayoutInflater layoutInflater;

    public TrainListAdapter(Context context, ArrayList<TrainModel> trains){
        this.context = context;
        this.trains = trains;
    }

    @Override
    public int getCount() {
        return trains.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.train_list_item, viewGroup, false);

        TextView trainName = view.findViewById(R.id.trainName);
        View bottomView = view.findViewById(R.id.bottomView);
        RelativeLayout parentView = view.findViewById(R.id.parentLyt);
        ImageView dropImage = view.findViewById(R.id.dropImage);
        bottomView.setVisibility(View.INVISIBLE);

            if(i == 0){
                trainName.setText("Qatarı seçin");
                trainName.setTextColor(Color.parseColor("#FF007BFE"));
            }
            else
            {
                String trainNumber = trains.get(i-1).getTrainNumber();
                trainName.setText(trainNumber);
                trainName.setTextColor(Color.parseColor("#000000"));
            }


        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.train_list_item, parent, false);

        TextView trainName = convertView.findViewById(R.id.trainName);
        View bottomView = convertView.findViewById(R.id.bottomView);
        ImageView dropImage = convertView.findViewById(R.id.dropImage);//

        if(position == trains.size())
        {
            bottomView.setVisibility(View.INVISIBLE);

        }
        else
        {
            bottomView.setVisibility(View.VISIBLE);
        }


            if(position == 0){
                trainName.setText("Qatarı seçin");
                trainName.setTextColor(Color.parseColor("#FF007BFE"));
                dropImage.setVisibility(View.INVISIBLE);
            }
            else
            {
                String trainNumber = trains.get(position-1).getTrainNumber();
                trainName.setText(trainNumber);
                trainName.setTextColor(Color.parseColor("#000000"));
                dropImage.setVisibility(View.INVISIBLE);
            }



        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        if(position == 0)
        {
            return  false;
        }
        else
        {
            return  true;
        }
    }
}
