package com.example.myfood.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.R;
import com.example.myfood.entity.Product;
import com.example.myfood.database.DatabaseHelper;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> productList;
    private final DatabaseHelper dbHelper;
    // Набор доступных цветов для кружка
    private final int[] colorOptions = new int[]{
            R.color.primary,
            R.color.primary_container,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        View colorIndicator;
        TextView textQuantityLabel;
        TextView textQuantity;
        EditText editWeight;
        TextView textUnit;
        ImageButton btnPlus;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textProductName);
            colorIndicator = itemView.findViewById(R.id.viewColorIndicator);
            textQuantityLabel = itemView.findViewById(R.id.textQuantityLabel);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            editWeight = itemView.findViewById(R.id.editWeight);
            textUnit = itemView.findViewById(R.id.textUnit);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }

    public ProductAdapter(List<Product> products, DatabaseHelper dbHelper) {
        this.productList = products;
        this.dbHelper = dbHelper;
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
        Product product = productList.get(position);

        holder.textName.setText(product.getName());

        // Оформление в зависимости от статуса (куплено или нет)
        if (!product.isActive()) {
            holder.textName.setPaintFlags(holder.textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.textName.setPaintFlags(holder.textName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(1f);
        }

        // Настройка цвета кружка
        int safeIndex = product.getColorIndex();
        if (safeIndex < 0 || safeIndex >= colorOptions.length) {
            safeIndex = 0;
            product.setColorIndex(safeIndex);
        }

        int colorResId = colorOptions[safeIndex];
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);
        ViewCompat.setBackgroundTintList(holder.colorIndicator, ColorStateList.valueOf(color));

        holder.colorIndicator.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(context);
            builder.setTitle("Выбор цвета");

            String[] items = new String[]{"Цвет 1", "Цвет 2", "Цвет 3", "Цвет 4"};
            ArrayAdapter<String> colorsAdapter = new ArrayAdapter<String>(
                    context,
                    R.layout.item_color_option,
                    R.id.textColorName,
                    items
            ) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View row = super.getView(position, convertView, parent);
                    View preview = row.findViewById(R.id.viewColorPreview);
                    int safe = position;
                    if (safe < 0 || safe >= colorOptions.length) safe = 0;
                    int previewColor = ContextCompat.getColor(context, colorOptions[safe]);
                    ViewCompat.setBackgroundTintList(preview, ColorStateList.valueOf(previewColor));
                    return row;
                }
            };

            builder.setAdapter(colorsAdapter, (dialog, which) -> {
                int index = which;
                if (index < 0 || index >= colorOptions.length) {
                    index = 0;
                }
                product.setColorIndex(index);
                int selectedColorResId = colorOptions[index];
                int selectedColor = ContextCompat.getColor(context, selectedColorResId);
                ViewCompat.setBackgroundTintList(holder.colorIndicator, ColorStateList.valueOf(selectedColor));
            });

            builder.show();
        });

        String unit = product.getUnit();
        if (unit == null || unit.isEmpty()) {
            unit = "pcs";
            product.setUnit(unit);
        }

        boolean isWeight = unit.equals("kg") || unit.equals("g");

        holder.editWeight.setVisibility(isWeight ? View.VISIBLE : View.GONE);
        holder.textUnit.setVisibility(isWeight ? View.VISIBLE : View.GONE);
        holder.btnPlus.setVisibility(isWeight ? View.GONE : View.VISIBLE);
        holder.textQuantityLabel.setVisibility(isWeight ? View.GONE : View.VISIBLE);
        holder.textQuantity.setVisibility(isWeight ? View.GONE : View.VISIBLE);

        if (isWeight) {
            holder.textUnit.setText(unit.equals("kg") ? "кг" : "г");
            holder.editWeight.setHint(unit.equals("kg") ? "0.5" : "100");
            String current = product.getQuantity() > 0 ? String.valueOf(product.getQuantity()) : "";
            if (!current.equals(holder.editWeight.getText().toString())) {
                holder.editWeight.setText(current);
            }

            TextWatcher oldWatcher = (TextWatcher) holder.editWeight.getTag(R.id.editWeight);
            if (oldWatcher != null) {
                holder.editWeight.removeTextChangedListener(oldWatcher);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString().trim();
                    double q = 0;
                    try {
                        if (!text.isEmpty()) {
                            q = Double.parseDouble(text.replace(',', '.'));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    product.setQuantity(q);
                    dbHelper.updateProductQuantityAndUnit(product.getId(), q, product.getUnit());
                }
            };
            holder.editWeight.setTag(R.id.editWeight, watcher);
            holder.editWeight.addTextChangedListener(watcher);
        } else {
            int qty = (int) (product.getQuantity() > 0 ? product.getQuantity() : 1);
            product.setQuantity(qty);
            holder.textQuantity.setText(String.valueOf(qty));

            holder.btnPlus.setOnClickListener(v -> {
                int newQty = (int) product.getQuantity() + 1;
                product.setQuantity(newQty);
                holder.textQuantity.setText(String.valueOf(newQty));
                dbHelper.updateProductQuantityAndUnit(product.getId(), newQty, product.getUnit());
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public Product getItem(int position) {
        return productList.get(position);
    }

    public void removeAt(int position) {
        if (position >= 0 && position < productList.size()) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }
}

