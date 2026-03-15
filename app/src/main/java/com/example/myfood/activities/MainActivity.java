package com.example.myfood.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.R;
import com.example.myfood.adapters.ProductAdapter;
import com.example.myfood.database.DatabaseHelper;
import com.example.myfood.entity.Product;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView productListView;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AutoCompleteTextView textProductField = findViewById(R.id.textProduct);
        Button btnAdd = findViewById(R.id.btnAdd);
        productListView = findViewById(R.id.listProduct);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productListView.setLayoutManager(layoutManager);
        dbHelper = new DatabaseHelper(getApplicationContext());

        // Настройка подсказок по словарю продуктов
        java.util.List<String> suggestions = dbHelper.getAllDictionaryNames();
        ArrayAdapter<String> adapterSuggestions = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );
        textProductField.setAdapter(adapterSuggestions);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = textProductField.getText() != null ? textProductField.getText().toString().trim() : "";
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Введите название продукта", Toast.LENGTH_LONG).show();
                    return;
                }
                Product objEmp = new Product(name, true);
                String unit = dbHelper.getUnitForName(name);
                if (unit == null || unit.isEmpty()) {
                    // Новый продукт: по умолчанию штучный и сразу добавляем в словарь
                    unit = "pcs";
                    dbHelper.insertDictIfNotExists(name, "other", unit);
                }
                objEmp.setUnit(unit);
                objEmp.setQuantity(unit.equals("kg") || unit.equals("g") ? 0.5 : 1.0);
                if (dbHelper.insertProduct(objEmp)) {
                    textProductField.setText("");
                    // Обновляем список подсказок, если добавлен новый продукт
                    java.util.List<String> newSuggestions = dbHelper.getAllDictionaryNames();
                    ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            newSuggestions
                    );
                    textProductField.setAdapter(newAdapter);
                    addProducts();
                } else {
                    Toast.makeText(getApplicationContext(), "Record not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });

        addProducts();
        setupSwipeToDelete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new android.content.Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addProducts() {
        List<Product> itemList = dbHelper.getAllUsers();
        adapter = new ProductAdapter(itemList, dbHelper);
        productListView.setAdapter(adapter);
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (adapter == null || position == RecyclerView.NO_POSITION) {
                    return;
                }

                Product product = adapter.getItem(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Свайп влево — удалить запись
                    dbHelper.deleteProduct(product.getId());
                    adapter.removeAt(position);
                    Toast.makeText(MainActivity.this, "Продукт удалён", Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Свайп вправо — пометить как купленное (зачеркнуть, но не удалять)
                    dbHelper.updateProductActive(product.getId(), false);
                    product.setActive(false);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(MainActivity.this, "Покупка отмечена как выполненная", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(productListView);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

