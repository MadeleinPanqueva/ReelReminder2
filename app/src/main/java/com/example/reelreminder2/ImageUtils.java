package com.example.reelreminder2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final String IMAGE_DIRECTORY = "reelreminder_images";
    
    /**
     * Save image to internal storage
     * @param context Application context
     * @param imageUri Uri of the image to save
     * @return Path to the saved image or null if failed
     */
    public static String saveImageToInternalStorage(Context context, Uri imageUri) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            File file = new File(directory, fileName);
            
            // Copy image to internal storage
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(file);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load image from internal storage
     * @param imagePath Path to the image
     * @return Bitmap of the image or null if failed
     */
    public static Bitmap loadImageFromStorage(String imagePath) {
        try {
            File file = new File(imagePath);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Delete image from internal storage
     * @param imagePath Path to the image
     * @return true if deleted successfully, false otherwise
     */
    public static boolean deleteImageFromStorage(String imagePath) {
        try {
            File file = new File(imagePath);
            return file.delete();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image: " + e.getMessage());
            return false;
        }
    }
} 