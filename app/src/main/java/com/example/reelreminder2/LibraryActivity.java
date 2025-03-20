package com.example.reelreminder2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryActivity extends BaseAuthenticatedActivity {
    
    private RecyclerView rvContent;
    private TextView tvEmptyLibrary;
    private TextView tvFilterAll;
    private TextView tvFilterMovies;
    private TextView tvFilterSeries;
    private TextView tvFilterYear;
    private TextView tvFilterGenre;
    private DatabaseHelper dbHelper;

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

        // Setup RecyclerView
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set adapter

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
        // TODO: Implement content loading with RecyclerView adapter
        tvEmptyLibrary.setVisibility(View.VISIBLE);
        rvContent.setVisibility(View.GONE);
    }
    
    private void applyFilter(String type, String genre) {
        // TODO: Implement filtering logic
        Toast.makeText(this, "Filtro aplicado: " + (type != null ? type : "Todos"), Toast.LENGTH_SHORT).show();
    }
    
    private void showYearPicker() {
        // TODO: Implement year picker dialog
        Toast.makeText(this, "Seleccionar año", Toast.LENGTH_SHORT).show();
    }
    
    private void showGenrePicker() {
        // TODO: Implement genre picker dialog
        Toast.makeText(this, "Seleccionar género", Toast.LENGTH_SHORT).show();
    }
} 