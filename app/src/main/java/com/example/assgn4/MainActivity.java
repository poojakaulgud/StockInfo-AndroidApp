package com.example.assgn4;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private RecyclerView stocksRecyclerView;
    private StockAdapter stocksAdapter;
    private List<Stock> stockItems; // Your data
    private RequestQueue requestQueue;
    private double balance;
    private final String BASE_URL = "https://assgn3-pooja.wl.r.appspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_Assgn4);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleTextView = findViewById(R.id.titleTextView);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView netWorthTextView = findViewById(R.id.networth);

        TextView poweredByLabel = findViewById(R.id.poweredByLabel);
        requestQueue = Volley.newRequestQueue(this);

        // Example usage
        getWallet(new Callback() {
            @Override
            public void onCompleted() {
                getPortfolio();
                getFavorites();
            }
        });

        // Example of setting dynamic text in one of the TextViews
        // This can be a value retrieved from a database, an API, or user input


        // If your search icon is clickable and opens a search interface
        searchIcon.setOnClickListener(view -> {
            // Handle search icon click event here
            // e.g., open search dialog or activity
        });

        // Similarly, if you want to set a click listener to the TextView
        poweredByLabel.setOnClickListener(view -> {
            Uri webpage = Uri.parse("https://www.finnhub.io/");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "No application can handle this request.", Toast.LENGTH_LONG).show();
            }
        });



    }


    private void getFavorites(){
        String url = BASE_URL + "/watchlist";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        ArrayList<FavoriteItem> favoriteItems = new ArrayList<>();
                        final double[] netWorthContainer = new double[1];

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject favoriteObject = response.getJSONObject(i);
                            String ticker = favoriteObject.getString("ticker");
                            String desc = favoriteObject.getString("description");

                            Log.d("JSONParsing", "Ticker: " + ticker + ", Desc: " + desc );
                            getCostValues(ticker, (receivedTicker, c, d, dp) -> {
                                Log.d("cvaluesblahblah", String.valueOf(c));
                                FavoriteItem item = new FavoriteItem(
                                        ticker,
                                        desc,
                                        (Math.round(c * 100.0) / 100.0),
                                        (Math.round(d * 100.0) / 100.0),
                                        (Math.round(dp * 100.0) / 100.0)
                                );
                                favoriteItems.add(item);
                                Log.d("FavoriteItem", item.toString());
                                if (favoriteItems.size() == response.length()) {
                                    // All items have been processed, update UI here
                                    updateFavoriteUI(favoriteItems);
                                }
                            });




                        }


                        Log.d("FavoriteResponse", favoriteItems.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                },
                error -> {
                    Log.e("getFavorite", "Error: " + error.toString());
                });
        requestQueue.add(jsonArrayRequest);
    }


    private void updateFavoriteUI(ArrayList<FavoriteItem> favoriteItems) {
        RecyclerView recyclerView = findViewById(R.id.favoritesRecyclerView);
        FavoritesAdapter f_adapter = new FavoritesAdapter(favoriteItems);
        recyclerView.setAdapter(f_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupFavoriteTouchHelper(recyclerView, favoriteItems, f_adapter);

    }


    private void setupFavoriteTouchHelper(RecyclerView recyclerView, ArrayList<FavoriteItem> favoriteItems, FavoritesAdapter f_adapter) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                final int toPosition = target.getAbsoluteAdapterPosition();

                // If specific position checks are needed, implement them here
                // For now, allow moving within any positions

                Collections.swap(favoriteItems, fromPosition, toPosition);
                f_adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                FavoriteItem swipedItem = ((FavoritesAdapter) Objects.requireNonNull(recyclerView.getAdapter())).favoriteItems.get(position);
                String ticker = swipedItem.getTicker(); // Assuming MyObject has a getTicker method

                // Log or use the Ticker as needed
                Log.d("SWIPED ITEM TICKER", "Ticker: " + ticker);

                deleteFavoritesItem(position, ticker);
            }

            public void deleteFavoritesItem(int position, String ticker) {
                Log.d("INDELETE", ticker);
                String url = BASE_URL + "/watchlist/" + ticker ;
                StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                        response -> {
                            try{
                            Log.d("DELETE", response);
                                if (position >= 0 && position < favoriteItems.size()) {
                                    favoriteItems.remove(position); // Remove the item from the list
                                    updateFavoriteUI(favoriteItems);
                                }
                            }catch (Exception e){
                                Log.e("DELETE ERROR", String.valueOf(e));
                            }
                        },
                        error -> {
                            // Handle error here
                            if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                                Log.e("DELETE", "Document with the specified ticker not found");
                            } else {
                                Log.e("DELETE", "Error occurred: " + error.toString());
                            }
                        }
                );
                requestQueue.add(deleteRequest);

            }


            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setAlpha(0.5f); // Change alpha to indicate dragging
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f); // Restore alpha after moving
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    Paint paint = new Paint();
                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.delete); // Your dustbin icon

                    if (dX < 0 && isCurrentlyActive) { // Left swipe action and the swipe is active
                        // Set your red color paint
                        paint.setColor(Color.RED);

                        // Draw the red background
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                        // Calculate position for the dustbin icon
                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;

                        // Set bounds and draw the dustbin icon
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }

                    // Clear any overdraw by the default ItemTouchHelper's onChildDraw method by passing in isCurrentlyActive
                    final float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
                    itemView.setAlpha(alpha);
                    itemView.setTranslationX(dX);
                } else {
                    // Reset View to its original state if the swipe is not active
                    itemView.setAlpha(1.0f);
                    itemView.setTranslationX(0);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



    private void getPortfolio(){
        String url = BASE_URL + "/portfolio";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        ArrayList<PortfolioItem> portfolioItems = new ArrayList<>();
                        final double[] netWorthContainer = new double[1];

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject portfolioObject = response.getJSONObject(i);
                            String ticker = portfolioObject.getString("ticker");
                            int qty = portfolioObject.getInt("qty");
                            double totalCost = portfolioObject.getDouble("total_cost");
                            double avgCost = portfolioObject.getDouble("avg_cost");

                            Log.d("JSONParsing", "Ticker: " + ticker + ", Qty: " + qty + ", TotalCost: " + totalCost + ", AvgCost: " + avgCost);
                            getCostValues(ticker, (receivedTicker, c, d, dp) -> {
                                Log.d("cvaluesblahblah", String.valueOf(c));
                                PortfolioItem item = new PortfolioItem(
                                        ticker,
                                        qty,
                                        totalCost,
                                        avgCost,
                                        (Math.round(c * 100.0) / 100.0),
                                        (Math.round(d * 100.0) / 100.0),
                                        (Math.round(dp * 100.0) / 100.0)
                                );
                                portfolioItems.add(item);
                                netWorthContainer[0] += (qty*c);

                                Log.d("networth", String.valueOf(netWorthContainer[0]));
                                Log.d("PortfolioItem", item.toString());
                                if (portfolioItems.size() == response.length()) {
                                    // All items have been processed, update UI here
                                    updateUI(portfolioItems, netWorthContainer[0]);
                                }
                            });






                            Log.d("portfolioobject", String.valueOf(portfolioObject));

                        }



                        Log.d("PortfolioResponse", portfolioItems.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                },
                error -> {
                    Log.e("getPortfolio", "Error: " + error.toString());
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void setupItemTouchHelper(RecyclerView recyclerView, ArrayList<PortfolioItem> portfolioItems, PortfolioAdapter p_adapter) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                final int toPosition = target.getAbsoluteAdapterPosition();

                // If specific position checks are needed, implement them here
                // For now, allow moving within any positions

                Collections.swap(portfolioItems, fromPosition, toPosition);
                p_adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // No swiping action needed
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setAlpha(0.5f); // Change alpha to indicate dragging
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f); // Restore alpha after moving
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



    private void updateUI(ArrayList<PortfolioItem> portfolioItems, double netWorth) {

        double finalNetWorth = netWorth + balance;
        Log.d("here", String.valueOf(netWorth));
        Log.d("here", String.valueOf(balance));

        TextView NetWorth = findViewById(R.id.networth);
        NetWorth.setText(String.format(Locale.getDefault(), "$%.2f", finalNetWorth));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        PortfolioAdapter p_adapter = new PortfolioAdapter(portfolioItems);
        recyclerView.setAdapter(p_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupItemTouchHelper(recyclerView, portfolioItems, p_adapter);

    }


    public interface QuoteResponseListener {
        void onQuoteReceived(String ticker, double c, double d, double dp);
    }
    private void getCostValues (String ticker, QuoteResponseListener listener){
        String url = BASE_URL + "/quote/" + ticker ;
        AtomicReference<Double> c = new AtomicReference<>((double) 0);
        AtomicReference<Double> d = new AtomicReference<>((double) 0);
        AtomicReference<Double> dp = new AtomicReference<>((double) 0);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                try {

                    JSONObject quoteObject = response;
                    Log.d("quoteValues" + ticker, quoteObject.toString());

                    c.set(quoteObject.getDouble("c"));
                    d.set(quoteObject.getDouble("c"));
                    dp.set(quoteObject.getDouble("c"));
                    listener.onQuoteReceived(ticker, c.get(), d.get(), dp.get());
//
                    Log.d("getCHangeValuesttry", String.valueOf(c.get()));
                }catch (JSONException e) {
                    Log.e("getCostValues", "Error: " + e.toString());
                }
                },
                error -> {
                    Log.e("getCostValues", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);
    }


    private void getWallet(Callback callback) {
        String url = BASE_URL + "/wallet";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Assuming 'balance' is a field in your JSON object
                        if (response.length() > 0) {
                            JSONObject walletObject = response.getJSONObject(0);
                            balance = walletObject.getDouble("balance");

                            Log.d("WalletResponse", walletObject.toString());
                            TextView cashBalanceTextView = findViewById(R.id.Balance);
                            String balanceText = String.format(Locale.getDefault(), "$%.2f", balance);
                            cashBalanceTextView.setText(balanceText);
                        }


//
                    } catch (JSONException e) {
                        Log.e("getWallet", "Error: " + e.toString());
                        // Handle the case where 'balance' field is not in the response or there is a parsing error
                    }
                    if (callback != null) {
                        callback.onCompleted();
                    }
                },
                error -> {
                    Log.e("getWallet", "Error: " + error.toString());
                });
        requestQueue.add(jsonArrayRequest);
    }
}
