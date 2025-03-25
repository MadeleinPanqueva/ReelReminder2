package com.example.reelreminder2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reelreminder2.adapters.ContentAdapter;
import com.example.reelreminder2.models.Content;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends BaseAuthenticatedActivity {
    
    private RecyclerView rvInProgress;
    private TextView tvEmptyDetails;
    private DatabaseHelper dbHelper;
    private ContentAdapter adapter;
    private List<Content> unwatchedContent;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_details;
    }

    @Override
    protected void initializeViews() {
        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        
        // Initialize views
        rvInProgress = findViewById(R.id.rvInProgress);
        tvEmptyDetails = findViewById(R.id.tvEmptyDetails);
        
        // Setup RecyclerView
        rvInProgress.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter(
            new ArrayList<>(),
            content -> {
                Toast.makeText(this, "Seleccionado: " + content.getTitle(), Toast.LENGTH_SHORT).show();
            },
            dbHelper
        );
        rvInProgress.setAdapter(adapter);
        
        // Load unwatched content
        loadInProgressContent();
    }
    
    private void loadInProgressContent() {
        unwatchedContent = new ArrayList<>();
        Cursor cursor = dbHelper.getUnwatchedContent();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Content content = new Content();
                content.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                content.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
                content.setType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE)));
                content.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DURATION)));
                content.setGenre(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENRE)));
                content.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH)));
                content.setYear(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR)));
                content.setWatched(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_WATCHED)) == 1);
                content.setCreatedAt(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                unwatchedContent.add(content);
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter.updateContent(unwatchedContent);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyDetails.setVisibility(View.VISIBLE);
            rvInProgress.setVisibility(View.GONE);
        } else {
            tvEmptyDetails.setVisibility(View.GONE);
            rvInProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInProgressContent(); // Recargar contenido al volver a la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 