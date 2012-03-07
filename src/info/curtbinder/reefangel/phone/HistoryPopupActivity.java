package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HistoryPopupActivity extends BaseActivity {

	public static final String TAG = HistoryPopupActivity.class.getSimpleName();

	private TextView historyT1;
	private TextView historyT2;
	private TextView historyT3;
	private TextView historyPH;
	private TextView historySalinity;
	private TextView historyDP;
	private TextView historyAP;
	private TextView historyAtoLo;
	private TextView historyAtoHi;
	private Button okButton;

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.historypopup );

		findViews();

		Intent i = getIntent();
		long id = i.getLongExtra( RAData.PCOL_ID, -1 );

		if ( id > -1 )
			loadData( id );

		okButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick ( View v ) {
				finish();
			}
		} );
	}

	private void loadData ( long id ) {
		Cursor c = null;
		try {
			c = rapp.data.getDataById( id );
			// short r = 0, ron = 0, roff = 0;

			if ( c.moveToFirst() ) {
				setTitle( c.getString( c.getColumnIndex( RAData.PCOL_LOGDATE ) ) );
				historyT1.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_T1 ) ) );
				historyT2.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_T2 ) ) );
				historyT3.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_T3 ) ) );
				historyPH.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_PH ) ) );
				historySalinity.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_SAL ) ) );
				historyDP.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_DP ) ) );
				historyAP.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_AP ) ) );
				historyAtoLo.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_ATOLO ) ) );
				historyAtoHi.setText( c.getString( c
						.getColumnIndex( RAData.PCOL_ATOHI ) ) );
				// r = c.getShort( c.getColumnIndex( RAData.PCOL_RDATA ) );
				// ron = c.getShort( c.getColumnIndex( RAData.PCOL_RONMASK ) );
				// roff = c.getShort( c.getColumnIndex( RAData.PCOL_ROFFMASK )
				// );
			}
			c.close();
		} catch ( SQLException e ) {
			Log.d( TAG, "ItemClick SQLException" );
		} finally {
			if ( c != null )
				c.close();
		}
	}

	private void findViews ( ) {
		historyT1 = (TextView) findViewById( R.id.historyT1 );
		historyT2 = (TextView) findViewById( R.id.historyT2 );
		historyT3 = (TextView) findViewById( R.id.historyT3 );
		historyPH = (TextView) findViewById( R.id.historyPH );
		historySalinity = (TextView) findViewById( R.id.historySalinity );
		historyDP = (TextView) findViewById( R.id.historyDP );
		historyAP = (TextView) findViewById( R.id.historyAP );
		historyAtoLo = (TextView) findViewById( R.id.historyAtoLo );
		historyAtoHi = (TextView) findViewById( R.id.historyAtoHi );
		okButton = (Button) findViewById( R.id.popupButton );
	}
}
