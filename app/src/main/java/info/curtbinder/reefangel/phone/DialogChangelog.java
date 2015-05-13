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

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

// This class is the same as DialogSupportChangeLog except it uses regular fragments
// This is needed for displaying the changelog from the preference screen
public class DialogChangelog extends DialogFragment {

    public DialogChangelog() {
    }

    public static DialogChangelog newInstance() {
        return new DialogChangelog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO improve getting current theme
        final int themeId = R.style.AlertDialogStyle;
        final ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getActivity(), themeId);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(themeWrapper);
        ChangeLogListView chgList = (ChangeLogListView)inflater.inflate(R.layout.dlg_changelog, null);
        return new AlertDialog.Builder(themeWrapper, themeId)
                .setTitle("Changelog")
                .setView(chgList)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
    }
}
