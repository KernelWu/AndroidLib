package wuzm.android.kframe.utils;

import android.app.Activity;
import android.graphics.Rect;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class ScreenUtils {
	
	/**
	 * 注意必须在activity的界面已经生成后调用才有用，不能在onCreate、onResume调用
	 * @param context
	 * @return
	 */
	public static int getScreenStatusBarHeight(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
	}

}
