package com.example.assgn4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private DetailFragment context;
    private static final int TYPE_BIG = 0;
    private static final int TYPE_SMALL = 1;
    private List<NewsItem> newsItems;

    public NewsAdapter(List<NewsItem> newsItems, DetailFragment context) {
        this.newsItems = newsItems;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_BIG : TYPE_SMALL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_big, parent, false);
            return new BigNewsViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_small, parent, false);
            return new SmallNewsViewHolder(view);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateNewsItems(List<NewsItem> newNewsItems) {
        newsItems.clear();
        newsItems.addAll(newNewsItems);
        notifyDataSetChanged();  // Notify any registered observers that the data set has changed.
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(v -> {
            showDialog(item, holder.itemView.getContext());
        });
        if (getItemViewType(position) == TYPE_BIG) {
            ((BigNewsViewHolder) holder).bind(item, context);
        } else {
            ((SmallNewsViewHolder) holder).bind(item, context);
        }
    }

    private void showDialog(NewsItem item, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_news_dialog, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.articleTitle);
        TextView tvSummary = dialogView.findViewById(R.id.articleSummary);
        TextView tvSource = dialogView.findViewById(R.id.articleSourceName);
        TextView tvDate = dialogView.findViewById(R.id.articleDate);
        ImageView chrome = dialogView.findViewById(R.id.chrome);
        ImageView twitter = dialogView.findViewById(R.id.twitter);
        ImageView facebook = dialogView.findViewById(R.id.facebook);

        chrome.setOnClickListener(view -> {
            Uri webpage = Uri.parse(item.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "No application can handle this request.", Toast.LENGTH_LONG).show();
            }
        });

        twitter.setOnClickListener(view -> {
            Uri webpage = Uri.parse("https://twitter.com/intent/tweet?title=Check out this Link: "+"&url="+item.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "No application can handle this request.", Toast.LENGTH_LONG).show();
            }
        });

        facebook.setOnClickListener(view -> {
            Uri webpage = Uri.parse("https://www.facebook.com/sharer/sharer.php?u="+item.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "No application can handle this request.", Toast.LENGTH_LONG).show();
            }
        });

        tvTitle.setText(item.getHeadline());
        tvSummary.setText(item.getSummary());
        tvSource.setText(item.getSource());
        tvDate.setText(item.getPublicationDate()); // Format date as needed

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    class BigNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, articleSource, articleElapsedTime;

        BigNewsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewBig);
            title = itemView.findViewById(R.id.articleTitleBig);
            articleSource = itemView.findViewById(R.id.articleSourceBig);
            articleElapsedTime = itemView.findViewById(R.id.articleElapsedTimeBig);
        }

        void bind(NewsItem item, Context context) {
            title.setText(item.getHeadline());
            Glide.with(context)
                    .load(item.getImageUrl())
                    .centerCrop()
                    .transform(new RoundedCorners(16))
                    .placeholder(R.drawable.search)
                    .error(R.drawable.close)
                    .into(imageView);
            // Load image into imageView
            articleSource.setText(item.getSource());
            articleElapsedTime.setText(String.format(Locale.getDefault(), " %d hours ago ", item.getElapsedTimeInHours()));
        }
    }

    class SmallNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, articleSource, articleElapsedTime;

        SmallNewsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewSmall);
            title = itemView.findViewById(R.id.articleTitleSmall);
            articleSource = itemView.findViewById(R.id.articleSourceSmall);
            articleElapsedTime = itemView.findViewById(R.id.articleElapsedTimeSmall);
        }

        void bind(NewsItem item, Context context) {
            title.setText(item.getHeadline());
            Glide.with(context)
                    .load(item.getImageUrl())
                    .centerCrop()
                    .transform(new RoundedCorners(16))
                    .placeholder(R.drawable.search)
                    .error(R.drawable.close)
                    .into(imageView);
            articleSource.setText(item.getSource());
            articleElapsedTime.setText(String.format(Locale.getDefault(), " %d hours ago ", item.getElapsedTimeInHours()));
        }
    }
}

