package com.example.reelreminder2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.reelreminder2.models.Content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DashboardActivity extends AppCompatActivity {
    
    private ImageView ivProfile;
    private CardView cardLibrary;
    private CardView cardDetails;
    private FloatingActionButton fabAddContent;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private Uri selectedImageUri;
    private static final String[] CONTENT_TYPES = {"Película", "Serie", "Anime", "Documental"};

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Update the ImageView in the dialog
                        ImageView ivContentImage = findViewById(R.id.ivContentImage);
                        if (ivContentImage != null) {
                            ivContentImage.setImageURI(selectedImageUri);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        // Initialize SessionManager and DatabaseHelper
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        
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
        fabAddContent.setOnClickListener(v -> showAddContentDialog());
    }

    private void showAddContentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_content, null);
        
        // Initialize dialog views
        TextInputEditText etTitle = dialogView.findViewById(R.id.etTitle);
        AutoCompleteTextView actvType = dialogView.findViewById(R.id.actvType);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etDuration);
        TextInputEditText etGenre = dialogView.findViewById(R.id.etGenre);
        ImageView ivContentImage = dialogView.findViewById(R.id.ivContentImage);
        View btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);

        // Setup content type dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, 
            Arrays.asList(CONTENT_TYPES));
        actvType.setAdapter(adapter);

        // Setup image selection
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        builder.setView(dialogView)
                .setTitle("Agregar nuevo contenido")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String title = etTitle.getText().toString();
                    String type = actvType.getText().toString();
                    String durationStr = etDuration.getText().toString();
                    String genre = etGenre.getText().toString();

                    if (title.isEmpty() || type.isEmpty() || durationStr.isEmpty() || genre.isEmpty()) {
                        Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int duration = Integer.parseInt(durationStr);
                    String imagePath = null;

                    if (selectedImageUri != null) {
                        imagePath = saveImageToInternalStorage(selectedImageUri);
                    }

                    // Create Content object and save to database
                    Content content = new Content();
                    content.setTitle(title);
                    content.setType(type);
                    content.setDuration(duration);
                    content.setGenre(genre);
                    content.setImagePath(imagePath);

                    long id = dbHelper.insertContent(content);
                    if (id != -1) {
                        Toast.makeText(this, "Contenido agregado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al agregar el contenido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Create directory if it doesn't exist
            File directory = new File(getFilesDir(), "content_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create unique filename
            String filename = "content_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, filename);

            // Copy image to internal storage
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
} 