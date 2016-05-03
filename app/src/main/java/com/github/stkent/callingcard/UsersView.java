package com.github.stkent.callingcard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public final class UsersView extends LinearLayout {

    public UsersView(Context context) {
        this(context, null);
    }

    public UsersView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UsersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
    }

    public void addUser(@NonNull final User user) {
        if (user.isValid()) {
            final UserView userView = new UserView(getContext());
            userView.bindUser(user);
            addView(userView);
        }
    }

    public void removeUser(@NonNull final User user) {
        final String idOfUserToRemove = user.getId();

        for (int index = getChildCount() - 1; index >= 0; index--) {
            final View child = getChildAt(index);
            final String userId = (String) child.getTag();

            if (idOfUserToRemove.equals(userId)) {
                removeViewAt(index);
            }
        }
    }

    public void removeAllUsers() {
        removeAllViews();
    }

}
