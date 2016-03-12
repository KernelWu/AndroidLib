package wuzm.android.kdao.sqlbuild;

import android.text.TextUtils;

import java.util.ArrayList;

import wuzm.android.kdao.SqlConstans;


public class SelectSqlBuilder {
	
	private String groupBy;
	private String orderBy;
	private String limit;
	private String where;
	
	private String sql;
	
	public SelectSqlBuilder(String type,ArrayList<String> columns,
			String from) {
		if(TextUtils.isEmpty(from)) {
			throw new IllegalArgumentException("table name can't equal null");
		}
		StringBuffer sb = new StringBuffer();
		sb.append(SqlConstans.SELECT);
		sb.append(" ");
		if( ! TextUtils.isEmpty(type)) {
			if( !(type.equals(SqlConstans.ALL) || type.equals(SqlConstans.DISTINCT)) ) {
				throw new IllegalArgumentException("type only equal null,all,distince");
			}else{
				sb.append(type);
				sb.append(" ");
			}
		}
		for(int i = 0 , len = columns.size() ; i < len - 1; i ++ ) {
			sb.append(columns.get(i));
			sb.append(",");
		}
		if(columns.size() > 1) {
			sb.append(columns.get(columns.size() - 1));
			sb.append(" ");
		}
//		sb.append(SqlConstans.FROM);
//		sb.append(" ");
		sb.append(from);
//		sb.append(" ");
		sql = sb.toString();
	}
	
	public SelectSqlBuilder setGroupBy(String groupBy,String having) {
		StringBuffer sb = new StringBuffer();
		if( ! TextUtils.isEmpty(groupBy)) {
			sb.append(SqlConstans.GROUP_BY);
			sb.append(" ");
			sb.append(groupBy);
			sb.append(" ");
			if( ! TextUtils.isEmpty(having) ) {
				sb.append(SqlConstans.HAVING);
				sb.append(" ");
				sb.append(having);
				sb.append(" ");
			}
		}
		this.groupBy = sb.toString();
		return this;
	}
	
	public SelectSqlBuilder setOrderBy(String orderBy) {
		StringBuffer sb = new StringBuffer();
		if( ! TextUtils.isEmpty(orderBy)) {
			sb.append(SqlConstans.ORDER_BY);
			sb.append(" ");
			sb.append(orderBy);
			sb.append(" ");
		}
		this.orderBy = sb.toString();
		return this;
	}
	
	public SelectSqlBuilder setLimit(int num) {
		StringBuffer sb = new StringBuffer(SqlConstans.LIMIT);
		sb.append(" ");
		sb.append(String.valueOf(num));
		sb.append(" ");
		this.limit = sb.toString();
		return this;
	}
	
	public SelectSqlBuilder setWhere(String where) {
		StringBuffer sb = new StringBuffer();
		if( ! TextUtils.isEmpty(where)) {
//			sb.append(SqlConstans.WHERE);
//			sb.append(" ");
			sb.append(where);
//			sb.append(" ");
		}
		this.where = sb.toString();
		return this;
	}
	
	public SelectSqlBuilder setWhere(String[] conditions) {
		if( conditions == null) {
			return this;
		}
		StringBuilder sb = new StringBuilder(SqlConstans.WHERE);
		sb.append(" ");
		for(int i = 0, len = conditions.length; i < len - 1; i ++ ) {
			sb.append(conditions[i]);
			sb.append("=?");
			sb.append(",");
		}
		if(conditions.length > 1) {
			sb.append(conditions[conditions.length - 1] );
			sb.append("=?");
		}
		sb.append(" ");
		this.where = sb.toString();
		return this;
	}
	
	public String build() {
		if( !TextUtils.isEmpty(this.where)) {
			sql += this.where;
		}
		if( !TextUtils.isEmpty(this.groupBy)) {
			sql += this.groupBy;
		}
		if( !TextUtils.isEmpty(this.orderBy)) {
			sql += this.where;
		}
		if( !TextUtils.isEmpty(this.limit)) {
			sql += this.limit;
		}
		return sql;
	}

}
