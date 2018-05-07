package com.example.bayardomoraga.aplicacionandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bayardomoraga.aplicacionandroid.R;
import com.example.bayardomoraga.aplicacionandroid.api.Api;
import com.example.bayardomoraga.aplicacionandroid.model.MarketModel;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMarket extends AppCompatActivity {

    EditText etname;
    EditText etaddress;
    EditText etdescription;
    Button btnagregar;
    Button btncancelar;
    String Invalid ="Campo requerido";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        etname = findViewById(R.id.etname);
        etaddress = findViewById(R.id.etaddress);
        etdescription = findViewById(R.id.etdescription);
        btnagregar = findViewById(R.id.btnagregar);
        btncancelar = findViewById(R.id.btncancelar);

        btnagregar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                MarketModel market = new MarketModel();
                market.setName(etname.getText().toString());
                market.setDescription(etdescription.getText().toString());
                market.setAddress(etaddress.getText().toString());
                validateData();
                createMarket(market);

                if(!isConnected(CreateMarket.this)){
                    CreatedMarketdb();
                }
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void validateData(){
        if(etname.getText().toString().trim().isEmpty() && etdescription.getText().toString().trim().isEmpty() && etaddress.getText().toString().trim().isEmpty()) {
            etname.setError(Invalid);
            etaddress.setError(Invalid);
            etdescription.setError(Invalid);

            if (!etname.getText().toString().trim().isEmpty()) {
                if (!etdescription.getText().toString().trim().isEmpty()) {
                    if (!etaddress.getText().toString().trim().isEmpty()) {

                    } else {
                        etaddress.setError(Invalid);
                    }
                } else {
                    etdescription.setError(Invalid);
                }
            } else {
                etname.setError(Invalid);
            }
        }
    }
    public void createMarket (MarketModel market){
        Call<MarketModel> call = Api.instance().createmarkets(market);
        call.enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(@NonNull Call<MarketModel> call, Response<MarketModel> response) {
                if(response.isSuccessful()){
                    Toast.makeText(CreateMarket.this,"Mercado Creado Exitosamente", Toast.LENGTH_SHORT).show();
                    etaddress.setText("");
                    etname.setText("");
                    etdescription.setText("");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketModel> call,@NonNull Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

    //Create DATABASE
    public void CreatedMarketdb()
    {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        //UNA CONSULTA PARA OBTENER EL ID
        RealmQuery<MarketModel> query = realm.where(MarketModel.class);
        RealmResults<MarketModel> results = query.findAll();
        String id="";
        int valor;

        for (int i =0; i<results.size(); i++)
        {
            id=results.get(i).getId();
        }
        //set values to model
        MarketModel market = realm.createObject(MarketModel.class);
        valor=Integer.parseInt(id)+1;
        market.setId(Integer.toString(valor));
        market.setName(etname.getText().toString());
        market.setDescription(etdescription.getText().toString());
        market.setAddress(etaddress.getText().toString());
        realm.commitTransaction();
        etaddress.setText("");
        etname.setText("");
        etdescription.setText("");
        Toast.makeText(CreateMarket.this,"Mercado Creado Exitosamente", Toast.LENGTH_SHORT).show();
        finish();
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
}
