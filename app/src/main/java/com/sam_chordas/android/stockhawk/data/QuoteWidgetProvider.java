package com.sam_chordas.android.stockhawk.data;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockWidgetService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by DShrout on 6/4/2016
 */
public class QuoteWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            // set up the intent
            Intent intent = new Intent(context, StockWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // set up the RemoteViews
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_quotes);
            remoteViews.setRemoteAdapter(widgetId, R.id.widget_listview, intent);
            remoteViews.setEmptyView(R.id.widget_listview, R.id.empty_view);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);


//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quotes);
//
//            // Create an Intent to launch MyStocksActivity
//            Intent launchIntent = new Intent(context, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            views.setTextViewText(R.id.widget_stock_symbol, "GOOG");
//            views.setTextViewText(R.id.widget_bid_price, "117.56");
//            views.setTextViewText(R.id.widget_change, "+1.2");
//
//            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
