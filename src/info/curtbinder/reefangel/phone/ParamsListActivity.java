package info.curtbinder.reefangel.phone;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class ParamsListActivity extends ListActivity {

	private static final String TAG = "RAParamsList";
	private static final int[] TO = {
		R.id.plog,
		R.id.pt1,
		R.id.pt2,
		R.id.pt3,
		/*
		R.id.pph,
		R.id.pdp,
		R.id.pap,
		R.id.psal,
		R.id.patoh,
		R.id.patol,
		R.id.pr,
		R.id.pron,
		R.id.proff
		*/
	};
    private static final String [] FROM = {
    	RADbAdapter.PCOL_LOGDATE,
		RADbAdapter.PCOL_T1,
		RADbAdapter.PCOL_T2,
		RADbAdapter.PCOL_T3
	};
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paramslist);
		Log.d(TAG, "Open database");
		RADbAdapter dbAdapter = new RADbAdapter(this);
        dbAdapter.open();
		try {
			Cursor c = dbAdapter.getAllParams();
			startManagingCursor(c);
			showEvents(c);
		} catch ( SQLException e ) {
			Log.d(TAG, "SQL Exception");
		} finally {
			Log.d(TAG, "Close database");
			dbAdapter.close();
		}
	}

	private void showEvents(Cursor cursor) {
		Log.d(TAG, "showEvents");
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.paramslistitem, 
				cursor, 
				FROM, 
				TO);
		this.setListAdapter(adapter);
	}

}
