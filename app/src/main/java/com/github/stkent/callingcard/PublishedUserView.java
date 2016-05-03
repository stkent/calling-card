package com.github.stkent.callingcard;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class PublishedUserView extends UserView {

    @ColorInt
    private static final int publishingBorderColor = Color.rgb(113, 217, 114);

    @ColorInt
    private static final int notPublishingBorderColor = Color.rgb(255, 127, 128);

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
    }

    public void setPublishing(final boolean publishing) {
        final int borderColor = publishing ? publishingBorderColor : notPublishingBorderColor;

        final int backgroundColor = getColorWithAlpha(borderColor, 102);

        // todo: set border color here
        setBackgroundColor(backgroundColor);
    }

    @ColorInt
    private static int getColorWithAlpha(
            @ColorInt final int colorInt,
            @IntRange(from = 0, to = 255) final int alpha) {

        return Color.argb(
                alpha,
                Color.red(colorInt),
                Color.green(colorInt),
                Color.blue(colorInt));
    }

}
