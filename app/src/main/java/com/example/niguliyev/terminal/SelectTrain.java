package com.example.niguliyev.terminal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
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
import com.example.niguliyev.terminal.Model.TrainModel;
import com.example.niguliyev.terminal.Model.WagonModel;

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
import java.util.Iterator;
import java.util.Map;

public class SelectTrain extends AppCompatActivity  {

    Spinner trainSpinner;
    Spinner dateSpinner;
    Spinner wagonSpinner;
    Spinner  stationSpinner;
    String serviceUrl = "https://ticket.ady.az/terminal_service.php";
    ProgressDialog dialog;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    HashMap<String, JSONObject> trainMap = new HashMap<>();
    ArrayList<TrainModel> trainModels = new ArrayList<>();
    ArrayList<WagonModel> wagonModels = new ArrayList<>();
    ArrayList<WagonModel> stationModels = new ArrayList<>();
    ArrayList<String> tripDates = new ArrayList<>();
    TrainListAdapter trainListAdapter;
    DateListAdapter dateListAdapter;
    WagonListAdapter wagonListAdapter;
    StationListAdapter stationListAdapter;
    Button nextBtn, backBtn;
    int trainId;
    String tripDate;
    TextView tripDateTxt;
    WagonModel selectedWagon = new  WagonModel();
    WagonModel selectedStation = new WagonModel();
    int StaionIds[];
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_train);

        trainSpinner = findViewById(R.id.spinner1);

        wagonSpinner = findViewById(R.id.spinner3);
        stationSpinner = findViewById(R.id.spinner4);
        nextBtn = findViewById(R.id.nextBtn);
        tripDateTxt = findViewById(R.id.tripDateTxt);
     //   backBtn = findViewById(R.id.backBtn);

        changeColor(R.color.blue);

        wagonSpinner.setEnabled(false);
        stationSpinner.setEnabled(false);


        wagonListAdapter = new WagonListAdapter(getApplicationContext(), wagonModels);
        wagonSpinner.setAdapter(wagonListAdapter);

        stationListAdapter = new StationListAdapter(getApplicationContext(), stationModels);
        stationSpinner.setAdapter(stationListAdapter);


        dialog = new ProgressDialog(SelectTrain.this);
        dialog.setCanceledOnTouchOutside(false);
        currentTime = Calendar.getInstance().getTime();
        dialog.setMessage("Zəhmət olmasa gözləyin..");
       // dialog.show();


        getTrainTrip();

        trainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 wagonModels.clear();
                 stationModels.clear();
                if(i != 0){
                    tripDateTxt.setText(trainModels.get(i - 1).getTripDate());
                    currentTime = Calendar.getInstance().getTime();
                    getWagons();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        wagonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    selectedWagon = wagonModels.get(i-1);
                  //  Log.i("SelectedWagon", selectedWagon.getWagon_no());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        stationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    selectedStation = stationModels.get(i-1);
                 //   Log.i("SelectedWagon", selectedWagon.getWagon_no());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner[] spinners = {trainSpinner, wagonSpinner, stationSpinner};
                String[] messages = {"Qatar", "Vaqon", "Stansiya"};
                int count = 0;

                for(int i = 0; i<3; i++){
                    if(spinners[i].getSelectedItemPosition() == 0){
                        showToast(messages[i] + " seçilməyib", Toast.LENGTH_SHORT);
                        count = 1;
                        break;
                    }
                }
                  if(count == 0){
                    Intent intent = new Intent(SelectTrain.this, Options.class);
                   // Log.i("Qatarlar", trainModels.toString() );
                      trainId = trainModels.get(trainSpinner.getSelectedItemPosition() -1).getTrainId();
                      tripDate = trainModels.get(trainSpinner.getSelectedItemPosition() -1).getTripDate();
                    intent.putExtra("trainId", trainId);
                    intent.putExtra("tripDate", tripDate);
                    intent.putExtra("wagonNo", selectedWagon.getWagon_no());
                    intent.putExtra("trainName", trainModels.get(trainSpinner.getSelectedItemPosition() -1).getTrainNumber());
                    intent.putExtra("stationIds", StaionIds);
                    intent.putExtra("StationId", selectedStation.getWagon_id());
                    //intent.putExtra("trainName", trainModels.get(trainSpinner.getSelectedItemPosition() -1).getTrainNumber());
                    startActivity(intent);
                    overridePendingTransition(R.anim.come_from_right, R.anim.exit_from_left);
                  }
            }
        });

    }


    private void getTrainTrip() {
        dialog.show();
        wagonSpinner.setEnabled(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
               // Log.i("Qatarlar", response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if(result.equals("0")){
                        showToast("Xəta baş verdi", Toast.LENGTH_SHORT);
                    }
                    else
                    {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        JSONObject jsonObject1 = dataArray.getJSONObject(0);
                        JSONArray trainTripArray = jsonObject1.getJSONArray("train_trip");

                        for(int j = 0; j<trainTripArray.length(); j++){
                            JSONObject trainObject = trainTripArray.getJSONObject(j);
                            int trainId = trainObject.getInt("train_id");
                            String tripDate = trainObject.getString("trip_date");
                            String trainNumber = trainObject.getString("train_number");
                            TrainModel trainModel = new TrainModel(trainId, tripDate, trainNumber);
                            trainModels.add(trainModel);
                        }
                    }

                    trainListAdapter = new TrainListAdapter(getApplicationContext(), trainModels);
                    trainSpinner.setAdapter(trainListAdapter);
                } catch (JSONException e) {
                    showToast("Json Error", Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
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

                String reqDate = sdf.format(currentTime);
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + DownloadTest.deviceId + reqDate));
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_train_trip");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", DownloadTest.deviceId);
                params.put("request_dt", reqDate);
                params.put("token", token);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getWagons(){
           dialog.show();
           wagonModels.clear();
           stationModels.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }

                try {
                  //  Log.i("VAqonlar",  response);

                      JSONObject jsonObject = new JSONObject(response);
                      String result = jsonObject.getString("result");
                      if(result.equals("0")){
                         showToast("Vaqon yoxdur", Toast.LENGTH_SHORT);
                      }
                      else
                      {
                         JSONObject dataObject = jsonObject.getJSONObject("data");
                         for(int j = 0; j<dataObject.getJSONArray("wagons").length(); j++){
                             JSONObject wagonObject = dataObject.getJSONArray("wagons").getJSONObject(j);
                             int wagonId = wagonObject.getInt("id");
                             String wagon_no = wagonObject.getString("wagon_no");
                             String wagon_type = wagonObject.getString("wagon_type_abbr");
                             WagonModel wagonModel = new WagonModel(wagonId, wagon_no, wagon_type);
                             wagonModels.add(wagonModel);
                          }

                          StaionIds = new int[dataObject.getJSONArray("stations").length()];
                          for(int k=0; k<dataObject.getJSONArray("stations").length()-1; k++){
                              JSONObject stationObject = dataObject.getJSONArray("stations").getJSONObject(k);
                              int stationId = stationObject.getInt("station_id");
                              String stationName = stationObject.getString("station_name");
                              WagonModel stationModel = new WagonModel(stationId, stationName);
                              StaionIds[k] = stationId;
                              stationModels.add(stationModel);
                            //  Log.i("StationIdCount", String.valueOf(StaionIds[k]));
                          }
                           wagonSpinner.setEnabled(true);
                           stationSpinner.setEnabled(true);
                      }

                      // field_type

                    wagonListAdapter = new WagonListAdapter(getApplicationContext(), wagonModels);
                    wagonSpinner.setAdapter(wagonListAdapter);
                    wagonSpinner.setEnabled(true);

                    stationListAdapter = new StationListAdapter(getApplicationContext(), stationModels);
                    stationSpinner.setAdapter(stationListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Json Error", Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
               // Log.i("Xeta", error.toString());

                trainListAdapter = new TrainListAdapter(getApplicationContext(), trainModels);
                trainSpinner.setAdapter(trainListAdapter);

                wagonListAdapter = new WagonListAdapter(getApplicationContext(), wagonModels);
                wagonSpinner.setAdapter(wagonListAdapter);

                stationListAdapter = new StationListAdapter(getApplicationContext(), stationModels);
                stationSpinner.setAdapter(stationListAdapter);
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
                trainId = trainModels.get(trainSpinner.getSelectedItemPosition() -1).getTrainId();
                tripDate = trainModels.get(trainSpinner.getSelectedItemPosition() - 1).getTripDate();
                String reqDate = sdf.format(currentTime);
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + String.valueOf(trainId) + tripDate + DownloadTest.deviceId + reqDate));
               // Log.i("Melumatlar", reqDate + " " + token);
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_wagon_e");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", "1");
                params.put("terminal_id", DownloadTest.deviceId);
                params.put("train_id", String.valueOf(trainId));
                params.put("trip_date", tripDate);
                params.put("request_dt", reqDate);
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

    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), resourseColor));
        }

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(resourseColor)));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(SelectTrain.this)
                .setTitle("Çıxış")
                .setMessage("Proqramdan çıxmaq istədiyinizə əminsiniz?")
                .setPositiveButton("Bəlİ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SelectTrain.this.finish();
                    }
                })
                .setNegativeButton("Xeyr", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public  void showToast(String txt, int duration){//
        if(toast != null){
            toast.cancel();
            toast = Toast.makeText(getApplicationContext(), txt, duration);
            toast.show();
        }else{
            toast = Toast.makeText(getApplicationContext(), txt, duration);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
       //   finish();
       // Log.i("RRRRRRR", "RRRRR");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
       // Log.i("RRRRRRR", String.valueOf(today));

        if (today != lastTimeStarted) {
           finish();
        }

        super.onResume();
    }
}
