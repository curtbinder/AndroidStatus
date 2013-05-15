/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HistoryPopupActivity extends Activity {

	public static final String TAG = HistoryPopupActivity.class.getSimpleName();
	// public static final String DATA = "data";

	private Uri historyUri;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.historypopup );

		// Intent i = getIntent();
		// long id = i.getLongExtra( DATA, -1 );
		// ContentValues v = (ContentValues) i.getParcelableExtra( DATA );
		// loadData( id );
		Bundle extras = getIntent().getExtras();
		if ( extras != null ) {
			historyUri =
					extras.getParcelable( StatusProvider.STATUS_ID_MIME_TYPE );
			loadData( historyUri );
		}

		Button b = (Button) findViewById( R.id.popupButton );
		b.setOnClickListener( new OnClickListener() {

			public void onClick ( View v ) {
				finish();
			}
		} );
	}

	private void loadData ( Uri uri ) {
		String[] projection =
				{	StatusTable.COL_LOGDATE,
					StatusTable.COL_T1,
					StatusTable.COL_T2,
					StatusTable.COL_T3,
					StatusTable.COL_PH,
					StatusTable.COL_SAL,
					StatusTable.COL_DP,
					StatusTable.COL_AP,
					StatusTable.COL_ATOLO,
					StatusTable.COL_ATOHI };
		Cursor c =
				getContentResolver().query( uri, projection, null, null, null );
		if ( c != null ) {
			c.moveToFirst();
			setTitle( c.getString( c.getColumnIndex( StatusTable.COL_LOGDATE ) ) );
			((TextView) findViewById( R.id.historyT1 )).setText( c.getString( c
					.getColumnIndex( StatusTable.COL_T1 ) ) );
			((TextView) findViewById( R.id.historyT2 )).setText( c.getString( c
					.getColumnIndex( StatusTable.COL_T2 ) ) );
			((TextView) findViewById( R.id.historyT3 )).setText( c.getString( c
					.getColumnIndex( StatusTable.COL_T3 ) ) );
			((TextView) findViewById( R.id.historyPH )).setText( c.getString( c
					.getColumnIndex( StatusTable.COL_PH ) ) );
			((TextView) findViewById( R.id.historySalinity )).setText( c
					.getString( c.getColumnIndex( StatusTable.COL_SAL ) ) );
			((TextView) findViewById( R.id.historyDP )).setText( c.getString( c
					.getColumnIndex( StatusTable.COL_DP ) ) );
			((TextView) findViewById( R.id.historyAP )).setText( c.getString( c
					.getColumnIndex( StatusTable.COL_AP ) ) );
			((TextView) findViewById( R.id.historyAtoLo )).setText( c
					.getString( c.getColumnIndex( StatusTable.COL_ATOLO ) ) );
			((TextView) findViewById( R.id.historyAtoHi )).setText( c
					.getString( c.getColumnIndex( StatusTable.COL_ATOHI ) ) );
			c.close();
		}
	}
}
