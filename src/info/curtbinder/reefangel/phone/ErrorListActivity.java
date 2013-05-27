/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.ErrorTable;
import info.curtbinder.reefangel.db.StatusProvider;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ErrorListActivity extends SherlockFragmentActivity {

	// private static final String TAG =
	// ErrorListActivity.class.getSimpleName();
	// private static final int[] TO = { 0, R.id.error_date, R.id.error_message
	// };

	private static final String[] FROM = {	ErrorTable.COL_ID,
											ErrorTable.COL_TIME,
											ErrorTable.COL_MESSAGE };
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
			return inflater.inflate( R.layout.errorslist, container, false );
		}

		@Override
		public void onActivityCreated ( Bundle savedInstanceState ) {
			super.onActivityCreated( savedInstanceState );
			getLoaderManager().restartLoader( 0, null, this );
		}

		@Override
		public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater ) {
			inflater.inflate( R.menu.delete_only_menu, menu );
		}

		@Override
		public boolean onOptionsItemSelected ( MenuItem item ) {
			switch ( item.getItemId() ) {
				case R.id.menu_delete:
					AlertDialog.Builder builder =
							new AlertDialog.Builder( getActivity() );
					builder.setMessage( rapp.getString( R.string.messageClearErrorHistory ) )
							.setCancelable( false )
							.setPositiveButton( rapp.getString( R.string.buttonYes ),
												new DialogInterface.OnClickListener() {
													public void onClick (
															DialogInterface dialog,
															int id ) {
														dialog.dismiss();
														deleteAll();
													}
												} )
							.setNegativeButton( rapp.getString( R.string.buttonNo ),
												new DialogInterface.OnClickListener() {
													public void onClick (
															DialogInterface dialog,
															int id ) {
														dialog.cancel();
													}
												} );

					AlertDialog alert = builder.create();
					alert.show();
					break;
			}
			return true;
		}

		private void deleteAll ( ) {
			Uri uri =
					Uri.parse( StatusProvider.CONTENT_URI + "/"
								+ StatusProvider.PATH_ERROR );
			getActivity().getContentResolver().delete( uri, null, null );
		}

		@Override
		public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
			Loader<Cursor> loader = null;
			Uri content =
					Uri.parse( StatusProvider.CONTENT_URI + "/"
								+ StatusProvider.PATH_ERROR );
			if ( id == 0 ) {
				loader =
						new CursorLoader( getActivity(), content, FROM, null,
							null, ErrorTable.COL_ID + " DESC" );
			}
			return loader;
		}

		@Override
		public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
			ListAdapter adapter = getListAdapter();
			if ( adapter == null || !(adapter instanceof CursorAdapter) ) {
				adapter = new ErrorListCursorAdapter( getActivity(), cursor, 0 );
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
