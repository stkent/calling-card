package com.github.stkent.callingcard;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class UserView extends CardView {

    private static final String TAG = "UserView";

    @DrawableRes
    private static final int PLACEHOLDER_IMAGE_RES = R.drawable.img_placeholder;

    @Bind(R.id.name_field)
    protected TextView nameField;

    @Bind(R.id.email_address_field)
    protected TextView emailAddressField;

    @Bind(R.id.photo_image_view)
    protected ImageView photoImageView;

    public UserView(final Context context) {
        this(context, null);
    }

    public UserView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserView(
            final Context context,
            @Nullable final AttributeSet attrs,
            final int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        final int padding = getResources().getDimensionPixelSize(R.dimen.user_view_padding);
        setContentPadding(padding, padding, padding, padding);

        LayoutInflater.from(context).inflate(R.layout.include_user_view, this);
        ButterKnife.bind(this);
    }

    public void bindUser(@NonNull final User user) {
        nameField.setText(user.getName());
        emailAddressField.setText(user.getEmailAddress());

        final Uri photoUrl = user.getPhotoUrl();

        if (photoUrl != null) {
            Log.d(TAG, "bindUser: User photo URL found: " + photoUrl);
            Log.d(TAG, "bindUser: Loading photo...");

            Picasso.with(getContext())
                    .load(photoUrl)
                    .placeholder(PLACEHOLDER_IMAGE_RES)
                    .error(PLACEHOLDER_IMAGE_RES)
                    .fit()
                    .into(photoImageView);
        } else {
            Log.d(TAG, "bindUser: No user photo URL found.");

            Picasso.with(getContext())
                    .load(PLACEHOLDER_IMAGE_RES)
                    .error(PLACEHOLDER_IMAGE_RES)
                    .fit()
                    .into(photoImageView);
        }

        setTag(user.getId());
    }

}
