package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {
	RAApplication rapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		rapp = (RAApplication) getApplication();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// if the service isn't running, start it
		// TODO move to have this run all the time
		if (!rapp.isServiceRunning)
			startService(new Intent(this, ControllerService.class));
	}

}
