package com.github.stkent.callingcard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import static android.widget.Toast.LENGTH_LONG;

public abstract class BaseActivity extends AppCompatActivity implements OnConnectionFailedListener {

    protected abstract String getLogTag();

    protected GoogleApiClient signInGoogleApiClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GoogleSignInOptions googleSignInOptions
                = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(getLogTag(), "onStart: Attempting silent sign-in.");

        final OptionalPendingResult<GoogleSignInResult> optionalPendingResult
                = Auth.GoogleSignInApi.silentSignIn(signInGoogleApiClient);

        if (optionalPendingResult.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.

            Log.d(getLogTag(), "onStart: User is already signed in and credentials are valid.");

            final GoogleSignInResult result = optionalPendingResult.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            Log.d(getLogTag(), "onStart: User has not signed in before, or sign-in has expired.");
            Log.d(getLogTag(), "onStart: Attempting to renew credentials or leverage cross-device sign-in.");

            showProgressDialog();

            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull final GoogleSignInResult result) {
                    hideProgressDialog();
                    handleSignInResult(result);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        final String connectionFailedMessage = "Google API Client connection failed.";

        toastError(connectionFailedMessage);
    }

    protected final void toastError(@Nullable final String message) {
        if (message != null) {
            Log.e(getLogTag(), message);
            Toast.makeText(this, "Error: " + message, LENGTH_LONG).show();
        }
    }

    @CallSuper
    protected void handleSignInResult(final GoogleSignInResult result) {
        if (result.isSuccess()) {
            Log.d(getLogTag(), "handleSignInResult: User is signed in.");
        } else {
            Log.e(getLogTag(), "handleSignInResult: User is not signed in.");
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading…");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

}
