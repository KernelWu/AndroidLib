package wuzm.android.kdao.sql;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wuzm.android.kdao.deserialization.Deserializator;
import wuzm.android.kdao.sqlbuild.FromBuilder;
import wuzm.android.kdao.sqlbuild.SelectSqlBuilder;


public class Query {
	
	private static final String TAG = Query.class.getSimpleName();
	
	public static ArrayList<? extends Object> query(SQLiteDatabase db, String tableName, Class<?> baseClass,
			String[] selections, String[] selectionArgs) {
		
		return query(db, tableName, baseClass, null, null, null, -1, selections, selectionArgs);
	}
	
	
	public static ArrayList<? extends Object> query(SQLiteDatabase db, String tableName, Class<?> baseClass,
			String orderBy, String[] selections, String[] selectionArgs) {
		
		return query(db, tableName, baseClass, null, null, orderBy, -1, selections, selectionArgs);
	}
	
	public static ArrayList<? extends Object> query(SQLiteDatabase db, String tableName, Class<?> baseClass,
			String groupBy, String having, String[] selections,
			String[] selectionArgs) {
		
		return query(db, tableName, baseClass, groupBy, having, null, -1, selections, selectionArgs);
	}
	
	
	public static ArrayList<? extends Object> query(SQLiteDatabase db, String tableName, Class<?> baseClass,
			String groupBy, String having, String orderBy, String[] selections,
			String[] selectionArgs) {
		
		return 	query(db, tableName, baseClass, groupBy, having, orderBy, -1, selections,
				selectionArgs);
	}

	public static ArrayList<? extends Object> query(SQLiteDatabase db, String tableName, Class<?> baseClass,
			String groupBy, String having, String orderBy, int limit, String[] selections,
			String[] selectionArgs) {
		
		SelectSqlBuilder sb = new SelectSqlBuilder(null, Deserializator.getPropertiesName(baseClass),
				FromBuilder.build(tableName));
		sb.setGroupBy(groupBy, having)
		  .setOrderBy(orderBy)
		  .setWhere(selections);
		if(limit > 0) {
			sb.setLimit(limit);
		}
//		Log.d(TAG,"sql query->" + sb.build());
		Cursor cursor = db.rawQuery(sb.build(), selectionArgs);
		
		ArrayList<Object> dzs = new ArrayList<Object>();
		Map<String,Object> map = null;
		for(int i = 0, len = cursor.getCount(); i < len; i ++ ) {
			map = new HashMap<String, Object>();
			cursor.moveToPosition(i);
//			Log.d(TAG,"column_count->" + cursor.getColumnCount());
			for(String key : cursor.getColumnNames()) {
				map.put(key, cursor.getString(cursor.getColumnIndex(key)) );
//				Log.d(TAG,"key->" + key);
//				Log.d(TAG,"value->" + cursor.getString(cursor.getColumnIndex(key)) );
			}
			dzs.add(Deserializator.parse(baseClass, map));
		}
		return dzs;
	}
}
