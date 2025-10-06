package com.didomi.sampleappjava;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;

import io.didomi.sdk.Didomi;
import io.didomi.sdk.DidomiInitializeParameters;
import io.didomi.sdk.events.ConsentChangedEvent;
import io.didomi.sdk.events.EventListener;
import io.didomi.sdk.exceptions.DidomiNotReadyException;
import io.didomi.sdk.models.CurrentUserStatus;
import io.didomi.sdk.models.UserStatus;

public final class MainApplication extends Application {

    private EventListener didomiEventListener;
    private static final String TAG = "Didomi - Sample App";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Didomi.getInstance().initialize(
                    this,
                    new DidomiInitializeParameters(
                            "7dd8ec4e-746c-455e-a610-99121b4148df",
                            null,
                            null,
                            null,
                            false,
                            null,
                            "DVLP9Qtd"
                    )
            );

            // Do not use the Didomi.getInstance() object here for anything else than registering your ready listener
            // The SDK might not be ready yet

            Didomi.getInstance().onReady(() -> {
                // The SDK is ready, you can now interact with it

                // Load your custom vendors in the onReady callback.
                // These vendors need to be conditioned manually to the consent status of the user.
                loadVendor();
            });
        } catch(Exception e) {
            Log.e("App", "Error while initializing the Didomi SDK", e);
        }

        // Load the IAB vendors; the consent status will be shared automatically with them.
        // Regarding the Google Mobile Ads SDK, we also delay App Measurement as described here:
        // https://developers.google.com/admob/ump/android/quick-start#delay_app_measurement_optional
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "MobileAds initialized.");
        });
    }

    private void loadVendor() {
        Didomi didomi = Didomi.getInstance();

        CurrentUserStatus status;
        try {
            status = didomi.getCurrentUserStatus();
        } catch (DidomiNotReadyException e) {
            Log.e("DIDOMI SAMPLE", e.getMessage(), e);
            return;
        }

        String vendorId = "c:customven-gPVkJxXD";
        CurrentUserStatus.VendorStatus vendor = status.getVendors().get(vendorId);
        boolean isVendorEnabled;
        if (vendor == null) {
            isVendorEnabled = false;
        } else {
            isVendorEnabled = vendor.getEnabled();
        }

        // Remove any existing event listener
        if (didomiEventListener != null) {
            didomi.removeEventListener(didomiEventListener);
        }

        if (isVendorEnabled) {
            // We have consent for the vendor
            // Initialize the vendor SDK
            CustomVendor.getInstance().initialize();
        } else {
            // We do not have consent information yet
            // Wait until we get the user information
            this.didomiEventListener = (new EventListener() {
                public void consentChanged(@NonNull ConsentChangedEvent event) {
                    loadVendor();
                }
            });
            didomi.addEventListener(didomiEventListener);
        }
    }
}
