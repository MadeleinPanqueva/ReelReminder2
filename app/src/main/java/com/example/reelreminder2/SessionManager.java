package com.example.reelreminder2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "ReelReminderPref";
    
    // Shared preferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    
    // Default values
    private static final String DEFAULT_EMAIL = "user@reelreminder.com";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_NAME = "Usuario";
    
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    /**
     * Create login session
     * @param email user email
     */
    public void createLoginSession(String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        
        // If it's the first login, set default name
        if (!pref.contains(KEY_NAME)) {
            editor.putString(KEY_NAME, DEFAULT_NAME);
        }
        
        // Commit changes
        editor.commit();
    }
    
    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Get stored session data
     * @return user data
     */
    public String getUserEmail() {
        // Usar el mismo pref que se inicializa en el constructor
        return pref.getString(KEY_EMAIL, DEFAULT_EMAIL);
    }
    
    /**
     * Get user name
     * @return user name
     */
    public String getUserName() {
        return pref.getString(KEY_NAME, DEFAULT_NAME);
    }
    
    /**
     * Set user name
     * @param name new user name
     */
    public void setUserName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }
    
    /**
     * Clear session details
     */
    public void logoutUser() {
        // Limpiar todas las preferencias relacionadas con la sesión
        editor.clear();
        editor.commit(); // Usar commit en lugar de apply para asegurar que se complete inmediatamente
        
        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Evitar transiciones
        context.startActivity(intent);
    }
    
    /**
     * Validate user credentials
     * @param email user email
     * @param password user password
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateCredentials(String email, String password) {
        // For demo purposes, we'll use hardcoded credentials
        // In a real app, this would check against a secure database
        return email.equals(DEFAULT_EMAIL) && password.equals(DEFAULT_PASSWORD);
    }
    
    /**
     * Guardar la URI de la imagen de perfil
     * @param imageUri URI de la imagen
     */
    public void saveProfileImageUri(String imageUri) {
        editor.putString(KEY_PROFILE_IMAGE, imageUri);
        editor.commit();
    }
    
    /**
     * Obtener la URI de la imagen de perfil
     * @return URI de la imagen o null si no hay
     */
    public String getProfileImageUri() {
        return pref.getString(KEY_PROFILE_IMAGE, null);
    }
} 