package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class License extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);
		
		TextView c = (TextView) findViewById(R.id.popupContent);
		c.setText(R.string.license_text);
		
		Button b = (Button) findViewById(R.id.popupButton);
		b.setText(R.string.okButton);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}
