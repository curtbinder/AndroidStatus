package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

/*
 * Displays the changelog
 */
public class DisplayLog {

	private static final String ASSET_CHANGELOG = "changelog.txt";

	@SuppressLint("InflateParams")
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
