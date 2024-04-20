package com.example.assgn4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private List<Stock> stockItems; // Assume Stock is a class representing your stock data

    public StockAdapter(List<Stock> stockItems) {
        this.stockItems = stockItems;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stockItem = stockItems.get(position);
        // Bind your data to the views here
    }

    @Override
    public int getItemCount() {
        return stockItems.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder {
        // Define views here

        public StockViewHolder(View itemView) {
            super(itemView);
            // Initialize views
        }
    }
}

