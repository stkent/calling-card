package com.github.stkent.callingcard;

import android.os.Build;
import android.provider.Settings;

import com.google.gson.annotations.Expose;

public final class DeviceData {

    @Expose
    private final String deviceModel;

    @Expose
    private final int deviceSdkVersion;

    @Expose
    private final String deviceId;

    public DeviceData() {
        deviceModel = Build.MODEL;
        deviceSdkVersion = Build.VERSION.SDK_INT;
        deviceId = Settings.Secure.ANDROID_ID;
    }

    @Override
    public String toString() {
        return deviceId + ": " + deviceModel + " running Android SDK Level " + deviceSdkVersion;
    }
}
