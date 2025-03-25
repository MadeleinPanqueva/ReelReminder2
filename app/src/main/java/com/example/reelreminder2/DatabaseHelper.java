package com.example.reelreminder2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.reelreminder2.models.Content;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ReelReminder.db";
    private static final int DATABASE_VERSION = 3;

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
    public static final String COLUMN_WATCHED = "watched";

    // Create table queries
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT,"
            + COLUMN_CREATED_AT + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_CONTENT = "CREATE TABLE " + TABLE_CONTENT + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_TYPE + " TEXT,"
            + COLUMN_DURATION + " INTEGER,"
            + COLUMN_GENRE + " TEXT,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_YEAR + " INTEGER,"
            + COLUMN_WATCHED + " INTEGER DEFAULT 0,"
            + COLUMN_CREATED_AT + " INTEGER"
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
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CONTENT + " ADD COLUMN " + COLUMN_YEAR + " INTEGER");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_CONTENT + " ADD COLUMN " + COLUMN_WATCHED + " INTEGER DEFAULT 0");
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
        values.put(COLUMN_WATCHED, content.isWatched() ? 1 : 0);
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
            content.setWatched(cursor.getInt(cursor.getColumnIndex(COLUMN_WATCHED)) == 1);
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
        values.put(COLUMN_WATCHED, content.isWatched() ? 1 : 0);
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

    public void setContentWatched(long id, boolean watched) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WATCHED, watched ? 1 : 0);
        db.update(TABLE_CONTENT, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public Cursor getWatchedContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTENT, null, 
                COLUMN_WATCHED + "=?", 
                new String[]{"1"}, 
                null, null, 
                COLUMN_CREATED_AT + " DESC");
    }

    public Cursor getUnwatchedContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTENT, null, 
                COLUMN_WATCHED + "=?", 
                new String[]{"0"}, 
                null, null, 
                COLUMN_CREATED_AT + " DESC");
    }

    public void insertSampleContent() {
        Content[] sampleContent = {
            createSampleContent("El Padrino", "Película", 175, "Drama", 
                "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg", 1972),
            createSampleContent("Pulp Fiction", "Película", 154, "Crimen", 
                "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg", 1994),
            createSampleContent("Inception", "Película", 148, "Ciencia Ficción", "https://example.com/inception.jpg", 2010),
            createSampleContent("La La Land", "Película", 128, "Musical", "https://example.com/lalaland.jpg", 2016),
            createSampleContent("El Señor de los Anillos", "Película", 178, "Fantasía", "https://example.com/lotr.jpg", 2001),
            createSampleContent("Parásitos", "Película", 132, "Drama", "https://example.com/parasite.jpg", 2019),
            createSampleContent("Matrix", "Película", 136, "Ciencia Ficción", "https://example.com/matrix.jpg", 1999),
            createSampleContent("El Rey León", "Película", 88, "Animación", "https://example.com/lionking.jpg", 1994),
            createSampleContent("Titanic", "Película", 195, "Romance", "https://example.com/titanic.jpg", 1997),
            createSampleContent("El Caballero Oscuro", "Película", 152, "Acción", "https://example.com/dark_knight.jpg", 2008),

            // Series
            createSampleContent("Breaking Bad", "Serie", 45, "Drama", "https://example.com/breaking_bad.jpg", 2008),
            createSampleContent("Stranger Things", "Serie", 50, "Ciencia Ficción", "https://example.com/stranger_things.jpg", 2016),
            createSampleContent("The Crown", "Serie", 58, "Drama Histórico", "https://example.com/the_crown.jpg", 2016),
            createSampleContent("Game of Thrones", "Serie", 60, "Fantasía", "https://example.com/got.jpg", 2011),
            createSampleContent("The Office", "Serie", 22, "Comedia", "https://example.com/the_office.jpg", 2005),
            createSampleContent("Black Mirror", "Serie", 60, "Ciencia Ficción", "https://example.com/black_mirror.jpg", 2011),
            createSampleContent("The Mandalorian", "Serie", 40, "Ciencia Ficción", "https://example.com/mandalorian.jpg", 2019),
            createSampleContent("Chernobyl", "Serie", 60, "Drama Histórico", "https://example.com/chernobyl.jpg", 2019),
            createSampleContent("The Witcher", "Serie", 60, "Fantasía", "https://example.com/witcher.jpg", 2019),
            createSampleContent("Friends", "Serie", 22, "Comedia", "https://example.com/friends.jpg", 1994)
        };

        for (Content content : sampleContent) {
            insertContent(content);
        }
    }

    private Content createSampleContent(String title, String type, int duration, String genre, String imagePath, int year) {
        Content content = new Content();
        content.setTitle(title);
        content.setType(type);
        content.setDuration(duration);
        content.setGenre(genre);
        content.setImagePath(imagePath);
        content.setYear(year);
        content.setWatched(false); // Por defecto, ningún contenido está visto
        content.setCreatedAt(System.currentTimeMillis());
        return content;
    }
} 