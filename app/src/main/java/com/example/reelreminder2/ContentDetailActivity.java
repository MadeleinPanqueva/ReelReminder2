package com.example.reelreminder2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.reelreminder2.models.Content;
import com.google.android.material.button.MaterialButton;

public class ContentDetailActivity extends BaseAuthenticatedActivity {

    private ImageView ivContentImage;
    private TextView tvTitle;
    private TextView tvType;
    private TextView tvDuration;
    private TextView tvGenre;
    private TextView tvYear;
    private TextView tvDetails;
    private MaterialButton btnWatched;
    private Content content;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content_detail;
    }

    @Override
    protected void initializeViews() {
        // Initialize views
        ivContentImage = findViewById(R.id.ivContentImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvType = findViewById(R.id.tvType);
        tvDuration = findViewById(R.id.tvDuration);
        tvGenre = findViewById(R.id.tvGenre);
        tvYear = findViewById(R.id.tvYear);
        tvDetails = findViewById(R.id.tvDetails);
        btnWatched = findViewById(R.id.btnWatched);

        // Get content ID from intent
        int contentId = getIntent().getIntExtra("content_id", -1);
        if (contentId == -1) {
            Toast.makeText(this, "Error al cargar el contenido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find content by ID
        for (Content c : DashboardActivity.getAllContent()) {
            if (c.getId() == contentId) {
                content = c;
                break;
            }
        }

        if (content == null) {
            Toast.makeText(this, "Contenido no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load content details
        loadContentDetails();
    }

    private void loadContentDetails() {
        // Set title in toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(content.getTitle());
        }

        // Load image
        Glide.with(this)
            .load(content.getImagePath())
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(ivContentImage);

        // Set text views
        tvTitle.setText(content.getTitle());
        tvType.setText(content.getType());
        tvDuration.setText(String.format("%d min", content.getDuration()));
        tvGenre.setText(content.getGenre());
        tvYear.setText(String.valueOf(content.getYear()));

        // Mostrar detalles si existen
        if (content.getDetails() != null && !content.getDetails().isEmpty()) {
            tvDetails.setText(content.getDetails());
            tvDetails.setVisibility(View.VISIBLE);
        } else {
            tvDetails.setVisibility(View.GONE);
        }

        // Configure watched button
        updateWatchedButton();
        btnWatched.setOnClickListener(v -> {
            content.setWatched(!content.isWatched());
            updateWatchedButton();
        });
    }

    private void updateWatchedButton() {
        btnWatched.setText(content.isWatched() ? "Visto" : "Marcar como visto");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 