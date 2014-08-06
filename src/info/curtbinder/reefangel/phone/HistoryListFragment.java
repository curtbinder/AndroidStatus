package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class HistoryListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int[] TO = {	0,
										R.id.plog,
										R.id.pt1,
										R.id.pt2,
										R.id.pt3 };

	private static final String[] FROM = {	StatusTable.COL_ID,
											StatusTable.COL_LOGDATE,
											StatusTable.COL_T1,
											StatusTable.COL_T2,
											StatusTable.COL_T3 };
	private static RAApplication rapp;

	@Override
	public View onCreateView (
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState ) {
		setHasOptionsMenu( true );
		rapp = (RAApplication) getActivity().getApplication();
		return inflater.inflate( R.layout.historylist, container, false );
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
				builder.setMessage( rapp.getString( R.string.messageDeleteAllPrompt ) )
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
							+ StatusProvider.PATH_STATUS );
		int rows = getActivity().getContentResolver().delete( uri, "1", null );
		String msg = rapp.getString( R.string.messageDeleted ) + ": " + rows;
		Toast.makeText( getActivity(), msg, Toast.LENGTH_SHORT ).show();
	}

	@Override
	public void onListItemClick ( ListView l, View v, int position, long id ) {
		// super.onListItemClick( l, v, position, id );
		Intent i = new Intent( getActivity(), HistoryPopupActivity.class );
		Uri historyUri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_STATUS + "/" + id );
		i.putExtra( StatusProvider.STATUS_ID_MIME_TYPE, historyUri );
		startActivity( i );
	}

	@Override
	public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
		Loader<Cursor> loader = null;
		Uri content =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_STATUS );
		if ( id == 0 ) {
			loader =
					new CursorLoader( getActivity(), content, FROM, null, null,
						StatusTable.COL_ID + " DESC" );
		}
		return loader;
	}

	@Override
	public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
		ListAdapter adapter = getListAdapter();
		if ( adapter == null || !(adapter instanceof CursorAdapter) ) {
			adapter =
					new SimpleCursorAdapter( getActivity(),
						R.layout.historylistitem, cursor, FROM, TO, 0 );
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
