package com.example.petmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextView registerTextBtn;
    private EditText usernameEditText, passwordEditText, emailEditText, confirmPasswordEditText;
    private Button registerButton;
    private ProgressBar buttonProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();  // Initialize Firestore

        // UI Elements
        registerTextBtn = findViewById(R.id.login_text_btn);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        buttonProgressBar = findViewById(R.id.buttonProgressBar);

        // Go to login screen
        registerTextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Register new user
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String role = "user";

            if (validateInput(username, email, password, confirmPassword)) {
                registerUser(username, email, password);
            }
        });
    }

    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        // Check for empty fields
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void registerUser(String username, String email, String password) {
        changeInProgress(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Store user info in Firestore and Realtime DB
                            String userId = firebaseUser.getUid();
                            storeUserInfoInFirestore(userId, username, email, "user"); // Default role to "user"
                            storeUserInfoInRealtimeDB(userId, username, email, password);

                           Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void storeUserInfoInFirestore(String userId, String username, String email, String role) {
        // Create a user data object
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("role", role);  // role could be "user" or "admin"

        // Store user data in the Firestore collection "users"
        mFirestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "User data saved successfully in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Failed to save user data in Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private void storeUserInfoInRealtimeDB(String userId, String username, String email, String password) {
        // Enter data to Realtime DB
        ReadwriteUserDetails writeUserDetails = new ReadwriteUserDetails(username, email, password);

        // Extracting from DB reference
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
        referenceProfile.child(userId).setValue(writeUserDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "User data saved in Realtime DB", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Failed to save user data in Realtime DB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeInProgress(boolean inProgress) {
        if (inProgress) {
            buttonProgressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);
        } else {
            buttonProgressBar.setVisibility(View.GONE);
            registerButton.setEnabled(true);
        }
    }
}
