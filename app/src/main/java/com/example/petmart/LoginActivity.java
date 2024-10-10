package com.example.petmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView signupIcon;
    private TextView registerTextBtn;

    Button loginBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ProgressBar buttonProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize the EditText and ImageView views
        emailEditText = findViewById(R.id.usernameEditText);  // Assuming you're using email to log in
        passwordEditText = findViewById(R.id.passwordEditText);
        signupIcon = findViewById(R.id.signupIcon);
        registerTextBtn = findViewById(R.id.register_text_btn);

        loginBtn = findViewById(R.id.loginButton);
        buttonProgressBar = findViewById(R.id.buttonProgressBar);


        loginBtn.setOnClickListener(v -> handleLogin());


        registerTextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    // Method to handle the login logic
    private void handleLogin() {
        buttonProgressBar.setVisibility(View.VISIBLE); // Show progress bar
        loginBtn.setText(""); // Clear button text

        // Get the email and password entered by the user
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            buttonProgressBar.setVisibility(View.GONE); // Hide progress bar on error
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            buttonProgressBar.setVisibility(View.GONE); // Hide progress bar on error
            return;
        }

        // Authenticate the user using Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Always hide the progress bar at the end of the task
                    buttonProgressBar.setVisibility(View.GONE);
                    loginBtn.setText("Login"); // Reset button text

                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Fetch user role from Firestore
                            fetchUserRole(firebaseUser.getUid());
                        }
                    } else {
                        // If login fails, display a message to the user
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserRole(String userId) {
        // Get user data from Firestore
        mFirestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");  // Get the username
                            String role = document.getString("role");  // Get the role

                            // Display a toast message with the user's name
                            Toast.makeText(LoginActivity.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();

                            // Check the user's role and direct them accordingly
                            if ("admin".equals(role)) {
                                // Redirect to AdminActivity
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(intent);
                                finish();  // End the login activity
                            } else {
                                // Redirect to HomeActivity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();  // End the login activity
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
