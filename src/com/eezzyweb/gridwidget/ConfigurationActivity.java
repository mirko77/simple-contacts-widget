package com.eezzyweb.gridwidget;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import com.example.gridwidget.R;

public class ConfigurationActivity extends Activity {

	private int mAppWidgetId;
	private ArrayList<String> group_list;
	private String group_selected;
	private int only_phone;
	private Spinner groups_sp;
	private CheckBox only_phone_cb;
	private SeekBar opacity_sb;
	private int opacity_value;

	public static final String PREFS_NAME = "preferences";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		//get hold of views
		only_phone_cb = (CheckBox) findViewById(R.id.only_phone);
		opacity_sb = (SeekBar) findViewById(R.id.bkg_opacity);
		groups_sp = (Spinner) findViewById(R.id.select_group);

		//set parameters for SeekBar
		opacity_sb.setProgress(100);
		opacity_sb.incrementProgressBy(10);
		opacity_sb.setActivated(true);
		
		//set default preferences values
		only_phone = 0;
		opacity_value = 100;

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		group_list = new ArrayList<String>();

		Cursor dataCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[] { ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DATA1 },
				ContactsContract.Data.MIMETYPE + "=?", new String[] { ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE }, null);

		//get list of contact groups from ContactsContract
		Cursor groups_cursor = getContentResolver()//
				.query(ContactsContract.Groups.CONTENT_SUMMARY_URI,//
						new String[] { //
						ContactsContract.Groups._ID,//
								ContactsContract.Groups.TITLE, //
								ContactsContract.Groups.DATA_SET,//
								ContactsContract.Groups.GROUP_VISIBLE,//
								ContactsContract.Groups.DELETED,//
								ContactsContract.Groups.SYSTEM_ID,//
								ContactsContract.Groups.SUMMARY_COUNT, //
								ContactsContract.Groups.SUMMARY_WITH_PHONES //
						},//
						null, null, null);//

		

		while (groups_cursor.moveToNext()) {

			//Log.i("group title", String.valueOf(groups_cursor.getString(groups_cursor.getColumnIndex("TITLE"))));
			String title = groups_cursor.getString(groups_cursor.getColumnIndex("title"));
			String count = String.valueOf(groups_cursor.getInt(groups_cursor.getColumnIndex("summ_count")));

			group_list.add(title + " (" + count + ")");

		}

		for (int i = 0; i < group_list.size(); i++) {

			Log.i("arraylist", group_list.get(i));
		}

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(//
				this, //
				android.R.layout.simple_spinner_dropdown_item,// 
				group_list//
		);//

		groups_sp.setAdapter(spinnerArrayAdapter);

		/**********************************************************************/
		//Set listener for spinner
		groups_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			//@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				group_selected = parentView.getItemAtPosition(position).toString();
			}

			//@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// Default to "My Contacts"
				group_selected = "My Contacts";
			}
		});
		/*********************************************************************/

		/********************************************************************/
		//set SeekBar listeners
		opacity_sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub   

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub      
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				opacity_value = progress;
			}

		});

	}//onCreate

	public void onClickCancel(View v) {

		this.finish();

	}

	public void onClickSave(View v) {

		//get value of checkbox
		if (only_phone_cb.isChecked()) {
			only_phone = 1;
		}

		//save values to SharedPreferences
		SharedPreferences.Editor prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
		prefs.putInt("only_phone", only_phone);
		prefs.putInt("opacity", opacity_value);
		
		//remove count from selected group string i.e "(23)"
		group_selected = group_selected.substring(0, group_selected.length() - 4);
		prefs.putString("group_selected", group_selected);
		prefs.commit();
		
		Log.i("group selected", group_selected);

		//From Google docs : how to update a widget on creation
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

		// Create a Remote View.
		RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_layout);

		appWidgetManager.updateAppWidget(mAppWidgetId, views);

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);

		//send an intent to this widget to trigger onUpdate in GridWidgetProvider
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, GridWidgetProvider.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { mAppWidgetId });
		sendBroadcast(intent);

		finish();

	}

}
