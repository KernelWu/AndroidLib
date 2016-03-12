package wuzm.android.kframe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Base64;

public class ImageUtils {
	/**
	 * 获得圆角图片
	 * 
	 * @author huxj
	 * @param bitmap
	 *            Bitmap资源
	 * @param roundPx
	 *            弧度
	 * @return Bitmap 圆角Bitmap资源
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

//	public static void saveMyBitmap(Bitmap bmp, String path) {
//		File file = new File(Constans.IMG_DIR);
//		if (!file.isDirectory()) {
//			file.mkdirs();
//		}
//		File f = new File(path);
//		try {
//			f.createNewFile();
//			FileOutputStream fOut = null;
//			fOut = new FileOutputStream(f);
//			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//			fOut.flush();
//			fOut.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public static Bitmap getBitmap(String path) {
//		if (path != null) {
//			BitmapFactory.Options options = null;
//			InputStream is = null;
//			try {
//				options = new BitmapFactory.Options();
//				options.inJustDecodeBounds = true;
//				is = new FileInputStream(path);
//				BitmapFactory.decodeStream(is, null, options);
//				int w = options.outWidth;
//				int h = options.outHeight;
//				options = new BitmapFactory.Options();
//				options.inSampleSize = Math.max(
//						(int) (w / BaseApplication.sScreenWidth), h
//								/ BaseApplication.sScreenHeight);
//				options.inJustDecodeBounds = false;
//				is.close();
//				is = new FileInputStream(path);
//				return BitmapFactory.decodeStream(is, null, options);
//			} catch (Throwable e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (is != null)
//						is.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return null;
//	}

	public static Bitmap getBitmapBySize(String path, int maxWidth,
			int maxHeight) {
		if (path != null) {
			BitmapFactory.Options options = null;
			InputStream is = null;
			try {
				options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				is = new FileInputStream(path);
				BitmapFactory.decodeStream(is, null, options);
				int w = options.outWidth;
				int h = options.outHeight;
				options = new BitmapFactory.Options();
				options.inSampleSize = Math.max((int) (w / maxWidth), h
						/ maxHeight);
				options.inJustDecodeBounds = false;
				is.close();
				is = new FileInputStream(path);
				return BitmapFactory.decodeStream(is, null, options);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static Bitmap getBitmapOrg(String path) {
		if (path != null) {
			InputStream is = null;
			Bitmap bm = null;
			BitmapFactory.Options options = null;
			try {
				options = new BitmapFactory.Options();
				options.inTempStorage = new byte[1024 * 1024 * 8]; // 5MB的临时存储空间
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Config.ARGB_4444;
				options.inPurgeable = true;
				options.inInputShareable = true;
				is = new FileInputStream(path);
				bm = BitmapFactory.decodeStream(is, null, options);

				return bm;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static Bitmap getBitmapHalf(String path) {
		if (path != null) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				InputStream is = new FileInputStream(path);
				options = new BitmapFactory.Options();
				options.inSampleSize = 2;// 缩小2倍
				options.inJustDecodeBounds = false;
				is.close();
				is = new FileInputStream(path);

				return BitmapFactory.decodeStream(is, null, options);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getBitmapFour(String path) {
		if (path != null) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				InputStream is = new FileInputStream(path);
				options = new BitmapFactory.Options();
				options.inSampleSize = 4;// 缩小4倍
				options.inJustDecodeBounds = false;
				is.close();
				is = new FileInputStream(path);

				return BitmapFactory.decodeStream(is, null, options);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getFitSizeImg(String path) {
		if (path == null || path.length() < 1)
			return null;
		try {
			File file = new File(path);
			Bitmap resizeBmp = null;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 数字越大读出的图片占用的heap越小 不然总是溢出
			if (file.length() < 20480) { // 0-20k
				opts.inSampleSize = 1;
			} else if (file.length() < 51200) { // 20-50k
				opts.inSampleSize = 2;
			} else if (file.length() < 307200) { // 50-300k
				opts.inSampleSize = 4;
			} else if (file.length() < 819200) { // 300-800k
				opts.inSampleSize = 6;
			} else if (file.length() < 1048576) { // 800-1024k
				opts.inSampleSize = 8;
			} else {
				opts.inSampleSize = 10;
			}
			resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
			return resizeBmp;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	public static Bitmap getBitmapForResource(Context context, int resourceId) {
//		try {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeResource(context.getResources(), resourceId,
//					options);
//			int w = options.outWidth;
//			int h = options.outHeight;
//			options = new BitmapFactory.Options();
//			// options.inSampleSize = 5;
//			options.inSampleSize = Math.max((int) (w / BaseApplication.sScreenWidth), h
//					/ BaseApplication.sScreenHeight);
//			options.inJustDecodeBounds = false;
//			return BitmapFactory.decodeResource(context.getResources(),
//					resourceId, options);
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	@SuppressLint("NewApi")
	public static Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * 处理图片
	 * 
	 * @param bm
	 *            所要转换的bitmap
	 * @param newWidth新的宽
	 * @param newHeight新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片

		Bitmap newbm = null;
		try {
			newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return newbm;
	}

	/**
	 * 处理图片
	 * 
	 * @param bm
	 *            所要转换的bitmap ，按宽度保持比例
	 * @param newWidth新的宽
	 * @return 指定宽的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	public static Bitmap zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// drawable转换成bitmap类型
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
								: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 获得图片的高与宽度的比例
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static float getImagehwScale(Context context, int resId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, options);
		return ((float) options.outHeight) / options.outWidth;
	}

//	@SuppressWarnings("deprecation")
//	public static Drawable cropDrawable(Context context, int resId, int wSize) {
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeResource(context.getResources(), resId, options);
////		int width = options.outWidth;
//		int height = options.outHeight;
//		options.inSampleSize = BaseApplication.sContentHeight / height;
//		options.inJustDecodeBounds = false;
//		options.inPurgeable = true;
//		options.inInputShareable = true;
//		Bitmap bt = BitmapFactory.decodeResource(context.getResources(), resId,
//				options);
//		bt = Bitmap.createBitmap(bt, 0, 0, wSize, bt.getHeight());
//		return new BitmapDrawable(bt);
//	}

}
