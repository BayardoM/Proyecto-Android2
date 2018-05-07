package com.example.bayardomoraga.aplicacionandroid.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.bayardomoraga.aplicacionandroid.R;
import com.example.bayardomoraga.aplicacionandroid.api.Api;
import com.example.bayardomoraga.aplicacionandroid.model.MarketModel;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateMarkets extends AppCompatActivity {
    private MarketModel market;
    private EditText name;
    private EditText description;
    private EditText address;
    //Recuperar Datos
    private String marketId;
    private String marketName;
    private String marketDescription;
    private String marketAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_markets);

        getExtras();
        initializeViews();
    }
    public void initializeViews (){
        name = findViewById(R.id.etname);
        description = findViewById(R.id.etdescription);
        address = findViewById(R.id.etaddress);

        name.setText(marketName);
        description.setText(marketDescription);
        address.setText(marketAddress);
    }

    private void getExtras(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            marketId = extras.getString("Id");
            marketName = extras.getString("Name");
            marketDescription = extras.getString("Description");
            marketAddress = extras.getString("Address");
        }
    }


    public void updateMarkets() {
        market = new MarketModel();

        market.setName(name.getText().toString());
        market.setDescription(description.getText().toString());
        market.setAddress(address.getText().toString());

        Call<MarketModel> call = Api.instance().updatemarkets(marketId, market);
        call.enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(@NonNull Call<MarketModel> call, @NonNull Response<MarketModel> response) {

            }

            @Override
            public void onFailure(@NonNull Call<MarketModel> call, @NonNull Throwable t) {
                Log.i("Debug: ", t.getMessage());
            }
        });
    }
    //UPDATE DATABASE

    private  void updateMarketdbs(){
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<MarketModel> query = realm.where(MarketModel.class);

        RealmResults<MarketModel> results = query.findAll();

        for(int i=0;i<results.size();i++) {
            if(marketId.equals(results.get(i).getId())){
                updateMarketdb(results.get(i));
            }
        }        realm.commitTransaction();

    }
    private void updateMarketdb(MarketModel market) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        market.setName(name.getText().toString());
        market.setDescription(description.getText().toString());
        market.setAddress(address.getText().toString());
    }


    public void acceptOnclick(View view) {
        updateMarkets();
        updateMarketdbs();
        finish();
    }

    public void cancelOnclick(View view) {
        finish();
    }

}
