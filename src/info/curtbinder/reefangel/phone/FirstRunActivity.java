package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FirstRunActivity extends BaseActivity implements OnClickListener {

	public static final String TAG = FirstRunActivity.class.getSimpleName();

	private Button finishButton;
	private EditText hostText;
	private EditText portText;
	private EditText useridText;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.firstrun );

		findViews();

		finishButton.setOnClickListener( this );
	}

	private void findViews ( ) {
		finishButton = (Button) findViewById( R.id.buttonFinish );
		hostText = (EditText) findViewById( R.id.firstHostText );
		portText = (EditText) findViewById( R.id.firstPortText );
		useridText = (EditText) findViewById( R.id.firstUsernameText );
	}

	public void onClick ( View v ) {

		boolean fPort = false;
		boolean fUser = false;
		if ( !rapp.validateHost( hostText.getText() ) )
			// not valid, stay on the page
			return;

		if ( !portText.getText().toString().equals( "" ) ) {
			// not empty, so we need to validate it
			if ( !rapp.validatePort( portText.getText() ) )
				// not valid, stay on page
				return;
			fPort = true;
		}

		if ( !useridText.getText().toString().equals( "" ) ) {
			// not empty, so we need to validate it
			if ( !rapp.validateUser( useridText.getText() ) )
				// not valid, stay on page
				return;
			fUser = true;
		}

		Log.w( TAG, "Saving settings" );
		rapp.setPref( R.string.prefHostKey, hostText.getText().toString() );
		if ( fPort ) {
			rapp.setPref( R.string.prefPortKey, portText.getText().toString() );
		}
		if ( fUser ) {
			rapp.setPref( R.string.prefUserIdKey, useridText.getText()
					.toString() );
		}
		Log.w( TAG, "Configured, starting app" );
		rapp.disableFirstRun();
		Intent i = new Intent( rapp.getBaseContext(), StatusActivity.class );
		i.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
		startActivity( i );
		finish();
	}
}
