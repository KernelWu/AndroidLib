package wuzm.android.kdao.sqlbuild;

import android.text.TextUtils;

import wuzm.android.kdao.SqlConstans;


public class DeleteSqlBuilder {

	public String from;
	public String where;
	public StringBuilder sql;
	
	public DeleteSqlBuilder (String from) {
		this.from = from;
	}
	
	public final DeleteSqlBuilder setWhere(String where) {
		this.where = where;
		return this;
	}
	
	public final String build() {
		sql = new StringBuilder(SqlConstans.DELETE);
		sql.append(" ");
		sql.append(from);
		sql.append(" ");
		if( !TextUtils.isEmpty(where)) {
			sql.append(where);
		}
		sql.append(";");
		return sql.toString();
	}
 	
}
