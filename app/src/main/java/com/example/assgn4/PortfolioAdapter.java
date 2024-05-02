package com.example.assgn4;

import android.graphics.Color;
import android.util.Log;
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

    public interface OnPfItemClickListener {
        void onPfItemClick(String displaySymbol);
    }

    public PortfolioAdapter(List<PortfolioItem> portfolioItems, OnPfItemClickListener listener) {
        this.portfolioItems = portfolioItems;
        this.listener = listener;
    }

    public List<PortfolioItem> getItems() {
        return portfolioItems;
    }
    private OnPfItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("InsidePfAdapter",portfolioItems.toString());
        PortfolioItem item = portfolioItems.get(position);
        holder.tvStockSymbol.setText(item.getTicker());
        holder.Shares.setText(item.getQty());
        double curr_price = Math.round(((item.getCurrentPrice()-item.getAvgCost())*item.getNumQty()) * 100.0) / 100.0;
        holder.tvMarketValue.setText( String.format(Locale.getDefault(), "$%.2f", (item.getNumQty()*item.getCurrentPrice())) );
        holder.d.setText(String.format(Locale.getDefault(), " $%.2f ", (curr_price)));
//        holder.dp.setText(String.valueOf((Math.round((curr_price/item.getTotalCost())*100) * 100) / 100)    );
        holder.dp.setText(String.format(Locale.getDefault(), "( %.2f%% )", (curr_price / item.getTotalCost()) * 100)   );
        holder.pf_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Delegate the click event to the listener
                listener.onPfItemClick(item.getTicker());
            }
        });

        if (curr_price < 0) {
            holder.d.setTextColor(Color.RED);
            holder.dp.setTextColor(Color.RED);
            holder.imageChangeIndicator.setImageResource(R.drawable.trending_down); // Set your down icon
        } else {
            holder.d.setTextColor(Color.parseColor("#489838"));
            holder.dp.setTextColor(Color.parseColor("#489838"));
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
        ImageView imageChangeIndicator,  pf_arrow;
        // Define other views

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStockSymbol = itemView.findViewById(R.id.tvStockSymbol);
            Shares = itemView.findViewById(R.id.Shares);
            tvMarketValue = itemView.findViewById(R.id.tvMarketValue);
            d = itemView.findViewById(R.id.tvPriceChangeValue);
            dp = itemView.findViewById(R.id.tvPriceChangePercentage);
            imageChangeIndicator = itemView.findViewById(R.id.imageChangeIndicator);

            pf_arrow = itemView.findViewById(R.id.pf_right_arrow);

            // Initialize other views
        }
    }
}
