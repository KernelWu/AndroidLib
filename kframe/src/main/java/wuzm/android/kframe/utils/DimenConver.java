package wuzm.android.kframe.utils;


/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class DimenConver {
	
	public static int px2dp(float density,int px) {
		return (int) (px / density);
	}
	
	public static int dp2px(float density,int dp) {
		return (int) (dp * density );
	}
	
}
