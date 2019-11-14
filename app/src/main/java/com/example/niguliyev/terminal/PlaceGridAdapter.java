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

import com.example.niguliyev.terminal.Model.SeatModel;

import java.util.ArrayList;

public class PlaceGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<SeatModel> seatModels;
    LayoutInflater layoutInflater;

    public PlaceGridAdapter(Context context, ArrayList<SeatModel> seatModels){
        this.context = context;
        this.seatModels = seatModels;
    }

    @Override
    public int getCount() {
        return seatModels.size();
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
        view = layoutInflater.inflate(R.layout.place_item_layout, viewGroup, false);

        TextView seatLblTxt = view.findViewById(R.id.seatLblId);
        ImageView doneImage = view.findViewById(R.id.doneImg);
        RelativeLayout itemLyt = view.findViewById(R.id.itemLyt);

        seatLblTxt.setText(seatModels.get(i).getSeatLabel());
        if(seatModels.get(i).getSeat_status_id() == 2){
            doneImage.setVisibility(View.INVISIBLE);
          //  itemLyt.setBackgroundColor(Color.parseColor("#FF368D04"));
            itemLyt.setBackgroundResource(R.drawable.empty_grid_item_background);

        }
        else
        {
            if(seatModels.get(i).getSeat_status_id() != 4){
              //  itemLyt.setBackgroundColor(Color.BLACK);
                itemLyt.setBackgroundResource(R.drawable.place_item_black_background);
            }
            else
            {
                if(seatModels.get(i).getE_ticket_status() == 0){
                   // itemLyt.setBackgroundColor(Color.RED);
                    itemLyt.setBackgroundResource(R.drawable.place_item_red_background);
                }
                else
                {
                  //  itemLyt.setBackgroundColor(Color.BLUE);
                    itemLyt.setBackgroundResource(R.drawable.place_item_blue_background);
                }
            }
            if(seatModels.get(i).getTicket_checkin_status() == 0){
                doneImage.setVisibility(View.INVISIBLE);
            }
            else
            {
                doneImage.setVisibility(View.VISIBLE);
            }
        }

        return view;

    }
}
