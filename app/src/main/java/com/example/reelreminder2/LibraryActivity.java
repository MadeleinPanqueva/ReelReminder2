package com.example.reelreminder2;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reelreminder2.adapters.ContentAdapter;
import com.example.reelreminder2.models.Content;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends BaseAuthenticatedActivity {
    
    private RecyclerView rvContent;
    private TextView tvEmptyLibrary;
    private TextView tvFilterAll;
    private TextView tvFilterMovies;
    private TextView tvFilterSeries;
    private TextView tvFilterYear;
    private TextView tvFilterGenre;
    private EditText etSearch;
    private DatabaseHelper dbHelper;
    private ContentAdapter adapter;
    private List<Content> allContent;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_library;
    }

    @Override
    protected void initializeViews() {
        dbHelper = new DatabaseHelper(this);
        
        // Initialize views
        rvContent = findViewById(R.id.rvContent);
        tvEmptyLibrary = findViewById(R.id.tvEmptyLibrary);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterMovies = findViewById(R.id.tvFilterMovies);
        tvFilterSeries = findViewById(R.id.tvFilterSeries);
        tvFilterYear = findViewById(R.id.tvFilterYear);
        tvFilterGenre = findViewById(R.id.tvFilterGenre);
        etSearch = findViewById(R.id.etSearch);

        // Setup RecyclerView
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter(
            new ArrayList<>(), 
            content -> {
                // TODO: Handle content item click
                Toast.makeText(this, "Seleccionado: " + content.getTitle(), Toast.LENGTH_SHORT).show();
            },
            dbHelper
        );
        rvContent.setAdapter(adapter);

        // Setup search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set click listeners
        tvFilterAll.setOnClickListener(v -> applyFilter(null, null));
        tvFilterMovies.setOnClickListener(v -> applyFilter("Película", null));
        tvFilterSeries.setOnClickListener(v -> applyFilter("Serie", null));
        tvFilterYear.setOnClickListener(v -> showYearPicker());
        tvFilterGenre.setOnClickListener(v -> showGenrePicker());

        // Load initial content
        loadContent();
    }
    
    private void loadContent() {
        allContent = new ArrayList<>();
        Cursor cursor = dbHelper.getAllContent();
        
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
                allContent.add(content);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Si no hay contenido, insertar datos de ejemplo
        if (allContent.isEmpty()) {
            dbHelper.insertSampleContent();
            loadContent(); // Recargar después de insertar
            return;
        }

        adapter.updateContent(allContent);
        updateEmptyState();
    }

    private void filterContent(String query) {
        if (allContent == null) return;

        List<Content> filteredList = new ArrayList<>();
        for (Content content : allContent) {
            if (content.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                content.getGenre().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(content);
            }
        }
        adapter.updateContent(filteredList);
        updateEmptyState();
    }
    
    private void applyFilter(String type, String genre) {
        if (allContent == null) return;

        List<Content> filteredList = new ArrayList<>();
        for (Content content : allContent) {
            boolean matchesType = type == null || content.getType().equals(type);
            boolean matchesGenre = genre == null || content.getGenre().equals(genre);
            
            if (matchesType && matchesGenre) {
                filteredList.add(content);
            }
        }
        adapter.updateContent(filteredList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyLibrary.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
        } else {
            tvEmptyLibrary.setVisibility(View.GONE);
            rvContent.setVisibility(View.VISIBLE);
        }
    }
    
    private void showYearPicker() {
        // TODO: Implement year picker dialog
        Toast.makeText(this, "Seleccionar año", Toast.LENGTH_SHORT).show();
    }
    
    private void showGenrePicker() {
        // TODO: Implement genre picker dialog
        Toast.makeText(this, "Seleccionar género", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent(); // Recargar contenido al volver a la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 