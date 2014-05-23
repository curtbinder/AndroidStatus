/*
 * Copyright (c) 2011-2013 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package info.curtbinder.reefangel.phone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class FirstRunActivity extends BaseActivity implements OnClickListener {

	public static final String TAG = FirstRunActivity.class.getSimpleName();

	private Button finishButton;
	private EditText hostText;
	private EditText portText;
	private EditText useridText;
	private boolean fPortalEnabled;

	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.firstrun );
		fPortalEnabled = false;
		
		((RadioButton)findViewById(R.id.radioButtonController)).setChecked( true );

		findViews();

		finishButton.setOnClickListener( this );
	}

	private void findViews ( ) {
		finishButton = (Button) findViewById( R.id.buttonFinish );
		hostText = (EditText) findViewById( R.id.firstHostText );
		portText = (EditText) findViewById( R.id.firstPortText );
		useridText = (EditText) findViewById( R.id.firstUsernameText );
	}

	public void onRadioButtonClicked ( View v ) {
		boolean checked = ((RadioButton) v).isChecked();
		// check which button was clicked
		switch ( v.getId() ) {
			case R.id.radioButtonController:
				if ( checked ) {
					fPortalEnabled = false;
					hostText.requestFocus();
				}
				break;
			case R.id.radioButtonPortal:
				if ( checked ) {
					fPortalEnabled = true;
					useridText.requestFocus();
				}
				break;
		}
	}
	
	public void onClick ( View v ) {
		boolean hasPort = false;
		boolean hasUser = false;
		
		String host = hostText.getText().toString();
		if ( fPortalEnabled ) {
			if ( !rapp.validateUser( useridText.getText() ) ) {
				// not valid, exit
				return;
			}
			hasUser = true;
			if ( !portText.getText().toString().equals( "" ) ) {
				// not empty, so we need to validate it
				if ( !rapp.validatePort( portText.getText() ) ) {
					// not valid, stay on page
					return;
				}
				hasPort = true;
			}
			if ( !hostText.getText().toString().equals("") ) {
				if ( !rapp.validateHost( hostText.getText() ) ){
					// not valid, stay on page
					return;
				}
			} else {
				// empty host, so use the default host
				host = rapp.getString( R.string.prefHostHomeDefault );
			}
			// if we made it here, we can save and are good
			// set device to be portal
			rapp.raprefs.set( R.string.prefDeviceKey, "1" );
		} else { 
			if ( !rapp.validateHost( hostText.getText() ) ) {
				// not valid, stay on the page
				return;
			}
			
			if ( !portText.getText().toString().equals( "" ) ) {
				// not empty, so we need to validate it
				if ( !rapp.validatePort( portText.getText() ) ) {
					// not valid, stay on page
					return;
				}
				hasPort = true;
			}
			
			if ( !useridText.getText().toString().equals( "" ) ) {
				// not empty, so we need to validate it
				if ( !rapp.validateUser( useridText.getText() ) ) {
					// not valid, stay on page
					return;
				}
				hasUser = true;
			}	
		}

		Log.w( TAG, "Saving settings" );
		rapp.raprefs.setHost( host );
		if ( hasPort ) {
			rapp.raprefs.setPort( portText.getText().toString() );
		}
		if ( hasUser ) {
			rapp.raprefs.setUserId( useridText.getText().toString() );
		}
		Log.w( TAG, "Configured, starting app" );
		rapp.raprefs.disableFirstRun();
		Intent i = new Intent( rapp.getBaseContext(), StatusActivity.class );
		i.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
		startActivity( i );
		finish();
	}
}
