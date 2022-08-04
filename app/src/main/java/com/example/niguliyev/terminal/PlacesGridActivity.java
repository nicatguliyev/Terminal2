package com.example.niguliyev.terminal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PlacesGridActivity extends AppCompatActivity {

    GridView placeGrid;
    Button backBtn;
    String wagonNo, trainName;
    TextView titleTxt;
    String serviceUrl = "https://ticket.ady.az/terminal_service.php";
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    String reqDate = "";
    ProgressDialog dialog;
    String tripDate = "";
    String trainId;
    String selectedSeat;
    Dialog checkDialog;
    TextView stationTxt, destinationTxt, trainTxt, wagonTxt, placeTxt, klassTxt, customerTxt, svNoTxt, birthdayTxt, ticketTypeTxt, confirmMsgTxt;
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_grid);

        handleSSLHandshake();

        dialog = new ProgressDialog(PlacesGridActivity.this);
        dialog.setMessage("Zəhmət olmasa gözləyin");
        dialog.setCanceledOnTouchOutside(false);

        backBtn = findViewById(R.id.backBtn);
        titleTxt = findViewById(R.id.titleTxt);

        wagonNo = getIntent().getStringExtra("wagonNo");
        trainName = getIntent().getStringExtra("trainName");
        tripDate = getIntent().getStringExtra("tripDate");
        trainId = getIntent().getStringExtra("trainId");

        titleTxt.setText(trainName + ", Vaqon " + wagonNo );

        placeGrid = findViewById(R.id.placeGrid);

        PlaceGridAdapter adapter = new PlaceGridAdapter(getApplicationContext(), PlacesActivity.allSeatsList);

        placeGrid.setAdapter(adapter);

        checkDialog = new Dialog(this);
        checkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkDialog.setContentView(R.layout.dialog_ticket_layout);
        checkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = checkDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        ImageView closeImage = checkDialog.findViewById(R.id.closeImg);
        stationTxt = checkDialog.findViewById(R.id.stNameTxt);
        destinationTxt = checkDialog.findViewById(R.id.destStationName);
        trainTxt = checkDialog.findViewById(R.id.trainNameTxt);
        wagonTxt = checkDialog.findViewById(R.id.vaqonValTxt);
        placeTxt = checkDialog.findViewById(R.id.yerValTxt);
        klassTxt = checkDialog.findViewById(R.id.klassValTxt);
        customerTxt = checkDialog.findViewById(R.id.srnsnNameTxt);
        svNoTxt = checkDialog.findViewById(R.id.senedNoTxt);
        birthdayTxt = checkDialog.findViewById(R.id.birthValTxt);
        ticketTypeTxt = checkDialog.findViewById(R.id.ticketTypeTxt);
        confirmMsgTxt = checkDialog.findViewById(R.id.confirmMsg);
        confirmBtn = checkDialog.findViewById(R.id.confirmBtn);

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDialog.dismiss();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        placeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(PlacesActivity.allSeatsList.get(i).getSeat_status_id() == 4){
                    selectedSeat = String.valueOf(PlacesActivity.allSeatsList.get(i).getSeat_id());
                    currentTime = Calendar.getInstance().getTime();
                    reqDate = sdf.format(currentTime);
                    getSeatInfo();
                }

            }
        });


    }


    public void getSeatInfo(){
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();

                Log.i("TOTO", response);

                try {
                    JSONObject responseJsonObject = new JSONObject(response);
                    if(responseJsonObject.getString("result").equals("0")){

                            if(responseJsonObject.get("data") instanceof  JSONObject){
                                Toast.makeText(getApplicationContext(), "Bilet tapılmadı", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Invalid parametr", Toast.LENGTH_SHORT).show();
                            }
                    }
                    else{
                        JSONObject dataJsonObject = responseJsonObject.getJSONObject("data");
                        JSONObject seatInfoJsonObject = dataJsonObject.getJSONArray("wagons").getJSONObject(0);
                        ticketTypeTxt.setText(seatInfoJsonObject.getString("ticket_type"));
                        birthdayTxt.setText(seatInfoJsonObject.getString("passenger_birth_date"));
                        customerTxt.setText(seatInfoJsonObject.getString("passenger"));
                        svNoTxt.setText(seatInfoJsonObject.getString("passenger_passport_no"));
                        stationTxt.setText(seatInfoJsonObject.getString("from_st"));
                        destinationTxt.setText(seatInfoJsonObject.getString("to_st"));
                        trainTxt.setText(seatInfoJsonObject.getString("train_number"));
                        wagonTxt.setText(seatInfoJsonObject.getString("wagon_no"));
                        placeTxt.setText(seatInfoJsonObject.getString("seat_label"));
                        klassTxt.setText(seatInfoJsonObject.getString("seat_class_abbr"));
                        confirmBtn.setVisibility(View.INVISIBLE);

                        checkDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("JSONXETA", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();

                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "İnternetə qoşulmayıb.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "İnternetə bağlantıda problem var", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server xətası", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Bilinməyən xəta", Toast.LENGTH_SHORT).show();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Username", "User");
//                params.put("Password", "222333444");
                String creds = String.format("%s:%s","User","222333444");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {//
                Map<String, String> params = new HashMap<>();
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + trainId + tripDate + wagonNo + selectedSeat + DownloadTest.deviceId + reqDate));
                params.put("action", "get_seat_info");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", DownloadTest.deviceId);
                params.put("train_id", trainId);
                params.put("trip_date", tripDate);
                params.put("request_dt", reqDate);
                params.put("seat_id", selectedSeat);
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

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}
