package com.example.niguliyev.terminal;

import android.app.ProgressDialog;
import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.niguliyev.terminal.Model.SeatModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlacesActivity extends AppCompatActivity {

    Button backBtn, umumiBtn, elekBtn, standartBtn, bosBtn, seePlacesBtn, refreshBtn;
    ProgressDialog dialog;
    String serviceUrl = "https://ticket.ady.az/terminal_service.php";
    String wagonNo = "";
    String tripDate = "";
    String trainId;
    String trainName;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    String reqDate = "";
    int freeSeatsCount, eSaleCount, sSaleCount, allPlaceCount;
    int checkedESaleCount = 0;
    int checkedSSaleCount = 0;
    public static  ArrayList<SeatModel> allSeatsList = new ArrayList<>();
    TextView titleTxt;
    Toast toast;
  //  public static ArrayList<SeatModel> eSeatsList = new ArrayList<>();
 //   public static ArrayList<SeatModel> sSeatsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        backBtn = findViewById(R.id.backBtn);
        elekBtn = findViewById(R.id.elekBtn);
        standartBtn = findViewById(R.id.standartBtn);
        umumiBtn = findViewById(R.id.umumiBtn);
        bosBtn = findViewById(R.id.bosBtn);
        seePlacesBtn = findViewById(R.id.seePlacesBtn);
        titleTxt = findViewById(R.id.titleTxt);
        refreshBtn = findViewById(R.id.reloadBtn);
        allSeatsList = new ArrayList<>();

        dialog = new ProgressDialog(PlacesActivity.this);
        dialog.setMessage("Zəhmət olmasa gözləyin..");
        dialog.setCanceledOnTouchOutside(false);
      //  dialog.show();

        wagonNo = getIntent().getStringExtra("wagonNo");
        tripDate = getIntent().getStringExtra("tripDate");
        trainId = getIntent().getStringExtra("trainId");
        trainName = getIntent().getStringExtra("trainName");

        titleTxt.setText(trainName + ",  Vaqon " + wagonNo);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.come_from_left, R.anim.exit_from_right);
            }
        });

        currentTime = Calendar.getInstance().getTime();
        reqDate = sdf.format(currentTime);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWagonStatus();
            }
        }, 100);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getWagonStatus();
            }
        });

        seePlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(allSeatsList.size() == 0){
                    showToast("Məlumatlar yüklənməyib. Səhifəni yeniləyin", Toast.LENGTH_LONG);
                }
                else
                {
                    Intent intent = new Intent(PlacesActivity.this, PlacesGridActivity.class);
                    intent.putExtra("trainName", trainName);
                    intent.putExtra("wagonNo", wagonNo);
                    intent.putExtra("trainId", String.valueOf(trainId));
                    intent.putExtra("tripDate", tripDate);

                    startActivity(intent);
                }

            }
        });
    }

    public void getWagonStatus(){
        dialog.show();

        umumiBtn.setText("---");
        bosBtn.setText("---");
        standartBtn.setText("-- / --");
        elekBtn.setText("-- / --");
        allPlaceCount = 0;
        freeSeatsCount = 0;
        eSaleCount = 0;
        sSaleCount = 0;
        checkedESaleCount = 0;
        checkedSSaleCount = 0;
        allSeatsList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();

//                int maxLogSize = 1000;
//                for(int i = 0; i <= response.length() / maxLogSize; i++) {
//                    int start = i * maxLogSize;
//                    int end = (i+1) * maxLogSize;
//                    end = end > response.length() ? response.length() : end;
//                    Log.v("WagonStatus", response.substring(start, end));
//                }
                try {
                    JSONObject responseJsonObject = new JSONObject(response);
                    if(responseJsonObject.getString("result").equals("0")){
                        Toast.makeText(getApplicationContext(), "Xəta baş verdi.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        JSONObject dataObject = responseJsonObject.getJSONObject("data");
                        JSONArray  wagonsJsonArray = dataObject.getJSONArray("wagons");
                        allPlaceCount = wagonsJsonArray.length();
                        for(int i = 0; i<wagonsJsonArray.length(); i++){
                            JSONObject seatObject = wagonsJsonArray.getJSONObject(i);
                            int seat_id = seatObject.getInt("seat_id");
                            String seatLbl = seatObject.getString("seat_label");
                            int seat_status = seatObject.getInt("seat_status_id");
                            int e_ticket_status  = seatObject.getInt("e_ticket_status");
                            int ticket_checkin_status = seatObject.getInt("ticket_checkin_status");
                            SeatModel seatModel = new SeatModel(seat_id, seatLbl, seat_status, e_ticket_status, ticket_checkin_status);
                            allSeatsList.add(seatModel);

                            if(seat_status == 2){
                                freeSeatsCount = freeSeatsCount + 1;
                            }
                            if(seat_status == 4 && e_ticket_status == 0){
                                sSaleCount = sSaleCount + 1;
                                if(ticket_checkin_status == 1){
                                    checkedSSaleCount = checkedSSaleCount + 1;
                                }
                            }
                            if(seat_status == 4 && e_ticket_status == 1){
                                eSaleCount = eSaleCount + 1;
                                if(ticket_checkin_status == 1){
                                    checkedESaleCount = checkedESaleCount + 1;
                                }
                            }
                        }

                        umumiBtn.setText(String.valueOf(allPlaceCount));
                        bosBtn.setText(String.valueOf(freeSeatsCount));
                        standartBtn.setText(String.valueOf(sSaleCount) + " / " + String.valueOf(checkedSSaleCount));
                        elekBtn.setText(String.valueOf(eSaleCount) + " / " + String.valueOf(checkedESaleCount));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                 //   Log.i("JSONXETA", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                try {
//                    Thread.sleep(1000);
//                }
//                catch (Exception E){
//                    E.printStackTrace();
//                }
                dialog.hide();

                if (error instanceof NoConnectionError) {
                    showToast("İnternetə qoşulmayıb", Toast.LENGTH_SHORT);
                }
                else if (error instanceof TimeoutError) {
                    showToast("İnternetə bağlantıda problem var", Toast.LENGTH_SHORT);
                }
                else if (error instanceof ServerError) {
                    showToast("Server xətası", Toast.LENGTH_SHORT);
                }
                else{
                    showToast("Bilinməyən xəta:" + String.valueOf(error.networkResponse.statusCode), Toast.LENGTH_SHORT);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + trainId + tripDate + wagonNo + "123456789" + reqDate));
                params.put("action", "get_wagon_status_e");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", "123456789");
                params.put("train_id", trainId);
                params.put("trip_date", tripDate);
                params.put("request_dt", reqDate);
                params.put("wagon_no", wagonNo);
                params.put("token", token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static String convertPassMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public  void showToast(String txt, int duration){
        if(toast != null){
            toast.cancel();
            toast = Toast.makeText(getApplicationContext(), txt, duration);
            toast.show();
        }else{
            toast = Toast.makeText(getApplicationContext(), txt, duration);
            toast.show();
        }
    }

}
