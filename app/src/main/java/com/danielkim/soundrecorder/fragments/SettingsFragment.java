package com.danielkim.soundrecorder.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.danielkim.soundrecorder.BuildConfig;
import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.activities.SettingsActivity;

import java.io.File;

/**
 * Created by Daniel on 5/22/2017.
 */

public class SettingsFragment extends PreferenceFragment {
    private Preference changeFile;
    private Preference changeLanguage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference highQualityPref = (CheckBoxPreference) findPreference(getResources().getString(R.string.pref_high_quality_key));
        highQualityPref.setChecked(MySharedPreferences.getPrefHighQuality(getActivity()));
        highQualityPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MySharedPreferences.setPrefHighQuality(getActivity(), (boolean) newValue);
                return true;
            }
        });

        Preference aboutPref = findPreference(getString(R.string.pref_about_key));
        aboutPref.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LicensesFragment licensesFragment = new LicensesFragment();
                licensesFragment.show(((SettingsActivity) getActivity()).getSupportFragmentManager().beginTransaction(), "dialog_licenses");
                return true;
            }
        });
        changeFile = findPreference("change_file");
        changeFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SelectFilesFragment fragment = SelectFilesFragment.newInstance();
                fragment.show(((SettingsActivity) getActivity()).getSupportFragmentManager().beginTransaction(), "change_file");
                fragment.setListener(new SelectFilesFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(Uri uri) {
                        setChangeFileSummary();
                        BaseAdapter adapter = (BaseAdapter) getPreferenceScreen().getRootAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });

//        changeLanguage = findPreference("change_data_file");
//        changeLanguage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                SelectDataLanguageFragment fragment = SelectDataLanguageFragment.newInstance();
//                fragment.show(((SettingsActivity) getActivity()).getSupportFragmentManager().beginTransaction(), "change_data_file");
//                fragment.setListener(new SelectDataLanguageFragment.OnFragmentInteractionListener() {
//                    @Override
//                    public void onFragmentInteraction(Uri uri) {
//                        setChangeDataFileSummary();
//                        BaseAdapter adapter = (BaseAdapter) getPreferenceScreen().getRootAdapter();
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//                return true;
//            }
//        });

        setChangeFileSummary();
//        setChangeDataFileSummary();
    }

    private void setChangeFileSummary() {
        changeFile.setSummary(MySharedPreferences.getSelectedFileName(getActivity()));
    }

//    private void setChangeDataFileSummary() {
//        String language = "";
//        switch (MySharedPreferences.getColumnIndex(getActivity())) {
//            case 2: {
//                language = "English";
//                break;
//            }
//            case 3: {
//                language = "Gujarati";
//                break;
//            }
//            case 4: {
//                language = "Hindi";
//                break;
//            }
//        }
//        changeLanguage.setSummary(language);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                Uri data1 = null;
                if (resultCode == Activity.RESULT_OK) {
                    data1 = data.getData();
                    String path = getPath(data1);
                    if (path == null) {
                        return;
                    }

                    if (!path.contains(".csv")) {
                        Toast.makeText(getActivity(), "Please select csv files only", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File f = new File(path);
                    Toast.makeText(getActivity(), "Selected file: " + f.getName(), Toast.LENGTH_SHORT).show();
                    MySharedPreferences.setSelectedFilePath(getActivity(), path);
                    MySharedPreferences.setSelectedFileName(getActivity(), f.getName());
                    MySharedPreferences.setLastReadIndex(getActivity(), 0);
                    setChangeFileSummary();
                }
            }
        }
    }

    private String getPath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // ExternalStorageProvider
            String[] split;
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory().getPath() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(getActivity(), contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                switch (type) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(getActivity(), contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(getActivity(), uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equalsIgnoreCase(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equalsIgnoreCase(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equalsIgnoreCase(uri.getAuthority());
    }

    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equalsIgnoreCase(uri.getAuthority());
    }
}
