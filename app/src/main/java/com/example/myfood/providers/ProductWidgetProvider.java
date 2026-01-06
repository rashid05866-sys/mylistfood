package com.example.myfood.providers;

import static com.example.myfood.R.layout.widget_product_list;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.myfood.activities.MainActivity;

public class ProductWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i=0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    /* context = */ context,
            /* requestCode = */ 0,
    /* intent = */ intent,
    /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );



        RemoteViews views = new RemoteViews(context.getPackageName(), widget_product_list);

        //views.setOnClickPendingIntent(R.id.button, pendingIntent);

    // Tell the AppWidgetManager to perform an update on the current app
    // widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}
