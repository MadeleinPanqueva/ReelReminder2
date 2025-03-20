package com.example.reelreminder2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reelreminder.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_CONTENT = "contenido";
    
    // Table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "tipo";
    public static final String COLUMN_TITLE = "titulo";
    public static final String COLUMN_YEAR = "año";
    public static final String COLUMN_GENRE = "genero";
    public static final String COLUMN_DURATION = "duracion";
    public static final String COLUMN_IMAGE_PATH = "imagen_ruta";
    public static final String COLUMN_STATUS = "estado";
    
    // Status values
    public static final String STATUS_IN_PROGRESS = "En progreso";
    public static final String STATUS_WATCHED = "Visto";
    
    // Type values
    public static final String TYPE_MOVIE = "Película";
    public static final String TYPE_SERIES = "Serie";

    // Create table SQL query
    private static final String CREATE_TABLE_CONTENT = "CREATE TABLE " + TABLE_CONTENT + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TYPE + " TEXT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_YEAR + " TEXT,"
            + COLUMN_GENRE + " TEXT,"
            + COLUMN_DURATION + " TEXT,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_STATUS + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_CONTENT);
        
        // Insert default user (for testing purposes)
        // Note: In a real app, user credentials should be stored securely
        // This is just for demonstration purposes
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        
        // Create tables again
        onCreate(db);
    }
    
    // Insert new content
    public long insertContent(String type, String title, String year, String genre, 
                             String duration, String imagePath, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_GENRE, genre);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_STATUS, status);
        
        // Insert row
        long id = db.insert(TABLE_CONTENT, null, values);
        
        // Close database connection
        db.close();
        
        return id;
    }
    
    // Get all content
    public List<Map<String, String>> getAllContent() {
        List<Map<String, String>> contentList = new ArrayList<>();
        
        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_CONTENT;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> content = new HashMap<>();
                content.put(COLUMN_ID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                content.put(COLUMN_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                content.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                content.put(COLUMN_YEAR, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                content.put(COLUMN_GENRE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)));
                content.put(COLUMN_DURATION, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                content.put(COLUMN_IMAGE_PATH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                content.put(COLUMN_STATUS, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                
                contentList.add(content);
            } while (cursor.moveToNext());
        }
        
        // Close cursor and database
        cursor.close();
        db.close();
        
        return contentList;
    }
    
    // Get content by status
    public List<Map<String, String>> getContentByStatus(String status) {
        List<Map<String, String>> contentList = new ArrayList<>();
        
        // Select query
        String selectQuery = "SELECT * FROM " + TABLE_CONTENT + " WHERE " + COLUMN_STATUS + " = ?";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{status});
        
        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> content = new HashMap<>();
                content.put(COLUMN_ID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                content.put(COLUMN_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                content.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                content.put(COLUMN_YEAR, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                content.put(COLUMN_GENRE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)));
                content.put(COLUMN_DURATION, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                content.put(COLUMN_IMAGE_PATH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                content.put(COLUMN_STATUS, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                
                contentList.add(content);
            } while (cursor.moveToNext());
        }
        
        // Close cursor and database
        cursor.close();
        db.close();
        
        return contentList;
    }
    
    // Get content by ID
    public Map<String, String> getContentById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_CONTENT, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        
        Map<String, String> content = new HashMap<>();
        
        if (cursor != null && cursor.moveToFirst()) {
            content.put(COLUMN_ID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            content.put(COLUMN_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
            content.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            content.put(COLUMN_YEAR, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
            content.put(COLUMN_GENRE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)));
            content.put(COLUMN_DURATION, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
            content.put(COLUMN_IMAGE_PATH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
            content.put(COLUMN_STATUS, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            
            cursor.close();
        }
        
        db.close();
        
        return content;
    }
    
    // Update content status
    public int updateContentStatus(long id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        
        // Updating row
        int result = db.update(TABLE_CONTENT, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        
        // Close database connection
        db.close();
        
        return result;
    }
    
    // Search content by title
    public List<Map<String, String>> searchContent(String query) {
        List<Map<String, String>> contentList = new ArrayList<>();
        
        // Select query
        String selectQuery = "SELECT * FROM " + TABLE_CONTENT + " WHERE " + COLUMN_TITLE + " LIKE ?";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + query + "%"});
        
        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> content = new HashMap<>();
                content.put(COLUMN_ID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                content.put(COLUMN_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                content.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                content.put(COLUMN_YEAR, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                content.put(COLUMN_GENRE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)));
                content.put(COLUMN_DURATION, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                content.put(COLUMN_IMAGE_PATH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                content.put(COLUMN_STATUS, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                
                contentList.add(content);
            } while (cursor.moveToNext());
        }
        
        // Close cursor and database
        cursor.close();
        db.close();
        
        return contentList;
    }
    
    // Filter content by year, genre, and type
    public List<Map<String, String>> filterContent(String year, String genre, String type) {
        List<Map<String, String>> contentList = new ArrayList<>();
        
        StringBuilder selectQuery = new StringBuilder("SELECT * FROM " + TABLE_CONTENT + " WHERE 1=1");
        List<String> args = new ArrayList<>();
        
        if (year != null && !year.isEmpty()) {
            selectQuery.append(" AND ").append(COLUMN_YEAR).append(" = ?");
            args.add(year);
        }
        
        if (genre != null && !genre.isEmpty()) {
            selectQuery.append(" AND ").append(COLUMN_GENRE).append(" = ?");
            args.add(genre);
        }
        
        if (type != null && !type.isEmpty()) {
            selectQuery.append(" AND ").append(COLUMN_TYPE).append(" = ?");
            args.add(type);
        }
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery.toString(), args.toArray(new String[0]));
        
        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> content = new HashMap<>();
                content.put(COLUMN_ID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                content.put(COLUMN_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                content.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                content.put(COLUMN_YEAR, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                content.put(COLUMN_GENRE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)));
                content.put(COLUMN_DURATION, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                content.put(COLUMN_IMAGE_PATH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                content.put(COLUMN_STATUS, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                
                contentList.add(content);
            } while (cursor.moveToNext());
        }
        
        // Close cursor and database
        cursor.close();
        db.close();
        
        return contentList;
    }
    
    // Get statistics for profile
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Count movies watched
        String movieQuery = "SELECT COUNT(*) FROM " + TABLE_CONTENT + 
                " WHERE " + COLUMN_TYPE + " = ? AND " + COLUMN_STATUS + " = ?";
        Cursor movieCursor = db.rawQuery(movieQuery, 
                new String[]{TYPE_MOVIE, STATUS_WATCHED});
        
        if (movieCursor.moveToFirst()) {
            stats.put("movies_watched", movieCursor.getInt(0));
        }
        movieCursor.close();
        
        // Count series watched
        String seriesQuery = "SELECT COUNT(*) FROM " + TABLE_CONTENT + 
                " WHERE " + COLUMN_TYPE + " = ? AND " + COLUMN_STATUS + " = ?";
        Cursor seriesCursor = db.rawQuery(seriesQuery, 
                new String[]{TYPE_SERIES, STATUS_WATCHED});
        
        if (seriesCursor.moveToFirst()) {
            stats.put("series_watched", seriesCursor.getInt(0));
        }
        seriesCursor.close();
        
        // Get most frequent genre
        String genreQuery = "SELECT " + COLUMN_GENRE + ", COUNT(*) as count FROM " + TABLE_CONTENT + 
                " WHERE " + COLUMN_STATUS + " = ? GROUP BY " + COLUMN_GENRE + 
                " ORDER BY count DESC LIMIT 1";
        Cursor genreCursor = db.rawQuery(genreQuery, new String[]{STATUS_WATCHED});
        
        if (genreCursor.moveToFirst()) {
            String mostFrequentGenre = genreCursor.getString(0);
            int genreCount = genreCursor.getInt(1);
            stats.put("genre_count", genreCount);
            // We can't put a String in the Map<String, Integer>, so we'll handle this separately
        }
        genreCursor.close();
        
        db.close();
        
        return stats;
    }
    
    // Get most frequent genre
    public String getMostFrequentGenre() {
        SQLiteDatabase db = this.getReadableDatabase();
        String genre = "";
        
        String genreQuery = "SELECT " + COLUMN_GENRE + " FROM " + TABLE_CONTENT + 
                " WHERE " + COLUMN_STATUS + " = ? GROUP BY " + COLUMN_GENRE + 
                " ORDER BY COUNT(*) DESC LIMIT 1";
        Cursor genreCursor = db.rawQuery(genreQuery, new String[]{STATUS_WATCHED});
        
        if (genreCursor.moveToFirst()) {
            genre = genreCursor.getString(0);
        }
        genreCursor.close();
        db.close();
        
        return genre;
    }
} 