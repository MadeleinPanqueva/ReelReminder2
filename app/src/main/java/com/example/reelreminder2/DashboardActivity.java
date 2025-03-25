package com.example.reelreminder2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.reelreminder2.adapters.ContentAdapter;
import com.example.reelreminder2.models.Content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    
    private ImageView ivProfile;
    private LinearLayout libraryButton;
    private LinearLayout detailsButton;
    private FloatingActionButton fabAddContent;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private Uri selectedImageUri;
    private static final String[] CONTENT_TYPES = {"Película", "Serie", "Anime", "Documental"};
    private RecyclerView rvRecentContent;
    private ContentAdapter contentAdapter;
    private SearchView searchView;
    private List<Content> recentContentList;

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
                // Implementar click en contenido
                Intent intent = new Intent(DashboardActivity.this, ContentDetailActivity.class);
                intent.putExtra("content_id", content.getId());
                startActivity(intent);
            },
            dbHelper
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

        // Configurar clicks
        libraryButton.setOnClickListener(v -> 
            startActivity(new Intent(DashboardActivity.this, LibraryActivity.class)));

        detailsButton.setOnClickListener(v -> 
            startActivity(new Intent(DashboardActivity.this, DetailsActivity.class)));
    }

    private void loadRecentContent() {
        recentContentList.clear();
        Cursor cursor = dbHelper.getRecentContent(5);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Content content = new Content();
                content.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                content.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
                content.setType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE)));
                content.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DURATION)));
                content.setGenre(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENRE)));
                content.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH)));
                content.setYear(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR)));
                content.setWatched(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_WATCHED)) == 1);
                recentContentList.add(content);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        contentAdapter.notifyDataSetChanged();
    }

    private void searchContent(String query) {
        recentContentList.clear();
        Cursor cursor = dbHelper.searchContent(query);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Content content = new Content();
                content.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                content.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
                content.setType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE)));
                content.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DURATION)));
                content.setGenre(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENRE)));
                content.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH)));
                content.setYear(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR)));
                content.setWatched(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_WATCHED)) == 1);
                recentContentList.add(content);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        contentAdapter.notifyDataSetChanged();
    }

    private void showAddContentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_content, null);
        
        // Initialize dialog views
        TextInputEditText etTitle = dialogView.findViewById(R.id.etTitle);
        AutoCompleteTextView actvType = dialogView.findViewById(R.id.actvType);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etDuration);
        TextInputEditText etYear = dialogView.findViewById(R.id.etYear);
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
                    String yearStr = etYear.getText().toString();
                    String genre = etGenre.getText().toString();

                    if (title.isEmpty() || type.isEmpty() || durationStr.isEmpty() || 
                        yearStr.isEmpty() || genre.isEmpty()) {
                        Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int duration = Integer.parseInt(durationStr);
                    int year = Integer.parseInt(yearStr);
                    
                    if (year < 1888 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                        Toast.makeText(this, "Por favor ingrese un año válido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String imagePath = null;
                    if (selectedImageUri != null) {
                        imagePath = saveImageToInternalStorage(selectedImageUri);
                    }

                    // Create Content object and save to database
                    Content content = new Content();
                    content.setTitle(title);
                    content.setType(type);
                    content.setDuration(duration);
                    content.setYear(year);
                    content.setGenre(genre);
                    content.setImagePath(imagePath);

                    long id = dbHelper.insertContent(content);
                    if (id != -1) {
                        Toast.makeText(this, "Contenido agregado exitosamente", Toast.LENGTH_SHORT).show();
                        loadRecentContent(); // Recargar la lista
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

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentContent(); // Recargar contenido al volver a la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 