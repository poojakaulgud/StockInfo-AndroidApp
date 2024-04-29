package com.example.assgn4;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


public class DetailFragment extends Fragment {
    private RequestQueue requestQueue;

    private final String BASE_URL = "https://assgn3-pooja.wl.r.appspot.com";
    public String sts;
    Double c;
    int qty;
    TextView fragTicker;
    TextView fragDesc;
    TextView fragMarketValue;
    TextView fragd;
    TextView pfShares;
    private double balance;
    TextView pfAvgCost;
    TextView pfTotalCost;
    TextView pfMarketValue;
    TextView cash_to_buy, multiplier;
    TextView pfChange;
    TextView fragdp, fragh;
    TextView fragl;
    TextView frago;
    TextView fragpc;
    TextView ipo, industry, peers, weburl;
    TextView total_mspr, neg_mspr, pos_mspr, total_change, pos_change, neg_change;
    ImageView fragimageChangeIndicator;

    Button Tradebutton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        if (getArguments() != null && getArguments().containsKey("selected_item")) {
            String selectedItem = getArguments().getString("selected_item");
            sts = selectedItem;
        }

        try {
            getQuoteValues(new Callback() {
                @Override
                public void onCompleted() {
                    getPortfolio();
                }
            });
            getDescription();
            getPeers();
            getInsider();
            getWallet();
        } catch (Exception e) {
            Log.e("DetailFragment", "Error fetching data", e);
        }




    }
    public static DetailFragment newInstance(String selectedItem) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("selected_item", selectedItem);
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        fragTicker = view.findViewById(R.id.fragTicker);
        fragDesc = view.findViewById(R.id.fragDesc);
        fragMarketValue = view.findViewById(R.id.fragMarketValue);
        fragd = view.findViewById(R.id.fragPriceChangeValue);
        fragdp = view.findViewById(R.id.fragPriceChangePercentage);
        fragimageChangeIndicator = view.findViewById(R.id.fragImageChangeIndicator);
        pfShares = view.findViewById(R.id.pfShares);
        pfMarketValue = view.findViewById(R.id.pfMarketValue);
        pfChange = view.findViewById(R.id.pfChange);
        pfAvgCost = view.findViewById(R.id.pfAvgCost);
        pfTotalCost = view.findViewById(R.id.pfTotalCost);
        fragh = view.findViewById(R.id.hprice);
        fragl = view.findViewById(R.id.lprice);
        fragpc = view.findViewById(R.id.pcprice);
        frago = view.findViewById(R.id.oprice);
        ipo = view.findViewById(R.id.ipo);
        weburl = view.findViewById(R.id.weburl);
        industry = view.findViewById(R.id.industry);
        peers = view.findViewById(R.id.peers);
        total_mspr = view.findViewById(R.id.total_mspr);
        neg_mspr = view.findViewById(R.id.neg_mspr);
        pos_mspr = view.findViewById(R.id.pos_mspr);
        total_change = view.findViewById(R.id.total_change);
        neg_change = view.findViewById(R.id.neg_change);
        pos_change = view.findViewById(R.id.pos_change);



        Tradebutton = view.findViewById(R.id.button_trade);

        Tradebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_trade_shares, null);

                // Create the dialog using the custom view
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Makes background transparent
                }
                dialog.show();

                // Find views within the dialog and set up click listeners or other interactions
                Button buyButton = dialogView.findViewById(R.id.button_buy);
                Button sellButton = dialogView.findViewById(R.id.button_sell);
                EditText editText = dialogView.findViewById(R.id.shares_entered);
                String valueString = editText.getText().toString();
                final int[] value = {0};
                if (!valueString.isEmpty()) {
                    try {
                        value[0] = Integer.parseInt(valueString);
                    } catch (NumberFormatException e) {
                        // Handle the exception if the string does not contain a parsable integer.
                        Log.e("DetailFragment", "NumberFormatException: " + e.getMessage());
                    }
                }

                cash_to_buy = dialogView.findViewById(R.id.cash_to_buy);
                multiplier = dialogView.findViewById(R.id.multiplier);
                cash_to_buy.setText(String.format(Locale.getDefault(), "$%.2f to buy %s", balance, sts));
                multiplier.setText(String.format(Locale.getDefault(), "%d*$%.2f/share = %.2f", value[0],c, (value[0] *c)));

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // This method is called to notify you that, within s,
                        // the count characters beginning at start are about to be replaced with new text with length after.
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // This method is called to notify you that, within s,
                        // the count characters beginning at start have just replaced old text that had length before.
                        // You can perform your action here that you want to do with every change in the EditText.
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (!s.toString().isEmpty()) {
                            try {
                                value[0] = Integer.parseInt(s.toString());
                                multiplier.setText(String.format(Locale.getDefault(), "%d*$%.2f/share = %.2f", value[0],c, (value[0] *c)));
                            } catch (NumberFormatException e) {
                                // Handle exception if string is not a valid integer
                            }
                        }
                    }
                });

                // Set up click listeners for your buttons
                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if( value[0]<0){
                            LayoutInflater inflater = LayoutInflater.from(view.getContext());
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText("Cannot buy non-positive shares");

                            Toast toast = new Toast(view.getContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }
                        else if((value[0] *c)>balance){
                            LayoutInflater inflater = LayoutInflater.from(view.getContext());
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText("Not enough money to buy");

                            Toast toast = new Toast(view.getContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }
                        else{
                            LayoutInflater inflater = LayoutInflater.from(view.getContext());
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText("Please enter a valid amount");

                            Toast toast = new Toast(view.getContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }
                    }
                });

                sellButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle sell button click
                        if( value[0]<0){
                            LayoutInflater inflater = LayoutInflater.from(view.getContext());
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText("Cannot sell non-positive shares");

                            Toast toast = new Toast(view.getContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }

                        else if(value[0] > qty){
                            LayoutInflater inflater = LayoutInflater.from(view.getContext());
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText("Not enough shares to sell");

                            Toast toast = new Toast(view.getContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }
                        else{
                            LayoutInflater inflater = LayoutInflater.from(view.getContext());
                            View layout = inflater.inflate(R.layout.custom_toast_layout,null);

                            TextView text = layout.findViewById(R.id.custom_toast_message);
                            text.setText("Please enter a valid amount");

                            Toast toast = new Toast(view.getContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout); // Set the custom layout to Toast
                            toast.show();
                        }
                    }
                });
            }
        });


        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tabs);

        // Create an instance of the ViewPagerAdapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        // Set the adapter on the ViewPager2
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.chart_hour);// Ensure you have drawable resources
                    break;
                case 1:
                    tab.setIcon(R.drawable.chart_historical);
                    break;
                // Add more cases if there are more tabs
            }
        }).attach();


        TextView textView = view.findViewById(R.id.detail_text);
        textView.setText(sts);


        return view;
    }


    private void getWallet() {
        String url = BASE_URL + "/wallet";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Assuming 'balance' is a field in your JSON object
                        if (response.length() > 0) {
                            JSONObject walletObject = response.getJSONObject(0);
                            balance = walletObject.getDouble("balance");

                            Log.d("WalletResponse", walletObject.toString());

                        }


//
                    } catch (JSONException e) {
                        Log.e("getWallet", "Error: " + e.toString());
                        // Handle the case where 'balance' field is not in the response or there is a parsing error
                    }
                },
                error -> {
                    Log.e("getWallet", "Error: " + error.toString());
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void getDescription(){
        String url = BASE_URL + "/description/" + sts;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String ticker = response.getString("ticker");
                        String description = response.getString("name");
                        String res_ipo = response.getString("ipo");
                        String res_industry = response.getString("finnhubIndustry");
                        String res_weburl = response.getString("weburl");

                        fragTicker.setText(ticker);
                        fragDesc.setText(description);
                        ipo.setText(res_ipo);
                        weburl.setText(res_weburl);
                        industry.setText(res_industry);

                        Log.d("Description", response.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                },
                error -> {
                    Log.e("getDescription", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void getInsider(){
        String url = BASE_URL + "/sentiment/" + sts;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        double res_neg_mspr=0, res_neg_change=0, res_pos_mspr=0, res_pos_change=0, res_change=0, res_mspr = 0;
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            double new_mspr = jsonObject.getDouble("mspr");
                            double new_change = jsonObject.getDouble("change");
                            res_mspr+=new_mspr;
                            res_change+=new_change;
                            if(jsonObject.getDouble("mspr")<0){
                                res_neg_mspr+=new_mspr;
                            }else{
                                res_pos_mspr+=new_mspr;
                            }
                            if(jsonObject.getDouble("change")<0){
                                res_neg_change+=new_change;
                            }else{
                                res_pos_change+=new_change;
                            }

                        }

                        Log.d("neg_mspr",String.valueOf(res_neg_mspr));
                        Log.d("pos_mspr",String.valueOf(res_pos_mspr));
                        Log.d("mspr",String.valueOf(res_mspr));

                        total_mspr.setText(String.format(Locale.getDefault(), "%.2f",res_mspr));
                        total_change.setText(String.format(Locale.getDefault(), "%.2f",res_change));
                        pos_mspr.setText(String.format(Locale.getDefault(), "%.2f",res_pos_mspr));
                        neg_mspr.setText(String.format(Locale.getDefault(), "%.2f",res_neg_mspr));
                        pos_change.setText(String.format(Locale.getDefault(), "%.2f",res_pos_change));
                        neg_change.setText(String.format(Locale.getDefault(), "%.2f",res_neg_change));


                        Log.d("getInsider", jsonArray.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                },
                error -> {
                    Log.e("getInsider", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void getPeers(){
        String url = BASE_URL + "/peers/" + sts;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        StringBuilder str = new StringBuilder("");

                        // Traversing the ArrayList
                        for (int i = 0; i < response.length(); i++) {
                            String res_peer = response.getString(i);
                            Log.d("peer",res_peer);

                            str.append(res_peer).append(", ");
                        }
                        peers.setText(str);

                        Log.d("getPeers", response.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                },
                error -> {
                    Log.e("getPeers", "Error: " + error.toString());
                });
        requestQueue.add(jsonArrayRequest);
    }



    private void getQuoteValues (Callback callback){
        String url = BASE_URL + "/quote/" + sts ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject quoteObject = response;
                        Log.d("quoteValues" + sts, quoteObject.toString());

                        c = quoteObject.getDouble("c");
                        Double d = quoteObject.getDouble("d");
                        Double dp = quoteObject.getDouble("dp");
                        c= (Math.round(c * 100.0) / 100.0);
                        d=  (Math.round(d * 100.0) / 100.0);
                        dp= (Math.round(dp * 100.0) / 100.0);
                        Double h = quoteObject.getDouble("h");
                        Double l = quoteObject.getDouble("l");
                        Double o = quoteObject.getDouble("o");
                        Double pc = quoteObject.getDouble("pc");
                        fragMarketValue.setText( String.format(Locale.getDefault(), "$%.2f",c));
                        fragd.setText(String.format(Locale.getDefault(), "$%.2f",d));
                        fragh.setText(String.format(Locale.getDefault(), "High Price: $%.2f",h));
                        fragl.setText(String.format(Locale.getDefault(), "Low Price: $%.2f",l));
                        frago.setText(String.format(Locale.getDefault(), "Open Price: $%.2f",o));
                        fragpc.setText(String.format(Locale.getDefault(), "Prev. Close: $%.2f",pc));
                        fragdp.setText(String.format(Locale.getDefault(), "( %.2f%% )",dp));
                        if (d < 0) {
                            fragd.setTextColor(Color.RED);
                            fragdp.setTextColor(Color.RED);
                            fragimageChangeIndicator.setImageResource(R.drawable.trending_down); // Set your down icon
                        } else {
                            fragd.setTextColor(Color.parseColor("#489838"));
                            fragdp.setTextColor(Color.parseColor("#489838"));
                            fragimageChangeIndicator.setImageResource(R.drawable.trending_up); // Set your up icon
                        }

//
                        Log.d("getQuoteValues", String.valueOf(c));
                    }catch (JSONException e) {
                        Log.e("getCostValues", "Error: " + e.toString());
                    }
                    if (callback != null) {
                        callback.onCompleted();
                    }
                },
                error -> {
                    Log.e("getCostValues", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);
    }


    private void getPortfolio(){
        String url = BASE_URL + "/portfolio/" + sts;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        JSONObject jsonObject = response;
                        qty = jsonObject.getInt("qty");
                        double total = jsonObject.getDouble("total_cost");
                        double avg = jsonObject.getDouble("avg_cost");
                        double curr_price = Math.round(((c-avg)*qty) * 100.0) / 100.0;
//                        holder.tvMarketValue.setText(  ));
                        double market_value = (qty*c);
//                        holder.d.setText(String.format(Locale.getDefault(), " $%.2f ", ));
                        double change = (curr_price);

                        pfShares.setText(String.valueOf(qty));
                        pfMarketValue.setText(String.format(Locale.getDefault(), "$%.2f",market_value));
                        pfTotalCost.setText(String.valueOf(total));
                        pfAvgCost.setText(String.valueOf(avg));
                        pfChange.setText(String.format(Locale.getDefault(), " $%.2f ",change));

                        if (curr_price < 0) {
                            pfChange.setTextColor(Color.RED);
                            pfMarketValue.setTextColor(Color.RED);// Set your down icon
                        } else {
                            pfChange.setTextColor(Color.parseColor("#489838"));
                            pfMarketValue.setTextColor(Color.parseColor("#489838"));
                        }




                        Log.d("PortfolioResponse", jsonObject.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                },
                error -> {
                    Log.e("getPortfolio", "Error: " + error.toString());
                });
        requestQueue.add(jsonObjectRequest);
    }
}

