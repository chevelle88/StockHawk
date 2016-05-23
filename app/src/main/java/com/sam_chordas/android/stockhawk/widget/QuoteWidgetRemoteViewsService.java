package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by chevelle on 4/17/16.
 */

import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.content.Intent;

public class QuoteWidgetRemoteViewsService extends RemoteViewsService {

    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }
}

class QuoteRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}