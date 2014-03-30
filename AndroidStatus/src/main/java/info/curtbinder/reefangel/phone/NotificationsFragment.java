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

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import info.curtbinder.reefangel.db.NotificationTable;
import info.curtbinder.reefangel.db.StatusProvider;

public class NotificationsFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] FROM = {
            NotificationTable.COL_ID,
            NotificationTable.COL_PARAM,
            NotificationTable.COL_CONDITION,
            NotificationTable.COL_VALUE};

    private static RAApplication raApp;
    private CheckBox ck;
    private ListView lv;
    private TextView tv;
    private boolean fRunOnStartup;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.frag_notification, container, false);
        raApp = (RAApplication) getActivity().getApplication();
        ck = (CheckBox) root.findViewById(R.id.checkEnableNotification);
        ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(
                    CompoundButton view,
                    boolean isChecked) {
                raApp.raprefs.set(R.string.prefNotificationEnableKey, isChecked);
                enableDisableView(lv, isChecked);

                // only toggle the empty list text if available
                if (tv != null) {
                    tv.setEnabled(isChecked);
                }
            }
        });

        lv = (ListView) root.findViewById(android.R.id.list);
        tv = (TextView) root.findViewById(android.R.id.empty);
        boolean fEnabledNotifications = raApp.raprefs.isNotificationEnabled();
        ck.setChecked(fEnabledNotifications);
        enableDisableView(lv, fEnabledNotifications);
        // only toggle the empty list text if available
        if (tv != null) {
            tv.setEnabled(fEnabledNotifications);
        }
        fRunOnStartup = true;
        return root;
    }

    public void enableDisableView(View view, boolean enabled) {
        // todo hide action items from menu if disabled, show if enabled
        view.setEnabled(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                enableDisableView(group.getChildAt(i), enabled);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_notifications, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if not enabled, don't allow changes to the list
        if (!ck.isChecked()) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_delete_notification:
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.messageClearNotifications))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.buttonYes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.dismiss();
                                        deleteAll();
                                    }
                                }
                        )
                        .setNegativeButton(getString(R.string.buttonNo),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.action_add_notification:
//                Intent i = new Intent( getActivity(), NotificationPopupActivity.class );
//                startActivity( i );
                break;
        }
        return true;
    }

    private void deleteAll() {
        Uri uri =
                Uri.parse(StatusProvider.CONTENT_URI + "/"
                        + StatusProvider.PATH_NOTIFICATION);
        getActivity().getContentResolver().delete(uri, null, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
//        Intent i = new Intent( getActivity(), NotificationPopupActivity.class );
//        Uri uri =
//                Uri.parse( StatusProvider.CONTENT_URI + "/"
//                        + StatusProvider.PATH_NOTIFICATION + "/" + id );
//        i.putExtra( StatusProvider.NOTIFICATION_ID_MIME_TYPE, uri );
//        startActivity( i );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader = null;
        Uri content = Uri.parse(StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_NOTIFICATION);
        if (id == 0) {
            loader = new CursorLoader(getActivity(), content, FROM, null, null,
                    NotificationTable.COL_ID + " ASC");
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ListAdapter adapter = getListAdapter();
        if (adapter == null || !(adapter instanceof CursorAdapter)) {
            adapter = new NotificationListCursorAdapter(getActivity(), cursor, 0);
            setListAdapter(adapter);
        } else {
            ((CursorAdapter) adapter).swapCursor(cursor);
        }

        if (fRunOnStartup) {
            lv.post(new Runnable() {

                @Override
                public void run() {
                    // to get the controls to update properly when the fragment
                    // is loaded, we have to enable/disable the individual list
                    // items after the loader finishes, otherwise we only have
                    // the list disabled and the rest of the items "look"
                    // enabled even though they are not
                    enableDisableView(lv, raApp.raprefs.isNotificationEnabled());
                    fRunOnStartup = false;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // on reset
    }
}
