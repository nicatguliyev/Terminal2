package com.example.niguliyev.terminal;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Options extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    Button scanBtn;
    Button senedBtn, backBtn;
    Button yerBtn;
    int trainId, stationId;
    String tripDate;
    String wagonNo;
    String trainName;
    ProgressDialog dialog;
    Dialog checkDialog;
    TextView stationTxt, destinationTxt, trainTxt, wagonTxt, placeTxt, klassTxt, customerTxt, svNoTxt, birthdayTxt, ticketTypeTxt, confirmMsgTxt, titleTxt;
    String serviceUrl = "https://ticket.ady.az/terminal_service.php";
    String saleId = "";
    String pass_no = "";
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    String reqDate = "";
    private ZXingScannerView zXingScannerView;
    Button confirmBtn;
    Button testBtn;
    EditText edt;
    int[] stationIds = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //changeColor(R.color.blue);

        scanBtn = (Button) findViewById(R.id.scanBtn);
        senedBtn = findViewById(R.id.senedBtn);
        backBtn = findViewById(R.id.backBtn);
        yerBtn = findViewById(R.id.yerBtn);
        titleTxt = findViewById(R.id.titleTxt);

        //testBtn = findViewById(R.id.test);
        edt = findViewById(R.id.edt);

        trainId = getIntent().getIntExtra("trainId", 0);
        tripDate = getIntent().getStringExtra("tripDate");
        wagonNo = getIntent().getStringExtra("wagonNo");
        trainName = getIntent().getStringExtra("trainName");
        stationId = getIntent().getIntExtra("StationId", 0);
        stationIds = getIntent().getIntArrayExtra("stationIds");

        Log.i("SttionId", String.valueOf(stationIds[0]));

        titleTxt.setText(trainName + ",  Vaqon " + wagonNo);

        dialog = new ProgressDialog(Options.this);
        dialog.setMessage("Zəhmət olmasa gözləyin");

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
        confirmMsgTxt = checkDialog.findViewById(R.id.confirmMsg);
        confirmBtn = checkDialog.findViewById(R.id.confirmBtn);

        Window window = checkDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDialog.dismiss();
            }
        });


        final Activity activity = this;
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt.setText("");
                Intent intent = new Intent();
                intent.setAction("com.symbol.datawedge.api.ACTION");
                intent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "TOGGLE_SCANNING");
                sendBroadcast(intent);

              /*  IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);

                integrator.initiateScan();  */

                //  zXingScannerView = new ZXingScannerView(getApplicationContext());
                //  setContentView(zXingScannerView);
                //  zXingScannerView.setResultHandler(Options.this);
                //  zXingScannerView.startCamera();
            }
        });

        senedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Number.class);
                intent.putExtra("trainId", String.valueOf(trainId));
                intent.putExtra("tripDate", tripDate);
                intent.putExtra("wagonNo", wagonNo);
                intent.putExtra("trainName", trainName);
                intent.putExtra("stationId", stationId);
                intent.putExtra("stationIds", stationIds);
                startActivity(intent);
                overridePendingTransition(R.anim.come_from_right, R.anim.exit_from_left);
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.come_from_left, R.anim.exit_from_right);
            }
        });

        yerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Options.this, PlacesActivity.class);
                i.putExtra("trainId", String.valueOf(trainId));
                i.putExtra("tripDate", tripDate);
                i.putExtra("wagonNo", wagonNo);
                i.putExtra("trainName", trainName);
                startActivity(i);
                overridePendingTransition(R.anim.come_from_right, R.anim.exit_from_left);
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


        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (edt.getText().toString().length() < 20) {
                    if (!edt.getText().toString().equals("")) {
                        String x = edt.getText().toString();
                        currentTime = Calendar.getInstance().getTime();
                        reqDate = sdf.format(currentTime);
                        getTicketDetailsFromBarCode(x);
                    }
                } else {
                    String text = null;
                    try {
                        String x = edt.getText().toString().substring(2);
                        byte[] z = Base64.decode(x, Base64.DEFAULT);
                        text = new String(z, "UTF-8");
                        JSONObject jsonObject = new JSONObject(text);
                        saleId = jsonObject.getString("sale_id");
                        pass_no = jsonObject.getString("passenger_passport_no");
                        currentTime = Calendar.getInstance().getTime();
                        reqDate = sdf.format(currentTime);

                        getTicketDetailsFromQR();

                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(getApplicationContext(), "Encoding Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Barcode Json Converting Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "QrCode data length error", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.i("Xeta", e.toString());
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Bilinməyən xəta", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), resourseColor));
        }

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(resourseColor)));
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
//            } else {
//
//                try {
//
//                    if(result.getFormatName().equals("QR_CODE")) {
//                        String x = result.getContents().substring(2);
//                        //Log.i("Sagopa", result.getFormatName());
//
//                        byte[] z = Base64.decode(x, Base64.DEFAULT);
//
//                        String text = new String(z, "UTF-8");
//                        Log.i("YeniJson", text);
//                        JSONObject jsonObject = new JSONObject(text);
//                        saleId = jsonObject.getString("sale_id");
//                        Log.i("SALEID", saleId);
//                        pass_no = jsonObject.getString("passenger_passport_no");
//                        currentTime = Calendar.getInstance().getTime();
//                        reqDate = sdf.format(currentTime);
//
//                        getTicketDetailsFromQR();
//                    }
//
//                    else
//                    {
//                        String x = result.getContents();
//                        currentTime = Calendar.getInstance().getTime();
//                        reqDate = sdf.format(currentTime);
//                        getTicketDetailsFromBarCode(x);
//                    }
//
//                } catch (UnsupportedEncodingException e) {
//                    Toast.makeText(this, "Encoding Error", Toast.LENGTH_LONG).show();
//
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    Toast.makeText(this, "Barcode Json Converting Error", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//                catch (StringIndexOutOfBoundsException e){
//                    e.printStackTrace();
//                    Toast.makeText(this, "QrCode data length error", Toast.LENGTH_LONG).show();
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Unknown Barcode Reading error", Toast.LENGTH_LONG).show();
//                }
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }


    public void getTicketDetailsFromQR() {
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();

                Log.i("TicketData", response);
                try {


                    JSONObject responseJsonObject = new JSONObject(response);

                    String resultCount = responseJsonObject.getString("result");
                    {
                        if (resultCount.equals("0")) {
                            if (responseJsonObject.get("data") instanceof JSONArray) {
                                Toast.makeText(getApplicationContext(), "Bilet tapılmadı", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid parametr", Toast.LENGTH_SHORT).show();
                            }

                        } else {
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


                            if (trainId != ticketDataJsonObject.getInt("train_id") || !tripDate.equals(ticketDataJsonObject.getString("trip_date"))) {
                                confirmMsgTxt.setText("Bilet bu reyse aid deyil.");
                                confirmMsgTxt.setVisibility(View.VISIBLE);
                                confirmBtn.setVisibility(View.GONE);
                            } else {
                                if (!wagonNo.equals(ticketDataJsonObject.getString("wagon_no"))) {
                                    confirmMsgTxt.setText("Bilet seçilmiş vaqona aid deyil");
                                    confirmMsgTxt.setVisibility(View.VISIBLE);
                                    confirmBtn.setVisibility(View.GONE);
                                } else {
                                    if (find(stationIds, stationId) >= find(stationIds, ticketDataJsonObject.getInt("from_station_id"))) {

                                        if (ticketDataJsonObject.getInt("is_checked") == 0) {
                                            confirmMsgTxt.setVisibility(View.INVISIBLE);
                                            confirmBtn.setVisibility(View.VISIBLE);
                                        } else {
                                            confirmMsgTxt.setText("Bilet artıq təsdiq edilib");
                                            confirmMsgTxt.setVisibility(View.VISIBLE);
                                            confirmBtn.setVisibility(View.GONE);
                                        }
                                    } else {

                                        confirmMsgTxt.setText("Minik stansiyasi fərqlidir");
                                        confirmMsgTxt.setVisibility(View.VISIBLE);
                                        confirmBtn.setVisibility(View.GONE);
                                    }
                                }
                            }

                            checkDialog.show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.i("JSONXETA", e.toString());
                    Toast.makeText(getApplicationContext(), "Json Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();

                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "İnternetə qoşulmayıb.", Toast.LENGTH_SHORT).show();
                }
                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "İnternetə bağlantıda problem var", Toast.LENGTH_SHORT).show();
                }
                if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server xətası", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + saleId + pass_no + reqDate));
                params.put("action", "check_e_ticket");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", "123456789");
                params.put("passenger_passport_no", pass_no);
                params.put("sale_id", saleId);
                params.put("request_dt", reqDate);
                params.put("token", token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getTicketDetailsFromBarCode(final String ticketNo) {
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();

                try {
                    JSONObject responseJsonObject = new JSONObject(response);
                    Log.i("BarcodeInfo", response);
                    String resultCount = responseJsonObject.getString("result");
                    {
                        if (resultCount.equals("0")) {
                            if (responseJsonObject.get("data") instanceof JSONArray) {
                                Toast.makeText(getApplicationContext(), "Bilet tapılmadı", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid parametr", Toast.LENGTH_SHORT).show();
                            }
                        } else {
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


                            if (trainId != ticketDataJsonObject.getInt("train_id") || !tripDate.equals(ticketDataJsonObject.getString("trip_date"))) {
                                confirmMsgTxt.setText("Bilet bu reyse aid deyil.");
                                confirmMsgTxt.setVisibility(View.VISIBLE);
                                confirmBtn.setVisibility(View.GONE);
                            } else {
                                if (!wagonNo.equals(ticketDataJsonObject.getString("wagon_no"))) {
                                    confirmMsgTxt.setText("Bilet seçilmiş vaqona aid deyil");
                                    confirmMsgTxt.setVisibility(View.VISIBLE);
                                    confirmBtn.setVisibility(View.GONE);
                                } else {
                                    if (find(stationIds, stationId) >= find(stationIds, ticketDataJsonObject.getInt("from_station_id"))) {

                                        if (ticketDataJsonObject.getInt("is_checked") == 0) {
                                            confirmMsgTxt.setVisibility(View.INVISIBLE);
                                            confirmBtn.setVisibility(View.VISIBLE);
                                        } else {
                                            confirmMsgTxt.setText("Bilet artıq təsdiq edilib");
                                            confirmMsgTxt.setVisibility(View.VISIBLE);
                                            confirmBtn.setVisibility(View.GONE);
                                        }

                                    } else {
                                        confirmMsgTxt.setText("Minik stansiyasi fərqlidir");
                                        confirmMsgTxt.setVisibility(View.VISIBLE);
                                        confirmBtn.setVisibility(View.GONE);
                                    }

                                }
                            }


                            checkDialog.show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                  //  Log.i("JSONXETA", e.toString());
                    Toast.makeText(getApplicationContext(), "Json Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();

                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "İnternetə qoşulmayıb.", Toast.LENGTH_SHORT).show();
                }
              else  if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "İnternetə bağlantıda problem var", Toast.LENGTH_SHORT).show();
                }
               else  if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server xətası", Toast.LENGTH_SHORT).show();
                }
               else{
                    Toast.makeText(getApplicationContext(), "Bilinməyən xəta: " + String.valueOf(error.networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String token = convertPassMd5(convertPassMd5(String.valueOf(Login.userId) + String.valueOf(trainId) + String.valueOf(tripDate) + ticketNo + reqDate));
                params.put("action", "check_standart_ticket");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("user_id", String.valueOf(Login.userId));
                params.put("terminal_id", "123456789");
                params.put("ticket_no_barcode", ticketNo);
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


    public void confirmTicket() {

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();
                Log.i("ConfirmMessage", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (result.equals("0")) {
                        Toast.makeText(getApplicationContext(), "Xəta: Bilet təsdiq edilmədi.", Toast.LENGTH_SHORT).show();
                    } else {
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
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Bilet təsdiq edilmədi. İnternetə bağlantıda problem var", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Bilet təsdiq edilmədi. Server xətası", Toast.LENGTH_SHORT).show();
                } else {
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


    public static int find(int[] a, int target) {
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


    @Override
    public void handleResult(Result result) {
        Log.i("Yeni", result.getText());
        //  zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.stopCamera();
        // setContentView(R.layout.activity_options);
    }
}
