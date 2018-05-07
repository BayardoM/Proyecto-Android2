package com.example.bayardomoraga.aplicacionandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bayardomoraga.aplicacionandroid.R;
import com.example.bayardomoraga.aplicacionandroid.holder.MarketViewHolder;
import com.example.bayardomoraga.aplicacionandroid.model.MarketModel;

import java.util.ArrayList;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketViewHolder> {
    private List<MarketModel> markets;

    public MarketAdapter(List<MarketModel>markets){
        this.markets = markets;
    }

    @Override
    public MarketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market, parent, false);
        return new MarketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MarketViewHolder holder, int position) {

        MarketModel market =markets.get(position);
        holder.getId().setText(market.getId());
        holder.getName().setText(market.getName());
        holder.getDescription().setText(market.getDescription());
        holder.getAddress().setText(market.getAddress());
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return markets.size();
    }
}
