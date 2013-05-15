/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.RADbHelper;
import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ParamsListActivity extends SherlockFragmentActivity {

	private static final String TAG = ParamsListActivity.class.getSimpleName();
	private static final int[] TO = { R.id.plog, R.id.pt1, R.id.pt2, R.id.pt3,
	/*
	 * R.id.pph, R.id.pdp, R.id.pap, R.id.psal, R.id.patoh, R.id.patol, R.id.pr,
	 * R.id.pron, R.id.proff
	 */
	                                  0
	};

	private static final String[] FROM = {	StatusTable.COL_LOGDATE,
											StatusTable.COL_T1,
											StatusTable.COL_T2,
											StatusTable.COL_T3,
											StatusTable.COL_ID};
	static RAApplication rapp;

	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		rapp = (RAApplication) getApplication();

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if ( fm.findFragmentById( android.R.id.content ) == null ) {
			CursorLoaderListFragment list = new CursorLoaderListFragment();
			fm.beginTransaction().add( android.R.id.content, list ).commit();
		}
	}

	public static class CursorLoaderListFragment extends SherlockListFragment
			implements LoaderManager.LoaderCallbacks<Cursor> {

		@Override
		public View onCreateView (
				LayoutInflater inflater,
				ViewGroup container,
				Bundle savedInstanceState ) {
			setHasOptionsMenu( true );
			return inflater.inflate( R.layout.paramslist, container, false );
		}

		@Override
		public void onActivityCreated ( Bundle savedInstanceState ) {
			super.onActivityCreated( savedInstanceState );
			getLoaderManager().restartLoader( 0, null, this );
		}

		@Override
		public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater ) {
			inflater.inflate( R.menu.paramslist_menu, menu );
		}

		@Override
		public boolean onOptionsItemSelected ( MenuItem item ) {
			switch ( item.getItemId() ) {
				case R.id.params_delete:
					AlertDialog.Builder builder =
							new AlertDialog.Builder( getActivity() );
					builder.setMessage( rapp.getString( R.string.messageDeleteAllPrompt ) )
							.setCancelable( false )
							.setPositiveButton( rapp.getString( R.string.buttonYes ),
												new DialogInterface.OnClickListener() {
													public void onClick (
															DialogInterface dialog,
															int id ) {
														Log.d(	TAG,
																"Delete all" );
														dialog.dismiss();
														// FIXME delete all data
														// rapp.data.deleteData();
														Toast.makeText( getActivity(),
																		rapp.getString( R.string.messageDeleted ),
																		Toast.LENGTH_SHORT )
																.show();
													}
												} )
							.setNegativeButton( rapp.getString( R.string.buttonNo ),
												new DialogInterface.OnClickListener() {
													public void onClick (
															DialogInterface dialog,
															int id ) {
														Log.d(	TAG,
																"Cancel Delete" );
														dialog.cancel();
													}
												} );

					AlertDialog alert = builder.create();
					alert.show();
					break;
			}
			return true;
		}

		@Override
		public void onListItemClick ( ListView l, View v, int position, long id ) {
			// super.onListItemClick( l, v, position, id );
			Intent i = new Intent( getActivity(), HistoryPopupActivity.class );
			Uri historyUri = Uri.parse( StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_STATUS + "/" + id );
			i.putExtra( StatusProvider.STATUS_ID_MIME_TYPE, historyUri );
			startActivity( i );
		}

		// private ContentValues loadData ( long id ) throws SQLException {
		// ContentValues cv = new ContentValues();
		// Cursor c = rapp.data.getDataById( id );
		// // short r = 0, ron = 0, roff = 0;
		//
		// if ( c.moveToFirst() ) {
		// cv.put( RADbHelper.PCOL_LOGDATE,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_LOGDATE ) ) );
		// cv.put( RADbHelper.PCOL_T1,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_T1 ) ) );
		// cv.put( RADbHelper.PCOL_T2,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_T2 ) ) );
		// cv.put( RADbHelper.PCOL_T3,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_T3 ) ) );
		// cv.put( RADbHelper.PCOL_PH,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_PH ) ) );
		// cv.put( RADbHelper.PCOL_SAL,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_SAL ) ) );
		// cv.put( RADbHelper.PCOL_DP,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_DP ) ) );
		// cv.put( RADbHelper.PCOL_AP,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_AP ) ) );
		// cv.put( RADbHelper.PCOL_ATOLO,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_ATOLO ) ) );
		// cv.put( RADbHelper.PCOL_ATOHI,
		// c.getString( c.getColumnIndex( RADbHelper.PCOL_ATOHI ) ) );
		// }
		// c.close();
		// return cv;
		// }

		@Override
		public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
			Loader<Cursor> loader = null;
			Uri content =
					Uri.parse( StatusProvider.CONTENT_URI + "/"
								+ StatusProvider.PATH_STATUS );
			if ( id == 0 ) {
				loader =
						new CursorLoader( getActivity(), content, FROM, null,
							null, StatusTable.COL_ID + " DESC" );
			}
			return loader;
		}

		@Override
		public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
			ListAdapter adapter = getListAdapter();
			if ( adapter == null || !(adapter instanceof CursorAdapter) ) {
				adapter =
						new SimpleCursorAdapter( getActivity(),
							R.layout.paramslistitem, cursor, FROM, TO, 0 );
				setListAdapter( adapter );
			} else {
				((CursorAdapter) adapter).swapCursor( cursor );
			}
		}

		@Override
		public void onLoaderReset ( Loader<Cursor> arg0 ) {
			// on reset
		}

	}
}
