package com.didomi.sampleappjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import io.didomi.sdk.Didomi;

public class WebViewActivity extends AppCompatActivity {

    private static final String URL = "https://didomi.github.io/webpage-for-sample-app-webview/?didomiConfig.notice.enable=false";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.didomi_web_view);

        /* The notice should automatically get hidden in the web view as consent is passed from the
         * mobile app to the website. However, it might happen that the notice gets displayed for a
         * very short time before being hidden. You can disable the notice in your web view to make
         * sure that it never shows by appending didomiConfig.notice.enable=false to the query
         * string of the URL that you are loading */

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Inject consent information into the WebView
                try {
                    Didomi.getInstance().onReady(() -> {
                        String didomiJavaScriptCode = Didomi.getInstance().getJavaScriptForWebView();

                        webView.evaluateJavascript(didomiJavaScriptCode, s -> {
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        webView.loadUrl(URL);
    }
}
