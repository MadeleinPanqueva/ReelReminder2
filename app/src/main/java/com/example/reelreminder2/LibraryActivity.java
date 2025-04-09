package com.example.reelreminder2;

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
import com.example.reelreminder2.fragments.ContentDetailBottomSheet;
import com.example.reelreminder2.models.Content;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends BaseAuthenticatedActivity implements ContentDetailBottomSheet.OnContentActionListener {
    
    private RecyclerView rvContent;
    private TextView tvEmptyLibrary;
    private TextView tvFilterAll;
    private TextView tvFilterMovies;
    private TextView tvFilterSeries;
    private TextView tvFilterYear;
    private TextView tvFilterGenre;
    private EditText etSearch;
    private ContentAdapter adapter;
    private List<Content> allContent;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_library;
    }

    @Override
    protected void initializeViews() {
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
                ContentDetailBottomSheet bottomSheet = ContentDetailBottomSheet.newInstance(content);
                bottomSheet.setContent(content);
                bottomSheet.setOnContentActionListener(this);
                bottomSheet.show(getSupportFragmentManager(), "content_detail");
            }
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
        // Usar la lista estática de DashboardActivity
        allContent = new ArrayList<>(DashboardActivity.getAllContent());
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
        Toast.makeText(this, "Seleccionar año", Toast.LENGTH_SHORT).show();
    }
    
    private void showGenrePicker() {
        Toast.makeText(this, "Seleccionar género", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
    }

    @Override
    public void onWatchedStateChanged(Content content) {
        adapter.notifyDataSetChanged();
    }
} 