package com.eezzyweb.gridwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.gridwidget.R;

public class GridWidgetProvider extends AppWidgetProvider {

	public static final String BADGE_ACTION = "com.example.gridwidget.BADGE_ACTION";
	public static final String PREFS_NAME = "preferences";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Iterate through each widget, creating a RemoteViews object and
		// applying the modified RemoteViews to each widget.
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {

			int appWidgetId = appWidgetIds[i];

			// Create a Remote View with the widget wrapper layout
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			// Bind this widget to a Remote Views Service.
			Intent intent = new Intent(context, GridRemoteViewsService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			views.setRemoteAdapter(R.id.widget_grid_view, intent);

			// Specify a View within the Widget layout hierarchy to display 
			// when the collection of contacts is empty.
			views.setEmptyView(R.id.widget_grid_view, R.id.widget_empty_text);
			
			//get user preferences for this widget
			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
			int opacity  = prefs.getInt("opacity", 200);
			
			Log.i("opacity", String.valueOf(opacity));
			
			//set background opacity (scale it up since the png is half trasparent)
			views.setInt(R.id.bkg, "setAlpha", 2 * opacity);

			//trigger an intent to the GridWidgetProvider
			Intent templateIntent = new Intent(context, ContactBadge.class);

			// Set the action for the intent.
			templateIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

			PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			views.setPendingIntentTemplate(R.id.widget_grid_view, templatePendingIntent);

			// Notify the App Widget Manager to update the widget using
			// the modified remote view.
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}// onUpdate

	// Called when the BroadcastReceiver receives an Intent broadcast.
	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);
	}
}
