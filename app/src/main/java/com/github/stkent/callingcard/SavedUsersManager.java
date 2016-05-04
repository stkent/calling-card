package com.github.stkent.callingcard;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public final class SavedUsersManager {

    private static final String SAVED_USERS_KEY = "SAVED_USERS_KEY";

    @NonNull
    private final SharedPreferences sharedPreferences;

    @NonNull
    private final Gson configuredGsonInstance;

    public SavedUsersManager(
            @NonNull final SharedPreferences sharedPreferences,
            @NonNull final Gson configuredGsonInstance) {

        this.sharedPreferences = sharedPreferences;
        this.configuredGsonInstance = configuredGsonInstance;
    }

    @NonNull
    public List<User> getSavedUsers() {
        final String savedUsersString = sharedPreferences.getString(SAVED_USERS_KEY, null);

        if (savedUsersString == null) {
            return new ArrayList<>();
        }

        // todo: handle parsing failures here...
        return configuredGsonInstance
                .fromJson(savedUsersString, new TypeToken<List<User>>(){}.getType());
    }

    public void saveUser(@NonNull final User user) {
        final List<User> savedUsers = getSavedUsers();

        if (!savedUsers.contains(user)) {
            savedUsers.add(user);

            sharedPreferences
                    .edit()
                    .putString(SAVED_USERS_KEY, configuredGsonInstance.toJson(savedUsers))
                    .apply();
        }
    }

}
