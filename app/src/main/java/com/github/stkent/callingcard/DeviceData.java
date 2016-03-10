package com.github.stkent.callingcard;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

public final class DeviceData {

    @Expose
    private final String deviceModel;

    @Expose
    private final int deviceSdkVersion;

    @Expose
    private final String deviceId;

    public DeviceData(@NonNull final Context context) {
        deviceModel = Build.MODEL;
        deviceSdkVersion = Build.VERSION.SDK_INT;

        deviceId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public String toString() {
        return deviceId + ": " + deviceModel + " running Android SDK Level " + deviceSdkVersion;
    }

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }

}
