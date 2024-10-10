package com.example.petmart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.View;

import android.util.Log;
import android.widget.Toast;

public class CustomerProductDetailActivity extends AppCompatActivity {

    private TextView productName, productCategory, productPrice, productDescription,showTot_price;
    private ImageView productImage;
    private Button addToCartButton;
    private Button addAmountButton;
    private Button cancelButton;
    private Product product;
    private TextInputEditText getQuantity;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_detail);

        // Initialize views
        productName = findViewById(R.id.productNameDetailCustomer);
        productCategory = findViewById(R.id.productCategoryDetailCustomer);
        productPrice = findViewById(R.id.productPriceDetailCustomer);
        productImage = findViewById(R.id.productImageDetailCustomer);
        productDescription = findViewById(R.id.productDescriptionDetailCustomer);
        addToCartButton = findViewById(R.id.addToCartButton);
        getQuantity = findViewById(R.id.GetQuantity);  // Corrected ID for TextInputEditText
        addAmountButton=findViewById(R.id.addToCartButton2);
        cancelButton=findViewById(R.id.addToCartButton3);
        showTot_price=findViewById(R.id.productDescriptionDetailCustomer3);
        databaseReference = FirebaseDatabase.getInstance().getReference("myCollection");
        // Retrieve product object from intent
        product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            displayProductDetails();
        } else {
            Log.e("CustomerProductDetailActivity", "Product is null");
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Add to Cart button click listener
        addAmountButton.setOnClickListener(v->multiplyAmount());
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuantity.setText("");
                showTot_price.setText("0");
                // Clear the quantity input field
            }
        });

//        addToCartButton.setOnClickListener(v -> sendTofirebase());
    }

    private void displayProductDetails() {
        productName.setText(product.getName());
        productCategory.setText("Category: " + product.getCategory());
        productPrice.setText("Price: $" + String.format("%.2f", Double.parseDouble(product.getPrice())));
        productDescription.setText(product.getDescription());

        // Load product image using Glide
        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(productImage);
        } else {
            productImage.setImageResource(R.drawable.home); // Set a placeholder image
        }
    }


//    private  void multyplyAmount(){
//        String quantityStr = getQuantity.getText().toString().trim();
//
//        try {
//            int quantity = Integer.parseInt(quantityStr);
//            String getFirebasedata=product.getPrice();
//            int conFirebasedata=Integer.parseInt(getFirebasedata);
//            int multiPly=quantity*conFirebasedata;
//            String conMulti=String.valueOf(multiPly);
//            Toast.makeText(this,conMulti,Toast.LENGTH_SHORT).show();
//            // Now you can use the `quantity` integer in your logic
//        } catch (NumberFormatException e) {
//            // Handle the case where the input is not a valid integer
//            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
private void multiplyAmount() {
    String quantityStr = getQuantity.getText().toString().trim();

    try {
        int quantity = Integer.parseInt(quantityStr);
        String getFirebasedata = product.getPrice(); // Assuming this is a String representing a price
        double conFirebasedata = Double.parseDouble(getFirebasedata); // Use double for price
        double multiPly = quantity * conFirebasedata; // Calculate total price
        String conMulti = String.format("%.2f", multiPly); // Format to two decimal places
        showTot_price.setText(conMulti);
        Toast.makeText(this, "Total Price: $" + conMulti, Toast.LENGTH_SHORT).show();
    } catch (NumberFormatException e) {
        // Handle the case where the input is not a valid integer
        Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
    }
}


}
