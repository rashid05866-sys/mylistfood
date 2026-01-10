package com.example.myfood.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextInputEditText textProductField = findViewById(R.id.textProduct);
        Button btnAdd = findViewById(R.id.btnAdd);
        RecyclerView productListView = findViewById(R.id.listProduct);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productListView.setLayoutManager(layoutManager);
        dbHelper = new DatabaseHelper(getApplicationContext());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product objEmp = new Product(textProductField.getText().toString(), true);
                if(dbHelper.insertProduct(objEmp)) {
                    addProducts(productListView);
                }else{
                    Toast.makeText(getApplicationContext(), "Record not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
        addProducts(productListView);

    }
    private void addProducts(RecyclerView productListView) {
        List<Product> itemList = dbHelper.getAllUsers();
        ProductAdapter adapter = new ProductAdapter(itemList);
        productListView.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
