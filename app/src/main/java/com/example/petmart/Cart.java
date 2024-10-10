package com.example.petmart;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    // Singleton instance
    private static Cart instance;
    private FirebaseFirestore firestore;
    private String userId;
    private List<Product> products;

    public interface CartOperationCallback {
        void onSuccess();
        void onError(Exception e);
    }

    // Private constructor for singleton pattern
    private Cart() {
        firestore = FirebaseFirestore.getInstance();
        products = new ArrayList<>();
    }

    // Get instance of Cart (Singleton)
    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    // Set the current user ID
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Get list of products in the cart
    public List<Product> getProducts() {
        return products;
    }

    // Method to add a product to the cart
    public void addProduct(Product product, CartOperationCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError(new IllegalStateException("User ID is not set."));
            return;
        }

        if (product == null) {
            callback.onError(new IllegalArgumentException("Product is null."));
            return;
        }

        // Add the product to the local list
        products.add(product);

        // Save the updated cart to Firestore
        firestore.collection("carts").document(userId)
                .update("products", products)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // Load cart products from Firestore
    public void loadCartFromFirestore(CartOperationCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError(new IllegalStateException("User ID is not set."));
            return;
        }

        firestore.collection("carts").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        products = (List<Product>) documentSnapshot.get("products");
                        callback.onSuccess();
                    } else {
                        // Initialize empty cart for new users
                        products = new ArrayList<>();
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(callback::onError);
    }
}
