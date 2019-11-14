package com.example.niguliyev.terminal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button loginBtn;
    EditText userEdt, passEdt;
    String loginUrl = "https://ticket.ady.az/terminal_service.php";
    ProgressDialog dialog;
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    public static int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        userEdt = findViewById(R.id.userEdt);
        passEdt = findViewById(R.id.passEdt);

        dialog = new ProgressDialog(Login.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Zəhmət olmasa gözləyin..");



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

     private void logIn(){

         StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {

             @Override
             public void onResponse(String response) {
                     dialog.hide();

                 try {
                     JSONObject jsonObject = new JSONObject(response);
                     if(jsonObject.getString("result").equals("0")){
                         Toast.makeText(getApplicationContext(), "Xeta bas verdi", Toast.LENGTH_SHORT).show();
                     }
                     else
                     {
                          JSONArray jsonArray = jsonObject.getJSONArray("data");
                          JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                          userId = jsonObject1.getInt("user_id");
                          finish();
                          Intent i = new Intent(Login.this, SelectTrain.class);
                          startActivity(i);
                          overridePendingTransition(R.anim.come_from_right, R.anim.exit_from_left);
                     }
                 } catch (JSONException e) {
                     Toast.makeText(getApplicationContext(), "Json Error", Toast.LENGTH_SHORT).show();
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 dialog.hide();
                 if(error instanceof NoConnectionError){
                     Toast.makeText(getApplicationContext(), "İnternetə qoşulmayıb." , Toast.LENGTH_SHORT).show();
                 }
                 if(error instanceof TimeoutError){
                     Toast.makeText(getApplicationContext(), "İnternetə bağlantıda problem var" , Toast.LENGTH_SHORT).show();
                 }
                 if(error instanceof ServerError){
                     Toast.makeText(getApplicationContext(), "Server xətası" , Toast.LENGTH_SHORT).show();

                 }
             }
         }) {
             @Override
             protected Map<String, String> getParams() {

                 String reqDate = sdf.format(currentTime);
                 String token = convertPassMd5(convertPassMd5("r.askerov"+"123456"+"123456789"+reqDate));
                 Map<String, String> params = new HashMap<>();
                 params.put("action", "t_user_login");
                 params.put("secure_code", "t1e2r3m4i5n6a7l8");
                 params.put("user_name", userEdt.getText().toString());
                 params.put("password", passEdt.getText().toString());
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
}
