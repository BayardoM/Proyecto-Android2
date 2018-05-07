package com.example.bayardomoraga.aplicacionandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class DeleteMarket extends AppCompatActivity {
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;
        getExtras();
        deleteProduct(context);//Llamando al metodo para eliminar de la API
        deleteProducts();//Llamando al metodo para eliminar de la BASE DE DATOS
        finish();
    }
    private void getExtras(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            id = extras.getString("Id");
        }
    }
    //delete API
    private void deleteProduct(final Context context){
        Call<MarketModel> call = Api.instance().deletemarkets(id);
        call.enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(Call<MarketModel> call, Response<MarketModel> response) {
                if(response.body() != null){
                    Toast.makeText(context, "Elemento Borrado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MarketModel> call, Throwable t) {
                Log.i("Debug: ", t.getMessage());

            }
        });
    }

    //delete DATABASE
    private void deleteProducts(){
        io.realm.Realm realm = Realm.getDefaultInstance();

        RealmQuery<MarketModel> query = realm.where(MarketModel.class);

        RealmResults<MarketModel> results = query.findAll();

        for (int i =0; i<results.size(); i++)
        {
            if (id.equals(results.get(i).getId()))
            {
                deleteProduct(results.get(i));
            }
        }
    }
    private void deleteProduct(MarketModel productModel){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        productModel.deleteFromRealm();
        realm.commitTransaction();
    }

}
