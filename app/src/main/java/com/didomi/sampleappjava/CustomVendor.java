package com.didomi.sampleappjava;

import android.util.Log;

public class CustomVendor {
    private static final CustomVendor instance = new CustomVendor();

    private CustomVendor(){}

    public static CustomVendor getInstance() {
        return instance;
    }

    public void initialize() {
        Log.d("Didomi - Sample App", "Initializing custom vendor");
    }
}
