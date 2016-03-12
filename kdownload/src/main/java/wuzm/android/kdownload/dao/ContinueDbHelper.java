package wuzm.android.kdownload.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ContinueDbHelper extends SQLiteOpenHelper{
	
	public static final String TABLE_NAME = "continue_table";
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + 
			" (id text,url text,file_path text,file_suffix text," +
			"start_pos text,cur_pos text,task_size text,block_size text,downloaded_size text);" ;
	
	public ContinueDbHelper(Context context,int version) {
		this(context, TABLE_NAME, null, version);
	}

	public ContinueDbHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
