package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.popup );

		TextView c = (TextView) findViewById( R.id.popupContent );
		String s =
				String.format(	"%s %s\n\n%s",
								getString( R.string.prefVersionTitle ),
								getString( R.string.app_version ),
								getString( R.string.about_text ) );
		c.setText( s );

		Button b = (Button) findViewById( R.id.popupButton );
		b.setText( R.string.okButton );
		b.setOnClickListener( new OnClickListener() {
			public void onClick ( View v ) {
				finish();
			}
		} );
	}
}
