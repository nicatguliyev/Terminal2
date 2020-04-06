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

import java.util.ArrayList;

public class DateListAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> dates;
    LayoutInflater layoutInflater;

    public DateListAdapter(Context context, ArrayList<String> dates){
        this.context = context;
        this.dates = dates;
    }

    @Override
    public int getCount() {
        return dates.size();
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

        TextView dateTxt = view.findViewById(R.id.trainName);
        View bottomView = view.findViewById(R.id.bottomView);
        RelativeLayout parentView = view.findViewById(R.id.parentLyt);
        ImageView dropImage = view.findViewById(R.id.dropImage);
        bottomView.setVisibility(View.INVISIBLE);

                String date = dates.get(i);
                dateTxt.setText(date);
                dateTxt.setTextColor(Color.parseColor("#000000"));

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.train_list_item, parent, false);

        TextView dateTxt = convertView.findViewById(R.id.trainName);
        View bottomView = convertView.findViewById(R.id.bottomView);
        ImageView dropImage = convertView.findViewById(R.id.dropImage);

        if(position == dates.size())
        {
            bottomView.setVisibility(View.INVISIBLE);//

        }
        else
        {
            bottomView.setVisibility(View.VISIBLE);
        }

                String trainNumber = dates.get(position);
                dateTxt.setText(trainNumber);
                dateTxt.setTextColor(Color.parseColor("#000000"));
                dropImage.setVisibility(View.INVISIBLE);

        return convertView;
    }

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
