package com.example.assgn4;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.fragment.app.FragmentActivity;

public class WebAppInterface {
    Context mContext;
    WebView mWebView;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c, WebView webView) {
        mContext = c;
        mWebView = webView;
    }

    /** Method to send data to JavaScript */
    @JavascriptInterface
    public void sendTickerToJavaScript(final String ticker) {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:fetchDataFromServer('" + ticker + "')");
            }
        });
    }
}

