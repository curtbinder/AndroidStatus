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
	private TextView messageText;
	private TextView t1Text;
	private TextView t2Text;
	private TextView t3Text;
	private TextView phText;
	private TextView dpText;
	private TextView apText;
	private TextView salinityText;
	
	// Threading
	private Handler guiThread;
	private ExecutorService statusThread;
	private Runnable updateTask;
	@SuppressWarnings("rawtypes")
	private Future statusPending;
	
	// View visibility
	private boolean showT2;
	private boolean showT3;
	private boolean showDP;
	private boolean showAP;
	private boolean showSalinity;
	private boolean showMessageText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViews();
        initThreading();
        updateViewsVisibility();
        
        refreshButton.setOnClickListener(this);
        updateTime.setText( R.string.messageNever );
    }
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
	}
	
	private void updateViewsVisibility() {
		// updates all the views visibility based on user settings
		// get values from Preferences
        showT2 = true;
        showT3 = true;
        showDP = true;
        showAP = true;
        showSalinity = true;
        showMessageText = false;
        
		if ( ! showT2 )
			t2Text.setVisibility(View.GONE);
		if ( ! showT3 )
			t3Text.setVisibility(View.GONE);
		if ( ! showDP )
			dpText.setVisibility(View.GONE);
		if ( ! showAP )
			apText.setVisibility(View.GONE);
		if ( ! showSalinity )
			salinityText.setVisibility(View.GONE);
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
		// TODO launch task
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
		// TODO change to message text
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