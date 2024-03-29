package com.example.niguliyev.terminal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
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

public class Login extends AppCompatActivity {

    Button loginBtn;
    EditText userEdt, passEdt;
    String loginUrl = "https://ticket.ady.az/terminal_service.php";
    ProgressDialog dialog;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    public static int userId;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        handleSSLHandshake();

        loginBtn = findViewById(R.id.loginBtn);
        userEdt = findViewById(R.id.userEdt);
        passEdt = findViewById(R.id.passEdt);

        dialog = new ProgressDialog(Login.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Zəhmət olmasa gözləyin...");

//        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//        toast.show();
//        toast.cancel();


      //  TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            requestReadPhoneStatePermission();

        } else {
            doPermissionGrantedStuffs();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                //  finish();
                // Intent i = new Intent(Login.this, Options.class);
                // startActivity(i);
                currentTime = Calendar.getInstance().getTime();
                logIn();
            }
        });

    }


    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(Login.this)
                    .setTitle("Permission Request")
                    .setMessage("Allow Permission")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(Login.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                alertAlert("Permission Read_PHONE is not granted");
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(Login.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })

                .show();
    }

    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i("DeviceId", String.valueOf(tm.getImei()));
            }
            else
            {
                Log.i("DeviceId", String.valueOf(tm.getDeviceId()));
            }

        }

    }

    private void logIn(){

         StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {

             @Override
             public void onResponse(String response) {
                 Log.i("nnnn", response);
                     dialog.dismiss();

                 try {
                     JSONObject jsonObject = new JSONObject(response);
                     if(jsonObject.getString("result").equals("0")){
                             showToast("Xəta baş verdi", Toast.LENGTH_SHORT);
                     }
                     else
                     {
                          //dialog.dismiss();
                          JSONArray jsonArray = jsonObject.getJSONArray("data");
                          JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                          userId = jsonObject1.getInt("user_id");

                          SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          Calendar calendar = Calendar.getInstance();
                          int today = calendar.get(Calendar.DAY_OF_YEAR);
                          SharedPreferences.Editor editor = settings.edit();
                          editor.putInt("last_time_started", today);
                          editor.commit();
                          userEdt.setText("");
                          passEdt.setText("");

                        //  finish();         // Diqqet
                          Intent i = new Intent(Login.this, SelectTrain.class);
                          startActivity(i);
                          overridePendingTransition(R.anim.come_from_right, R.anim.exit_from_left);
                     }
                 } catch (JSONException e) {
                     showToast("Json Error", Toast.LENGTH_SHORT);
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 dialog.dismiss();
                 if(error instanceof NoConnectionError){
                     showToast("İnternetə qoşulmayıb", Toast.LENGTH_SHORT);
                 }
                 if(error instanceof TimeoutError){
                     showToast("Bağlantıda problem var", Toast.LENGTH_SHORT);
                 }
                 if(error instanceof ServerError){
                     showToast("Server xətası", Toast.LENGTH_SHORT);

                 }
             }
         })
         {
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
             protected Map<String, String> getParams() {

                 String reqDate = sdf.format(currentTime);
                 String token = convertPassMd5(convertPassMd5( userEdt.getText().toString()+ passEdt.getText().toString()+DownloadTest.deviceId+reqDate));
                 Map<String, String> params = new HashMap<>();
                 params.put("action", "t_user_login");
                 params.put("secure_code", "t1e2r3m4i5n6a7l8");
                 params.put("user_name", userEdt.getText().toString());
                 params.put("password", passEdt.getText().toString());
                 params.put("terminal_id", DownloadTest.deviceId);
                 params.put("request_dt", reqDate);
                 params.put("token", token);

                 return  params;
             }
         };

         RequestQueue requestQueue = Volley.newRequestQueue(this);
         requestQueue.add(stringRequest); ///
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
