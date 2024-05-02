package com.example.assgn4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements FavoritesAdapter.OnItemClickListener, PortfolioAdapter.OnPfItemClickListener {
    String[] autoCompleteArray;
    private RecyclerView stocksRecyclerView;
    private StockAdapter stocksAdapter;
    private List<Stock> stockItems; // Your data
    private RequestQueue requestQueue;
    private double balance;
    String d_symbol, d_desc;
    boolean flagFavorite = false;
    ImageView searchIcon, backIcon, crossIcon, starIcon;
    AutoCompleteTextView actv;
    private SharedViewModel viewModel;

    TextView titleTextView;
    private final String BASE_URL = "https://assgn3-pooja.wl.r.appspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_Assgn4);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);


//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView tickerTextView = findViewById(R.id.tickerTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView netWorthTextView = findViewById(R.id.networth);
        searchIcon = findViewById(R.id.searchIcon);
        backIcon = findViewById(R.id.backIcon);
        crossIcon = findViewById(R.id.crossIcon);
        starIcon = findViewById(R.id.starIcon);

        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        if (actv != null) {
            actv.setThreshold(1);

            // The callback method should be the place where you set the adapter
            actv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No action required here for this scenario
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // This is where you would call getAutoCompleteArray each time the text changes
                    if (s.length() >= 1) { // Checking if at least one character is entered
                        getAutoCompleteArray(s.toString(), new CallbackNew() {
                            @Override
                            public void onCompletedNew(String[] autoCompleteArray) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                                MainActivity.this,
                                                R.layout.custom_select_dialog_item,
                                                autoCompleteArray);
                                        actv.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // No action required here for this scenario
                }
            });





            actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    searchIcon.setVisibility(View.GONE);
                    backIcon.setVisibility(View.VISIBLE);
                    crossIcon.setVisibility(View.GONE);
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    String displaySymbol = selectedItem.split(" \\| ")[0];  // Extract displaySymbol
                    actv.setText(displaySymbol);  // Set only the displaySymbol as the text
                    actv.setSelection(displaySymbol.length());
                    d_symbol = displaySymbol;
                    d_desc = selectedItem.split(" \\| ")[1];
                    actv.setVisibility(View.GONE);
                    starIcon.setVisibility(View.VISIBLE);
                    isFavorite(displaySymbol, new Callback() {
                        @Override
                        public void onCompleted() {
                            if(flagFavorite){
                                starIcon.setImageResource(R.drawable.full_star);
                            }
                            else{
                                starIcon.setImageResource(R.drawable.star_border);
                            }
                        }
                    });

                    tickerTextView.setVisibility(View.VISIBLE);
                    tickerTextView.setText(displaySymbol);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    DetailFragment detailFragment = DetailFragment.newInstance(displaySymbol);

                    fragmentTransaction.replace(R.id.homePage, detailFragment);


                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                    LinearLayout homePageContent = findViewById(R.id.homePageContent);
                    homePageContent.setVisibility(View.GONE);
                }

//                private void hideKeyboard(View view) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm != null) {
//                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
//                }
            });

        }

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



        starIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(flagFavorite){
                    deleteFavorite(d_symbol, new Callback() {
                        @Override
                        public void onCompleted() {
                            starIcon.setImageResource(R.drawable.star_border);
                        }
                    });
                }else{
                    addFavorite(d_symbol, d_desc, new Callback() {
                        @Override
                        public void onCompleted() {
                            starIcon.setImageResource(R.drawable.full_star);
                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText(d_symbol+ " added to Favorites");

                            Toast toast = new Toast(MainActivity.this);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }
                    });
                }
            }
        });





        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTextView.setVisibility(View.GONE);
                searchIcon.setVisibility(View.GONE);
                backIcon.setVisibility(View.VISIBLE);
                crossIcon.setVisibility(View.VISIBLE);
                actv.setVisibility(View.VISIBLE);
                tickerTextView.setVisibility(View.GONE);
                starIcon.setVisibility(View.GONE);
//                actv.setVisibility(View.VISIBLE);
                // Request focus for the EditText and open the keyboard
                actv.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(actv, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        crossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv.setText("");
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the EditText and the cross, show the title and search icon
                LinearLayout homePageContent = findViewById(R.id.homePageContent);
                getWallet(new Callback() {
                    @Override
                    public void onCompleted() {
                        getPortfolio();
                        getFavorites();
                    }
                });
                homePageContent.setVisibility(View.VISIBLE);
                tickerTextView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.VISIBLE);
                backIcon.setVisibility(View.GONE);
                crossIcon.setVisibility(View.GONE);

                starIcon.setImageResource(R.drawable.star_border);
                starIcon.setVisibility(View.GONE);
                actv.setVisibility(View.GONE);
                actv.setText("");
                ScrollView firstFragment = findViewById(R.id.firstFragment);
                firstFragment.setVisibility(View.GONE);
//                actv.setVisibility(View.GONE);

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);
            }
        });

        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    // Perform search and open new fragment with search query
                    performSearchAndOpenFragment(v.getText().toString());

                    // Reset views to default state if needed
                    tickerTextView.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.VISIBLE);
                    searchIcon.setVisibility(View.VISIBLE);
                    backIcon.setVisibility(View.GONE);
                    crossIcon.setVisibility(View.GONE);
                    actv.setVisibility(View.GONE);


                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    EmptyFragment emptyFragment = new EmptyFragment();

                    fragmentTransaction.replace(R.id.homePage, emptyFragment);
                    fragmentTransaction.commit();


                    return true; // Consume the action
                }

                if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
                    searchIcon.setVisibility(View.GONE);
                    backIcon.setVisibility(View.VISIBLE);
                    crossIcon.setVisibility(View.GONE);
                    String displaySymbol = actv.getText().toString().toUpperCase();  // Extract displaySymbol
                    actv.setText(displaySymbol);  // Set only the displaySymbol as the text
                    actv.setSelection(displaySymbol.length());
                    actv.setVisibility(View.GONE);
                    starIcon.setVisibility(View.VISIBLE);
                    isFavorite(displaySymbol, new Callback() {
                        @Override
                        public void onCompleted() {
                            if(flagFavorite){
                                starIcon.setImageResource(R.drawable.full_star);
                            }
                            else{
                                starIcon.setImageResource(R.drawable.star_border);
                            }
                        }
                    });
                    tickerTextView.setVisibility(View.VISIBLE);
                    tickerTextView.setText(displaySymbol);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    DetailFragment detailFragment = DetailFragment.newInstance(displaySymbol);

                    fragmentTransaction.replace(R.id.homePage, detailFragment);


                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                    LinearLayout homePageContent = findViewById(R.id.homePageContent);
                    homePageContent.setVisibility(View.GONE);
                }

                return false; // Pass on to other listeners if not a search action
            }



            private void performSearchAndOpenFragment(String query) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);
                }

                // Replace with your code to create the fragment and perform the transaction

            }

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

    public void updateACTView(String ticker) {
        TextView tickerTextView = findViewById(R.id.tickerTextView);
        tickerTextView.setText(ticker);
        d_symbol = ticker;
        Log.d("my ticker", ticker);
        if(ticker.equals("QCOM")){
            d_desc = "Qualcomm Inc";
        }
    }



    private void deleteFavorite(String ticker, Callback callback){
        String url = BASE_URL + "/watchlist/" + ticker;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    try{
                        Log.d("deleteFavorite", response.toString());
                        flagFavorite = false;
                        if (callback != null) {
                            callback.onCompleted();
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

    private void addFavorite(String ticker, String description, Callback callback){
        String url = BASE_URL + "/watchlist";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("ticker", ticker);
            jsonBody.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        Log.d("addFavorite", response.toString());
                        flagFavorite = true;
                        if (callback != null) {
                            callback.onCompleted();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("addFavorite", error.toString());
                    }

                }
        );
        requestQueue.add(jsonObjectRequest);;
    }


    private void isFavorite(String ticker, Callback callback){
        String url = BASE_URL + "/watchlist/"+ticker;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        flagFavorite = true;
                        Log.d("isFavorite",response.toString());
                    }catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (callback != null) {
                        callback.onCompleted();
                    }
                },
                error -> {
                    flagFavorite = false;
                    Log.e("isFavorite", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);
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

    public void getAutoCompleteArray(String inputText, CallbackNew callback ) {
        String url = BASE_URL + "/autocomplete/"+ inputText;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray autoArray = null;
                        List<String> filteredSymbols = new ArrayList<>();
                        if (response.length() > 0) {
                            autoArray = response.getJSONArray("result");

                            for (int i = 0; i < autoArray.length(); i++) {
                                JSONObject jsonObject = autoArray.getJSONObject(i);
                                if (jsonObject.has("displaySymbol")) {
                                    String displaySymbol = jsonObject.getString("displaySymbol");
                                    String description = jsonObject.getString("description");
                                    String type = jsonObject.getString("type");
                                    if (!displaySymbol.contains(".") && (type.equals("Common Stock"))) {
                                        filteredSymbols.add(displaySymbol +" | "+description);
                                    }
                                }
                            }

                        }

                        Log.d("autoObject", String.valueOf(autoArray));
                        Log.d("filteredSymbols", String.valueOf(filteredSymbols));


                        autoCompleteArray = filteredSymbols.toArray(new String[0]);

//
                    } catch (JSONException e) {
                        Log.e("getAutocompleteError", "Error: " + e.toString());
                    }
                    if (callback != null) {
                        callback.onCompletedNew(autoCompleteArray);
                    }
                },
                error -> {
                    Log.e("getAutocomplete", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);




//        autoCompleteArray = new String[]{"Apple", "Apricot", "Banana", "Cherry", "Date", "Fig", "Grape", "Kiwi"};
//        if (callback != null) {
//            callback.onCompletedNew(autoCompleteArray);
//        }

    }


    private void updateFavoriteUI(ArrayList<FavoriteItem> favoriteItems) {
        RecyclerView recyclerView = findViewById(R.id.favoritesRecyclerView);
        FavoritesAdapter f_adapter = new FavoritesAdapter(favoriteItems, this);
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


                            Log.d("PortfolioResponse", portfolioItems.toString());
                        }
                        Log.d("My new PortfolioResponse", response.toString());
                        if(response.toString().equals("[]")){
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setVisibility(View.GONE);
                        }else{
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        Log.d("Iam here", String.valueOf(e));
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
        PortfolioAdapter p_adapter = new PortfolioAdapter(portfolioItems, this);
        recyclerView.setAdapter(p_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupItemTouchHelper(recyclerView, portfolioItems, p_adapter);

    }

    @Override
    public void onItemClick(String displaySymbol) {
        TextView tickerTextView = findViewById(R.id.tickerTextView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        searchIcon.setVisibility(View.GONE);
        backIcon.setVisibility(View.VISIBLE);
        crossIcon.setVisibility(View.GONE);// Extract displaySymbol
        actv.setText(displaySymbol);  // Set only the displaySymbol as the text
        actv.setSelection(displaySymbol.length());
        d_symbol = displaySymbol;
        actv.setVisibility(View.GONE);
        starIcon.setVisibility(View.VISIBLE);
        isFavorite(displaySymbol, new Callback() {
            @Override
            public void onCompleted() {
                if(flagFavorite){
                    starIcon.setImageResource(R.drawable.full_star);
                }
                else{
                    starIcon.setImageResource(R.drawable.star_border);
                }
            }
        });

        tickerTextView.setVisibility(View.VISIBLE);
        tickerTextView.setText(displaySymbol);
        titleTextView.setVisibility(View.GONE);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DetailFragment detailFragment = DetailFragment.newInstance(displaySymbol);

        fragmentTransaction.replace(R.id.homePage, detailFragment);


        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        LinearLayout homePageContent = findViewById(R.id.homePageContent);
        homePageContent.setVisibility(View.GONE);
    }

    @Override
    public void onPfItemClick(String displaySymbol) {
        TextView tickerTextView = findViewById(R.id.tickerTextView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        searchIcon.setVisibility(View.GONE);
        backIcon.setVisibility(View.VISIBLE);
        crossIcon.setVisibility(View.GONE);// Extract displaySymbol
        actv.setText(displaySymbol);  // Set only the displaySymbol as the text
        actv.setSelection(displaySymbol.length());
        d_symbol = displaySymbol;
        actv.setVisibility(View.GONE);
        starIcon.setVisibility(View.VISIBLE);
        isFavorite(displaySymbol, new Callback() {
            @Override
            public void onCompleted() {
                if(flagFavorite){
                    starIcon.setImageResource(R.drawable.full_star);
                }
                else{
                    starIcon.setImageResource(R.drawable.star_border);
                }
            }
        });

        tickerTextView.setVisibility(View.VISIBLE);
        tickerTextView.setText(displaySymbol);
        titleTextView.setVisibility(View.GONE);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DetailFragment detailFragment = DetailFragment.newInstance(displaySymbol);

        fragmentTransaction.replace(R.id.homePage, detailFragment);


        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        LinearLayout homePageContent = findViewById(R.id.homePageContent);
        homePageContent.setVisibility(View.GONE);
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
                    d.set(quoteObject.getDouble("d"));
                    dp.set(quoteObject.getDouble("dp"));
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
