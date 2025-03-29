package com.example.reelreminder2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;

public class ProfileActivity extends BaseAuthenticatedActivity {
    
    private DatabaseHelper dbHelper;
    private TextView tvUserEmail;
    private TextView tvContentCount;
    private TextView tvWatchedCount;
    private Button btnLogout;
    private ImageView ivProfileIcon;
    private SessionManager sessionManager;
    
    // Lanzador para seleccionar imagen
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Guardar la URI de la imagen seleccionada
                    sessionManager.saveProfileImageUri(uri.toString());
                    // Cargar la imagen en el ImageView
                    loadProfileImage();
                }
            }
    );

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initializeViews() {
        // Initialize database helper and session manager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        
        // Initialize views
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvContentCount = findViewById(R.id.tvContentCount);
        tvWatchedCount = findViewById(R.id.tvWatchedCount);
        btnLogout = findViewById(R.id.btnLogout);
        ivProfileIcon = findViewById(R.id.ivProfileIcon);
        
        // Set user email
        String userEmail = sessionManager.getUserEmail();
        tvUserEmail.setText(userEmail);
        
        // Load stats
        loadUserStats();
        
        // Load profile image
        loadProfileImage();
        
        // Set click listener for profile image
        ivProfileIcon.setOnClickListener(v -> {
            openImagePicker();
        });
        
        // Set logout button click listener
        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }
    
    private void loadProfileImage() {
        // Obtener la URI de la imagen de perfil guardada
        String imageUri = sessionManager.getProfileImageUri();
        
        if (imageUri != null && !imageUri.isEmpty()) {
            // Cargar la imagen guardada usando Glide
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(ivProfileIcon);
        } else {
            // Cargar la imagen predeterminada
            ivProfileIcon.setImageResource(R.drawable.ic_profile);
        }
    }
    
    private void openImagePicker() {
        try {
            // Lanzar el selector de imágenes
            imagePickerLauncher.launch("image/*");
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir el selector de imágenes", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadUserStats() {
        // Get total content count
        Cursor allContentCursor = dbHelper.getAllContent();
        int totalContent = allContentCursor != null ? allContentCursor.getCount() : 0;
        if (allContentCursor != null) {
            allContentCursor.close();
        }
        
        // Get watched content count
        Cursor watchedCursor = dbHelper.getWatchedContent();
        int watchedContent = watchedCursor != null ? watchedCursor.getCount() : 0;
        if (watchedCursor != null) {
            watchedCursor.close();
        }
        
        // Update UI
        tvContentCount.setText(String.valueOf(totalContent));
        tvWatchedCount.setText(String.valueOf(watchedContent));
    }
    
    private void logout() {
        // Cerrar la sesión
        sessionManager.logoutUser();
        
        // Para asegurarnos de que el usuario se redirija a la pantalla de login,
        // también podemos iniciar la actividad de login explícitamente
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity(); // Cierra todas las actividades de la tarea actual
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadUserStats();
        loadProfileImage(); // También refrescar la imagen de perfil
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 