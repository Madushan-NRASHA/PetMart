package com.example.petmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setUserIdFromAuth();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load cart products for the user
        loadCartProducts();
    }

    private void setUserIdFromAuth() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Get user ID from Firebase Auth
        } else {
            userId = null;  // No user is signed in
        }
    }

    private void loadCartProducts() {
        if (userId != null) {
            Cart.getInstance().setUserId(userId);
            Cart.getInstance().loadCartFromFirestore(new Cart.CartOperationCallback() {
                @Override
                public void onSuccess() {
                    cartAdapter = new CartAdapter(Cart.getInstance().getProducts());
                    recyclerView.setAdapter(cartAdapter);
                }

                @Override
                public void onError(Exception e) {
                    if (e.getMessage().contains("No cart found")) {
                        Toast.makeText(CartActivity.this, "No cart exists for this user. Add items to your cart.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
