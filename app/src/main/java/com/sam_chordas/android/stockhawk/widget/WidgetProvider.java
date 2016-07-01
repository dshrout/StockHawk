package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockHistoryActivity;

/**
 * Created by DShrout on 6/4/2016
 */
public class WidgetProvider extends AppWidgetProvider {
    public static final String HISTORY_ACTION = "widgetHistoryAction";

    private static final String FORMAT_DATA = "widgetFormatData";
    private static final String REFRESH_DATA = "widgetRefreshData";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // create Intent to launch MyStocksActivity
            Intent launchIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // create Intent to launch StockHistoryActivity
            Intent historyIntent = new Intent(context, StockHistoryActivity.class);
            historyIntent.setAction(HISTORY_ACTION);
            historyIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent historyPendingIntent = PendingIntent.getActivity(context, 0, historyIntent, 0);
            remoteViews.setPendingIntentTemplate(R.id.widget_listview, historyPendingIntent);

            // create the service intent
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // set up the collection
            remoteViews.setEmptyView(R.id.widget_listview, R.id.empty_view);
            remoteViews.setRemoteAdapter(R.id.widget_listview, intent);

            // set the click handlers for the title bar icons
            remoteViews.setOnClickPendingIntent(R.id.widget_format, getPendingSelfIntent(context, FORMAT_DATA));
            remoteViews.setOnClickPendingIntent(R.id.widget_refresh, getPendingSelfIntent(context, REFRESH_DATA));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (REFRESH_DATA.equals(intent.getAction())) {
            updateWidget(context);
            Toast toast = Toast.makeText(context, R.string.refresh_message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
            toast.show();
        }

        if (FORMAT_DATA.equals(intent.getAction())) {
            Utils.showPercent = !Utils.showPercent;
            context.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
            updateWidget(context);
        }
    }

    protected void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName component = new ComponentName(context, WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(component);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
