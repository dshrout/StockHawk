package com.sam_chordas.android.stockhawk.rest;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.sam_chordas.android.stockhawk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DShrout on 6/5/2016
 */
public class WidgetViewsFactory implements RemoteViewsFactory {
    private static final int mCount = 10;
    private List<WidgetItem> mWidgetItems = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;

    private class WidgetItem {
        public String stockSymbol;
        public String bidPrice;
        public String change;
    }

    public WidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        WidgetItem google = new WidgetItem();
        WidgetItem apple = new WidgetItem();
        WidgetItem yahoo = new WidgetItem();
        WidgetItem microsoft = new WidgetItem();

        google.stockSymbol = "GOOG";
        google.bidPrice = "721.60";
        google.change = "-1.10%";

        apple.stockSymbol = "AAPL";
        apple.bidPrice = "97.91";
        apple.change = "+0.20%";

        yahoo.stockSymbol = "YHOO";
        yahoo.bidPrice = "36.50";
        yahoo.change = "-1.48%";

        microsoft.stockSymbol = "MSFT";
        microsoft.bidPrice = "51.70";
        microsoft.change = "-1.31%";

        mWidgetItems.add(google);
        mWidgetItems.add(apple);
        mWidgetItems.add(yahoo);
        mWidgetItems.add(microsoft);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        WidgetItem widgetItem = new WidgetItem();
        widgetItem = mWidgetItems.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        views.setTextViewText(R.id.widget_stock_symbol, widgetItem.stockSymbol);
        views.setTextViewText(R.id.widget_bid_price, widgetItem.bidPrice);
        views.setTextViewText(R.id.widget_change, widgetItem.change);

        return views;
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
