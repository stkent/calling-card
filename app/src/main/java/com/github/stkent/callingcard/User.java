package com.github.stkent.callingcard;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class User implements Parcelable {

    // Required fields; values must be populated from a valid Google Account

    @Expose
    @SerializedName("name")
    // NonNull whenever isValid returns true;
    private final String name;

    @Expose
    @SerializedName("emailAddress")
    // NonNull whenever isValid returns true;
    private final String emailAddress;

    @Expose
    @SerializedName("id")
    // NonNull whenever isValid returns true;
    private final String id;

    // Optional fields

    @Expose
    @Nullable
    @SerializedName("photoUrlString")
    private final Uri photoUrl;

    public User(@NonNull final GoogleSignInAccount googleSignInAccount) {
        this.name = googleSignInAccount.getDisplayName();
        this.emailAddress = googleSignInAccount.getEmail();

        /*
         * "Returns the unique ID for the Google account if you built your configuration starting
         * rom new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)} or with
         * requestId() configured; null otherwise."
         */
        //noinspection ConstantConditions
        this.id = googleSignInAccount.getId();

        /*
         * "Returns the photo url of the signed in user if the user has a profile picture and you
         * built your configuration either starting from new
         * GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)} or with
         * requestProfile() configured; null otherwise. Not guaranteed to be present for all users,
         * even when configured."
         */
        this.photoUrl = googleSignInAccount.getPhotoUrl();
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public boolean isValid() {
        return name != null && emailAddress != null && id != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof User)) {
            return false;
        }

        final User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(this.name);
        dest.writeString(this.emailAddress);
        dest.writeString(this.id);
        dest.writeParcelable(this.photoUrl, flags);
    }

    protected User(@NonNull final Parcel in) {
        this.name = in.readString();
        this.emailAddress = in.readString();
        this.id = in.readString();
        this.photoUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(final Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(final int size) {
            return new User[size];
        }
    };

}
