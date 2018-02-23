package com.example.faith.revenuecollection2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Registration extends AppCompatActivity {

    EditText etName, etPhone, etIDnumber,etPassword, etConfirm;
    Button registration;
    TextView tvLogin;

    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();

        etName = (EditText)findViewById(R.id.etName);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etIDnumber = (EditText)findViewById(R.id.etIDnumber);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirm = (EditText)findViewById(R.id.etConfirm);

        registration = (Button)findViewById(R.id.btnRegister);
        tvLogin = (TextView)findViewById(R.id.tvLogin);

    }

    public void register(View view) throws JSONException {
        String fullName = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String idNumber = etIDnumber.getText().toString();
        String password = etPassword.getText().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("full_name",fullName);
        jsonObject.put("id_number",idNumber);
        jsonObject.put("phone_number",phone);
        jsonObject.put("pin_code",password);

        String newUrl = MainActivity.url+"?view=260:0&data=";

        send(newUrl,jsonObject.toString());
    }

    public void send(String url,String json){
        okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MainActivity.JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("action","udata")
                .addHeader("content-type", "application/json")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("IOError",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try(ResponseBody responseBody = response.body()){
                    if (!response.isSuccessful())throw new IOException("Unexpected code " + response);
                    Headers responseHeaders = response.headers();

                    Log.e("Returned","TEST"+String.valueOf(responseHeaders.size()));
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    String respo = responseBody.string();
                    System.out.println("BASE1020 "+respo);

                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });

    }

    public void login(View view){
        Intent intent = new Intent(Registration.this,MainActivity.class);
        startActivity(intent);
    }
}
