/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.RAData;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HistoryPopupActivity extends Activity {

	public static final String TAG = HistoryPopupActivity.class.getSimpleName();
	public static final String DATA = "data";

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.historypopup );

		Intent i = getIntent();
		ContentValues v = (ContentValues) i.getParcelableExtra( DATA );
		loadData( v );

		Button b = (Button) findViewById( R.id.popupButton );
		b.setOnClickListener( new OnClickListener() {

			public void onClick ( View v ) {
				finish();
			}
		} );
	}

	private void loadData ( ContentValues v ) {
		setTitle( v.getAsString( RAData.PCOL_LOGDATE ) );
		((TextView) findViewById( R.id.historyT1 )).setText( v
				.getAsString( RAData.PCOL_T1 ) );
		((TextView) findViewById( R.id.historyT2 )).setText( v
				.getAsString( RAData.PCOL_T2 ) );
		((TextView) findViewById( R.id.historyT3 )).setText( v
				.getAsString( RAData.PCOL_T3 ) );
		((TextView) findViewById( R.id.historyPH )).setText( v
				.getAsString( RAData.PCOL_PH ) );
		((TextView) findViewById( R.id.historySalinity )).setText( v
				.getAsString( RAData.PCOL_SAL ) );
		((TextView) findViewById( R.id.historyDP )).setText( v
				.getAsString( RAData.PCOL_DP ) );
		((TextView) findViewById( R.id.historyAP )).setText( v
				.getAsString( RAData.PCOL_AP ) );
		((TextView) findViewById( R.id.historyAtoLo )).setText( v
				.getAsString( RAData.PCOL_ATOLO ) );
		((TextView) findViewById( R.id.historyAtoHi )).setText( v
				.getAsString( RAData.PCOL_ATOHI ) );
	}
}
