package com.example.petmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProductActivity extends AppCompatActivity {

    Button addProduct, viewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        addProduct = findViewById(R.id.addProductsBtn);

        viewProduct = findViewById(R.id.viewProductsBtn);


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        viewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductActivity.this, ViewProductActivity.class);
            startActivity(intent);
        });

    }
}