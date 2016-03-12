package wuzm.android.kdao.sqlbuild;

import android.text.TextUtils;

import java.util.Iterator;
import java.util.Map;

import wuzm.android.kdao.SqlConstans;


public class UpdateSqlBuilder {

	private String table;
	private String where;
	private Map<String,Object> sets;
	
	private String sql;
	
	public UpdateSqlBuilder(String table,Map<String,Object> sets) {
		if(TextUtils.isEmpty(table) || sets == null || sets.size() == 0) {
			throw new IllegalArgumentException("table name can't equal null," +
					"and sets can't equla null" +
					"sets size can't be zero");
		}
		this.table = table;
		this.sets = sets;
	}
	
	public UpdateSqlBuilder setWhere(String where) {
		if( ! TextUtils.isEmpty(where)) {
			this.where = where;
		}
		return this;
	}
	
	public String build() {
		StringBuffer sb = new StringBuffer();
		sb.append(SqlConstans.UPDATE);
		sb.append(" ");
		sb.append(table);
		sb.append(" ");
		sb.append(SqlConstans.SET);
		sb.append(" ");
		Iterator<String> keys = sets.keySet().iterator();
		String key ;
		for(int i = 0 , len = sets.keySet().size() ; i < len - 1; i ++ ) {
			key = keys.next();
			sb.append(key);
			sb.append("=");
			sb.append(sets.get(key));
			sb.append(",");
		}
		if(sets.keySet().size() > 1) {
			key = keys.next();
			sb.append(key);
			sb.append("=");
			sb.append(sets.get(key));
		}
		
		if( ! TextUtils.isEmpty(where)) {
//			sb.append(" ");
//			sb.append(SqlConstans.WHERE);
//			sb.append(" ");
			sb.append(where);
		}
		sb.append(";");
		this.sql = sb.toString();
		return sql;
	}
}
