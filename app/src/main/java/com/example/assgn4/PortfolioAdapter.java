package com.example.assgn4;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {
    private final List<PortfolioItem> portfolioItems;

    public PortfolioAdapter(List<PortfolioItem> portfolioItems) {
        this.portfolioItems = portfolioItems;
    }

    public List<PortfolioItem> getItems() {
        return portfolioItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PortfolioItem item = portfolioItems.get(position);
        holder.tvStockSymbol.setText(item.getTicker());
        holder.Shares.setText(item.getQty());
        double curr_price = Math.round(((item.getCurrentPrice()-item.getAvgCost())*item.getNumQty()) * 100.0) / 100.0;
        holder.tvMarketValue.setText( String.format(Locale.getDefault(), "$%.2f", (item.getNumQty()*item.getCurrentPrice())) );
        holder.d.setText(String.format(Locale.getDefault(), " $%.2f ", (curr_price)));
//        holder.dp.setText(String.valueOf((Math.round((curr_price/item.getTotalCost())*100) * 100) / 100)    );
        holder.dp.setText(String.format(Locale.getDefault(), "( %.2f%% )", (curr_price / item.getTotalCost()) * 100)   );


        if (curr_price < 0) {
            holder.d.setTextColor(Color.RED);
            holder.dp.setTextColor(Color.RED);
            holder.imageChangeIndicator.setImageResource(R.drawable.trending_down); // Set your down icon
        } else {
            holder.d.setTextColor(Color.GREEN);
            holder.dp.setTextColor(Color.GREEN);
            holder.imageChangeIndicator.setImageResource(R.drawable.trending_up); // Set your up icon
        }
        // Set other views based on the item data
    }

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStockSymbol;
        TextView Shares;
        TextView tvMarketValue;
        TextView d;
        TextView dp;
        ImageView imageChangeIndicator;
        // Define other views

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStockSymbol = itemView.findViewById(R.id.tvStockSymbol);
            Shares = itemView.findViewById(R.id.Shares);
            tvMarketValue = itemView.findViewById(R.id.tvMarketValue);
            d = itemView.findViewById(R.id.tvPriceChangeValue);
            dp = itemView.findViewById(R.id.tvPriceChangePercentage);
            imageChangeIndicator = itemView.findViewById(R.id.imageChangeIndicator);

            // Initialize other views
        }
    }
}
