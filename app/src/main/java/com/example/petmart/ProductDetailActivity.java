package com.example.petmart;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productCategory, productPrice, productDescription;
    private ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productName = findViewById(R.id.productNameDetail);
        productCategory = findViewById(R.id.productCategoryDetail);
        productPrice = findViewById(R.id.productPriceDetail);
        productImage = findViewById(R.id.productImageDetail);
        productDescription = findViewById(R.id.productDescriptionDetail);

        // Retrieve product object from intent
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            productName.setText(product.getName());
            productCategory.setText(product.getCategory());
            productPrice.setText(product.getPrice());
            productDescription.setText(product.getDescription());
            Glide.with(this).load(product.getImageUrl()).into(productImage);
        }
    }
}