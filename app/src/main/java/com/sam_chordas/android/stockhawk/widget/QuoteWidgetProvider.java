package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by chevelle on 4/17/16.
 */


import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.app.PendingIntent;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

public class QuoteWidgetProvider extends AppWidgetProvider {

    static final String SHOW_STOCK_ACTION = "com.sam_chordas.android.stockhawk.SHOW_STOCK_ACTION";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(SHOW_STOCK_ACTION)) {
            Intent stockIntent = new Intent(context, StockDetailActivity.class);
            stockIntent.fillIn(intent, 0);

            context.startActivity(stockIntent);
        }

        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent;
        Intent showStock;
        RemoteViews stocks;
        PendingIntent stockItem;

        for (int i = 0; i < appWidgetIds.length; i++) {
            intent = new Intent(context, QuoteWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            stocks = new RemoteViews(context.getPackageName(), R.layout.widget_collection);
            stocks.setRemoteAdapter(R.id.widget_list,  intent);
            stocks.setEmptyView(R.id.widget_list, R.id.empty_view);

            showStock = new Intent(context, QuoteWidgetProvider.class);
            showStock.setAction(SHOW_STOCK_ACTION);
            showStock.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            stockItem = PendingIntent.getBroadcast(context, 0, showStock, PendingIntent.FLAG_UPDATE_CURRENT);
            stocks.setPendingIntentTemplate(R.id.widget_list, stockItem);

            appWidgetManager.updateAppWidget(appWidgetIds[i], stocks);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
