package com.example.place_its;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "_name";
    public static final String KEY_YEAR = "_year";
    public static final String KEY_MONTH = "_month";
    public static final String KEY_DAY = "_day";
    private static final String TAG = "DBAdapter";
    
    private static final String DATABASE_NAME = "ReminderDB";
    private static final String DATABASE_TABLE = "_reminders";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
        "create table reminders (_id integer primary key autoincrement, "
        + "name text not null, email text not null);";
        
    private final Context context;    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	try {
        		db.execSQL(DATABASE_CREATE);	
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS reminders");
            onCreate(db);
        }
    }    

    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() {
        DBHelper.close();
    }
    
    //---insert a reminder into the database---
    public long insertReminder(String name, int year, int month, int day) {
        ContentValues initialValues = new ContentValues();
        initialValues.put( KEY_NAME, name  );
        initialValues.put( KEY_YEAR, year  );
        initialValues.put( KEY_MONTH, year );
        initialValues.put( KEY_DAY, year   );
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular reminder---
    public boolean deleteReminder(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the reminders---
    public Cursor getAllReminders() 
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_YEAR, KEY_MONTH, KEY_DAY}, null, null, null, null, null);
    }

    //---retrieves a particular reminder---
    public Cursor getReminder(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_NAME, KEY_YEAR, KEY_MONTH, KEY_DAY}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a reminder---
    public boolean updateReminder(long rowId, String name, int year, int month, int day) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put( KEY_YEAR, year );
        args.put( KEY_MONTH, month );
        args.put( KEY_DAY, day );
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
