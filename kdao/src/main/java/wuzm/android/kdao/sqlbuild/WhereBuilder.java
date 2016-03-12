package wuzm.android.kdao.sqlbuild;


import wuzm.android.kdao.SqlConstans;

public class WhereBuilder {

	public static final String buildComplex(String expression) {
		StringBuilder sb = new StringBuilder(SqlConstans.WHERE);
		sb.append(" ");
		sb.append(expression);
		sb.append(" ");
		return sb.toString();
	}
	
}
