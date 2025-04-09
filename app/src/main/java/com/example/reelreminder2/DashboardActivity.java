package com.example.reelreminder2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.reelreminder2.adapters.ContentAdapter;
import com.example.reelreminder2.models.Content;
import com.example.reelreminder2.fragments.ContentDetailBottomSheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements ContentDetailBottomSheet.OnContentActionListener {
    
    private ImageView ivProfile;
    private LinearLayout libraryButton;
    private LinearLayout detailsButton;
    private FloatingActionButton fabAddContent;
    private SessionManager sessionManager;
    private Uri selectedImageUri;
    private static final String[] CONTENT_TYPES = {"Película", "Serie", "Anime", "Documental"};
    private RecyclerView rvRecentContent;
    private ContentAdapter contentAdapter;
    private SearchView searchView;
    private List<Content> recentContentList;
    private static List<Content> allContent = new ArrayList<>();
    private File photoFile;
    private ImageView currentImageView;
    private static final int PERMISSION_REQUEST_CODE = 123;

    public static List<Content> getAllContent() {
        return allContent;
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && currentImageView != null) {
                        currentImageView.setImageURI(selectedImageUri);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && photoFile != null) {
                    selectedImageUri = Uri.fromFile(photoFile);
                    if (currentImageView != null) {
                        currentImageView.setImageURI(selectedImageUri);
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    showImageSourceDialog(currentImageView);
                } else {
                    Toast.makeText(this, "Se requiere permiso para acceder a la cámara/galería", Toast.LENGTH_SHORT).show();
                }
            });

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
        libraryButton = findViewById(R.id.libraryButton);
        detailsButton = findViewById(R.id.detailsButton);
        fabAddContent = findViewById(R.id.fabAdd);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        searchView = findViewById(R.id.searchView);
        rvRecentContent = findViewById(R.id.rvRecentContent);
        
        // Set click listeners
        ivProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        libraryButton.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));
        detailsButton.setOnClickListener(v -> startActivity(new Intent(this, DetailsActivity.class)));
        fabAddContent.setOnClickListener(v -> showAddContentDialog());

        // Configurar RecyclerView
        recentContentList = new ArrayList<>();
        contentAdapter = new ContentAdapter(
            recentContentList, 
            content -> {
                ContentDetailBottomSheet bottomSheet = ContentDetailBottomSheet.newInstance(content);
                bottomSheet.setContent(content);
                bottomSheet.setOnContentActionListener(this);
                bottomSheet.show(getSupportFragmentManager(), "content_detail");
            }
        );

        rvRecentContent.setLayoutManager(new LinearLayoutManager(this));
        rvRecentContent.setAdapter(contentAdapter);

        // Cargar contenido reciente
        loadRecentContent();

        // Configurar búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContent(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    searchContent(newText);
                } else if (newText.isEmpty()) {
                    loadRecentContent();
                }
                return true;
            }
        });
    }

    private void loadRecentContent() {
        recentContentList.clear();
        // Get the 5 most recent items from allContent
        int startIndex = Math.max(0, allContent.size() - 5);
        recentContentList.addAll(allContent.subList(startIndex, allContent.size()));
        contentAdapter.notifyDataSetChanged();
    }

    private void searchContent(String query) {
        recentContentList.clear();
        String lowercaseQuery = query.toLowerCase();
        for (Content content : allContent) {
            if (content.getTitle().toLowerCase().contains(lowercaseQuery) ||
                content.getGenre().toLowerCase().contains(lowercaseQuery)) {
                recentContentList.add(content);
            }
        }
        contentAdapter.notifyDataSetChanged();
    }

    private void showAddContentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_content, null);
        
        // Initialize dialog views
        TextInputEditText etTitle = dialogView.findViewById(R.id.etTitle);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etDuration);
        TextInputEditText etGenre = dialogView.findViewById(R.id.etGenre);
        TextInputEditText etYear = dialogView.findViewById(R.id.etYear);
        AutoCompleteTextView actvType = dialogView.findViewById(R.id.actvType);
        ImageView ivContentImage = dialogView.findViewById(R.id.ivContentImage);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);

        // Setup content type dropdown
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, CONTENT_TYPES);
        actvType.setAdapter(typeAdapter);

        // Setup image picker for both the ImageView and the Button
        View.OnClickListener imagePickerListener = v -> {
            currentImageView = ivContentImage;
            checkAndRequestPermissions();
        };
        
        ivContentImage.setOnClickListener(imagePickerListener);
        btnSelectImage.setOnClickListener(imagePickerListener);

        builder.setView(dialogView)
               .setTitle("Agregar Contenido")
               .setPositiveButton("Guardar", (dialog, which) -> {
                   String title = etTitle.getText().toString();
                   String type = actvType.getText().toString();
                   String durationStr = etDuration.getText().toString();
                   String genre = etGenre.getText().toString();
                   String yearStr = etYear.getText().toString();
                   String details = ((TextInputEditText) dialogView.findViewById(R.id.etDetails)).getText().toString();

                   // Solo el título es obligatorio
                   if (title.isEmpty()) {
                       Toast.makeText(this, "Por favor ingrese un título", Toast.LENGTH_SHORT).show();
                       return;
                   }

                   Content newContent = new Content();
                   newContent.setId(allContent.size() + 1); // Simple ID generation
                   newContent.setTitle(title);
                   newContent.setType(type.isEmpty() ? "Sin especificar" : type);
                   newContent.setDuration(durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr));
                   newContent.setGenre(genre.isEmpty() ? "Sin especificar" : genre);
                   newContent.setYear(yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr));
                   newContent.setWatched(false);
                   newContent.setDetails(details);
                   newContent.setCreatedAt(System.currentTimeMillis());

                   if (selectedImageUri != null) {
                       String imagePath = saveImageToInternalStorage(selectedImageUri);
                       newContent.setImagePath(imagePath);
                   } else {
                       // Si no se selecciona imagen, usar una imagen por defecto
                       newContent.setImagePath("android.resource://" + getPackageName() + "/" + R.drawable.placeholder_image);
                   }

                   allContent.add(newContent);
                   loadRecentContent();
                   Toast.makeText(this, "Contenido agregado exitosamente", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancelar", null)
               .show();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            showImageSourceDialog(currentImageView);
        }
    }

    private void showImageSourceDialog(ImageView imageView) {
        String[] options = {"Tomar foto", "Seleccionar de galería"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen")
               .setItems(options, (dialog, which) -> {
                   if (which == 0) {
                       // Tomar foto
                       openCamera();
                   } else {
                       // Seleccionar de galería
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
                
                // Agregar flags para dar permisos temporales
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
            imageFileName,  /* prefix */
            ".jpg",        /* suffix */
            storageDir     /* directory */
        );
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            String fileName = "content_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentContent();
    }

    @Override
    public void onWatchedStateChanged(Content content) {
        contentAdapter.notifyDataSetChanged();
    }
} 