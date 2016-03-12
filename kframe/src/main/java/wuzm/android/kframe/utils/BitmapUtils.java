package wuzm.android.kframe.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class BitmapUtils {
	
	public static Bitmap zoom(Bitmap bm,float z) {
		Matrix matrix = new Matrix();
		matrix.postScale(z, z);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}
	
	public static Bitmap zoom(Bitmap bm,float zx,float zy) {
		Matrix matrix = new Matrix();
		matrix.postScale(zx, zy);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}
	
	public static Bitmap zoomTo(Bitmap bm,float targetSize) {
		return zoom(bm,targetSize / bm.getWidth(), targetSize / bm.getHeight());
	}
	
	public static Bitmap zoomTo(Bitmap bm,float targetwidth,float targetheight) {
		return zoom(bm,targetwidth / bm.getWidth() , targetheight / bm.getHeight());
	}
	
}
