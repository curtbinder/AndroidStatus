package info.curtbinder.reefangel.phone;

import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NotificationListFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String[] FROM = {	NotificationTable.COL_ID,
											NotificationTable.COL_PARAM,
											NotificationTable.COL_CONDITION,
											NotificationTable.COL_VALUE };

	private static RAApplication rapp;
	private CheckBox ck;
	private ListView lv;
	private TextView tv;
	private boolean fRunOnStartup;

	@Override
	public View onCreateView (
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState ) {
		setHasOptionsMenu( true );
		rapp = (RAApplication) getActivity().getApplication();
		View v =
				inflater.inflate( R.layout.notificationslist, container, false );
		ck = (CheckBox) v.findViewById( R.id.checkEnableNotification );
		ck.setOnCheckedChangeListener( new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged (
					CompoundButton view,
					boolean isChecked ) {
				rapp.raprefs
						.set( R.string.prefNotificationEnableKey, isChecked );
				enableDisableView( lv, isChecked );

				// only toggle the empty list text if available
				if ( tv != null ) {
					tv.setEnabled( isChecked );
				}
			}
		} );

		lv = (ListView) v.findViewById( android.R.id.list );
		tv = (TextView) v.findViewById( android.R.id.empty );
		boolean fEnabledNotifications = rapp.raprefs.isNotificationEnabled();
		ck.setChecked( fEnabledNotifications );
		enableDisableView( lv, fEnabledNotifications );
		// only toggle the empty list text if available
		if ( tv != null ) {
			tv.setEnabled( fEnabledNotifications );
		}
		fRunOnStartup = true;
		return v;
	}

	public void enableDisableView ( View view, boolean enabled ) {
		view.setEnabled( enabled );

		if ( view instanceof ViewGroup ) {
			ViewGroup group = (ViewGroup) view;

			for ( int i = 0; i < group.getChildCount(); i++ ) {
				enableDisableView( group.getChildAt( i ), enabled );
			}
		}
	}

	@Override
	public void onActivityCreated ( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		getLoaderManager().restartLoader( 0, null, this );
	}

	@Override
	public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.notification_menu, menu );
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item ) {
		// if not enabled, don't allow changes to the list
		if ( !ck.isChecked() ) {
			return true;
		}
		switch ( item.getItemId() ) {
			case R.id.menu_delete:
				AlertDialog.Builder builder =
						new AlertDialog.Builder( getActivity() );
				builder.setMessage( rapp.getString( R.string.messageClearNotifications ) )
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
			case R.id.menu_add:
				Intent i =
						new Intent( getActivity(),
							NotificationPopupActivity.class );
				startActivity( i );
				break;
		}
		return true;
	}

	@Override
	public void onListItemClick ( ListView l, View v, int position, long id ) {
		Intent i = new Intent( getActivity(), NotificationPopupActivity.class );
		Uri uri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_NOTIFICATION + "/" + id );
		i.putExtra( StatusProvider.NOTIFICATION_ID_MIME_TYPE, uri );
		startActivity( i );
	}

	private void deleteAll ( ) {
		Uri uri =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_NOTIFICATION );
		getActivity().getContentResolver().delete( uri, null, null );
	}

	@Override
	public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
		Loader<Cursor> loader = null;
		Uri content =
				Uri.parse( StatusProvider.CONTENT_URI + "/"
							+ StatusProvider.PATH_NOTIFICATION );
		if ( id == 0 ) {
			loader =
					new CursorLoader( getActivity(), content, FROM, null, null,
						NotificationTable.COL_ID + " ASC" );
		}
		return loader;
	}

	@Override
	public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
		ListAdapter adapter = getListAdapter();
		if ( adapter == null || !(adapter instanceof CursorAdapter) ) {
			adapter =
					new NotificationListCursorAdapter( getActivity(), cursor, 0 );
			setListAdapter( adapter );
		} else {
			((CursorAdapter) adapter).swapCursor( cursor );
		}
		
		if ( fRunOnStartup ) {
			lv.post( new Runnable() {

				@Override
				public void run ( ) {
					// to get the controls to update properly when the fragment
					// is loaded, we have to enable/disable the individual list
					// items after the loader finishes, otherwise we only have 
					// the list disabled and the rest of the items "look" 
					// enabled even though they are not
					enableDisableView( lv, rapp.raprefs.isNotificationEnabled() );
					fRunOnStartup = false;
				}
			} );
		}
	}

	@Override
	public void onLoaderReset ( Loader<Cursor> arg0 ) {
		// on reset
	}

}
