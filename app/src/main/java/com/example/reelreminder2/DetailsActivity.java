package com.example.reelreminder2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reelreminder2.adapters.ContentAdapter;
import com.example.reelreminder2.fragments.ContentDetailBottomSheet;
import com.example.reelreminder2.models.Content;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends BaseAuthenticatedActivity implements ContentDetailBottomSheet.OnContentActionListener {
    
    private RecyclerView rvInProgress;
    private TextView tvEmptyDetails;
    private ContentAdapter adapter;
    private List<Content> unwatchedContent;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_details;
    }

    @Override
    protected void initializeViews() {
        // Initialize views
        rvInProgress = findViewById(R.id.rvInProgress);
        tvEmptyDetails = findViewById(R.id.tvEmptyDetails);
        
        // Setup RecyclerView
        rvInProgress.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter(
            new ArrayList<>(),
            content -> {
                ContentDetailBottomSheet bottomSheet = ContentDetailBottomSheet.newInstance(content);
                bottomSheet.setContent(content);
                bottomSheet.setOnContentActionListener(this);
                bottomSheet.show(getSupportFragmentManager(), "content_detail");
            }
        );
        rvInProgress.setAdapter(adapter);
        
        // Load unwatched content
        loadInProgressContent();
    }
    
    private void loadInProgressContent() {
        unwatchedContent = new ArrayList<>();
        List<Content> allContent = DashboardActivity.getAllContent();
        
        for (Content content : allContent) {
            if (!content.isWatched()) {
                unwatchedContent.add(content);
            }
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
        loadInProgressContent();
    }

    @Override
    public void onWatchedStateChanged(Content content) {
        adapter.notifyDataSetChanged();
        // Si se marca como visto, debemos removerlo de la lista de pendientes
        if (content.isWatched()) {
            unwatchedContent.remove(content);
            adapter.updateContent(unwatchedContent);
            updateEmptyState();
        }
    }
} 