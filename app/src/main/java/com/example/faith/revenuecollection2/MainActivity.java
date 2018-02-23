package com.example.faith.revenuecollection2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    public static final String url = "http://demo.dewcis.com/revenuecollection/dataserver";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    EditText etPhone, etPassword;
    Button btnSigUp;
    TextView tvRegister;
    String token;

    OkHttpClient okHttpClient,client;

    ConstraintLayout constraintLayout;

    SweetAlertDialog pDialog;

    //Preferences
    private static final String PREFS_NAME = "preferences";
    private static final String PREF_UPHONE = "Username";
    private static final String PREF_PASSWORD = "Password";

    private final String DefaultUnameValue = "";
    private String UphoneValue;

    private final String DefaultPasswordValue = "";
    private String PasswordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            etPhone = (EditText)findViewById(R.id.etPhone);
            etPassword = (EditText)findViewById(R.id.etPassword);
            btnSigUp = (Button)findViewById(R.id.btnRegister);
            tvRegister = (TextView)findViewById(R.id.tvLogin);



            constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);

        }

    }

    //Button sign in
    public void signIn(View view) throws JSONException, IOException {
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone_number",phone);
        jsonObject.put("pin_code",password);

        authenticate(url,phone,password);
        JSONObject json = new JSONObject(authenticate(url,phone,password));

        String resultCode = json.getString("ResultCode");
        if (resultCode.equals("1")){

            pDialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#64c8d2"));
            pDialog.setTitleText("Wrong Username or Password");
            pDialog.setCancelable(true);
            pDialog.show();

        }else {
            token = json.getString("access_token");
            post(url,jsonObject.toString(),token);
        }

    }

    ///Method to get token
    public String authenticate(String url, String appKey, String appPass) throws IOException, JSONException {
        okHttpClient = new OkHttpClient();
        byte[] user = appKey.getBytes("UTF-8");
        byte[] pass = appPass.getBytes("UTF-8");

        String authUser = Base64.encodeToString(user,Base64.URL_SAFE|Base64.NO_PADDING|Base64.NO_WRAP);
        String authPass = Base64.encodeToString(pass, Base64.URL_SAFE|Base64.NO_PADDING|Base64.NO_WRAP);


        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("action", "authorization")
                .addHeader("authuser", authUser)
                .addHeader("authpass", authPass)
                .addHeader("cache-control", "no-cache")
                .build();

        okhttp3.Response response = okHttpClient.newCall(request).execute();
        String rBody = response.body().string();

        System.out.println("BASE 1010 : " + rBody);
        //BASE 1010 : {"ResultDesc":"Wrong username or password","ResultCode":1}

        return rBody;
    }

    public void post(String url, String json, String authenticate) throws IOException {
        pDialog = new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON,json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization",authenticate )
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("IOError",e.toString());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try(ResponseBody responseBody = response.body()){
                    if (!response.isSuccessful())throw new IOException("Unexpected code " + response);
                    Headers responseHeaders = response.headers();

                    Log.e("Returned","TEST"+String.valueOf(responseHeaders.size()));
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    String respo = responseBody.string();
                    System.out.println("BASE1020 "+respo);
                    pDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, Categories.class);
                    intent.putExtra("token",token);
                    startActivity(intent);

                }
            }
        });

    }

    public void register(View view){
        Intent intent = new Intent(MainActivity.this,Registration.class);
        intent.putExtra("token",token);
        startActivity(intent);
    }

    private void savePreferences(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        //Edit and commit
        UphoneValue = etPhone.getText().toString();
        PasswordValue = etPassword.getText().toString();

        editor.putString(PREF_UPHONE,UphoneValue);
        editor.putString(PREF_PASSWORD,PasswordValue);
        editor.commit();
    }

    private void loadPreferences(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

        //Get values
        UphoneValue = settings.getString(PREF_UPHONE, DefaultUnameValue);
        PasswordValue = settings.getString(PREF_PASSWORD, DefaultPasswordValue);

        etPhone.setText(UphoneValue);
        etPassword.setText(PasswordValue);
        System.out.println("onResume load name: " + UphoneValue);
        System.out.println("onResume load password: " + PasswordValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }


}
