package wuzm.android.kdao.sqlbuild;

import java.util.ArrayList;

import wuzm.android.kdao.SqlConstans;


public class InBuilder {

	public static final String build(ArrayList<String> sets) {
		StringBuilder sb = new StringBuilder(SqlConstans.IN);
		sb.append("(");
		for(int i = 0 ,len = sets.size() ; i < len - 1; i ++) {
			sb.append(sets.get(i));
			sb.append(",");
		}
		if(sets.size() > 1) {
			sb.append(sets.get(sets.size() - 1));
		}
		sb.append(")");
		sb.append(" ");
		return sb.toString();
	}
}
