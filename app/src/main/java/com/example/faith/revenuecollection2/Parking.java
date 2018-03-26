package com.example.faith.revenuecollection2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.faith.revenuecollection2.adapters.VehiclesAdapter;
import com.example.faith.revenuecollection2.models.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Parking extends AppCompatActivity {

    ListView listView;
    FloatingActionButton fabAdd;
    OkHttpClient okHttpClient;
    public static String token;
    InputFilter [] filter;

    SweetAlertDialog alertDialog;
    String vehicleList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        listView = (ListView)findViewById(R.id.listView);
        fabAdd = (FloatingActionButton)findViewById(R.id.fabAdd);

        filter = new InputFilter[2];
        filter[0] = new InputFilter.LengthFilter(7);
        filter[1] = new InputFilter.AllCaps();

        token = getIntent().getStringExtra("token");

        setToken(token);

        alertDialog = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);

        try {
            get_vehicles(MainActivity.url+"?view=265:1&data=",token);
            vehicleList = get_vehicles(MainActivity.url+"?view=265:1&data=",token);
            refresh();
           /* JSONObject jsonObject = new JSONObject(vehicleList);

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Log.e("VehicleList",jsonArray.toString());
            VehiclesAdapter vehiclesAdapter = new VehiclesAdapter(this, makeArrayList(jsonArray));
            listView.setAdapter(vehiclesAdapter);*/

            //makeArrayList(jsonArray);

        } catch (IOException e) {
            Log.e("IOError",e.toString());
        } catch (JSONException e){
            Log.e("JSONError",e.toString());
        }

        Log.e("TOKE_park",token);


    }


    //Add Vehicle
    public void add_vehicle(View view){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.vehicle_addition);
        dialog.show();

        final EditText etRegNumber = (EditText)dialog.findViewById(R.id.etRegNumber);
        Button btnAdd = (Button)dialog.findViewById(R.id.btnAdd);
        final Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);

        etRegNumber.setFilters(filter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                String regNumber = etRegNumber.getText().toString();

                boolean plateNo = regNumber.matches("K[A-Z]{2}[0-9]{3}[A-Z]");
                boolean oldPlateNo = regNumber.matches("K[A-Z]{2}[0-9]{3}");

                if (regNumber.equals("")){
                    etRegNumber.setError("Fill vehicle registration number");
                }else if(plateNo==false &&oldPlateNo==false) {
                    etRegNumber.setError("Wrong plate number format");
                }else {
                    try {
                        jsonObject.put("reg_number",regNumber);
                        //Register a new Vehicle
                        String p_url = MainActivity.url +"?view=265:1&data=";

                        register_vehicle(p_url,token,jsonObject.toString());

                        dialog.dismiss();

                    } catch (JSONException e) {
                        Log.e("JSONError ",e.toString());
                    }
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    //Register a new Vehicle
    public void register_vehicle(String url, String token, String json){
        okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MainActivity.JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("action","data")
                .addHeader("authorization",token)
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


                }
            }
        });
    }

    //Get vehicles
    public String get_vehicles(String url, String token) throws IOException {
        okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("action","read")
                .addHeader("authorization",token)
                .addHeader("content-type", "application/json")
                .build();

        okhttp3.Response response = okHttpClient.newCall(request).execute();
        String rBody = response.body().string();

        System.out.println("BASE 1010 : " + rBody);

        return rBody;
    }

    public ArrayList<Vehicle> makeArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<Vehicle> arrayList = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++){
            try {
                arrayList.add(getJSON(jsonArray.getJSONObject(i)));
            }catch (JSONException e){
                e.toString();
            }
        }

        return arrayList;
    }

    public Vehicle getJSON(JSONObject json) throws JSONException {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegNumber(json.getString("reg_number"));
        vehicle.setVehicle_id(json.getString("keyfield"));

        return vehicle;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Intent intent = new Intent(Parking.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.home:
                Intent home = new Intent(Parking.this,Categories.class);
                home.putExtra("token",token);
                startActivity(home);
                break;
            case R.id.refresh:
                try {
                    refresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() throws JSONException {
        JSONObject jsonObject = new JSONObject(vehicleList);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        Log.e("VehicleList",jsonArray.toString());
        VehiclesAdapter vehiclesAdapter = new VehiclesAdapter(this, makeArrayList(jsonArray));
        listView.setAdapter(vehiclesAdapter);
    }

}
