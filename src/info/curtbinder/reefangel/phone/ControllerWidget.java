package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 * 
 * This work is made available under the terms of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class ControllerWidget extends ScrollView {
	private static final String TAG = ControllerWidget.class.getSimpleName();
	private static final int T1_INDEX = 0;
	private static final int T2_INDEX = 1;
	private static final int T3_INDEX = 2;
	private static final int PH_INDEX = 3;
	private static final int DP_INDEX = 4;
	private static final int AP_INDEX = 5;
	private static final int SALINITY_INDEX = 6;
	private static final int ORP_INDEX = 7;

	Context ctx; // saved context from parent
	private TextView t1Text;
	private TextView t2Text;
	private TextView t3Text;
	private TextView phText;
	private TextView dpText;
	private TextView apText;
	private TextView salinityText;
	private TextView t1Label;
	private TextView t2Label;
	private TextView t3Label;
	private TextView phLabel;
	private TextView dpLabel;
	private TextView apLabel;
	private TextView salinityLabel;

	// private TextView orpLabel;

	public ControllerWidget ( Context context ) {
		super( context );
		addViewsFromLayout( context );
		ctx = context;
		// setDefaults();
	}

	public ControllerWidget ( Context context, AttributeSet attrs ) {
		super( context, attrs );
		addViewsFromLayout( context );
		ctx = context;
		// setDefaults();
	}

	private void addViewsFromLayout ( Context context ) {
		LayoutInflater layoutInflater =
				(LayoutInflater) context
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.controller, this );
		findViews();
	}

	private void findViews ( ) {
		t1Text = (TextView) findViewById( R.id.temp1 );
		t2Text = (TextView) findViewById( R.id.temp2 );
		t3Text = (TextView) findViewById( R.id.temp3 );
		phText = (TextView) findViewById( R.id.ph );
		dpText = (TextView) findViewById( R.id.dp );
		apText = (TextView) findViewById( R.id.ap );
		salinityText = (TextView) findViewById( R.id.salinity );
		t1Label = (TextView) findViewById( R.id.t1_label );
		t2Label = (TextView) findViewById( R.id.t2_label );
		t3Label = (TextView) findViewById( R.id.t3_label );
		phLabel = (TextView) findViewById( R.id.ph_label );
		dpLabel = (TextView) findViewById( R.id.dp_label );
		apLabel = (TextView) findViewById( R.id.ap_label );
		salinityLabel = (TextView) findViewById( R.id.salinity_label );
		// orpLabel = (TextView) findViewById( R.id.orp_label );
	}

	public void setT1Label ( String label ) {
		t1Label.setText( label );
	}

	public void setT2Label ( String label ) {
		t2Label.setText( label );
	}

	public void setT3Label ( String label ) {
		t3Label.setText( label );
	}

	public void setAPLabel ( String label ) {
		apLabel.setText( label );
	}

	public void setDPLabel ( String label ) {
		dpLabel.setText( label );
	}

	public void setPHLabel ( String label ) {
		phLabel.setText( label );
	}

	public void setSalinityLabel ( String label ) {
		salinityLabel.setText( label );
	}

	public void setT2Visibility ( boolean fVisible ) {
		if ( fVisible ) {
			Log.d( TAG, "T2 visible" );
			t2Text.setVisibility( View.VISIBLE );
			t2Label.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "T2 gone" );
			t2Text.setVisibility( View.GONE );
			t2Label.setVisibility( View.GONE );
		}
	}

	public void setT3Visibility ( boolean fVisible ) {
		if ( fVisible ) {
			Log.d( TAG, "T3 visible" );
			t3Text.setVisibility( View.VISIBLE );
			t3Label.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "T3 gone" );
			t3Text.setVisibility( View.GONE );
			t3Label.setVisibility( View.GONE );
		}
	}

	public void setPHVisibility ( boolean fVisible ) {
		if ( fVisible ) {
			Log.d( TAG, "PH visible" );
			phText.setVisibility( View.VISIBLE );
			phLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "PH gone" );
			phText.setVisibility( View.GONE );
			phLabel.setVisibility( View.GONE );
		}
	}

	public void setAPVisibility ( boolean fVisible ) {
		if ( fVisible ) {
			Log.d( TAG, "AP visible" );
			apText.setVisibility( View.VISIBLE );
			apLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "AP gone" );
			apText.setVisibility( View.GONE );
			apLabel.setVisibility( View.GONE );
		}
	}

	public void setDPVisibility ( boolean fVisible ) {
		if ( fVisible ) {
			Log.d( TAG, "DP visible" );
			dpText.setVisibility( View.VISIBLE );
			dpLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "DP gone" );
			dpText.setVisibility( View.GONE );
			dpLabel.setVisibility( View.GONE );
		}
	}

	public void setSalinityVisibility ( boolean fVisible ) {
		if ( fVisible ) {
			Log.d( TAG, "Salinity visible" );
			salinityText.setVisibility( View.VISIBLE );
			salinityLabel.setVisibility( View.VISIBLE );
		} else {
			Log.d( TAG, "Salinity gone" );
			salinityText.setVisibility( View.GONE );
			salinityLabel.setVisibility( View.GONE );
		}
	}

	public void updateDisplay ( String[] v ) {
		t1Text.setText( v[T1_INDEX] );
		t2Text.setText( v[T2_INDEX] );
		t3Text.setText( v[T3_INDEX] );
		phText.setText( v[PH_INDEX] );
		dpText.setText( v[DP_INDEX] );
		apText.setText( v[AP_INDEX] );
		salinityText.setText( v[SALINITY_INDEX] );
	}

}
