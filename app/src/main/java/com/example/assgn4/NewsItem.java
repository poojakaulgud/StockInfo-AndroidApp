package com.example.assgn4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NewsItem {
    private String headline;
    private String summary;
    private String imageUrl;
    private String url;
    private String source;
    private long datetime; // Storing related stocks or topics

    // Constructor
    public NewsItem(String headline, String summary, String imageUrl, String url, String source, long datetime) {
        this.headline=headline;
        this.summary=summary;
        this.imageUrl=imageUrl;
        this.url=url;
        this.source=source;
        this.datetime=datetime;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "headline='" + headline + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", url='" + url + '\'' +
                ", source='" + source + '\'' +
                ", datetime=" + datetime +
                '}';
    }

    // Getters
    public String getHeadline() {
        return headline;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public long getDatetime() {
        return datetime;
    }

    // Calculating elapsed time in hours from the datetime
    public long getElapsedTimeInHours() {
        long currentTimestamp = System.currentTimeMillis() / 1000;  // Current time in seconds
        return TimeUnit.SECONDS.toHours(currentTimestamp - this.datetime);
    }

    public String getPublicationDate() {
        Date date = new Date(datetime * 1000);

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);

        // Format the date
        return sdf.format(date);
    }
}
