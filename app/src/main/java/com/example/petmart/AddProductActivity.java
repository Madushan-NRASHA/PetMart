package com.example.petmart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;

    private EditText productNameEditText, descriptionEditText, priceEditText, categoryEditText;
    private ImageView productImageView;
    private Button selectImageButton, submitProductButton;
    private ProgressBar progressBar;
    private Uri imageUri; // To store the image URI
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firestore and FirebaseStorage
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize UI elements
        productNameEditText = findViewById(R.id.productNameEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        selectImageButton = findViewById(R.id.selectImageButton);
        productImageView = findViewById(R.id.productImageView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        submitProductButton = findViewById(R.id.submitProductButton);
        progressBar = findViewById(R.id.buttonProgressBar);
        progressBar.setVisibility(View.GONE); // Hide progress bar initially

        // Handle image selection button click
        selectImageButton.setOnClickListener(v -> selectImage());

        // Handle submit button click
        submitProductButton.setOnClickListener(v -> uploadProduct());
    }

    // Method to open image picker
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data.getData();
            productImageView.setImageURI(imageUri);
        }
    }

    // Method to upload product data to Firestore
    private void uploadProduct() {
        String productName = productNameEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        if (productName.isEmpty() || category.isEmpty() || description.isEmpty() || price.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Show the progress bar before starting the upload

        // Upload image to Firebase Storage
        StorageReference productImageRef = storageReference.child("product_images/" + productName + ".jpg");
        productImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Update progress bar when upload is successful
                    productImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Save product details to Firestore
                        Map<String, Object> product = new HashMap<>();
                        product.put("name", productName);
                        product.put("category", category);
                        product.put("description", description);
                        product.put("price", price);
                        product.put("imageUrl", downloadUri.toString());

                        firestore.collection("products")
                                .add(product)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                                    clearFields();
                                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                                });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddProductActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                    });
                })
                .addOnProgressListener(snapshot -> {
                    // Calculate progress percentage
                    double progressPercentage = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progressPercentage); // Update progress bar
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                });
    }

    // Method to clear input fields after adding the product
    private void clearFields() {
        productNameEditText.setText("");
        categoryEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
        productImageView.setImageResource(android.R.color.transparent);

    }
}
