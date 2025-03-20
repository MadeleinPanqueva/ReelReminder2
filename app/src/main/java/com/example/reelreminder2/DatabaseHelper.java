package com.example.reelreminder2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.reelreminder2.models.Content;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ReelReminder.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CONTENT = "content";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Users table columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    // Content table columns
    private static final String KEY_TITLE = "title";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_IMAGE_PATH = "image_path";

    // Create table queries
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_CREATED_AT + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_CONTENT = "CREATE TABLE " + TABLE_CONTENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT,"
            + KEY_TYPE + " TEXT,"
            + KEY_DURATION + " INTEGER,"
            + KEY_GENRE + " TEXT,"
            + KEY_IMAGE_PATH + " TEXT,"
            + KEY_CREATED_AT + " INTEGER"
            + ")";

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
        values.put(KEY_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID};
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
        values.put(KEY_TITLE, content.getTitle());
        values.put(KEY_TYPE, content.getType());
        values.put(KEY_DURATION, content.getDuration());
        values.put(KEY_GENRE, content.getGenre());
        values.put(KEY_IMAGE_PATH, content.getImagePath());
        values.put(KEY_CREATED_AT, content.getCreatedAt());
        return db.insert(TABLE_CONTENT, null, values);
    }

    public Content getContent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTENT, null, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Content content = new Content();
            content.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            content.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            content.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
            content.setDuration(cursor.getInt(cursor.getColumnIndex(KEY_DURATION)));
            content.setGenre(cursor.getString(cursor.getColumnIndex(KEY_GENRE)));
            content.setImagePath(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)));
            content.setCreatedAt(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT)));
            cursor.close();
            return content;
        }
        return null;
    }

    public Cursor getAllContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTENT, null, null, null, null, null, KEY_CREATED_AT + " DESC");
    }

    public int updateContent(Content content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, content.getTitle());
        values.put(KEY_TYPE, content.getType());
        values.put(KEY_DURATION, content.getDuration());
        values.put(KEY_GENRE, content.getGenre());
        values.put(KEY_IMAGE_PATH, content.getImagePath());
        return db.update(TABLE_CONTENT, values, KEY_ID + "=?",
                new String[]{String.valueOf(content.getId())});
    }

    public void deleteContent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTENT, KEY_ID + "=?",
                new String[]{String.valueOf(id)});
    }
} 