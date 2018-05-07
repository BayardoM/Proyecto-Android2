package com.example.bayardomoraga.aplicacionandroid.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bayardomoraga.aplicacionandroid.R;
import com.example.bayardomoraga.aplicacionandroid.adapter.MarketAdapter;
import com.example.bayardomoraga.aplicacionandroid.api.Api;
import com.example.bayardomoraga.aplicacionandroid.model.MarketModel;
import com.tumblr.remember.Remember;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;

    private static final String IS_FIRST_TIME = "is_first_time";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        srl = findViewById(R.id.actualizar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateMarket.class));
                srl.setRefreshing(false);
            }
        });


        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //VALIDACION SI ESTA CON CONEXION TRAER LOS DATOS DE LA API , SINO CARGAR LOS DE LA BD
                if(!isConnected(MainActivity.this)){
                   getFromDataBase();
                }
                else{
                    getMarket();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                    }
                }, 1500);
            }

        });

        if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();

        else{
        }

        initViews();
        configureRecyclerView();
        srl.setRefreshing(false);

        if (!isFirstTime()) {
            getMarket();
            storeFirstTime();
        } else {
            getFromDataBase();
        }

    }

    private void storeFirstTime() {
        Remember.putBoolean(IS_FIRST_TIME, true);
    }

    private boolean isFirstTime() {
        return Remember.getBoolean(IS_FIRST_TIME, false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    public void getMarket()
    {
        Call<List<MarketModel>> call = Api.instance().getmarkets();
        call.enqueue(new Callback<List<MarketModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MarketModel>> call, Response<List<MarketModel>> response) {
                if (response.isSuccessful()) {
                    MarketAdapter marketsAdapter = new MarketAdapter(response.body());
                    recyclerView.setAdapter(marketsAdapter);

                    sync(response.body());
                    srl.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MarketModel>> call, @NonNull Throwable t) {
                Log.e("Debug: ", t.getMessage());
            }
        });
    }

    private void sync(List<MarketModel> productModels) {
        for(MarketModel productModel : productModels) {
            store(productModel);
        }
    }

    private void store(MarketModel marketModelFromApi){

        String a=marketModelFromApi.getId();
        if (exist(a)==false) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            MarketModel productModel = realm.createObject(MarketModel.class); // Create a new object

            productModel.setId(marketModelFromApi.getId());
            productModel.setName(marketModelFromApi.getName());
            productModel.setAddress(marketModelFromApi.getAddress());
            productModel.setDescription(marketModelFromApi.getDescription());
            realm.commitTransaction();
        }

    }

    private void getFromDataBase() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MarketModel> query = realm.where(MarketModel.class);

        RealmResults<MarketModel> results = query.findAll();

        mAdapter = new MarketAdapter(results);
        recyclerView.setAdapter(mAdapter);
    }
    private boolean exist(String id){

        Boolean exist=false;
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<MarketModel> query = realm.where(MarketModel.class);

        RealmResults<MarketModel> results = query.findAll();

        for (int i=0; i<results.size(); i++)
        {
            if (id.equals(results.get(i).getId()))
            {
                exist=true;
            }
        }
        return exist;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else
                return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Sin conexion a Internet");
        builder.setMessage("¿Desea trabajar sin conexion?");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        return builder;
    }

}
