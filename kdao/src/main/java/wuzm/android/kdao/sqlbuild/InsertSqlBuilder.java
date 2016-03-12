package wuzm.android.kdao.sqlbuild;

import android.text.TextUtils;

import java.util.ArrayList;

import wuzm.android.kdao.SqlConstans;


public class InsertSqlBuilder {

	private String table;
	private ArrayList<String> columns;
	
	private String sql;
	
	public InsertSqlBuilder(String table,ArrayList<String> columns) {
		if(TextUtils.isEmpty(table) || columns == null || columns.size() == 0 ) {
			throw new IllegalArgumentException("table name can't equal null," +
					"and columns can't equal null," +
					"columns size can't be zero");
		}
		this.table = table;
		this.columns = columns;
	}
	
	public String build() {
		StringBuffer sb = new StringBuffer();
		sb.append(SqlConstans.INSERT);
		sb.append(" ");
		sb.append(SqlConstans.INTO);
		sb.append(" ");
		sb.append(table);
		sb.append("(");
		for(int i = 0 , len = columns.size() ; i < len - 1 ; i ++ ) {
			sb.append(columns.get(i));
			sb.append(",");
		}
		if(columns.size() > 1) {
			sb.append(columns.get(columns.size() - 1));
			sb.append(")");
		}
		sb.append(SqlConstans.VALUES);
		sb.append(" ");
		sb.append("(");
		for(int i = 0 , len = columns.size() ; i < len - 1 ; i ++ ) {
			sb.append("?");
			sb.append(",");
		}
		if(columns.size() > 1) {
			sb.append("?");
		}
		sb.append(")");
		sb.append(";");
		this.sql = sb.toString();
		return sql;
	}
}
