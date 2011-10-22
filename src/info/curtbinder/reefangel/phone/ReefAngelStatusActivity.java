package info.curtbinder.reefangel.phone;

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

public class ReefAngelStatusActivity extends Activity implements OnClickListener {
	private static final String TAG = "RAStatus";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View v) {
    	switch ( v.getId() ){
    	case R.id.refresh_button:
    		// launch the update
    		Log.d(TAG, "onClick Refresh button");
    		break;
    	}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch ( keyCode ) {
		case KeyEvent.KEYCODE_R:
			// launch the update
			Log.d(TAG, "onKeyDown R");
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
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