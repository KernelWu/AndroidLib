package wuzm.android.kdao.sqlbuild;

import java.util.ArrayList;

import wuzm.android.kdao.SqlConstans;


public class FromBuilder {

	public static final String build(String tableName) {
		StringBuilder sb = new StringBuilder(SqlConstans.FROM);
		sb.append(" ");
		sb.append(tableName);
		sb.append(" ");
		return sb.toString();
	}
	
	public static final String build(ArrayList<String> tableNames) {
		StringBuilder sb = new StringBuilder(SqlConstans.FROM);
		sb.append(" ");
		for(int i = 0 , len = tableNames.size() ; i < len - 1; i ++) {
			sb.append(tableNames.get(i));
			sb.append(",");
		}
		if(tableNames.size() > 1) {
			sb.append(tableNames.get(tableNames.size() - 1));
		}
		sb.append(" ");
		return sb.toString();
	}
	
	public static final String buildComplex(String expression) {
		StringBuilder sb = new StringBuilder(SqlConstans.FROM);
		sb.append(" ");
		sb.append(expression);
		sb.append(" ");
		return sb.toString();
	}
}
