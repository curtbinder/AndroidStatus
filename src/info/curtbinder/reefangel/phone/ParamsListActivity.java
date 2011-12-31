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
    	RAData.PCOL_LOGDATE,
    	RAData.PCOL_T1,
    	RAData.PCOL_T2,
    	RAData.PCOL_T3
	};
    RAApplication rapp;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paramslist);
		rapp = (RAApplication)getApplication();
		try {
			Cursor c = rapp.data.getAllData();
			startManagingCursor(c);
			showEvents(c);
		} catch ( SQLException e ) {
			Log.d(TAG, "SQL Exception");
		} finally {
			//rapp.getRAData().close();
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
