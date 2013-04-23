/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.RAData;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ParamsListActivity extends SherlockListActivity {

	private static final String TAG = ParamsListActivity.class.getSimpleName();
	private static final int[] TO = { R.id.plog, R.id.pt1, R.id.pt2, R.id.pt3,
	/*
	 * R.id.pph, R.id.pdp, R.id.pap, R.id.psal, R.id.patoh, R.id.patol, R.id.pr,
	 * R.id.pron, R.id.proff
	 */
	};

	private static final String[] FROM = {	RAData.PCOL_LOGDATE,
											RAData.PCOL_T1,
											RAData.PCOL_T2,
											RAData.PCOL_T3 };
	RAApplication rapp;

	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.paramslist );
		rapp = (RAApplication) getApplication();
		updateData();
	}

	@SuppressWarnings("deprecation")
	private void showEvents ( Cursor cursor ) {
		Log.d( TAG, "showEvents" );
		SimpleCursorAdapter adapter =
				new SimpleCursorAdapter( this, R.layout.paramslistitem, cursor,
					FROM, TO );
		this.setListAdapter( adapter );
	}

	@SuppressWarnings("deprecation")
	private void updateData ( ) {
		try {
			Cursor c = rapp.data.getAllData();
			startManagingCursor( c );
			showEvents( c );
		} catch ( SQLException e ) {
			Log.d( TAG, "SQL Exception" );
		}
	}

	public boolean onCreateOptionsMenu ( Menu menu ) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate( R.menu.paramslist_menu, menu );
		return true;
	}

	public boolean onOptionsItemSelected ( MenuItem item ) {
		switch ( item.getItemId() ) {
			case R.id.params_delete:
				AlertDialog.Builder builder =
						new AlertDialog.Builder( ParamsListActivity.this );
				builder.setMessage( rapp.getString( R.string.messageDeleteAllPrompt ) )
						.setCancelable( false )
						.setPositiveButton( rapp.getString( R.string.buttonYes ),
											new DialogInterface.OnClickListener() {
												public void onClick (
														DialogInterface dialog,
														int id ) {
													Log.d( TAG, "Delete all" );
													dialog.dismiss();
													rapp.data.deleteData();
													Toast.makeText( ParamsListActivity.this,
																	rapp.getString( R.string.messageDeleted ),
																	Toast.LENGTH_SHORT )
															.show();
													updateData();
												}
											} )
						.setNegativeButton( rapp.getString( R.string.buttonNo ),
											new DialogInterface.OnClickListener() {
												public void onClick (
														DialogInterface dialog,
														int id ) {
													Log.d( TAG, "Cancel Delete" );
													dialog.cancel();
												}
											} );

				AlertDialog alert = builder.create();
				alert.show();
				break;
		}
		return true;
	}

	protected void onListItemClick ( ListView l, View v, int position, long id ) {
		// super.onListItemClick( l, v, position, id );
		// Log.d(TAG, "Clicked: position:" + position + ", id:" + id);
		Intent i = new Intent( this, HistoryPopupActivity.class );
		ContentValues cv = loadData( id );
		i.putExtra( HistoryPopupActivity.DATA, cv );
		startActivity( i );
	}

	private ContentValues loadData ( long id ) throws SQLException {
		ContentValues cv = new ContentValues();
		Cursor c = rapp.data.getDataById( id );
		// short r = 0, ron = 0, roff = 0;

		if ( c.moveToFirst() ) {
			cv.put( RAData.PCOL_LOGDATE,
					c.getString( c.getColumnIndex( RAData.PCOL_LOGDATE ) ) );
			cv.put( RAData.PCOL_T1,
					c.getString( c.getColumnIndex( RAData.PCOL_T1 ) ) );
			cv.put( RAData.PCOL_T2,
					c.getString( c.getColumnIndex( RAData.PCOL_T2 ) ) );
			cv.put( RAData.PCOL_T3,
					c.getString( c.getColumnIndex( RAData.PCOL_T3 ) ) );
			cv.put( RAData.PCOL_PH,
					c.getString( c.getColumnIndex( RAData.PCOL_PH ) ) );
			cv.put( RAData.PCOL_SAL,
					c.getString( c.getColumnIndex( RAData.PCOL_SAL ) ) );
			cv.put( RAData.PCOL_DP,
					c.getString( c.getColumnIndex( RAData.PCOL_DP ) ) );
			cv.put( RAData.PCOL_AP,
					c.getString( c.getColumnIndex( RAData.PCOL_AP ) ) );
			cv.put( RAData.PCOL_ATOLO,
					c.getString( c.getColumnIndex( RAData.PCOL_ATOLO ) ) );
			cv.put( RAData.PCOL_ATOHI,
					c.getString( c.getColumnIndex( RAData.PCOL_ATOHI ) ) );
		}
		c.close();
		return cv;
	}
}
