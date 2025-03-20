package com.example.reelreminder2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.reelreminder2.models.Content;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ReelReminder.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_USERS = "users";
    public static final String TABLE_CONTENT = "content";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Users table columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    // Content table columns
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_YEAR = "year";

    // Create table queries
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT,"
            + COLUMN_CREATED_AT + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_CONTENT = "CREATE TABLE " + TABLE_CONTENT + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_TYPE + " TEXT NOT NULL, " +
            COLUMN_DURATION + " INTEGER, " +
            COLUMN_GENRE + " TEXT, " +
            COLUMN_IMAGE_PATH + " TEXT, " +
            COLUMN_YEAR + " INTEGER, " +
            COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CONTENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CONTENT + 
                      " ADD COLUMN " + COLUMN_YEAR + " INTEGER");
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        onCreate(db);
    }

    // User related methods
    public long createUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, password);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = KEY_EMAIL + "=? AND " + KEY_PASSWORD + "=?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Content related methods
    public long insertContent(Content content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, content.getTitle());
        values.put(COLUMN_TYPE, content.getType());
        values.put(COLUMN_DURATION, content.getDuration());
        values.put(COLUMN_GENRE, content.getGenre());
        values.put(COLUMN_IMAGE_PATH, content.getImagePath());
        values.put(COLUMN_YEAR, content.getYear());
        values.put(COLUMN_CREATED_AT, content.getCreatedAt());
        return db.insert(TABLE_CONTENT, null, values);
    }

    public Content getContent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTENT, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Content content = new Content();
            content.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            content.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            content.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
            content.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
            content.setGenre(cursor.getString(cursor.getColumnIndex(COLUMN_GENRE)));
            content.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)));
            content.setYear(cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR)));
            content.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT)));
            cursor.close();
            return content;
        }
        return null;
    }

    public Cursor getAllContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTENT, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");
    }

    public int updateContent(Content content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, content.getTitle());
        values.put(COLUMN_TYPE, content.getType());
        values.put(COLUMN_DURATION, content.getDuration());
        values.put(COLUMN_GENRE, content.getGenre());
        values.put(COLUMN_IMAGE_PATH, content.getImagePath());
        values.put(COLUMN_YEAR, content.getYear());
        return db.update(TABLE_CONTENT, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(content.getId())});
    }

    public void deleteContent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTENT, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public Cursor getRecentContent(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTENT, null, null, null, null, null, 
                       COLUMN_CREATED_AT + " DESC", String.valueOf(limit));
    }

    public Cursor searchContent(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchPattern = "%" + query + "%";
        String selection = COLUMN_TITLE + " LIKE ? OR " +
                         COLUMN_TYPE + " LIKE ? OR " +
                         COLUMN_GENRE + " LIKE ?";
        String[] selectionArgs = {searchPattern, searchPattern, searchPattern};
        return db.query(TABLE_CONTENT, null, selection, selectionArgs, null, null, 
                       COLUMN_CREATED_AT + " DESC");
    }
} 