package com.github.stkent.callingcard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ReceivedDeviceDataView extends LinearLayout {
    public ReceivedDeviceDataView(Context context) {
        this(context, null);
    }

    public ReceivedDeviceDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReceivedDeviceDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
    }

    public void addDeviceData(@NonNull final DeviceData deviceData) {
        final TextView textView = new TextView(getContext());
        textView.setText(deviceData.toString());
        addView(textView, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    public void clearAllDeviceData() {
        removeAllViews();
    }

}
