package com.github.stkent.callingcard;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

public class CustomApplication extends Application {

    private static final String TAG = "CustomApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        configureSingletonPicassoInstance();
    }

    private void configureSingletonPicassoInstance() {
        final Picasso picasso = new Picasso.Builder(this)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(
                            final Picasso picasso,
                            final Uri uri,
                            final Exception exception) {

                        Log.e(TAG, "onImageLoadFailed", exception);
                    }
                })
                .loggingEnabled(true)
                .build();

        Picasso.setSingletonInstance(picasso);
    }

}
