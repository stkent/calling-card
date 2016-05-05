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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class UsersView extends LinearLayout {

    @Bind(R.id.empty_state_view)
    protected TextView emptyStateLabel;

    @Bind(R.id.user_view_container)
    protected ViewGroup userViewContainer;

    private final List<User> displayedUsers = new ArrayList<>();

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

    public void addUser(@NonNull final User userToAdd) {
        if (userToAdd.isValid() && !displayedUsers.contains(userToAdd)) {
            final UserView userView = new UserView(getContext());
            userView.bindUser(userToAdd);
            userView.setTag(userToAdd);
            userViewContainer.addView(userView);

            displayedUsers.add(userToAdd);

            updateEmptyStateVisibility();
        }
    }

    public void setUsers(@NonNull final List<User> users) {
        removeAllUsers();

        for (final User user: users) {
            addUser(user);
        }
    }

    public void removeUser(@NonNull final User userToRemove) {
        if (!displayedUsers.contains(userToRemove)) {
            return;
        }

        for (int index = userViewContainer.getChildCount() - 1; index >= 0; index--) {
            final View child = userViewContainer.getChildAt(index);
            final User viewUser = (User) child.getTag();

            if (userToRemove.equals(viewUser)) {
                userViewContainer.removeViewAt(index);

                displayedUsers.remove(userToRemove);

                updateEmptyStateVisibility();
            }
        }
    }

    public void removeAllUsers() {
        userViewContainer.removeAllViews();

        displayedUsers.clear();

        updateEmptyStateVisibility();
    }

    private void updateEmptyStateVisibility() {
        emptyStateLabel.setVisibility(displayedUsers.size() == 0 ? VISIBLE : GONE);
    }

}
