package com.example.niguliyev.terminal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

public class DownloadTest extends AppCompatActivity {

    Button downloadBtn;
    private ProgressDialog pDialog;
    ImageView image;
    String version = "5";
    String updateUrl = "https://ticket.ady.az/terminal_service.php";
    ProgressDialog dialog;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    RelativeLayout infolyt;
    Button tryBtn;

    public static final int progressType = 0;
    private static String fileUrl = "";
    public static String deviceId = "";
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);

        downloadBtn = findViewById(R.id.yukleBtn);
        image = findViewById(R.id.imageV);
        tryBtn = findViewById(R.id.tryBtn);


        dialog = new ProgressDialog(DownloadTest.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Zəhmət olmasa gözləyin..");
        infolyt = findViewById(R.id.infoLyt);
        infolyt.setVisibility(View.GONE);


//        SharedPreferences prefs = getSharedPreferences("PREF", MODE_PRIVATE);
//        deviceId = prefs.getString("DEVICEID", "");
//
//        if(deviceId.equals("")){
//            SharedPreferences.Editor editor = getSharedPreferences("PREF", MODE_PRIVATE).edit();
//            editor.putString("name", "Elena");
//        }

        tryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infolyt.setVisibility(View.GONE);
                dialog.show();
                currentTime = Calendar.getInstance().getTime();
                checkVersion();
            }
        });



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            requestReadPhoneStatePermission();

        } else {
            doPermissionGrantedStuffs();
            if (ContextCompat.checkSelfPermission(DownloadTest.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                currentTime = Calendar.getInstance().getTime();
                dialog.show();
                checkVersion();
            } else
            {
                requestStoragePermission();
            }
        }

    }

    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Log.i("DeviceId", String.valueOf(tm.getImei()));
                deviceId = String.valueOf(tm.getImei());
            }
            else
            {
               // Log.i("DeviceId", String.valueOf(tm.getDeviceId()));
                 deviceId = String.valueOf(tm.getDeviceId());
            }

        }

    }


    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            new AlertDialog.Builder(DownloadTest.this)
                    .setTitle("Permission Request")
                    .setMessage("Allow Permission")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(DownloadTest.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DownloadTest.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkVersion();
            } else {
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doPermissionGrantedStuffs();
                if (ContextCompat.checkSelfPermission(DownloadTest.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    currentTime = Calendar.getInstance().getTime();
                    dialog.show();
                    checkVersion();
                } else
                {
                    requestStoragePermission();
                }

            } else {
                alertAlert("Permission Read_PHONE is not granted");
            }
        }

    }

    private void checkVersion(){
         dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.hide();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if(result.equals("1")){
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        JSONObject jsonObject2 = dataArray.getJSONObject(0);
                        String lastVersion = jsonObject2.getString("version");
                         String url = jsonObject2.getString("download_url");
                          fileUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());

                        if(!(version.equals(lastVersion))){
                            new DownloadImage().execute(fileUrl);
                        }
                        else
                        {
                           Intent intent = new Intent(DownloadTest.this, Login.class);
                           startActivity(intent);
                           finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Xəta baş verdi: Invalid parameter", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Json Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                infolyt.setVisibility(VISIBLE);
                if(error instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), "İnternetə qoşulmayıb." , Toast.LENGTH_SHORT).show();
                }
               else  if(error instanceof TimeoutError){
                    Toast.makeText(getApplicationContext(), "İnternetə bağlantıda problem var" , Toast.LENGTH_SHORT).show();
                }
               else   if(error instanceof ServerError){
                    Toast.makeText(getApplicationContext(), "Server xətası" , Toast.LENGTH_SHORT).show();

                }
               else {
                    Toast.makeText(getApplicationContext(), "Bilinməyın xəta" , Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String reqDate = sdf.format(currentTime);
                String token = convertPassMd5(convertPassMd5("1"+"123456789"+reqDate));
                Map<String, String> params = new HashMap<>();
                params.put("action", "t_version_control");
                params.put("soft_id", "1");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("terminal_id", "123456789");
                params.put("request_dt", reqDate);
                params.put("token", token);

                return  params;
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
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case progressType:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Gozleyin");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
                default:
                    return null;
        }
    }

    class DownloadImage extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progressType);
        }

        @Override
        protected String doInBackground(String... fileUrl) {
            int count;
            try{
                URL url = new URL(fileUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

                String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "/newimage2.apk";
                File imageFile = new File(storageDir + fileName);
                OutputStream outputStream = new FileOutputStream(imageFile);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = inputStream.read(data)) != -1){
                    total = total + count;
                    publishProgress("" +(int)((total*100)/lengthOfFile));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Xeta", "Xeta1");
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infolyt.setVisibility(VISIBLE);
                    }
                });
                e.printStackTrace();
                Log.i("Xeta", "Xeta2");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
           // dismissDialog(progressType);
            finish();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new   File(Environment.getExternalStorageDirectory()  + "/newimage2.apk")), "application/vnd.android.package-archive");
            startActivity(intent);

        }

    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(DownloadTest.this)
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


}
