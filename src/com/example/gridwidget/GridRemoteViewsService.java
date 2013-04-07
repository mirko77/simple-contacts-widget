package com.example.gridwidget;

import java.io.InputStream;
import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class GridRemoteViewsService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new GridRemoteViewsFactory(getApplicationContext(), intent);
	}

	class GridRemoteViewsFactory implements RemoteViewsFactory {

		private ArrayList<Bundle> items = new ArrayList<Bundle>();
		private Context context;
		private Intent intent;
		private int widgetId;
		private Bitmap image;

		public GridRemoteViewsFactory(Context context, Intent intent) {

			// Optional constructor implementation.
			// Useful for getting references to the
			// Context of the calling widget
			this.context = context;
			this.intent = intent;
			widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// Set up any connections / cursors to your data source.
		// Heavy lifting, like downloading data should be
		// deferred to onDataSetChanged()or getViewAt().
		// Taking more than 20 seconds in this call will result
		// in an ANR.
		public void onCreate() {

			//get contacts data to display
			final String[] PROJECTION = new String[] { ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
					ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };

			//get starred contacts using Contacts Provider (starred == 1) ordering by ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
			Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, "starred=?", new String[] { "1" }, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

			while (cursor.moveToNext()) {
				
				//get contact details to be displayed on widget
				String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
				String photo_url = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
				long contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				long lookup_key = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

				String[] full_name = display_name.split(" ");
				String first_name = full_name[0];

				Bundle widget_item = new Bundle();
				widget_item.putString("first_name", first_name);
				widget_item.putString("photo_url", photo_url);
				widget_item.putLong("contact_id", contact_id);
				widget_item.putLong("lookup_key", lookup_key);
				
				//add widget itme to array
				items.add(widget_item);

			}

			cursor.close();

		}

		// Called when the underlying data collection being displayed is
		// modified. You can use the AppWidgetManager�s
		// notifyAppWidgetViewDataChanged method to trigger this handler.
		public void onDataSetChanged() {
			// TODO Processing when underlying data has changed.
		}

		// Return the number of items in the collection being displayed.
		public int getCount() {
			return items.size();

		}

		// Return true if the unique IDs provided by each item are stable --
		// that is, they don�t change at run time.
		public boolean hasStableIds() {
			return false;
		}

		// Return the unique ID associated with the item at a given index.
		public long getItemId(int index) {
			return index;
		}

		// The number of different view definitions. Usually 1.
		public int getViewTypeCount() {
			return 1;
		}

		// Optionally specify a �loading" view to display. Return null to
		// use the default.
		public RemoteViews getLoadingView() {
			return null;
		}

		// Create and populate the View to display at the given index.
		public RemoteViews getViewAt(int index) {

			// Create a view to display at the required index.
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_layout);

			// Populate the view from the underlying data.
			rv.setTextViewText(R.id.name, items.get(index).getString("first_name"));

			//check if we have a photo set
			if (items.get(index).getString("photo_url") != null) {

				rv.setImageViewUri(R.id.photo_url, Uri.parse(items.get(index).getString("photo_url")));
			}

			Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, items.get(index).getLong("contact_id"));
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
			
			
			
			if (input != null) {

				Log.i("input", String.valueOf(input));

				image = BitmapFactory.decodeStream(input);

			} else {
				image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_dark);
			}

			rv.setImageViewBitmap(R.id.photo_url, image);

			// Create an item-specific fill-in Intent that will populate
			// the Pending Intent template created in the App Widget Provider (GridWidgetProvider).
			Intent fillInIntent = new Intent();
			
			fillInIntent.putExtra("Uri", String.valueOf(contactUri));
			
			rv.setOnClickFillInIntent(R.id.wrapper, fillInIntent);
			return rv;
		}

		// Close connections, cursors, or any other persistent state you
		// created in onCreate.
		public void onDestroy() {
			items.clear();
		}
	}
}
