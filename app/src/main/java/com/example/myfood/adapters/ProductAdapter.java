package com.example.myfood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.R;
import com.example.myfood.entity.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    private List<Product> productList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textProductName);
            //textEmail = itemView.findViewById(R.id.textEmail);
        }
    }

    public ProductAdapter(List<Product> products) {
        this.productList = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product user = productList.get(position);

        holder.textName.setText(user.getName());

        //holder.textEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
