package com.danielkim.soundrecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Daniel on 5/22/2017.
 */

public class MySharedPreferences {
    private static String PREF_HIGH_QUALITY = "pref_high_quality";
    private static String PREF_LAST_NAME = "last_index";
    private static String PREF_FILE_NAME = "file_name";
    private static String PREF_FILE_PATH = "file_path";
    private static String PREF_USER_NAME = "user_name";
    private static String PREF_COLUMN_INDEX = "column_index";
    private static String PREF_FILE_NAME_COLUMN_INDEX = "file_name_column_index";


    public static void setPrefHighQuality(Context context, boolean isEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_HIGH_QUALITY, isEnabled);
        editor.apply();
    }

    public static boolean getPrefHighQuality(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_HIGH_QUALITY, false);
    }

    public static void setLastReadIndex(Context context, int name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_LAST_NAME, name);
        editor.apply();
    }

    public static int getLastReadIndex(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PREF_LAST_NAME, 0);
    }

    public static String getSelectedFileName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_FILE_NAME, "Default");
    }

    public static void setSelectedFileName(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_FILE_NAME, name);
        editor.apply();
    }

    public static void setSelectedFilePath(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_FILE_PATH, name);
        editor.apply();
    }

    public static String getSelectedFilePath(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_FILE_PATH, "");
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USER_NAME, "");
    }

    public static void setUserName(Context context, String name) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_USER_NAME, name);
        editor.apply();
    }

    public static int getColumnIndex(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PREF_COLUMN_INDEX, 3);
    }

    public static void setColumnIndex(Context context, int index) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_COLUMN_INDEX, index);
        editor.apply();
    }

    public static int getFileNameColumnIndex(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PREF_FILE_NAME_COLUMN_INDEX, 2);
    }

    public static void setFileNameColumnIndex(Context context, int index) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_FILE_NAME_COLUMN_INDEX, index);
        editor.apply();
    }
}
