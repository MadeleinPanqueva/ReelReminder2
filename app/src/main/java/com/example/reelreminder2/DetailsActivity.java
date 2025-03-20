package com.example.reelreminder2;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DetailsActivity extends BaseAuthenticatedActivity {
    
    private RecyclerView rvInProgress;
    private TextView tvEmptyDetails;
    private DatabaseHelper databaseHelper;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_details;
    }

    @Override
    protected void initializeViews() {
        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Initialize views
        rvInProgress = findViewById(R.id.rvInProgress);
        tvEmptyDetails = findViewById(R.id.tvEmptyDetails);
        
        // Load content in progress
        loadInProgressContent();
    }
    
    private void loadInProgressContent() {
        // TODO: Implement content loading with RecyclerView adapter
        // For now, just show empty state
        tvEmptyDetails.setVisibility(View.VISIBLE);
        rvInProgress.setVisibility(View.GONE);
    }
} 