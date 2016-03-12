package wuzm.android.kdao.sqlbuild;


import wuzm.android.kdao.SqlConstans;

public class BetweenBuilder {

	public static final String build(String left,String right) {
		StringBuilder sb = new StringBuilder(SqlConstans.BETWEEN);
		sb.append(" ");
		sb.append(left);
		sb.append(" ");
		sb.append(SqlConstans.AND);
		sb.append(" ");
		sb.append(right);
		sb.append(" ");
		return sb.toString();
	}
}
