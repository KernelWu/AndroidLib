package wuzm.android.kdownload.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ContinueDB {
    
	private static Boolean lock = new Boolean(false);
	public static volatile ContinueDB instance;
	private ContinueDbHelper dbHelper;
	private SQLiteDatabase readDb;
	private SQLiteDatabase writeDb;
	
	public static ContinueDB getInstance(Context context,int version) {
		
		if(instance != null) {
			return instance;
		}
		synchronized (lock) {
			instance = new ContinueDB(context, version);
		}
		return instance;
	}
	
	private  ContinueDB(Context context,int version) {
		dbHelper = new ContinueDbHelper(context, version);
		readDb = dbHelper.getReadableDatabase();
		writeDb = dbHelper.getWritableDatabase();
	}
	
	public SQLiteDatabase getReadDb() {
		if(readDb == null) {
			readDb = dbHelper.getReadableDatabase();
		}
		return readDb;
	}
	
	public SQLiteDatabase getWriteDb() {
		if(writeDb == null) {
			writeDb = dbHelper.getWritableDatabase();
		}
		return writeDb;
	}
	
	public void close() {
		readDb = null;
		writeDb = null;
		dbHelper.close();
	}
	
}
