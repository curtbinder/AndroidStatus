package info.curtbinder.reefangel.phone;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	
	private View refreshButton;
	private TextView updateTime;
	private TextView t1Text;
	private TextView t2Text;
	private TextView t3Text;
	private TextView phText;
	private TextView dpText;
	private TextView apText;
	private TextView salinityText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViews();
        
        refreshButton.setOnClickListener(this);
        updateTime.setText( R.string.never );
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
	
	public void launchStatusTask() {
		// TODO launch task
		Log.d(TAG, "launchStatusTask");
		Controller r = new Controller();
		r.setTemp1(780);
		r.setTemp2(900);
		r.setTemp3(760);
		updateDisplay(r);
	}
	
	public void updateDisplay(Controller ra) {
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
        	//startActivity(new Intent(this, Prefs.class));
            break;
        case R.id.about:
        	// launch about box
        	Log.d(TAG, "Menu About clicked");
        	startActivity(new Intent(this, About.class));
            break;
        case R.id.memory:
        	// launch memory
        	break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}