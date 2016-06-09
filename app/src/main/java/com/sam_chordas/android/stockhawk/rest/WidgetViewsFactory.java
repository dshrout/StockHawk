package com.sam_chordas.android.stockhawk.rest;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DShrout on 6/5/2016
 */
public class WidgetViewsFactory implements RemoteViewsFactory, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int mCount = 10;
    private List<WidgetItem> mWidgetItems = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor;

    private class WidgetItem {
        public String stockSymbol;
        public String bidPrice;
        public String change;
        public String percentChange;
        public String isUp;
        public String isCurrent;
    }

    private static final String[] QUOTE_COLUMNS = {
            "Distinct " + QuoteColumns.SYMBOL,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.BIDPRICE,
            QuoteColumns.ISUP,
            QuoteColumns.ISCURRENT
    };
    // QUOTE_COLUMNS indices
    private static final int COL_SYMBOL = 0;
    private static final int COL_PERCENT_CHANGE = 1;
    private static final int COL_CHANGE = 2;
    private static final int COL_BIDPRICE = 3;
    private static final int COL_ISUP = 4;
    private static final int COL_ISCURRENT = 5;

    // constructor
    public WidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onCreate() {
    }

    @Override
    public RemoteViews getViewAt(int position) {
        WidgetItem widgetItem = mWidgetItems.get(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        int color = widgetItem.isUp.equals("1") ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red;

        views.setTextViewText(R.id.widget_stock_symbol, widgetItem.stockSymbol);
        views.setTextViewText(R.id.widget_bid_price, widgetItem.bidPrice);
        views.setTextViewText(R.id.widget_change, Utils.showPercent ? widgetItem.percentChange : widgetItem.change);
        views.setInt(R.id.widget_change, "setBackgroundResource", color);

        return views;
    }

    @Override
    public void onDataSetChanged() {
        final long token = Binder.clearCallingIdentity();
        try {
            List<WidgetItem> WidgetItems = new ArrayList<>();
            mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, QUOTE_COLUMNS, QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);

            if (mCursor != null && mCursor.getCount() > 0) {
                try {
                    while (mCursor.moveToNext()) {
                        WidgetItem widgetItem = new WidgetItem();
                        widgetItem.stockSymbol = mCursor.getString(COL_SYMBOL);
                        widgetItem.percentChange = mCursor.getString(COL_PERCENT_CHANGE);
                        widgetItem.change = mCursor.getString(COL_CHANGE);
                        widgetItem.bidPrice = mCursor.getString(COL_BIDPRICE);
                        widgetItem.isUp = mCursor.getString(COL_ISUP);
                        widgetItem.isCurrent = mCursor.getString(COL_ISCURRENT);

                        WidgetItems.add(widgetItem);
                    }

                    // if we have updates, swap out the data
                    if (WidgetItems.size() > 0) {
                        mWidgetItems.clear();
                        mWidgetItems.addAll(WidgetItems);
                    }
                } finally {
                    mCursor.close();
                }
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
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
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
