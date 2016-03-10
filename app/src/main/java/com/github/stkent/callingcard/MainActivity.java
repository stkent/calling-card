package com.github.stkent.callingcard;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, OnCheckedChangeListener {

    private static final int PUBLISHING_ERROR_RESOLUTION_CODE = 10235321;
    private static final int SUBSCRIBING_ERROR_RESOLUTION_CODE = 10236546;

    private final PublishCallback publishCallback = new PublishCallback() {
        // Note: this seems to be invoked on a background thread!
        @Override
        public void onExpired() {
            /*
             * From https://developers.google.com/nearby/messages/android/pub-sub:
             *
             *   When actively publishing and subscribing, a "Nearby is in use" notification is
             *   presented, informing users that Nearby is active. This notification is only
             *   displayed when one or more apps are actively using Nearby, giving users a chance
             *   to conserve battery life if Nearby is not needed. It provides users with the
             *   following options:
             *
             *     - Navigate to an app to disable Nearby.
             *     - Force an app to stop using Nearby.
             *     - Navigate to the Nearby Settings screen.
             *
             *   You can use PublishCallback() [and SubscribeCallback()] to listen for cases when a
             *   user forces the app to stop using Nearby. When this happens, the onExpired()
             *   method is triggered.
             */

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelAllNearbyOperations();
                }
            });
        }
    };

    private final SubscribeCallback subscribeCallback = new SubscribeCallback() {
        // All comments in publishCallback apply here too.
        @Override
        public void onExpired() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelAllNearbyOperations();
                }
            });
        }
    };

    private final PublishOptions publishOptions
            = new PublishOptions.Builder().setCallback(publishCallback).build();

    private final SubscribeOptions subscribeOptions
            = new SubscribeOptions.Builder().setCallback(subscribeCallback).build();

    private final MessageListener messageListener = new MessageListener() {
        @Override
        public void onFound(final Message message) {
            try {
                final DeviceData deviceData
                        = new Gson().fromJson(new String(message.getContent()), DeviceData.class);

                receivedDeviceDataView.addDeviceData(deviceData);
            } catch (final JsonSyntaxException ignored) {
                toastError("Invalid message received!");
            }
        }

        @Override
        public void onLost(final Message message) {
            try {
                final DeviceData deviceData
                        = new Gson().fromJson(new String(message.getContent()), DeviceData.class);

                receivedDeviceDataView.removeDeviceData(deviceData);
            } catch (final JsonSyntaxException ignored) {
                toastError("Invalid message reported as lost!");
            }
        }
    };

    @Bind(R.id.publishing_switch)
    protected Switch publishingSwitch;

    @Bind(R.id.published_message_field)
    protected TextView publishedMessageField;

    @Bind(R.id.subscribing_switch)
    protected Switch subscribingSwitch;

    @Bind(R.id.received_device_data_view)
    protected ReceivedDeviceDataView receivedDeviceDataView;

    private Message messageToPublish;
    private GoogleApiClient googleApiClient;
    private boolean attemptingToPublish = false;
    private boolean attemptingToSubscribe = false;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        publishingSwitch.setOnCheckedChangeListener(this);
        subscribingSwitch.setOnCheckedChangeListener(this);

        final DeviceData deviceData = new DeviceData(this);
        publishedMessageField.setText(deviceData.toString());

        messageToPublish = new Message(new Gson().toJson(deviceData).getBytes());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        cancelAllNearbyOperations();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PUBLISHING_ERROR_RESOLUTION_CODE) {
            if (attemptingToPublish && resultCode == Activity.RESULT_OK) {
                // User was presented with the Nearby opt-in dialog and pressed "Allow".
                attemptToPublish();
            } else {
                // User declined to opt-in.
                publishingSwitch.setChecked(false);
            }

            attemptingToPublish = false;
        } else if (requestCode == SUBSCRIBING_ERROR_RESOLUTION_CODE) {
            if (attemptingToSubscribe && resultCode == Activity.RESULT_OK) {
                // User was presented with the Nearby opt-in dialog and pressed "Allow".
                attemptToSubscribe();
            } else {
                // User declined to opt-in.
                subscribingSwitch.setChecked(false);
            }

            attemptingToSubscribe = false;
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.publishing_switch:
                if (publishingSwitch.isChecked()) {
                    if (googleApiClient.isConnected()) {
                        attemptToPublish();
                    } else if (!googleApiClient.isConnecting()) {
                        googleApiClient.connect();
                    }
                } else {
                    stopPublishing();
                    attemptingToPublish = false;
                }

                break;
            case R.id.subscribing_switch:
                if (subscribingSwitch.isChecked()) {
                    if (googleApiClient.isConnected()) {
                        attemptToSubscribe();
                    } else if (!googleApiClient.isConnecting()) {
                        googleApiClient.connect();
                    }
                } else {
                    stopSubscribing();
                    attemptingToSubscribe = false;
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        syncSwitchStateWithGoogleApiClientState();

        if (publishingSwitch.isChecked()) {
            attemptToPublish();
        }

        if (subscribingSwitch.isChecked()) {
            attemptToSubscribe();
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {
        handleGoogleApiClientConnectionIssue();
        // TODO: all usual error handling and resolution goes here
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        handleGoogleApiClientConnectionIssue();
        // TODO: all usual error handling and resolution goes here
    }

    private void handleGoogleApiClientConnectionIssue() {
        syncSwitchStateWithGoogleApiClientState();
        publishingSwitch.setChecked(false);
        subscribingSwitch.setChecked(false);
    }

    private void cancelAllNearbyOperations() {
        publishingSwitch.setChecked(false);
        subscribingSwitch.setChecked(false);

        if (googleApiClient.isConnected() || googleApiClient.isConnecting()) {
            googleApiClient.disconnect();
        }

        receivedDeviceDataView.clearAllDeviceData();
    }

    private void attemptToPublish() {
        attemptingToPublish = true;

        Nearby.Messages.publish(googleApiClient, messageToPublish, publishOptions)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull final Status status) {
                        if (status.isSuccess()) {
                            attemptingToPublish = false;
                        } else if (status.hasResolution()) {
                            try {
                                status.startResolutionForResult(
                                        MainActivity.this, PUBLISHING_ERROR_RESOLUTION_CODE);

                            } catch (final IntentSender.SendIntentException e) {
                                attemptingToPublish = false;
                                toastError(status.getStatusMessage());
                            }
                        } else {
                            attemptingToPublish = false;
                            toastError(status.getStatusMessage());
                            // TODO: error-specific handling if desired
                        }
                    }
                });
    }

    private void stopPublishing() {
        // TODO: check PendingResult of this call and retry if it is not a success?
        Nearby.Messages.unpublish(googleApiClient, messageToPublish);
    }

    private void attemptToSubscribe() {
        attemptingToSubscribe = true;

        Nearby.Messages.subscribe(googleApiClient, messageListener, subscribeOptions)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull final Status status) {
                        if (status.isSuccess()) {
                            attemptingToSubscribe = false;
                        } else if (status.hasResolution()) {
                            try {
                                status.startResolutionForResult(
                                        MainActivity.this, SUBSCRIBING_ERROR_RESOLUTION_CODE);

                            } catch (final IntentSender.SendIntentException e) {
                                attemptingToSubscribe = false;
                                toastError(status.getStatusMessage());
                            }
                        } else {
                            attemptingToSubscribe = false;
                            toastError(status.getStatusMessage());
                            // TODO: error-specific handling if desired
                        }
                    }
                });
    }

    private void stopSubscribing() {
        // TODO: check PendingResult of this call and retry if it is not a success?
        Nearby.Messages.unsubscribe(googleApiClient, messageListener);
        receivedDeviceDataView.clearAllDeviceData();
    }

    private void syncSwitchStateWithGoogleApiClientState() {
        final boolean googleApiClientConnected = googleApiClient.isConnected();

        publishingSwitch.setEnabled(googleApiClientConnected);
        subscribingSwitch.setEnabled(googleApiClientConnected);
    }

    private void toastError(@NonNull final String message) {
        Toast.makeText(this, "Error: " + message, LENGTH_LONG).show();
    }

}
