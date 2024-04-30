package com.example.assgn4;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    final List<FavoriteItem> favoriteItems;

    public interface OnItemClickListener {
        void onItemClick(String displaySymbol);
    }

    public FavoritesAdapter(List<FavoriteItem> favoriteItems, OnItemClickListener listener) {
        this.favoriteItems = favoriteItems;
        this.listener = listener;
    }

    public List<FavoriteItem> getItems() {
        return favoriteItems;
    }

    private OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem item = favoriteItems.get(position);
        holder.fvTicker.setText(item.getTicker());
        holder.fvDesc.setText(item.getDesc());
        holder.fvMarketValue.setText( String.format(Locale.getDefault(), "$%.2f", (item.getCurrentPrice()) ));
        holder.d.setText(String.format(Locale.getDefault(), " $%.2f ", (item.getD())));
//        holder.dp.setText(String.valueOf((Math.round((curr_price/item.getTotalCost())*100) * 100) / 100)    );
        holder.dp.setText(String.format(Locale.getDefault(), "( %.2f%% )", (item.getDP()))   );
        holder.fav_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Delegate the click event to the listener
                listener.onItemClick(item.getTicker());
            }
        });

        if (item.getD() < 0) {
            holder.d.setTextColor(Color.RED);
            holder.dp.setTextColor(Color.RED);
            holder.fvimageChangeIndicator.setImageResource(R.drawable.trending_down); // Set your down icon
        } else {
            holder.d.setTextColor(Color.parseColor("#489838"));
            holder.dp.setTextColor(Color.parseColor("#489838"));
            holder.fvimageChangeIndicator.setImageResource(R.drawable.trending_up); // Set your up icon
        }
        // Set other views based on the item data
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fvTicker;
        TextView fvDesc;
        TextView fvMarketValue;
        TextView d;
        TextView dp;
        ImageView fvimageChangeIndicator, fav_arrow;
        // Define other views

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fvTicker = itemView.findViewById(R.id.fvTicker);
            fvDesc = itemView.findViewById(R.id.fvDesc);
            fvMarketValue = itemView.findViewById(R.id.fvMarketValue);
            d = itemView.findViewById(R.id.fvPriceChangeValue);
            dp = itemView.findViewById(R.id.fvPriceChangePercentage);
            fvimageChangeIndicator = itemView.findViewById(R.id.fvImageChangeIndicator);

            fav_arrow = itemView.findViewById(R.id.fav_right_arrow);



            // Initialize other views
        }
    }
}
