//package com.example.petmart;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
//import java.util.List;
//
//public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
//
//    private final List<Product> productList;
//    private final Context context;
//
//    public ProductAdapter(List<Product> productList, Context context) {
//        this.productList = productList;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//        Product product = productList.get(position);
//        holder.bind(product);
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    class ProductViewHolder extends RecyclerView.ViewHolder {
//        TextView productName, productCategory, productPrice;
//        ImageView productImage;
//
//        public ProductViewHolder(@NonNull View itemView) {
//            super(itemView);
//            productName = itemView.findViewById(R.id.productName);
//            productCategory = itemView.findViewById(R.id.productCategory);
//            productPrice = itemView.findViewById(R.id.productPrice);
//            productImage = itemView.findViewById(R.id.productImage);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        Log.d("ProductAdapter", "Item clicked at position: " + position);
//                        Intent intent = new Intent(context, ProductDetailActivity.class);
//                        intent.putExtra("product", productList.get(position)); // Pass Serializable Product
//                        context.startActivity(intent);
//                    }
//                }
//            });
//
//        }
//
//        public void bind(Product product) {
//            productName.setText(product.getName());
//            productCategory.setText(product.getCategory());
//            productPrice.setText(product.getPrice());
//            Glide.with(itemView.getContext()).load(product.getImageUrl()).into(productImage);
//        }
//    }
//}


package com.example.petmart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productCategory, productPrice;
        ImageView productImage, editProductIcon;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            editProductIcon = itemView.findViewById(R.id.editProductIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Log.d("ProductAdapter", "Item clicked at position: " + position);
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra("product", productList.get(position)); // Pass Serializable Product
                        context.startActivity(intent);
                    }
                }
            });

            editProductIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Log.d("ProductAdapter", "Edit icon clicked at position: " + position);
                        Intent intent = new Intent(context, ProductEditActivity.class);
                        intent.putExtra("product", productList.get(position)); // Pass product to edit
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productCategory.setText(product.getCategory());
            productPrice.setText(product.getPrice());
            Glide.with(itemView.getContext()).load(product.getImageUrl()).into(productImage);
        }
    }
}

