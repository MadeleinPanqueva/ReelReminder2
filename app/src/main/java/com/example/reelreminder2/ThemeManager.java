package com.example.reelreminder2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class ThemeManager {
    private static final String PREF_NAME = "ThemePreferences";
    private static final String KEY_DARK_THEME = "dark_theme";
    private static ThemeManager instance;
    private final SharedPreferences preferences;
    private boolean isDarkTheme;

    private ThemeManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isDarkTheme = preferences.getBoolean(KEY_DARK_THEME, false);
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isDarkTheme() {
        return isDarkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.isDarkTheme = darkTheme;
        preferences.edit().putBoolean(KEY_DARK_THEME, darkTheme).apply();
    }

    public void toggleTheme() {
        setDarkTheme(!isDarkTheme);
    }

    public static int getThemeResource(boolean isDarkTheme) {
        return isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light;
    }
} 