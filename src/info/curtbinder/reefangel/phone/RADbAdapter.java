package info.curtbinder.reefangel.phone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import static info.curtbinder.reefangel.phone.Globals.*;

public class RADbAdapter {
		
	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	
	public RADbAdapter(Context ctx) { 
		this.mCtx = ctx;
	}
	
	public RADbAdapter open() throws SQLException {
		mDbHelper = new DbHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	public long addParamsEntry(Controller ra) {
		ContentValues v = new ContentValues();
		v.put(PCOL_T1, ra.getTemp1());
		v.put(PCOL_T2, ra.getTemp2());
		v.put(PCOL_T3, ra.getTemp3());
		v.put(PCOL_PH, ra.getPH());
		v.put(PCOL_DP, ra.getPwmD());
		v.put(PCOL_AP, ra.getPwmA());
		v.put(PCOL_SAL, ra.getSalinity());
		v.put(PCOL_ATOHI, ra.getAtoHigh());
		v.put(PCOL_ATOLO, ra.getAtoLow());
		v.put(PCOL_LOGDATE, ra.getLogDate());
		v.put(PCOL_RDATA, ra.getMainRelay().getRelayData());
		v.put(PCOL_RONMASK, ra.getMainRelay().getRelayOnMask());
		v.put(PCOL_ROFFMASK, ra.getMainRelay().getRelayOffMask());
		return mDb.insert(PTABLE_NAME, null, v);
	}
	
	private Cursor getParams(String limit) throws SQLException {
		Cursor mCursor = mDb.query(PTABLE_NAME, 
				getAllColumns(), 
				null, 
				null, 
				null, 
				null, 
				PCOL_ID + " DESC", 
				limit);
		return mCursor;
	}
	
	public Cursor getLatestParams() throws SQLException {
		return getParams("1");
	}
	
	public Cursor getAllParams() throws SQLException {
		return getParams(null);
	}

	public String[] getAllColumns() {
		// returns a string list of all the columns
		return new String [] {
				PCOL_ID, PCOL_T1, PCOL_T2, PCOL_T3, PCOL_PH, PCOL_DP,
				PCOL_AP, PCOL_SAL, PCOL_ATOHI, PCOL_ATOLO, 
				PCOL_LOGDATE, PCOL_RDATA, PCOL_RONMASK, PCOL_ROFFMASK
		};
	}
}
