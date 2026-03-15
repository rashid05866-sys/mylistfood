package com.example.myfood.providers;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.myfood.R;
import com.example.myfood.database.DatabaseHelper;
import com.example.myfood.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ProductRemoteViewsFactory(getApplicationContext());
    }

    static class ProductRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context context;
        private final List<Product> products = new ArrayList<>();

        ProductRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            // no-op
        }

        @Override
        public void onDataSetChanged() {
            products.clear();
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            products.addAll(dbHelper.getAllUsers());
            dbHelper.close();
        }

        @Override
        public void onDestroy() {
            products.clear();
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == android.widget.AdapterView.INVALID_POSITION || position >= products.size()) {
                return null;
            }

            Product product = products.get(position);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item_product);
            views.setTextViewText(R.id.widget_product_name, product.getName());

            // Перечёркивание для неактивных продуктов
            if (!product.isActive()) {
                views.setInt(R.id.widget_product_name, "setPaintFlags",
                        android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                views.setInt(R.id.widget_product_name, "setPaintFlags", 0);
            }

            // Нажатие по строке – переключение статуса (зачёркивание)
            Intent toggleIntent = new Intent();
            toggleIntent.setAction(ProductWidgetProvider.ACTION_TOGGLE_PRODUCT);
            toggleIntent.putExtra(ProductWidgetProvider.EXTRA_PRODUCT_ID, product.getId());
            views.setOnClickFillInIntent(R.id.widget_item_root, toggleIntent);

            // Нажатие по кнопке удаления
            Intent deleteIntent = new Intent();
            deleteIntent.setAction(ProductWidgetProvider.ACTION_DELETE_PRODUCT);
            deleteIntent.putExtra(ProductWidgetProvider.EXTRA_PRODUCT_ID, product.getId());
            views.setOnClickFillInIntent(R.id.widget_btn_delete, deleteIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (position < products.size()) {
                return products.get(position).getId();
            }
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}

