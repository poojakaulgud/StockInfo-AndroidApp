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

public class SecondTabFragment extends Fragment {
    WebView main_webview;
    String ticker;

    private WebAppInterface mainWebAppInterface;
    public SecondTabFragment(String ticker) {
        this.ticker = ticker;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_tab, container, false);
        main_webview = view.findViewById(R.id.main_webview);

        main_webview.getSettings().setJavaScriptEnabled(true);

        mainWebAppInterface = new WebAppInterface(getActivity(), main_webview);

        main_webview.addJavascriptInterface(mainWebAppInterface, "Android");


        main_webview.loadUrl("file:///android_asset/main_highcharts.html");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        main_webview.setWebChromeClient(new WebChromeClient() {


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

        main_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // WebView content loaded, safe to make calls
                updatemainChartWithTicker(ticker);
            }
        });


        return view;
    }


    public void updatemainChartWithTicker(String ticker) {
        if (main_webview != null) {
            Log.d("WebView Debuggggggggggggg", "WebView is properly initialized");
            mainWebAppInterface.sendTickerToJavaScript(ticker);
        } else {
            Log.e("WebView Debuggggggggggggggg", "WebView is not initialized");
        }
    }
}
