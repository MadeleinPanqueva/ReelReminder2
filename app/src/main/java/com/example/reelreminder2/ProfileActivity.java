package com.example.reelreminder2;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.reelreminder2.models.Content;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProfileActivity extends BaseAuthenticatedActivity {
    
    private TextView tvUserEmail;
    private TextView tvContentCount;
    private TextView tvWatchedCount;
    private Button btnLogout;
    private ImageView ivProfileIcon;
    private SessionManager sessionManager;
    private File photoFile;
    
    // Lanzador para seleccionar imagen de galería
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        sessionManager.saveProfileImageUri(imageUri.toString());
                        loadProfileImage();
                    }
                }
            }
    );

    // Lanzador para la cámara
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && photoFile != null) {
                    Uri photoUri = Uri.fromFile(photoFile);
                    sessionManager.saveProfileImageUri(photoUri.toString());
                    loadProfileImage();
                }
            }
    );

    // Lanzador para solicitar permisos
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    showImageSourceDialog();
                } else {
                    Toast.makeText(this, "Se requiere permiso para acceder a la cámara", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initializeViews() {
        // Initialize managers
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
        ivProfileIcon.setOnClickListener(v -> checkAndRequestPermissions());
        
        // Set logout button click listener
        btnLogout.setOnClickListener(v -> logout());
    }
    
    private void checkAndRequestPermissions() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        } else {
            showImageSourceDialog();
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Tomar foto", "Seleccionar de galería"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen de perfil")
               .setItems(options, (dialog, which) -> {
                   if (which == 0) {
                       openCamera();
                   } else {
                       openGallery();
                   }
               })
               .show();
    }

    private void openCamera() {
        try {
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.reelreminder2.fileprovider",
                        photoFile);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                
                try {
                    cameraLauncher.launch(takePictureIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al abrir la cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        );
    }
    
    private void loadProfileImage() {
        String imageUri = sessionManager.getProfileImageUri();
        
        if (imageUri != null && !imageUri.isEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(ivProfileIcon);
        } else {
            ivProfileIcon.setImageResource(R.drawable.ic_profile);
        }
    }
    
    private void loadUserStats() {
        List<Content> allContent = DashboardActivity.getAllContent();
        int totalContent = allContent.size();
        int watchedContent = 0;
        
        for (Content content : allContent) {
            if (content.isWatched()) {
                watchedContent++;
            }
        }
        
        tvContentCount.setText(String.valueOf(totalContent));
        tvWatchedCount.setText(String.valueOf(watchedContent));
    }
    
    private void logout() {
        sessionManager.logoutUser();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadUserStats();
        loadProfileImage();
    }
} 