/*
 *@author: Felipe Bernardes Cisilo
 */
package br.edu.fateczl.maisidiomas.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import br.edu.fateczl.maisidiomas.database.DatabaseHelper;
import br.edu.fateczl.maisidiomas.model.User;
import br.edu.fateczl.maisidiomas.utils.PasswordEncryptor;

public class AuthController {
    private Context context;
    private DatabaseHelper dbHelper;
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_USER_EMAIL = "UserEmail";
    private SharedPreferences preferences;

    public AuthController(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean login(String email, String password) {
        User user = dbHelper.getUserByEmail(email);

        if (user != null && PasswordEncryptor.verifyPassword(password, user.getEncryptedPassword())) {
            saveUserSession(email);
            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean register(String name, String email, String password) {
        if (dbHelper.getUserByEmail(email) != null) {
            Toast.makeText(context, "Email already registered!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String encryptedPassword = PasswordEncryptor.encryptPassword(password);
        User newUser = new User(dbHelper.generateNewUserId(), name, email, encryptedPassword);
        dbHelper.saveNewUser(newUser);

        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean isUserLoggedIn() {
        String userEmail = preferences.getString(KEY_USER_EMAIL, null);
        return userEmail != null;
    }

    private void saveUserSession(String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        try {
            dbHelper.resetProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, null);
    }
}