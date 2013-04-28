package com.eezzyweb.gridwidget;

import com.example.gridwidget.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.widget.QuickContactBadge;

public class ContactBadge extends Activity {

	private QuickContactBadge mPhotoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_badge);

		Intent intent = getIntent();

		Uri uri = Uri.parse(intent.getStringExtra(("Uri")));

		Rect rect = intent.getSourceBounds();

		QuickContact.showQuickContact(getApplicationContext(), rect, uri, QuickContact.MODE_SMALL, null);
		
		this.finish();

	}

}
