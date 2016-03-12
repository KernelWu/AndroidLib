package wuzm.android.kframe.utils;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class StringCheckUtil {

	public static boolean isEmpty(String s) {
		if( s != null ) {
			return s.isEmpty();
		}
		return true;
	}
}
