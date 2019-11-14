package com.example.niguliyev.terminal;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.example.niguliyev.terminal.Model.WagonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Number extends AppCompatActivity {

    Button backBtn, checkBtn;
    RadioButton svBtn, biletBtn;
    RadioGroup radioGroup;
    EditText numberEdt;
    ProgressDialog dialog;
    String serviceUrl = "https://ticket.ady.az/terminal_service.php";
    int trainId;
    String tripDate;
    String wagonNo, trainName;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    String reqDate = "";
    String field_type = "1";
    Dialog checkDialog;
    String station, destionation, train, wagon, place, klas, customer, svNo, birthDate, tikcetType;
    TextView stationTxt, destinationTxt, trainTxt, wagonTxt, placeTxt, klassTxt, customerTxt, svNoTxt, birthdayTxt, ticketTypeTxt, titleTxt;
    Button confirmBtn;
    TextView confirmMsg;
    String saleId = "";
    TextView notFoundTextView;
    ActivityManager am;
    ComponentName cn;
    int stationId;
    int stationIds[] = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        backBtn = findViewById(R.id.backBtn);
        radioGroup = findViewById(R.id.radioGroup);
        numberEdt = findViewById(R.id.numberEdt);
        checkBtn  = findViewById(R.id.checkBtn);
        titleTxt = findViewById(R.id.titleTxt);

        dialog = new ProgressDialog(Number.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Zəhmət olmasa gözləyin..");

        trainId = Integer.parseInt(getIntent().getStringExtra("trainId"));
        tripDate = getIntent().getStringExtra("tripDate");
        wagonNo = getIntent().getStringExtra("wagonNo");
        trainName = getIntent().getStringExtra("trainName");
        stationId = getIntent().getIntExtra("stationId", 0);
        stationIds = getIntent().getIntArrayExtra("stationIds");

        titleTxt.setText(trainName + ", Vaqon " + wagonNo);


        checkDialog = new Dialog(this);
        checkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkDialog.setContentView(R.layout.dialog_ticket_layout);
        checkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
        confirmBtn = checkDialog.findViewById(R.id.confirmBtn);
        confirmMsg = checkDialog.findViewById(R.id.confirmMsg);

        Window window = checkDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.come_from_left, R.anim.exit_from_right);
            }
        });

        svBtn = findViewById(R.id.svBtn);
        biletBtn = findViewById(R.id.biletBtn);

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDialog.dismiss();
            }
        });

        biletBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                     numberEdt.setHint("Şəxsiyyət vəsiqəsinin nömrəsi");
                     field_type = "1";
                }
                else{
                     numberEdt.setHint("Biletin nömrəsi");
                     field_type = "2";
                }
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                currentTime = Calendar.getInstance().getTime();
                reqDate = sdf.format(currentTime);
                getTicketDetails();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTime = Calendar.getInstance().getTime();
                reqDate = sdf.format(currentTime);
               confirmTicket();
            }
        });

//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                finish();//or whatever you want to do
//
//
//            }
//        }, 5000);
    }

    public void getTicketDetails(){
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();

                try {
                    Log.i("TicketData", response);

                    JSONObject responseJsonObject = new JSONObject(response);

                    String resultCount = responseJsonObject.getString("result");
                    {
                        if(resultCount.equals("0")){
                            if(responseJsonObject.get("data") instanceof  JSONArray){
                               Toast.makeText(getApplicationContext(), "Bilet tapılmadı", Toast.LENGTH_SHORT).show();
                               // notFoundTextView.setVisibility(View.VISIBLE);
                                try {
                                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                } catch (Exception e) {
                                    //
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Invalid parametr", Toast.LENGTH_SHORT).show();
                               // notFoundTextView.setVisibility(View.INVISIBLE);
                                try {
                                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                } catch (Exception e) {
                                    //
                                }

                            }

                        }
                        else
                        {
                            Log.i("Netice", responseJsonObject.getJSONObject("data").toString());
                            JSONObject jsonObject = responseJsonObject.getJSONObject("data");
                            JSONArray saleDataArray = jsonObject.getJSONArray("sale_data");
                            JSONObject ticketDataJsonObject = saleDataArray.getJSONObject(0);
                            ticketTypeTxt.setText(ticketDataJsonObject.getString("ticket_type"));
                            birthdayTxt.setText(ticketDataJsonObject.getString("passenger_birth_date"));
                            customerTxt.setText(ticketDataJsonObject.getString("passenger"));
                            svNoTxt.setText(ticketDataJsonObject.getString("passenger_passport_no"));
                            stationTxt.setText(ticketDataJsonObject.getString("from_st"));
                            destinationTxt.setText(ticketDataJsonObject.getString("to_st"));
                            trainTxt.setText(ticketDataJsonObject.getString("train_number"));
                            wagonTxt.setText(ticketDataJsonObject.getString("wagon_no"));
                            placeTxt.setText(ticketDataJsonObject.getString("seat_label"));
                            klassTxt.setText(ticketDataJsonObject.getString("seat_class_abbr"));
                            saleId = String.valueOf(ticketDataJsonObject.getInt("sale_id"));
                          //  notFoundTextView.setVisibility(View.INVISIBLE);

                            Log.i("HHHH", String.valueOf(trainId));
                            Log.i("HHHH", String.valueOf(tripDate));
                            Log.i("HHHH", String.valueOf(ticketDataJsonObject.getInt("train_id")));
                            Log.i("HHHH", String.valueOf(ticketDataJsonObject.getString("trip_date")));

                                   if(trainId != ticketDataJsonObject.getInt("train_id") || !tripDate.equals(ticketDataJsonObject.getString("trip_date"))){

                                       confirmMsg.setText("Bilet bu reysə aid deyil." );
                                       confirmMsg.setVisibility(View.VISIBLE);
                                     confirmBtn.setVisibility(View.GONE);
                                   }
                              else {
                            if(!wagonNo.equals(ticketDataJsonObject.getString("wagon_no"))){
                                confirmMsg.setText("Bilet seçilmiş vaqona aid deyil" );
                                confirmMsg.setVisibility(View.VISIBLE);
                                confirmBtn.setVisibility(View.GONE);
                            }
                            else
                            {
                                if(find(stationIds, stationId) >=     find(stationIds, ticketDataJsonObject.getInt("from_station_id"))){

                                    if(ticketDataJsonObject.getInt("is_checked") == 0){
                                        confirmMsg.setVisibility(View.INVISIBLE);
                                        confirmBtn.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        confirmMsg.setText("Bilet artıq təsdiq edilib" );
                                        confirmMsg.setVisibility(View.VISIBLE);
                                        confirmBtn.setVisibility(View.GONE);
                                    }
                                }
                                else
                                {
                                    confirmMsg.setText("Minik stansiyası fərqlidir." );
                                    confirmMsg.setVisibility(View.VISIBLE);
                                    confirmBtn.setVisibility(View.GONE);
                                }

                            }
                              }

                            checkDialog.show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Xəta: Json Error", Toast.LENGTH_SHORT).show();
                   // notFoundTextView.setVisibility(View.INVISIBLE);
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
                else{
                    Toast.makeText(getApplicationContext(), "Bilinməyən xəta", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + numberEdt.getText().toString() + field_type + String.valueOf(trainId) + tripDate + "123456789" + reqDate));
                params.put("action", "check_e_ticket_from_passport");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", "123456789");
                params.put("passenger_passport_no", numberEdt.getText().toString());
                params.put("train_id", String.valueOf(trainId) );
                params.put("trip_date", tripDate);
                params.put("request_dt", reqDate);
                params.put("token", token);
                params.put("field_type", field_type);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public  void  confirmTicket(){

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();
                Log.i("ConfirmMessage", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if(result.equals("0")){
                        Toast.makeText(getApplicationContext(), "Xəta: Bilet təsdiq edilmədi.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Bilet təsdiq edildi", Toast.LENGTH_SHORT).show();
                        dialog.hide();
                        checkDialog.hide();

                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Confirm Json Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();

                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Bilet təsdiq edilmədi. İnternetə qoşulmayıb.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Bilet təsdiq edilmədi. İnternetə bağlantıda problem var", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Bilet təsdiq edilmədi. Server xətası", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Bilet təsdiq edilmədi. Bilinməyən xəta", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + saleId + "123456789" + reqDate));
                params.put("action", "ticket_checkin");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", "123456789");
                params.put("sale_id", saleId);
                params.put("request_dt", reqDate);
                params.put("token", token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public static int find(int[] a, int target)
    {
        for (int i = 0; i < a.length; i++)
            if (a[i] == target)
                return i;

        return -1;
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
