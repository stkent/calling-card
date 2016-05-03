package com.github.stkent.callingcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class UsersView extends LinearLayout {

    @Bind(R.id.empty_state_view)
    protected TextView emptyStateLabel;

    @Bind(R.id.user_view_container)
    protected ViewGroup userViewContainer;

    public UsersView(@NonNull final Context context) {
        this(context, null);
    }

    public UsersView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UsersView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            final int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.include_users_view, this, true);
        ButterKnife.bind(this);

        final TypedArray typedArray
                = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UsersView, 0, 0);

        final String emptyStateText = typedArray.getString(R.styleable.UsersView_empty_state_text);
        emptyStateLabel.setText(emptyStateText);

        typedArray.recycle();
    }

    public void addUser(@NonNull final User user) {
        if (user.isValid()) {
            final UserView userView = new UserView(getContext());
            userView.bindUser(user);
            userViewContainer.addView(userView);
            updateEmptyStateVisibility();
        }
    }

    public void removeUser(@NonNull final User user) {
        final String idOfUserToRemove = user.getId();

        for (int index = userViewContainer.getChildCount() - 1; index >= 0; index--) {
            final View child = getChildAt(index);
            final String userId = (String) child.getTag();

            if (idOfUserToRemove.equals(userId)) {
                removeViewAt(index);
                updateEmptyStateVisibility();
            }
        }
    }

    public void removeAllUsers() {
        userViewContainer.removeAllViews();
        updateEmptyStateVisibility();
    }

    private void updateEmptyStateVisibility() {
        emptyStateLabel.setVisibility(userViewContainer.getChildCount() == 0 ? VISIBLE : GONE);
    }

}
