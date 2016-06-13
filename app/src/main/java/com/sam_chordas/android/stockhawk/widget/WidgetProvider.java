package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by DShrout on 6/4/2016
 */
public class WidgetProvider extends AppWidgetProvider {
    private static final String FORMAT_DATA = "widgetFormatData";
    private static final String REFRESH_DATA = "widgetRefreshData";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Create an Intent to launch MyStocksActivity
            Intent launchIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

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
            Toast.makeText(context, "Ahhh, refreshing!", Toast.LENGTH_SHORT).show();

//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//
//            RemoteViews remoteViews;
//            ComponentName watchWidget;
//
//            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//            watchWidget = new ComponentName(context, WidgetProvider.class);
//
//            remoteViews.setTextViewText(R.id.widget_refresh, "TESTING");
//            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }

        if (FORMAT_DATA.equals(intent.getAction())) {
            Toast.makeText(context, "Money, money, money!", Toast.LENGTH_SHORT).show();
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
