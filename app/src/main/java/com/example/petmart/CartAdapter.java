package com.example.petmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    // List to hold the products in the cart
    private final List<Product> productList;

    // Constructor to initialize the adapter with the product list
    public CartAdapter(List<Product> productList) {
        this.productList = productList;
    }

    // Inflate the cart item layout and create ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual cart items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to each ViewHolder based on the position in the product list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the product at the current position
        Product product = productList.get(position);

        // Set the product details to the respective TextViews
        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: $" + product.getPrice());
        holder.productCategory.setText("Category: " + product.getCategory());
        holder.productDescription.setText("Description: " + product.getDescription());
    }

    // Return the total number of products in the cart
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class that holds references to the views in the cart item layout
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TextViews for product details
        TextView productName, productPrice, productCategory, productDescription;

        // Constructor to initialize the views from the layout
        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize the TextViews using their respective IDs
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
            productDescription = itemView.findViewById(R.id.productDescription);
        }
    }
}
