package info.curtbinder.reefangel.phone;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ReefAngelStatusActivity extends Activity implements OnClickListener {
	private static final String TAG = "RAStatus";
	
	// Display views
	private View refreshButton;
	private TextView updateTime;
	//private TextView messageText;
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
	private TextView dpLabel;
	private TextView apLabel;
	private TextView salinityLabel;
	
	// Threading
	private Handler guiThread;
	private ExecutorService statusThread;
	private Runnable updateTask;
	@SuppressWarnings("rawtypes")
	private Future statusPending;
	
	// View visibility
	/*
	private boolean showT2;
	private boolean showT3;
	private boolean showDP;
	private boolean showAP;
	private boolean showSalinity;
	*/
	//private boolean showMessageText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViews();
        initThreading();
        
        // restore values if restarted due to configuration change
        final String[] values = (String []) getLastNonConfigurationInstance();
        if ( values != null ) {
        	loadDisplayedControllerValues(values);
        } else {
        	loadDisplayedControllerValues(new String[] {
        		getString(R.string.messageNever),
        		getString(R.string.defaultStatusText),
        		getString(R.string.defaultStatusText),
        		getString(R.string.defaultStatusText),
        		getString(R.string.defaultStatusText),
        		getString(R.string.defaultStatusText),
        		getString(R.string.defaultStatusText),
        		getString(R.string.defaultStatusText)
        	});
        }
        updateViewsVisibility();
        
        refreshButton.setOnClickListener(this);
    }
    
	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	private void findViews() {
		refreshButton = findViewById(R.id.refresh_button);
		updateTime = (TextView) findViewById(R.id.updated);
		t1Text = (TextView) findViewById(R.id.temp1);
		t2Text = (TextView) findViewById(R.id.temp2);
		t3Text = (TextView) findViewById(R.id.temp3);
		phText = (TextView) findViewById(R.id.ph);
		dpText = (TextView) findViewById(R.id.dp);
		apText = (TextView) findViewById(R.id.ap);
		salinityText = (TextView) findViewById(R.id.salinity);
		t1Label = (TextView) findViewById(R.id.t1_label);
		t2Label = (TextView) findViewById(R.id.t2_label);
		t3Label = (TextView) findViewById(R.id.t3_label);
		dpLabel = (TextView) findViewById(R.id.dp_label);
		apLabel = (TextView) findViewById(R.id.ap_label);
		salinityLabel = (TextView) findViewById(R.id.salinity_label);
	}
	
	private void updateViewsVisibility() {
		// updates all the views visibility based on user settings
		// get values from Preferences
        //showMessageText = false;
        
        // Labels
        t1Label.setText(Prefs.getT1Label(getBaseContext()));
        t2Label.setText(Prefs.getT2Label(getBaseContext()));
        t3Label.setText(Prefs.getT3Label(getBaseContext()));
        dpLabel.setText(Prefs.getDPLabel(getBaseContext()));
        apLabel.setText(Prefs.getAPLabel(getBaseContext()));
        
        // Visibility
		if ( Prefs.getT2Visibility(getBaseContext()) ) {
			Log.d(TAG, "T2 visible");
			t2Text.setVisibility(View.VISIBLE);
			t2Label.setVisibility(View.VISIBLE);
		} else {
			Log.d(TAG, "T2 gone");
			t2Text.setVisibility(View.GONE);
			t2Label.setVisibility(View.GONE);
		}
		if ( Prefs.getT3Visibility(getBaseContext()) ) {
			Log.d(TAG, "T3 visible");
			t3Text.setVisibility(View.VISIBLE);
			t3Label.setVisibility(View.VISIBLE);
		} else {
			Log.d(TAG, "T3 gone");
			t3Text.setVisibility(View.GONE);
			t3Label.setVisibility(View.GONE);
		}
		if ( Prefs.getDPVisibility(getBaseContext()) ) {
			Log.d(TAG, "DP visible");
			dpText.setVisibility(View.VISIBLE);
			dpLabel.setVisibility(View.VISIBLE);
		} else {
			Log.d(TAG, "DP gone");
			dpText.setVisibility(View.GONE);
			dpLabel.setVisibility(View.GONE);
		}
		if ( Prefs.getAPVisibility(getBaseContext()) ) {
			Log.d(TAG, "AP visible");
			apText.setVisibility(View.VISIBLE);
			apLabel.setVisibility(View.VISIBLE);
		} else {
			Log.d(TAG, "AP gone");
			apText.setVisibility(View.GONE);
			apLabel.setVisibility(View.GONE);
		}
		if ( Prefs.getSalinityVisibility(getBaseContext()) ) {
			Log.d(TAG, "Salinity visible");
			salinityText.setVisibility(View.VISIBLE);
			salinityLabel.setVisibility(View.VISIBLE);
		} else {
			Log.d(TAG, "Salinity gone");
			salinityText.setVisibility(View.GONE);
			salinityLabel.setVisibility(View.GONE);
		}
		//if ( ! showMessageText )
		//	messageText.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
    	switch ( v.getId() ){
    	case R.id.refresh_button:
    		// launch the update
    		Log.d(TAG, "onClick Refresh button");
    		launchStatusTask();
    		break;
    	}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch ( keyCode ) {
		case KeyEvent.KEYCODE_R:
			// launch the update
			Log.d(TAG, "onKeyDown R");
			launchStatusTask();
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	private void initThreading() {
		guiThread = new Handler();
		statusThread = Executors.newSingleThreadExecutor();
		updateTask = new Runnable() {
			public void run() {
				// Task to be run
				
				// Cancel any previous status check if exists
				if ( statusPending != null )
					statusPending.cancel(true);

				try {
					// Get IP & Port
					Host h = new Host(
							Prefs.getHost(getBaseContext()),
							Prefs.getPort(getBaseContext()),
							Globals.requestStatusOld);
					Log.d(TAG, h.toString());
					// Create ControllerTask
					ControllerTask cTask = new ControllerTask(
							ReefAngelStatusActivity.this,
							h,
							true);
					statusPending = statusThread.submit(cTask);
					// Add ControllerTask to statusThread to be run
				} catch ( RejectedExecutionException e) {
					Log.e(TAG, "initThreading RejectedExecution");
					updateTime.setText(R.string.messageError);
				}
			}
		};
	}
	
	private void launchStatusTask() {
		/**
		 * Creates the thread that communicates with the controller
		 * Then that function calls updateDisplay when it finishes
		 */
		Log.d(TAG, "launchStatusTask");
		// cancel any previous update if it hasn't started yet
		guiThread.removeCallbacks(updateTask);
		// start an update
		guiThread.post(updateTask);
	}
	
	public void guiUpdateDisplay(final Controller ra) {
		/**
		 * Updates the display with the values from the Controller
		 * 
		 * Called from other threads
		 */
		guiThread.post(new Runnable() {
			public void run() {
				Log.d(TAG, "updateDisplay");
				DateFormat dft =
						DateFormat.getDateTimeInstance( DateFormat.DEFAULT,
														DateFormat.DEFAULT, Locale.getDefault() );
				updateTime.setText(dft.format(new Date()));
				t1Text.setText(ra.getTemp1());
				t2Text.setText(ra.getTemp2());
				t3Text.setText(ra.getTemp3());
				phText.setText(ra.getPH());
				dpText.setText(ra.getPwmD());
				apText.setText(ra.getPwmA());
				salinityText.setText(ra.getSalinity());
			}
		});
	}
	
	public void guiUpdateTimeText(final String msg) {
		/**
		 * Updates the UpdatedTime text box only
		 * 
		 * Called from other threads to indicate an error or interruption
		 */
		guiThread.post(new Runnable() {
			public void run() {
				Log.d(TAG, "updateTimeText");
				updateTime.setText(msg);
			}
		});
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final String[] controllerValues = getDisplayedControllerValues();
		return controllerValues;
	}
	
	private String[] getDisplayedControllerValues() {
		// The order must match with the order in loadDisplayedControllerValues
		return new String[] {
				(String) updateTime.getText(),
				(String) t1Text.getText(),
				(String) t2Text.getText(),
				(String) t3Text.getText(),
				(String) phText.getText(),
				(String) dpText.getText(),
				(String) apText.getText(),
				(String) salinityText.getText()
		};
	}
	
	private void loadDisplayedControllerValues(String[] v) {
		// The order must match with the order in getDisplayedControllerValues
		updateTime.setText(v[0]);
		t1Text.setText(v[1]);
		t2Text.setText(v[2]);
		t3Text.setText(v[3]);
		phText.setText(v[4]);
		dpText.setText(v[5]);
		apText.setText(v[6]);
		salinityText.setText(v[7]);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.settings:
        	// launch settings
        	Log.d(TAG, "Menu Settings clicked");
        	startActivity(new Intent(this, Prefs.class));
            break;
        case R.id.about:
        	// launch about box
        	Log.d(TAG, "Menu About clicked");
        	startActivity(new Intent(this, About.class));
            break;
        //case R.id.memory:
        	// launch memory
        //	break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}