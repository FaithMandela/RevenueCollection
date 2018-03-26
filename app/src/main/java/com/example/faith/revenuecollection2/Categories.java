package com.example.faith.revenuecollection2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Categories extends AppCompatActivity {

    ListView listView;
    String [] names = {"My Account","Parking","Rent","Land","Market"};
    int [] images = {R.drawable.ic_action_account,R.drawable.ic_action_parking,
            R.drawable.ic_action_rent,R.drawable.land, R.drawable.market};

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        listView = (ListView)findViewById(R.id.listView);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        token = getIntent().getStringExtra("token");
        Log.e("TOKEN",token);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    Intent intent = new Intent(Categories.this,Account.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }else if (i==1){
                    Intent intent = new Intent(Categories.this,Parking.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }

            }
        });
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.categories_list,null);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView = (TextView)view.findViewById(R.id.tvName);

            imageView.setImageResource(images[i]);
            textView.setText(names[i]);

            return view;
        }
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
                Intent intent = new Intent(Categories.this,MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
