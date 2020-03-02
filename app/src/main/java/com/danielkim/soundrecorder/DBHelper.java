package com.danielkim.soundrecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.danielkim.soundrecorder.listeners.OnDatabaseChangedListener;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Daniel on 12/29/2014.
 */
public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static final String LOG_TAG = "DBHelper";

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    public static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;


    public static abstract class DBHelperItem implements BaseColumns {
        public static final String TABLE_NAME = "saved_recordings";

        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
        public static final String COLUMN_NAME_FOLDER = "folder";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME + " (" +
                    DBHelperItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_FOLDER + " TEXT " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }

    public void deleteAllFilesFromFolder(ArrayList<String> name) {
        SQLiteDatabase db = getWritableDatabase();
        for (String n : name) {
            String[] whereArgs = {String.valueOf(n)};
            db.delete(DBHelperItem.TABLE_NAME, DBHelperItem.COLUMN_NAME_FOLDER + "=?", whereArgs);
        }
        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onDatabaseEntryRemoved();
        }
    }

    public RecordingItem getItemAt(int position, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBHelperItem._ID,
                DBHelperItem.COLUMN_NAME_RECORDING_NAME,
                DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH,
                DBHelperItem.COLUMN_NAME_RECORDING_LENGTH,
                DBHelperItem.COLUMN_NAME_TIME_ADDED
        };
        Cursor c;
        if (date.isEmpty()) {
            c = db.rawQuery("select max(" + DBHelperItem._ID + ") " + DBHelperItem._ID + "," + DBHelperItem.COLUMN_NAME_RECORDING_NAME + "," + DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH + "," +
                    DBHelperItem.COLUMN_NAME_RECORDING_LENGTH + "," +
                    DBHelperItem.COLUMN_NAME_TIME_ADDED + "," + DBHelperItem.COLUMN_NAME_FOLDER + " from " + DBHelperItem.TABLE_NAME + " GROUP BY " + DBHelperItem.COLUMN_NAME_FOLDER + ";", null);

        } else {
            c = db.rawQuery("select " + DBHelperItem._ID + "," + DBHelperItem.COLUMN_NAME_RECORDING_NAME + "," + DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH + "," +
                            DBHelperItem.COLUMN_NAME_RECORDING_LENGTH + "," +
                            DBHelperItem.COLUMN_NAME_TIME_ADDED + "," + DBHelperItem.COLUMN_NAME_FOLDER + " from " + DBHelperItem.TABLE_NAME + " where " + DBHelperItem.COLUMN_NAME_FOLDER + " like '%" + date + "%';"
                    , null);
        }
//        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            RecordingItem item = new RecordingItem();
            item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
            item.setName(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_NAME)));
            item.setFilePath(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH)));
            item.setLength(c.getInt(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH)));
            item.setTime(c.getLong(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME_ADDED)));
            item.setFolder(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_FOLDER)));
            c.close();
            return item;
        }
        return null;
    }

    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        db.delete(DBHelperItem.TABLE_NAME, "_ID=?", whereArgs);
    }

    public int getCount(String date) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {DBHelperItem._ID};
        Cursor c;
        if (date.isEmpty()) {
            c = db.rawQuery("select max(" + DBHelperItem._ID + ")" + " from " + DBHelperItem.TABLE_NAME + " GROUP BY " + DBHelperItem.COLUMN_NAME_FOLDER + ";", null);

        } else {
            c = db.rawQuery("select " + DBHelperItem._ID + " from " + DBHelperItem.TABLE_NAME + " where " + DBHelperItem.COLUMN_NAME_FOLDER + " like '%" + date + "%';"
                    , null);
        }
//        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public Context getContext() {
        return mContext;
    }

    public class RecordingComparator implements Comparator<RecordingItem> {
        public int compare(RecordingItem item1, RecordingItem item2) {
            Long o1 = item1.getTime();
            Long o2 = item2.getTime();
            return o2.compareTo(o1);
        }
    }

    public long addRecording(String recordingName, String filePath, long length, String simpleFolderName) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + DBHelperItem.TABLE_NAME + " where " + DBHelperItem.COLUMN_NAME_FOLDER + " = '" + simpleFolderName + "' and " + DBHelperItem.COLUMN_NAME_RECORDING_NAME + " = '" + recordingName + "'", null);
        if (c != null && c.getCount() > 0) {
            return c.getCount();
        }
        if (c != null) {
            c.close();
        }
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, length);
        cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
        cv.put(DBHelperItem.COLUMN_NAME_FOLDER, simpleFolderName);
        long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }

    public void renameItem(RecordingItem item, String recordingName, String filePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
        db.update(DBHelperItem.TABLE_NAME, cv,
                DBHelperItem._ID + "=" + item.getId(), null);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
        }
    }

    public long restoreRecording(RecordingItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, item.getName());
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, item.getFilePath());
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, item.getLength());
        cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED, item.getTime());
        cv.put(DBHelperItem._ID, item.getId());
        long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);
        if (mOnDatabaseChangedListener != null) {
            //mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }
        return rowId;
    }
}
