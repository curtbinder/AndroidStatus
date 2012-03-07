package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LicenseActivity extends Activity {
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.popup );

		TextView c = (TextView) findViewById( R.id.popupContent );
		c.setText( R.string.textLicense );

		Button b = (Button) findViewById( R.id.popupButton );
		b.setText( R.string.buttonOk );
		b.setOnClickListener( new OnClickListener() {
			public void onClick ( View v ) {
				finish();
			}
		} );
	}
}
