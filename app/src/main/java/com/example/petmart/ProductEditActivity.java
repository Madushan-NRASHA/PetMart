//package com.example.petmart;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ProductEditActivity extends AppCompatActivity {
//
//    private EditText productName, productCategory, productPrice, productDescription;
//    private ImageView productImage;
//    private Button saveButton, imageButton;
//    private FirebaseFirestore db;
//    private StorageReference storageReference;
//    private Uri imageUri;
//    private String productId;
//    private ProgressBar progressBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product_edit);
//
//        // Initialize Firestore and Storage
//        db = FirebaseFirestore.getInstance();
//        storageReference = FirebaseStorage.getInstance().getReference("product_images");
//
//        productName = findViewById(R.id.productName);
//        productCategory = findViewById(R.id.productCategory);
//        productPrice = findViewById(R.id.productPrice); // Ensure this is initialized
//        productDescription = findViewById(R.id.productDescription);
//        productImage = findViewById(R.id.productImage); // Ensure this is initialized
//        saveButton = findViewById(R.id.saveButton);
//        imageButton = findViewById(R.id.selectImageButton);
//        progressBar = findViewById(R.id.buttonProgressBar);
//
//        // Retrieve product object from intent
//        Product product = (Product) getIntent().getSerializableExtra("product");
//        if (product != null) {
//            productId = product.getId();
//            productName.setText(product.getName());
//            productCategory.setText(product.getCategory());
//            productPrice.setText(product.getPrice());
//            productDescription.setText(product.getDescription());
//            Glide.with(this).load(product.getImageUrl()).into(productImage);
//        } else {
//            Log.e("ProductEditActivity", "Product data is null");
//            Toast.makeText(this, "No product data available", Toast.LENGTH_SHORT).show();
//            finish(); // Close the activity if no product is found
//        }
//
//        // Image selection button click listener
//        imageButton.setOnClickListener(v -> selectImage());
//
//        // Save button click listener
//        saveButton.setOnClickListener(v -> updateProduct());
//    }
//
//    private void selectImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        imagePickerLauncher.launch(intent);
//    }
//
//    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    imageUri = result.getData().getData();
//                    // Use Glide to load the selected image into the ImageView
//                    Glide.with(ProductEditActivity.this).load(imageUri).into(productImage);
//                } else {
//                    Toast.makeText(ProductEditActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    private void updateProduct() {
//        String updatedName = productName.getText().toString();
//        String updatedCategory = productCategory.getText().toString();
//        String updatedPrice = productPrice.getText().toString();
//        String updatedDescription = productDescription.getText().toString();
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        if (imageUri != null) {
//            // Upload the selected image
//            StorageReference fileRef = storageReference.child(productId + ".jpg");
//            UploadTask uploadTask = fileRef.putFile(imageUri);
//
//            uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String imageUrl = uri.toString();
//                // Save all updated data to Firestore
//                saveProduct(updatedName, updatedCategory, updatedPrice, updatedDescription, imageUrl);
//            })).addOnFailureListener(e -> {
//                Log.e("ProductEditActivity", "Image upload failed", e);
//                Toast.makeText(ProductEditActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            });
//        } else {
//            // Save product without new image
//            saveProduct(updatedName, updatedCategory, updatedPrice, updatedDescription, null); // Pass null if no new image
//        }
//    }
//
//    private void saveProduct(String newName, String newCategory, String newPrice, String newDescription, String newImageUrl) {
//        Map<String, Object> updatedProduct = new HashMap<>();
//        updatedProduct.put("name", newName);
//        updatedProduct.put("category", newCategory);
//        updatedProduct.put("price", newPrice);
//        updatedProduct.put("description", newDescription);
//
//        // Only update image URL if it's not null
//        if (newImageUrl != null) {
//            updatedProduct.put("imageUrl", newImageUrl);
//        }
//
//        DocumentReference productRef = db.collection("products").document(productId);
//        productRef.update(updatedProduct)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(ProductEditActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                    finish(); // Close the activity after saving
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("ProductEditActivity", "Error updating product: ", e);
//                    Toast.makeText(ProductEditActivity.this, "Error updating product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                });
//    }
//}



package com.example.petmart;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProductEditActivity extends AppCompatActivity {

    private EditText productName, productCategory, productPrice, productDescription;
    private ImageView productImage;
    private Button saveButton, imageButton, deleteButton;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Uri imageUri;
    private String productId;
    private String oldImageUrl; // To store the previous image URL
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        productName = findViewById(R.id.productName);
        productCategory = findViewById(R.id.productCategory);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImage = findViewById(R.id.productImage);
        saveButton = findViewById(R.id.saveButton);
        imageButton = findViewById(R.id.selectImageButton);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.buttonProgressBar);
        progressBar.setVisibility(View.GONE); // Hide the progress bar initially

        // Retrieve product object from intent
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            productId = product.getId();
            productName.setText(product.getName());
            productCategory.setText(product.getCategory());
            productPrice.setText(product.getPrice());
            productDescription.setText(product.getDescription());
            oldImageUrl = product.getImageUrl(); // Store old image URL
            Glide.with(this).load(oldImageUrl).into(productImage);
        } else {
            Log.e("ProductEditActivity", "Product data is null");
            Toast.makeText(this, "No product data available", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no product is found
        }

        // Image selection button click listener
        imageButton.setOnClickListener(v -> selectImage());

        // Save button click listener
        saveButton.setOnClickListener(v -> updateProduct());

        // Delete button click listener
        deleteButton.setOnClickListener(v -> deleteProduct());
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
                    Glide.with(ProductEditActivity.this).load(imageUri).into(productImage);
                } else {
                    Toast.makeText(ProductEditActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    private void updateProduct() {
        String updatedName = productName.getText().toString();
        String updatedCategory = productCategory.getText().toString();
        String updatedPrice = productPrice.getText().toString();
        String updatedDescription = productDescription.getText().toString();

        // Input validation
        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedCategory) || TextUtils.isEmpty(updatedPrice)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            // Upload the selected image
            StorageReference fileRef = storageReference.child(productId + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                // Save all updated data to Firestore
                saveProduct(updatedName, updatedCategory, updatedPrice, updatedDescription, imageUrl);
                // Delete the old image if a new one is uploaded
                deleteOldImage();
            })).addOnFailureListener(e -> {
                Log.e("ProductEditActivity", "Image upload failed", e);
                Toast.makeText(ProductEditActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
        } else {
            // Save product without new image
            saveProduct(updatedName, updatedCategory, updatedPrice, updatedDescription, oldImageUrl); // Use old image URL
        }
    }

    private void saveProduct(String newName, String newCategory, String newPrice, String newDescription, String newImageUrl) {
        Map<String, Object> updatedProduct = new HashMap<>();
        updatedProduct.put("name", newName);
        updatedProduct.put("category", newCategory);
        updatedProduct.put("price", newPrice);
        updatedProduct.put("description", newDescription);

        // Only update image URL if it's not null
        if (newImageUrl != null) {
            updatedProduct.put("imageUrl", newImageUrl);
        }

        DocumentReference productRef = db.collection("products").document(productId);
        productRef.update(updatedProduct)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProductEditActivity.this, "Product updated successfully", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    finish(); // Optionally close activity
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductEditActivity", "Product update failed", e);
                    Toast.makeText(ProductEditActivity.this, "Failed to update product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void deleteOldImage() {
        if (oldImageUrl != null) {
            StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
            oldImageRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("ProductEditActivity", "Old image deleted successfully.");
            }).addOnFailureListener(e -> {
                Log.e("ProductEditActivity", "Failed to delete old image", e);
            });
        }
    }

    private void deleteProduct() {
        if (productId == null) {
            Toast.makeText(ProductEditActivity.this, "Product ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference the product document by ID
        DocumentReference productRef = FirebaseFirestore.getInstance().collection("products").document(productId);

        // Delete the product document
        productRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProductEditActivity.this, "Product deleted successfully", Toast.LENGTH_LONG).show();
                    finish(); // Close the activity after deletion
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductEditActivity", "Product deletion failed", e);
                    Toast.makeText(ProductEditActivity.this, "Failed to delete product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}
