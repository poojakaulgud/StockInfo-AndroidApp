package com.example.assgn4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {
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
        if (getArguments() != null && getArguments().containsKey("selected_item")) {
            String selectedItem = getArguments().getString("selected_item");
            TextView textView = view.findViewById(R.id.detail_text);
            textView.setText(selectedItem); // Display the selected item's details
        }
        return view;
    }
}

