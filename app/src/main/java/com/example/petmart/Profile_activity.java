package com.example.petmart;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Profile_activity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private EditText nameTxt, emailTxt;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button imagebtn, updateProfileButton, logoutButton;
    private Uri imageUri;
    private String userId;
    private String name, email, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTxt = findViewById(R.id.nameEditText);
        emailTxt = findViewById(R.id.emailEditText);
        imageView = findViewById(R.id.userProfileImageView);
        progressBar = findViewById(R.id.buttonProgressBar);
        imagebtn = findViewById(R.id.editProfileImageButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        authProfile = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(Profile_activity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            finish(); // Close activity if no user is logged in
        } else {
            userId = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);
            showProfile(firebaseUser);
        }

        // Handle image selection
        imagebtn.setOnClickListener(v -> selectImage());

        // Handle profile update
        updateProfileButton.setOnClickListener(v -> updateProfile());

//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Clear user session data
//                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.clear(); // Clears all data in the preferences
//                editor.apply(); // Commit the changes
//
//                // Redirect to Login Activity
//                Intent intent = new Intent(Profile_activity.this, LoginActivity.class); // Replace with your login activity class
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
//                startActivity(intent);
//                finish(); // Close the Profile_activity
//            }
//        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri); // Show selected image in the ImageView
                } else {
                    Toast.makeText(Profile_activity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    private void showProfile(FirebaseUser firebaseUser) {
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                name = documentSnapshot.getString("username");
                email = documentSnapshot.getString("email");
                imageUrl = documentSnapshot.getString("imageUrl");

                nameTxt.setText(name != null ? name : "");
                emailTxt.setText(email != null ? email : "");

                // Load profile image with Picasso or Glide
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).placeholder(R.drawable.ic_home).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.ic_home); // Placeholder if no image
                }
            } else {
                Toast.makeText(Profile_activity.this, "Failed to retrieve data", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Log.e("ProfileActivity", "Error fetching profile: ", e);
            Toast.makeText(Profile_activity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void updateProfile() {
        String newName = nameTxt.getText().toString().trim();
        String newEmail = emailTxt.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            // Upload the selected image
            StorageReference fileRef = storageReference.child(userId + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Save all updated data to Firestore
                saveProfile(newName, newEmail, imageUrl);
            })).addOnFailureListener(e -> {
                // Log the error for debugging
                Log.e("ProfileActivity", "Image upload failed", e);
                Toast.makeText(Profile_activity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            });
        } else {
            // Save profile without new image
            saveProfile(newName, newEmail, imageUrl); // Note: Ensure imageUrl is from Firestore if not changed
        }
    }

    private void saveProfile(String newName, String newEmail, String newImageUrl) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", newName);
        userProfile.put("email", newEmail);
        userProfile.put("imageUrl", newImageUrl);

        firestore.collection("users").document(userId).set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Profile_activity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Log the error for debugging
                    Log.e("ProfileActivity", "Profile update failed", e);
                    Toast.makeText(Profile_activity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

}
