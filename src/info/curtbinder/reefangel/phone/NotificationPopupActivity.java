/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NotificationPopupActivity extends Activity {

	public static final String TAG = NotificationPopupActivity.class
			.getSimpleName();
	private static final Uri NOTIFY_URI = Uri
			.parse( StatusProvider.CONTENT_URI + "/"
					+ StatusProvider.PATH_NOTIFICATION );

	private Spinner paramSpinner;
	private Spinner condSpinner;
	private EditText valueText;
	private Button saveButton;
	private Button cancelButton;
	private Button deleteButton;
	private long id;
	private Uri uri = null;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.notificationpopup );

		findViews();
		setAdapters();

		Bundle extras = getIntent().getExtras();
		if ( extras != null ) {
			uri =
					extras.getParcelable( StatusProvider.NOTIFICATION_ID_MIME_TYPE );
			id = Long.parseLong( uri.getLastPathSegment() );
			loadData();
		} else {
			id = -1;
		}

		updateDisplay();

		loadOnClickListeners();
	}

	private boolean isUpdate ( ) {
		if ( id > -1 ) {
			return true;
		}
		return false;
	}

	private void updateDisplay ( ) {
		// updates the buttons at the bottom of the window
		// based on the type of window we are displaying
		// If we are updating the notification item, then we display
		// Cancel, Delete, Save
		// If we are creating a new notification item, then we display
		// Cancel, Save

		// Set the title of the window
		// If updating, display Update Notification
		// If new, display Create Notification
		if ( isUpdate() ) {
			setTitle( getString( R.string.titleUpdateNotification ) );
		} else {
			deleteButton.setVisibility( View.GONE );
			setTitle( getString( R.string.titleCreateNotification ) );
		}
	}

	private void findViews ( ) {
		paramSpinner = (Spinner) findViewById( R.id.notifyParameterSpin );
		condSpinner = (Spinner) findViewById( R.id.notifyConditionSpin );
		valueText = (EditText) findViewById( R.id.notifyValue );
		cancelButton = (Button) findViewById( R.id.cancelButton );
		deleteButton = (Button) findViewById( R.id.deleteButton );
		saveButton = (Button) findViewById( R.id.saveButton );
	}

	private void setAdapters ( ) {
		ArrayAdapter<CharSequence> p =
				ArrayAdapter
						.createFromResource(	this,
												R.array.deviceParameters,
												android.R.layout.simple_spinner_item );
		p.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		paramSpinner.setAdapter( p );
		ArrayAdapter<CharSequence> c =
				ArrayAdapter
						.createFromResource(	this,
												R.array.notifyConditions,
												android.R.layout.simple_spinner_item );
		c.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		condSpinner.setAdapter( c );
	}

	private void loadOnClickListeners ( ) {
		cancelButton.setOnClickListener( new OnClickListener() {

			public void onClick ( View v ) {
				// discard the working form
				finish();
			}
		} );
		deleteButton.setOnClickListener( new OnClickListener() {
			public void onClick ( View v ) {
				deleteNotification();
			}
		} );
		saveButton.setOnClickListener( new OnClickListener() {
			public void onClick ( View v ) {
				saveNotification();
			}
		} );
	}

	private void loadData ( ) {
		String[] projection =
				{	NotificationTable.COL_PARAM,
					NotificationTable.COL_CONDITION,
					NotificationTable.COL_VALUE };
		Cursor c =
				getContentResolver().query( uri, projection, null, null, null );
		if ( c != null ) {
			c.moveToFirst();

			if ( c.moveToFirst() ) {
				paramSpinner.setSelection( c.getInt( c
						.getColumnIndex( NotificationTable.COL_PARAM ) ), true );
				condSpinner
						.setSelection(	c.getInt( c
												.getColumnIndex( NotificationTable.COL_CONDITION ) ),
										true );
				valueText.setText( c.getString( c
						.getColumnIndex( NotificationTable.COL_VALUE ) ) );
			}
			c.close();
		}

	}

	protected void saveNotification ( ) {
		String s = valueText.getText().toString();
		Log.d(	TAG,
				"Save Notification: " + paramSpinner.getSelectedItemPosition()
						+ ", " + condSpinner.getSelectedItemPosition() + ", "
						+ s );
		// do error checking to ensure that there is a value
		if ( TextUtils.isEmpty( s ) ) {
			// Empty string
			Toast.makeText( this,
							getResources()
									.getString( R.string.messageEmptyValue ),
							Toast.LENGTH_SHORT ).show();
			return;
		}

		ContentValues v = new ContentValues();
		v.put(	NotificationTable.COL_PARAM,
				paramSpinner.getSelectedItemPosition() );
		v.put(	NotificationTable.COL_CONDITION,
				condSpinner.getSelectedItemPosition() );
		v.put( NotificationTable.COL_VALUE, valueText.getText().toString() );

		if ( isUpdate() ) {
			// update the values in the table
			getContentResolver()
					.update( NOTIFY_URI, v, NotificationTable.COL_ID + "=?",
								new String[] { Long.toString( id ) } );
		} else {
			// Insert the values in the table
			getContentResolver().insert( NOTIFY_URI, v );
		}

		finish();
	}

	protected void deleteNotification ( ) {
		Uri deleteUri = Uri.withAppendedPath( NOTIFY_URI, Long.toString( id ) );
		getContentResolver().delete( deleteUri, null, null );
		finish();
	}
}
