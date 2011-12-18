package info.curtbinder.reefangel.phone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static info.curtbinder.reefangel.phone.Globals.*;

public class RADbAdapter {
	private static final String TAG = "RADbAdapter";
	private static final String DB_NAME = "radata.db";
	private static final int DB_VERSION = 1;
	
	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// create the tables here
			createParamsTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading db from v" + oldVersion + " to v" + newVersion + 
					", which will destroy all old data");
			// initially, just drop tables and create new ones
			db.execSQL("DROP TABLE IF EXISTS " + PTABLE_NAME);
			onCreate(db);
		}

		private void createParamsTable(SQLiteDatabase db) {
			// create parameters table
			db.execSQL("CREATE TABLE " + PTABLE_NAME + " (" + 
					PCOL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					PCOL_T1 + " TEXT, " + 
					PCOL_T2 + " TEXT, " +
					PCOL_T3 + " TEXT, " +
					PCOL_PH + " TEXT, " +
					PCOL_DP + " TEXT, " +
					PCOL_AP + " TEXT, " +
					PCOL_SAL + " TEXT, " +
					PCOL_ATOHI + " INTEGER, " +
					PCOL_ATOLO + " INTEGER, " +
					PCOL_LOGDATE + " TEXT, " +
					PCOL_RDATA + " INTEGER, " +
					PCOL_RONMASK + " INTEGER, " +
					PCOL_ROFFMASK + " INTEGER, " +
					PCOL_R1DATA + " INTEGER, " +
					PCOL_R1ONMASK + " INTEGER, " +
					PCOL_R1OFFMASK + " INTEGER, " +
					PCOL_R2DATA + " INTEGER, " +
					PCOL_R2ONMASK + " INTEGER, " +
					PCOL_R2OFFMASK + " INTEGER, " +
					PCOL_R3DATA + " INTEGER, " +
					PCOL_R3ONMASK + " INTEGER, " +
					PCOL_R3OFFMASK + " INTEGER, " +
					PCOL_R4DATA + " INTEGER, " +
					PCOL_R4ONMASK + " INTEGER, " +
					PCOL_R4OFFMASK + " INTEGER, " +
					PCOL_R5DATA + " INTEGER, " +
					PCOL_R5ONMASK + " INTEGER, " +
					PCOL_R5OFFMASK + " INTEGER, " +
					PCOL_R6DATA + " INTEGER, " +
					PCOL_R6ONMASK + " INTEGER, " +
					PCOL_R6OFFMASK + " INTEGER, " +
					PCOL_R7DATA + " INTEGER, " +
					PCOL_R7ONMASK + " INTEGER, " +
					PCOL_R7OFFMASK + " INTEGER, " +
					PCOL_R8DATA + " INTEGER, " +
					PCOL_R8ONMASK + " INTEGER, " +
					PCOL_R8OFFMASK + " INTEGER " + 
					");"
					);
			
			// create TRIGGER for params table
			db.execSQL("CREATE TRIGGER prune_params_entries INSERT ON " + PTABLE_NAME +
					" BEGIN DELETE FROM " + PTABLE_NAME +
					" WHERE " + PCOL_ID + " NOT IN " +
					"(SELECT " + PCOL_ID + " FROM " + PTABLE_NAME + " ORDER BY " + PCOL_ID +
					" DESC LIMIT " + PTABLE_MAX_COUNT + ");" +
					"END;"
					);
		}
	}
	
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
