package com.example.petmart;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ImageView profileIcon;
    private ImageView cartbtn;
    private RecyclerView productRecyclerView;
    private CustomerProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;  // FirebaseAuth instance
    private FirebaseUser currentUser;  // FirebaseUser instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        profileIcon = findViewById(R.id.profileIcon);
        cartbtn = findViewById(R.id.cartIcon);
        productRecyclerView = findViewById(R.id.productRecyclerView);

        // Set up RecyclerView with a layout manager and adapter
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new CustomerProductAdapter(this, productList);
        productRecyclerView.setAdapter(productAdapter);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth
        currentUser = mAuth.getCurrentUser();  // Get current user
        loadProductsFromFirestore();

        // Set up cart button to navigate to CartActivity
        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class); // Assuming CartActivity exists
                startActivity(intent);
            }
        });

        // Set up profile button to navigate to LoginActivity
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Load products from Firestore and update RecyclerView
    private void loadProductsFromFirestore() {
        firestore.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            } else {
                // Handle error
                Toast.makeText(HomeActivity.this, "Error getting products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adding product to cart
    private void addToCart(Product product) {
        Cart cart = Cart.getInstance();
        cart.addProduct(product, new Cart.CartOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(HomeActivity.this, "Product added to cart!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                if (e instanceof IllegalStateException) {
                    Toast.makeText(HomeActivity.this, "Please log in to add items to your cart.", Toast.LENGTH_LONG).show();
                    // Redirect to login activity if needed
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to add product to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Save the cart with the given product and user ID
    private void saveCart(Product product) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();  // Use authenticated user ID
            Cart cart = Cart.getInstance();  // Use the Singleton Cart instance

            cart.addProduct(product, new Cart.CartOperationCallback() {
                @Override
                public void onSuccess() {
                    // Save the cart data to Firestore or notify the user
                    Toast.makeText(HomeActivity.this, "Product added to cart successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(HomeActivity.this, "Failed to save cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Prompt user to log in
            promptUserToLogin();
        }
    }

    // Prompt the user to log in if they are not logged in
    private void promptUserToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
