package com.example.reelreminder2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {
    
    private ImageView ivProfile;
    private CardView cardLibrary;
    private CardView cardDetails;
    private FloatingActionButton fabAddContent;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Check if user is logged in, if not, redirect to login
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // Initialize views
        ivProfile = findViewById(R.id.ivProfile);
        cardLibrary = findViewById(R.id.cardLibrary);
        cardDetails = findViewById(R.id.cardDetails);
        fabAddContent = findViewById(R.id.fabAddContent);
        
        // Set click listeners
        ivProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        cardLibrary.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));
        cardDetails.setOnClickListener(v -> startActivity(new Intent(this, DetailsActivity.class)));
        fabAddContent.setOnClickListener(v -> {
            // TODO: Implement add content functionality
            // This will be implemented later with a dialog or new activity
        });
    }
} 