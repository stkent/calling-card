package com.github.stkent.callingcard;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public final class PublishedUserView extends UserView {

    @ColorInt
    private static final int publishingBackgroundColor = Color.argb(102, 113, 217, 114);

    @ColorInt
    private static final int notPublishingBackgroundColor = Color.argb(102, 255, 127, 128);

    public PublishedUserView(final Context context) {
        this(context, null);
    }

    public PublishedUserView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishedUserView(
            final Context context,
            @Nullable final AttributeSet attrs,
            final int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        // Default to not publishing background color.
        setCardBackgroundColor(notPublishingBackgroundColor);
    }

    public void setPublishing(final boolean publishing) {
        final int backgroundColor
                = publishing ? publishingBackgroundColor : notPublishingBackgroundColor;

        setCardBackgroundColor(backgroundColor);
    }

}
