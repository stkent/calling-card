package com.github.stkent.callingcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class SignInActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int SIGN_IN_REQUEST_CODE = 9162;

    @Bind(R.id.sign_in_button)
    protected SignInButton signInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        signInButton.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: Sign-In result received.");

            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.d(TAG, "onClick: User clicked Sign-In button.");

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(signInGoogleApiClient);
                startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void handleSignInResult(final GoogleSignInResult result) {
        super.handleSignInResult(result);

        if (result.isSuccess()) {
            final GoogleSignInAccount account = result.getSignInAccount();

            if (account != null) {
                final UserData userData = new UserData(account);

                if (userData.isValid()) {
                    MainActivity.launchWithUserData(userData, this);
                    finish();
                } else {
                    toastError("Could not verify user name and email address.");
                }
            } else {
                toastError("Could not retrieve authenticated account.");
            }
        } else {
            final String statusMessage = result.getStatus().getStatusMessage();

            if (statusMessage != null) {
                toastError(statusMessage);
            }
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

}
