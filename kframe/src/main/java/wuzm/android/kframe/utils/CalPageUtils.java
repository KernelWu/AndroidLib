package wuzm.android.kframe.utils;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class CalPageUtils {
	
	public static int calPage(int itemCount,int countPerPage) {
		int tmp = itemCount % countPerPage;
		return tmp == 0 ? itemCount / countPerPage : itemCount / countPerPage + 1;
	}

}
