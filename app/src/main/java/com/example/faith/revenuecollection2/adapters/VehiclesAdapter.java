package com.example.faith.revenuecollection2.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.faith.revenuecollection2.MainActivity;
import com.example.faith.revenuecollection2.Parking;
import com.example.faith.revenuecollection2.R;
import com.example.faith.revenuecollection2.models.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Faith on 2/13/2018.
 */

public class VehiclesAdapter extends BaseAdapter {
    Context context;
    ArrayList<Vehicle> vehicleItems = new ArrayList<>();
    OkHttpClient okHttpClient;
    String token;
    Parking parking = new Parking();

    public VehiclesAdapter(Context context, ArrayList<Vehicle> vehicleItems){
        this.context = context;
        this.vehicleItems = vehicleItems;
    }

    @Override
    public int getCount() {
        return vehicleItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.vehicle_list,null);

        final TextView Balance = (TextView)view.findViewById(R.id.tvBalance);
        final TextView Registration = (TextView)view.findViewById(R.id.tvRegistration);
        TextView Payment    = (TextView)view.findViewById(R.id.tvPay);

        Registration.setText(vehicleItems.get(i).getRegNumber());
        Balance.setText("Balance is :"+vehicleItems.get(i).getBalance());
        final String id_number = vehicleItems.get(i).getVehicle_id();


        Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registration = Registration.getText().toString();
                JSONArray jsonArray = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    json.put("id",id_number);
                    jsonArray.put(json);

                    token = Parking.token;
                    Log.e("TestREGID",id_number);
                    //Pay parking for specific vehicle
                    String new_url = MainActivity.url +"?view=265:0&operation=0";
                    String result = pay_parking(new_url,token,jsonArray.toString());

                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("ResultCode");


                    if (resultCode.equals("0")){
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setTitleText("Insufficient account balace.");
                        sweetAlertDialog.setContentText("Please top up your account and try again");
                        sweetAlertDialog.show();
                    }else {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setContentText("Parking paid successfully");
                        sweetAlertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }

                Log.e("TestREG",registration);
            }
        });

        return view;
    }

    public String pay_parking(String url, String token, String json) throws IOException {
        okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MainActivity.JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("action","operation")
                .addHeader("authorization",token)
                .addHeader("content-type", "application/json")
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String respo = response.body().string();

        System.out.println("BASE1020 "+respo);

        return respo;
    }

    public void showDialog(Context context){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.show();
    }
}
