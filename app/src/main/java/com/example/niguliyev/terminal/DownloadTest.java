package com.example.niguliyev.terminal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
    String version = "4";
    String updateUrl = "https://test-ticket.ady.az/terminal_service.php";
    ProgressDialog dialog;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    RelativeLayout infolyt;
    Button tryBtn;
    Toast toast;

    public static final int progressType = 0;
    private static String fileUrl = "";
    public static String deviceId = "";
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);
        // Download Test
        downloadBtn = findViewById(R.id.yukleBtn);
        image = findViewById(R.id.imageV);
        tryBtn = findViewById(R.id.tryBtn);
        System.out.println();


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
            if (ContextCompat.checkSelfPermission(DownloadTest.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DownloadTest.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                if (ContextCompat.checkSelfPermission(DownloadTest.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
            public void onResponse(String response) {//
                //Log.i("MMMUU", response);
                dialog.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if(result.equals("1")){
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        JSONObject jsonObject2 = dataArray.getJSONObject(0);
                        String lastVersion = jsonObject2.getString("version");
                         String url = jsonObject2.getString("download_url");
                          fileUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
                         Log.i("NNNMMM", url);

                        if(!(version.equals(lastVersion))){
                           // new DownloadImage().execute(fileUrl);
                            startDownload(url);
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

                dialog.dismiss();
                infolyt.setVisibility(VISIBLE);
                if (error instanceof NoConnectionError) {
                    showToast("İnternetə qoşulmayıb", Toast.LENGTH_SHORT);
                }
                else if (error instanceof TimeoutError) {
                    showToast("Timeout xəta", Toast.LENGTH_SHORT);
                }
                else if (error instanceof ServerError) {

                    showToast("Server xətası: " + error.networkResponse.statusCode, Toast.LENGTH_SHORT);
                }
                else{
                    showToast("Bilinməyən xəta: " + String.valueOf(error.networkResponse.statusCode), Toast.LENGTH_SHORT);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String reqDate = sdf.format(currentTime);
                String token = convertPassMd5(convertPassMd5("1"+deviceId+reqDate));
                Map<String, String> params = new HashMap<>();
                params.put("action", "t_version_control");
                params.put("soft_id", "1");
                params.put("secure_code", "t1e2r3m4i5n6a7l8");
                params.put("terminal_id", deviceId);
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
                pDialog.setMessage("Zəhmət olmasa gözləyin");
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
                String fileName = "/terminal.apk";
                File imageFile = new File(directory() + fileName);
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
               // Log.i("Xeta", "Xeta2");
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
           // Log.i("TESTT", "Testt");
            finish();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".provider",
                    new File(directory()+"/terminal.apk") );
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

    private String directory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    private void startDownload(String url) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("PROQRAM GÜNCƏLLƏNMƏ");
        progressDialog.setMessage("HAZIRLANIR....");
        progressDialog.setCancelable(false);

        progressDialog.show();

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        final int downloadId = PRDownloader.download(url,
                directory(), "terminal.apk")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        progressDialog.setMessage("YÜKLƏNİR");
                    }
                })
                .start(new OnDownloadListener() {

                    @Override
                    public void onDownloadComplete() {
                        progressDialog.setMessage("TAMAMLANDI");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider",
                                new File(directory() + "/terminal.apk"));
                        intent.setDataAndType(apkUri,
                                "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(com.downloader.Error error) {
                        progressDialog.setMessage("ERROR");
                        Log.i("Erroooor", String.valueOf(error.getConnectionException()));
                    }

                });

    }

}
