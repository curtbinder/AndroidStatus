package info.curtbinder.reefangel.phone;

import static info.curtbinder.reefangel.phone.Globals.PCOL_AP;
import static info.curtbinder.reefangel.phone.Globals.PCOL_ATOHI;
import static info.curtbinder.reefangel.phone.Globals.PCOL_ATOLO;
import static info.curtbinder.reefangel.phone.Globals.PCOL_DP;
import static info.curtbinder.reefangel.phone.Globals.PCOL_ID;
import static info.curtbinder.reefangel.phone.Globals.PCOL_LOGDATE;
import static info.curtbinder.reefangel.phone.Globals.PCOL_PH;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R1DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R1OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R1ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R2DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R2OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R2ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R3DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R3OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R3ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R4DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R4OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R4ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R5DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R5OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R5ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R6DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R6OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R6ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R7DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R7OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R7ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R8DATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R8OFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_R8ONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_RDATA;
import static info.curtbinder.reefangel.phone.Globals.PCOL_ROFFMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_RONMASK;
import static info.curtbinder.reefangel.phone.Globals.PCOL_SAL;
import static info.curtbinder.reefangel.phone.Globals.PCOL_T1;
import static info.curtbinder.reefangel.phone.Globals.PCOL_T2;
import static info.curtbinder.reefangel.phone.Globals.PCOL_T3;
import static info.curtbinder.reefangel.phone.Globals.PTABLE_MAX_COUNT;
import static info.curtbinder.reefangel.phone.Globals.PTABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "radata.db";
	private static final int DB_VERSION = 1;
	private static final String TAG = "DbHelper";
	
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