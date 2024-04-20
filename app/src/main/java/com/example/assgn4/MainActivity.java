package com.example.assgn4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assgn4.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView stocksRecyclerView;
    private StockAdapter stocksAdapter;
    private List<Stock> stockItems; // Your data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_Assgn4);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleTextView = findViewById(R.id.titleTextView);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView netWorthTextView = findViewById(R.id.networth);
        TextView cashBalanceTextView = findViewById(R.id.Balance);
        TextView poweredByLabel = findViewById(R.id.poweredByLabel);

        // Example of setting dynamic text in one of the TextViews
        // This can be a value retrieved from a database, an API, or user input
        netWorthTextView.setText("$25000.00");
        cashBalanceTextView.setText("$25000.00");
        dateTextView.setText("1 May 2024");

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
}
