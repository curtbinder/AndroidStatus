/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import info.curtbinder.reefangel.db.ErrorTable;
import info.curtbinder.reefangel.db.StatusProvider;

public class ErrorsFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] FROM = {
            ErrorTable.COL_ID,
            ErrorTable.COL_TIME,
            ErrorTable.COL_MESSAGE };

    private static final Uri uri = Uri.parse( StatusProvider.CONTENT_URI + "/"
                                            + StatusProvider.PATH_ERROR );

    public static ErrorsFragment newInstance() {
        return new ErrorsFragment();
    }

    public ErrorsFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.frag_errors, container, false);
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
        getLoaderManager().restartLoader( 0, null, this );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DialogYesNo.DELETE_ALL_CALL:
                if ( resultCode == Activity.RESULT_OK ) {
                    // User wants to delete the entire history
                    deleteAll();
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater ) {
        inflater.inflate( R.menu.delete_only_menu, menu );
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_delete:
                DialogYesNo d1 = DialogYesNo.newInstance(R.string.messageClearErrorHistory);
                d1.setTargetFragment(this, DialogYesNo.DELETE_ALL_CALL);
                d1.show(getFragmentManager(), "dlgyesno");
                break;
        }
        return true;
    }

    private void deleteAll ( ) {
        getActivity().getContentResolver().delete( uri, null, null );
    }

    @Override
    public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
        Loader<Cursor> loader = null;
        if ( id == 0 ) {
            loader = new CursorLoader( getActivity(), uri, FROM, null, null,
                            ErrorTable.COL_ID + " DESC" );
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
