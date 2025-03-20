package com.example.reelreminder2;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LibraryActivity extends BaseAuthenticatedActivity {
    
    private RecyclerView rvContent;
    private TextView tvEmptyLibrary;
    private TextView tvFilterAll;
    private TextView tvFilterMovies;
    private TextView tvFilterSeries;
    private TextView tvFilterYear;
    private TextView tvFilterGenre;
    private DatabaseHelper databaseHelper;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_library;
    }

    @Override
    protected void initializeViews() {
        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Initialize views
        rvContent = findViewById(R.id.rvContent);
        tvEmptyLibrary = findViewById(R.id.tvEmptyLibrary);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterMovies = findViewById(R.id.tvFilterMovies);
        tvFilterSeries = findViewById(R.id.tvFilterSeries);
        tvFilterYear = findViewById(R.id.tvFilterYear);
        tvFilterGenre = findViewById(R.id.tvFilterGenre);
        
        // Set click listeners for filters
        tvFilterAll.setOnClickListener(v -> applyFilter(null, null));
        tvFilterMovies.setOnClickListener(v -> applyFilter(DatabaseHelper.TYPE_MOVIE, null));
        tvFilterSeries.setOnClickListener(v -> applyFilter(DatabaseHelper.TYPE_SERIES, null));
        tvFilterYear.setOnClickListener(v -> showYearPicker());
        tvFilterGenre.setOnClickListener(v -> showGenrePicker());
        
        // Load initial content
        loadContent();
    }
    
    private void loadContent() {
        // TODO: Implement content loading with RecyclerView adapter
        // For now, just show empty state
        tvEmptyLibrary.setVisibility(View.VISIBLE);
        rvContent.setVisibility(View.GONE);
    }
    
    private void applyFilter(String type, String genre) {
        // TODO: Implement filtering
    }
    
    private void showYearPicker() {
        // TODO: Implement year picker dialog
    }
    
    private void showGenrePicker() {
        // TODO: Implement genre picker dialog
    }
} 