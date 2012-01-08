package info.curtbinder.reefangel.phone;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class RAApplication extends Application {

	private static final String TAG = RAApplication.class.getSimpleName();
	private SharedPreferences prefs;
	// Error code stuff
	private String[] errorCodes;
	private String[] errorCodesStrings;
	public int errorCode;
	// Devices stuff
	private String[] devicesArray;

	// Controller Data
	public RAData data;

	// Relay labels
	private int[][] relayLabels;
	private int[] relayDefaultLabels;

	// Service Stuff
	public boolean isServiceRunning;

	@Override
	public void onCreate ( ) {
		prefs = PreferenceManager.getDefaultSharedPreferences( this );
		errorCodes = getResources().getStringArray( R.array.errorCodes );
		errorCodesStrings =
				getResources().getStringArray( R.array.errorCodesStrings );
		errorCode = 0; // set to no error initially
		data = new RAData( this );
		devicesArray = getResources().getStringArray( R.array.devicesValues );
		isServiceRunning = false;

		fillRelayLabels();

		if ( !isServiceRunning )
			startService( new Intent( this, ControllerService.class ) );
	}

	@Override
	public void onTerminate ( ) {
		super.onTerminate();
		data.close();

		if ( isServiceRunning )
			stopService( new Intent( this, ControllerService.class ) );
	}

	// Data handling
	public void insertData ( Intent i ) {
		ContentValues v = new ContentValues();
		v.put( RAData.PCOL_T1, i.getStringExtra( RAData.PCOL_T1 ) );
		v.put( RAData.PCOL_T2, i.getStringExtra( RAData.PCOL_T2 ) );
		v.put( RAData.PCOL_T3, i.getStringExtra( RAData.PCOL_T3 ) );
		v.put( RAData.PCOL_PH, i.getStringExtra( RAData.PCOL_PH ) );
		v.put( RAData.PCOL_DP, i.getStringExtra( RAData.PCOL_DP ) );
		v.put( RAData.PCOL_AP, i.getStringExtra( RAData.PCOL_AP ) );
		v.put( RAData.PCOL_SAL, i.getStringExtra( RAData.PCOL_SAL ) );
		v.put( RAData.PCOL_ATOHI, i.getBooleanExtra( RAData.PCOL_ATOHI, false ) );
		v.put( RAData.PCOL_ATOLO, i.getBooleanExtra( RAData.PCOL_ATOLO, false ) );
		v.put( RAData.PCOL_LOGDATE, i.getStringExtra( RAData.PCOL_LOGDATE ) );
		v.put(	RAData.PCOL_RDATA,
				i.getShortExtra( RAData.PCOL_RDATA, (short) 0 ) );
		v.put(	RAData.PCOL_RONMASK,
				i.getShortExtra( RAData.PCOL_RONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_ROFFMASK,
				i.getShortExtra( RAData.PCOL_ROFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R1DATA,
				i.getShortExtra( RAData.PCOL_R1DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R1ONMASK,
				i.getShortExtra( RAData.PCOL_R1ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R1OFFMASK,
				i.getShortExtra( RAData.PCOL_R1OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R2DATA,
				i.getShortExtra( RAData.PCOL_R2DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R2ONMASK,
				i.getShortExtra( RAData.PCOL_R2ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R2OFFMASK,
				i.getShortExtra( RAData.PCOL_R2OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R3DATA,
				i.getShortExtra( RAData.PCOL_R3DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R3ONMASK,
				i.getShortExtra( RAData.PCOL_R3ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R3OFFMASK,
				i.getShortExtra( RAData.PCOL_R3OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R4DATA,
				i.getShortExtra( RAData.PCOL_R4DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R4ONMASK,
				i.getShortExtra( RAData.PCOL_R4ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R4OFFMASK,
				i.getShortExtra( RAData.PCOL_R4OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R5DATA,
				i.getShortExtra( RAData.PCOL_R5DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R5ONMASK,
				i.getShortExtra( RAData.PCOL_R5ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R5OFFMASK,
				i.getShortExtra( RAData.PCOL_R5OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R6DATA,
				i.getShortExtra( RAData.PCOL_R6DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R6ONMASK,
				i.getShortExtra( RAData.PCOL_R6ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R6OFFMASK,
				i.getShortExtra( RAData.PCOL_R6OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R7DATA,
				i.getShortExtra( RAData.PCOL_R7DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R7ONMASK,
				i.getShortExtra( RAData.PCOL_R7ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R7OFFMASK,
				i.getShortExtra( RAData.PCOL_R7OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R8DATA,
				i.getShortExtra( RAData.PCOL_R8DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R8ONMASK,
				i.getShortExtra( RAData.PCOL_R8ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R8OFFMASK,
				i.getShortExtra( RAData.PCOL_R8OFFMASK, (short) 0 ) );
		data.insert( v );
	}

	// Error Logging
	public void error ( int errorCodeIndex, Throwable t, String msg ) {
		errorCode = Integer.parseInt( errorCodes[errorCodeIndex] );
		Log.e( TAG, msg, t );
	}

	public String getErrorMessage ( ) {
		String s =
				(String) getResources().getText( R.string.messageUnknownError );
		// loop through array of error codes and match with the current code
		for ( int i = 0; i < errorCodes.length; i++ ) {
			if ( Integer.parseInt( errorCodes[i] ) == errorCode ) {
				// found code
				s =
						String.format(	"%s %d: %s",
										getResources()
												.getText( R.string.messageError ),
										errorCode, errorCodesStrings[i] );
				break;
			}
		}
		return s;
	}

	// Preferences
	protected void fillRelayLabels ( ) {
		relayLabels =
				new int[][] {	{	R.string.prefMainPort1LabelKey,
									R.string.prefMainPort2LabelKey,
									R.string.prefMainPort3LabelKey,
									R.string.prefMainPort4LabelKey,
									R.string.prefMainPort5LabelKey,
									R.string.prefMainPort6LabelKey,
									R.string.prefMainPort7LabelKey,
									R.string.prefMainPort8LabelKey },
								{	R.string.prefExp1Port1LabelKey,
									R.string.prefExp1Port2LabelKey,
									R.string.prefExp1Port3LabelKey,
									R.string.prefExp1Port4LabelKey,
									R.string.prefExp1Port5LabelKey,
									R.string.prefExp1Port6LabelKey,
									R.string.prefExp1Port7LabelKey,
									R.string.prefExp1Port8LabelKey },
								{	R.string.prefExp2Port1LabelKey,
									R.string.prefExp2Port2LabelKey,
									R.string.prefExp2Port3LabelKey,
									R.string.prefExp2Port4LabelKey,
									R.string.prefExp2Port5LabelKey,
									R.string.prefExp2Port6LabelKey,
									R.string.prefExp2Port7LabelKey,
									R.string.prefExp2Port8LabelKey },
								{	R.string.prefExp3Port1LabelKey,
									R.string.prefExp3Port2LabelKey,
									R.string.prefExp3Port3LabelKey,
									R.string.prefExp3Port4LabelKey,
									R.string.prefExp3Port5LabelKey,
									R.string.prefExp3Port6LabelKey,
									R.string.prefExp3Port7LabelKey,
									R.string.prefExp3Port8LabelKey },
								{	R.string.prefExp4Port1LabelKey,
									R.string.prefExp4Port2LabelKey,
									R.string.prefExp4Port3LabelKey,
									R.string.prefExp4Port4LabelKey,
									R.string.prefExp4Port5LabelKey,
									R.string.prefExp4Port6LabelKey,
									R.string.prefExp4Port7LabelKey,
									R.string.prefExp4Port8LabelKey },
								{	R.string.prefExp5Port1LabelKey,
									R.string.prefExp5Port2LabelKey,
									R.string.prefExp5Port3LabelKey,
									R.string.prefExp5Port4LabelKey,
									R.string.prefExp5Port5LabelKey,
									R.string.prefExp5Port6LabelKey,
									R.string.prefExp5Port7LabelKey,
									R.string.prefExp5Port8LabelKey },
								{	R.string.prefExp6Port1LabelKey,
									R.string.prefExp6Port2LabelKey,
									R.string.prefExp6Port3LabelKey,
									R.string.prefExp6Port4LabelKey,
									R.string.prefExp6Port5LabelKey,
									R.string.prefExp6Port6LabelKey,
									R.string.prefExp6Port7LabelKey,
									R.string.prefExp6Port8LabelKey },
								{	R.string.prefExp7Port1LabelKey,
									R.string.prefExp7Port2LabelKey,
									R.string.prefExp7Port3LabelKey,
									R.string.prefExp7Port4LabelKey,
									R.string.prefExp7Port5LabelKey,
									R.string.prefExp7Port6LabelKey,
									R.string.prefExp7Port7LabelKey,
									R.string.prefExp7Port8LabelKey },
								{	R.string.prefExp8Port1LabelKey,
									R.string.prefExp8Port2LabelKey,
									R.string.prefExp8Port3LabelKey,
									R.string.prefExp8Port4LabelKey,
									R.string.prefExp8Port5LabelKey,
									R.string.prefExp8Port6LabelKey,
									R.string.prefExp8Port7LabelKey,
									R.string.prefExp8Port8LabelKey } };
		relayDefaultLabels =
				new int[] { R.string.labelPort1,
							R.string.labelPort2,
							R.string.labelPort3,
							R.string.labelPort4,
							R.string.labelPort5,
							R.string.labelPort6,
							R.string.labelPort7,
							R.string.labelPort8 };
	}

	public boolean isCommunicateController ( ) {
		boolean b = false;
		if ( getPrefDevice().equals( devicesArray[0] ) ) {
			b = true;
		}
		return b;
	}

	public String getPrefHost ( ) {
		return prefs.getString( getString( R.string.prefHostKey ),
								getString( R.string.prefHostDefault ) );
	}

	public String getPrefPort ( ) {
		return prefs.getString( getString( R.string.prefPortKey ),
								getString( R.string.prefPortDefault ) );
	}

	public boolean getPrefT2Visibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefT2VisibilityKey ),
									true );
	}

	public boolean getPrefT3Visibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefT3VisibilityKey ),
									true );
	}

	public boolean getPrefDPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefDPVisibilityKey ),
									true );
	}

	public boolean getPrefAPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefAPVisibilityKey ),
									true );
	}

	public boolean getPrefSalinityVisibility ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefSalinityVisibilityKey ),
								false );
	}

	public String getPrefT1Label ( ) {
		return prefs.getString( getString( R.string.prefT1LabelKey ),
								getString( R.string.labelTemp1 ) );
	}

	public String getPrefT2Label ( ) {
		return prefs.getString( getString( R.string.prefT2LabelKey ),
								getString( R.string.labelTemp2 ) );
	}

	public String getPrefT3Label ( ) {
		return prefs.getString( getString( R.string.prefT3LabelKey ),
								getString( R.string.labelTemp3 ) );
	}

	public CharSequence getPrefPHLabel ( ) {
		return prefs.getString( getString( R.string.prefPHLabelKey ),
								getString( R.string.labelPH ) );
	}

	public String getPrefDPLabel ( ) {
		return prefs.getString( getString( R.string.prefDPLabelKey ),
								getString( R.string.labelDP ) );
	}

	public String getPrefAPLabel ( ) {
		return prefs.getString( getString( R.string.prefAPLabelKey ),
								getString( R.string.labelAP ) );
	}
	
	public String getPrefSalinityLabel ( ) {
		return prefs.getString( getString( R.string.prefSalinityLabelKey ) ,
		                        getString( R.string.labelSalinity) );
	}

	public String getPrefMainRelayLabel ( int port ) {
		return getPrefRelayLabel(0, port-1);
	}

	public String getPrefRelayLabel ( int relay, int port ) {
		return prefs.getString( getString( relayLabels[relay][port] ),
								getString( relayDefaultLabels[port] ) );
	}
	
	public void setPrefRelayLabel( int relay, int port, String label ) {
		setPref( getString(relayLabels[relay][port]), label );
	}
	
	public void setPref(String key, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString( key, value );
		editor.commit();
	}
	
	public void setPref(int keyid, String value) {
		setPref(getString(keyid), value);
	}

	public String getPrefDevice ( ) {
		return prefs.getString( getString( R.string.prefDeviceKey ),
								getString( R.string.prefDeviceDefault ) );
	}

	public String getPrefUserId ( ) {
		return prefs.getString( getString( R.string.prefUserIdKey ),
								getString( R.string.prefUserIdDefault ) );
	}
}
