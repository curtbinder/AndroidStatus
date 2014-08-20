/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/*
 * Displays the changelog
 */
public class DisplayLog {

    private static final String ASSET_CHANGELOG = "changelog.txt";

    private static void displayLog ( Activity a, int titleId, String data ) {
        final AlertDialog.Builder bld = new AlertDialog.Builder( a );
        bld.setCancelable( false );
        bld.setTitle( a.getString( titleId ) );
        LayoutInflater inf =
                (LayoutInflater) a.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inf.inflate( R.layout.displaylog, null );
        WebView wv = (WebView) layout.findViewById( R.id.logText );
        wv.loadDataWithBaseURL( null, data, "text/html", "utf-8", null );
        bld.setView( layout );
        bld.setPositiveButton(	a.getString( R.string.buttonOk ),
                new DialogInterface.OnClickListener() {

                    public void onClick (
                            DialogInterface dialog,
                            int which ) {
                        dialog.dismiss();
                    }
                } );
        bld.create().show();
    }

    public static void displayChangelog ( Activity a ) {
        // always display the changelog when this is called
        try {
            InputStreamReader is =
                    new InputStreamReader( a.getAssets().open( ASSET_CHANGELOG ) );
            displayLog(a, R.string.titleChangelog,
                    readFile(a, is));
        } catch ( IOException e ) {
        }
    }

    private static String readFile ( Context a, Reader reader ) {
        BufferedReader in = null;
        StringBuilder buf;
        try {
            in = new BufferedReader( reader );
            buf = new StringBuilder( 8192 );
            String line;
            while ( (line = in.readLine()) != null )
                buf.append( line ).append( '\n' );
        } catch ( IOException e ) {
            buf = new StringBuilder( "" );
        } finally {
            if ( in != null ) {
                try {
                    in.close();
                } catch ( IOException e ) {
                }
            }
        }
        return buf.toString();
    }

    public static void displayErrorlog ( Activity a, String logFile ) {
        try {
            displayLog(a, R.string.titleErrorlog, readFile(a, new FileReader(logFile)));
        } catch ( FileNotFoundException e ) {
        }
    }
}
