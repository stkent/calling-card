package com.github.stkent.callingcard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

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

    public void addDeviceData(@NonNull final UserData userData) {
        final UserView userView = new UserView(getContext());
        userView.bindUserData(userData);
        addView(userView);
    }

    public void removeDeviceData(@NonNull final UserData userData) {
        final String deviceIdToRemove = userData.getId();

        for (int index = getChildCount() - 1; index >= 0; index--) {
            final View child = getChildAt(index);
            final String deviceId = (String) child.getTag();

            if (deviceIdToRemove.equals(deviceId)) {
                removeViewAt(index);
            }
        }
    }

    public void clearAllDeviceData() {
        removeAllViews();
    }

}
