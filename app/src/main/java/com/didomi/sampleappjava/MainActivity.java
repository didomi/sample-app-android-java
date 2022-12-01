package com.didomi.sampleappjava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import io.didomi.sdk.Didomi;
import io.didomi.sdk.events.ConsentChangedEvent;
import io.didomi.sdk.events.EventListener;
import io.didomi.sdk.functionalinterfaces.DidomiEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Didomi - Sample App";

    private final Didomi didomi = Didomi.getInstance();

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton buttonPurposesPreferences = findViewById(R.id.didomi_button_preferences_purposes);
        AppCompatButton buttonVendorsPreferences = findViewById(R.id.didomi_button_preferences_vendors);
        AppCompatButton buttonWebView = findViewById(R.id.didomi_button_web_view);
        AppCompatButton buttonAd = findViewById(R.id.didomi_button_ad);

        buttonPurposesPreferences.setOnClickListener(view -> showPurposesPreferences());
        buttonVendorsPreferences.setOnClickListener(view -> showVendorsPreferences());
        buttonWebView.setOnClickListener(view -> showWebView());
        buttonAd.setOnClickListener(view -> showAd());

        didomi.setupUI(this);

        prepareAd();
    }

    private void showPurposesPreferences() {
        try {
            didomi.onReady(() -> didomi.showPreferences(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVendorsPreferences() {
        try {
            didomi.onReady(() -> didomi.showPreferences(this, "vendors"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWebView() {
        Intent intent = new Intent(this, WebViewActivity.class);
        startActivity(intent);
    }

    private void showAd() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
            loadAd();
        }
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        interstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d(TAG, "Ad was loaded.");
                        MainActivity.this.interstitialAd = interstitialAd;

                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.d(TAG, "Ad showed fullscreen content.");
                                MainActivity.this.interstitialAd = null;
                                loadAd();
                            }
                        });
                    }
                });
    }

    /**
     * Will reset / preload Ad after each consent change
     * Consent allows Ads: the ad cache will be prepared (ad is displayed on first click after reject -> accept)
     * Consent rejects Ads: the ad cache will be purged (no ad on first click after reject)
     */
    private void prepareAd() {
        try {
            didomi.onReady(() -> {
                loadAd();
                DidomiEventListener didomiEventListener = (new EventListener() {
                    public void consentChanged(@NonNull ConsentChangedEvent event) {
                        // The consent status of the user has changed
                        loadAd();
                    }
                });
                didomi.addEventListener(didomiEventListener);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
