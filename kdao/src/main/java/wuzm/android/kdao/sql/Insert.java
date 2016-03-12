package wuzm.android.kdao.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import wuzm.android.kdao.serialization.Serializator;
import wuzm.android.kdao.sqlbuild.InsertSqlBuilder;

public class Insert {
	
	private static final String TAG = Insert.class.getSimpleName();

	public static boolean insert(SQLiteDatabase db, String tableName, ArrayList<? extends Object> objs) {
		
		try {
			db.beginTransaction();
			
			InsertSqlBuilder ib = new InsertSqlBuilder(tableName, Serializator
					.getPropertyNames(objs.get(0).getClass()));
			
//			Log.d(TAG,"sql insert->" + ib.build());
			SQLiteStatement sqlSt = db.compileStatement(ib.build());
//			Log.d(TAG,"value_count->" + objs.size());
			for(int i = 0, len = objs.size(); i < len; i ++) {
				Map<String,Object> properies = Serializator.getProperties(objs.get(i).getClass(),
						objs.get(i));
				String key = null;
				Iterator<String> it = properies.keySet().iterator();
				for(int j = 1, jlen = properies.size(); j <= jlen; j ++ ) {
					key = it.next();
					sqlSt.bindString(j, String.valueOf(properies.get(key)) );
//					Log.d(TAG,"key->" + key);
//					Log.d(TAG,"value->" + String.valueOf(properies.get(key)) );
				}
				sqlSt.executeInsert();
			}
			db.setTransactionSuccessful();
			db.endTransaction();	
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
