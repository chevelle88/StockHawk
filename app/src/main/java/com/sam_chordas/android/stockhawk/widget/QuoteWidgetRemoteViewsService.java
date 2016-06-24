package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by chevelle on 4/17/16.
 */

import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.rest.Utils;

public class QuoteWidgetRemoteViewsService extends RemoteViewsService {

    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuoteRemoteViewFactory(getApplicationContext(), intent);
    }
}

class QuoteRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Intent intent;
    private Cursor cursor;
    private boolean hasData;

    // SELECT Clause Columns
    private String [] cols = { QuoteColumns._ID, QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE, QuoteColumns.CHANGE};

    // WHERE Clause Expression and Arguments
    private String quotesFilter = QuoteColumns.ISCURRENT + "= ?";
    private String quotesFilterArg [] = { "1" };

    public QuoteRemoteViewFactory(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        getNewStocks();
    }

    @Override
    public void onDataSetChanged() {
        getNewStocks();
    }

    @Override
    public void onDestroy() {

        if (hasData) {
            cursor.close();
        }

    }

    @Override
    public int getCount() {
        int totalStocks = (hasData) ? cursor.getCount() : 0;

        return totalStocks;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_collection_item);

        if (hasData) {
            cursor.moveToPosition(position);

            view.setTextViewText(R.id.stock_symbol,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));

            view.setTextViewText(R.id.change,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));

            Intent stockIntent = new Intent();
            stockIntent.putExtra(Utils.STOCK_SYMBOL,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
            stockIntent.putExtra(Utils.STOCK_BID_PRICE,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            stockIntent.putExtra(Utils.STOCK_PRICE_CHANGE,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));

            view.setOnClickFillInIntent(R.id.widget_list_item, stockIntent);

        }

        return view;
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
        long itemId = -1;
        int columnIdx = cursor.getColumnIndex(QuoteColumns._ID);

        if (hasData) {
            cursor.moveToPosition(position);
            itemId = cursor.getInt(columnIdx);
        }

        return itemId;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void getNewStocks() {

        if (cursor != null) {
            cursor.close();
        }

        cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, cols,
                quotesFilter, quotesFilterArg, QuoteColumns.SYMBOL);

        hasData = ((cursor != null) && (cursor.getCount() > 0));
    }
}