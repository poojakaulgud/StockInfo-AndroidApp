package com.example.assgn4;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FirstTabFragment extends Fragment {
    WebView price_webview;
    String ticker;

    private WebAppInterface priceWebAppInterface;

    public FirstTabFragment(String ticker) {
        this.ticker = ticker;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.first_tab, container, false);

        price_webview = view.findViewById(R.id.price_webview);

        price_webview.getSettings().setJavaScriptEnabled(true);

        priceWebAppInterface = new WebAppInterface(getActivity(), price_webview);

        price_webview.addJavascriptInterface(priceWebAppInterface, "Android");


        price_webview.loadUrl("file:///android_asset/price_highcharts.html");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        price_webview.setWebChromeClient(new WebChromeClient() {


            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String logMsg = consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId();

                switch (consoleMessage.messageLevel()) {
                    case ERROR:
                        Log.e("MyApplication", "ERROR: " + logMsg);
                        break;
                    case WARNING:
                        Log.w("MyApplication", "WARNING: " + logMsg);
                        break;
                    case LOG:
                        Log.i("MyApplication", "LOG: " + logMsg);
                        break;
                    case DEBUG:
                        Log.d("MyApplication", "DEBUG: " + logMsg);
                        break;
                    case TIP:
                        Log.i("MyApplication", "TIP: " + logMsg);
                        break;
                    default:
                        Log.v("MyApplication", logMsg);
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });

        price_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // WebView content loaded, safe to make calls
                updatepriceChartWithTicker(ticker);
            }
        });
        return view;
    }


    public void updatepriceChartWithTicker(String ticker) {
        if (price_webview != null) {
            Log.d("WebView Debuggggggggggggg", "WebView is properly initialized");
            priceWebAppInterface.sendTickerToJavaScript(ticker);
        } else {
            Log.e("WebView Debuggggggggggggggg", "WebView is not initialized");
        }
    }
}
