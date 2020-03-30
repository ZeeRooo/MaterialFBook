package me.zeeroooo.materialfb.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Created by https://github.com/mitchtabian. Thank you :)
 * Adapted by me.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MFBBookmarks.db";
    private static final String TABLE_NAME = "mfb_table";
    private static final String COL1 = "TITLE";
    private static final String COL2 = "URL";
    private static final String COL3 = "BL";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, URL TEXT, BL TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String title, String url, String blackword) {
        final ContentValues contentValues = new ContentValues();
        if (title != null && url != null) {
            contentValues.put(COL1, title);
            contentValues.put(COL2, url);
        } else
            contentValues.put(COL3, blackword);

        long result = getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        return result != -1;
    }

    public void remove(String title, String url, String s) {
        final SQLiteDatabase db = this.getWritableDatabase();
        String query;
        if (s == null)
            query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + title + "'" + " AND " + COL2 + " = '" + url + "'";
        else
            query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL3 + " = '" + s + "'";
        db.execSQL(query);
    }
}