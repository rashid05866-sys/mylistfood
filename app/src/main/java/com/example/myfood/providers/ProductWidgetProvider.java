package com.example.myfood.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.myfood.R;
import com.example.myfood.activities.MainActivity;
import com.example.myfood.database.DatabaseHelper;
import com.example.myfood.entity.Product;

public class ProductWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_TOGGLE_PRODUCT = "com.example.myfood.action.TOGGLE_PRODUCT";
    public static final String ACTION_DELETE_PRODUCT = "com.example.myfood.action.DELETE_PRODUCT";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_product_list);
        views.setOnClickPendingIntent(R.id.widget_title, openAppPendingIntent);

        Intent serviceIntent = new Intent(context, ProductWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.list_view, serviceIntent);
        views.setEmptyView(R.id.list_view, R.id.widget_empty);

        Intent clickIntentTemplate = new Intent(context, ProductWidgetProvider.class);
        PendingIntent clickPendingIntentTemplate = PendingIntent.getBroadcast(
                context,
                0,
                clickIntentTemplate,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setPendingIntentTemplate(R.id.list_view, clickPendingIntentTemplate);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action == null) {
            return;
        }

        if (ACTION_TOGGLE_PRODUCT.equals(action) || ACTION_DELETE_PRODUCT.equals(action)) {
            int productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
            if (productId == -1) {
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(context);

            if (ACTION_TOGGLE_PRODUCT.equals(action)) {
                Product product = dbHelper.getProductById(productId);
                if (product != null) {
                    dbHelper.updateProductActive(productId, !product.isActive());
                }
            } else if (ACTION_DELETE_PRODUCT.equals(action)) {
                dbHelper.deleteProduct(productId);
            }

            dbHelper.close();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, ProductWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }
    }
}

