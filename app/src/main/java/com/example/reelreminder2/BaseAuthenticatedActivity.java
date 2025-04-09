package com.example.reelreminder2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity {
    
    protected SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar el tema antes de super.onCreate
        setTheme(ThemeManager.getThemeResource(ThemeManager.getInstance(this).isDarkTheme()));
        super.onCreate(savedInstanceState);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // Set content view
        setContentView(getLayoutResourceId());
        
        // Setup back button if needed
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
        
        // Initialize views
        initializeViews();
    }
    
    // Method to be implemented by child classes to provide layout resource ID
    protected abstract int getLayoutResourceId();
    
    // Method to be implemented by child classes to initialize views
    protected abstract void initializeViews();
} 