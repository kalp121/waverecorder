package com.danielkim.soundrecorder;

import android.content.Context;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Helper {
    public static String getPath(Context context, String folderName) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return "/SoundRecorder/" + folderName + File.separator + new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH).format(cal.getTime()) + File.separator;
    }

    public static String getSimplePath(Context context, String folderName) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return "/SoundRecorder/" + folderName + File.separator + new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH).format(cal.getTime());
    }


    public static String getFolderName(Context context) {
        int folderIndex = MySharedPreferences.getColumnIndex(context);
        String folderName;
        switch (folderIndex) {
            case 3:
                folderName = "gu";
                break;
            case 2:
                folderName = "en";
                break;
            case 4:
                folderName = "hi";
                break;
            default:
                folderName = "gu";
                break;
        }
        return folderName;
    }

    public static String getFileName(Context context) {
        int folderIndex = MySharedPreferences.getFileNameColumnIndex(context);
        String folderName;
        switch (folderIndex) {
            case 3:
                folderName = "gu";
                break;
            case 2:
                folderName = "en";
                break;
            case 4:
                folderName = "hi";
                break;
            default:
                folderName = "gu";
                break;
        }
        return folderName;
    }

    public static String getSimpleFolderName(Context context, String folderName) {
        return folderName + File.separator + new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH).format(Calendar.getInstance(Locale.getDefault()).getTime());
    }

    public static String getAppFolderPath() {
        return android.os.Environment.getExternalStorageDirectory().toString() + "/SoundRecorder";
    }

    public static String getDownloadFilePath() {
        return android.os.Environment.getExternalStorageDirectory().toString() + "/SoundRecorder" + File.separator + "Downloads";
    }

    public static String getTodayDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(System.currentTimeMillis());
    }

//    public static String getZipFileSuffix() {
//
//    }
}
