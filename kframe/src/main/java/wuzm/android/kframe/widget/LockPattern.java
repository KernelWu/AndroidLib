package wuzm.android.kframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import wuzm.android.kframe.R;
import wuzm.android.kframe.utils.BitmapUtils;


/**
 * a guesture lock
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 * @see
 * <ul>
 * <li><strong>the main methods you mayby use</strong></li>
 * <li>{@link #setInputCompleteListner(wuzm.android.frame.widget.LockPattern.InputCompleteListener)} a listener will invake when you input all the password </li>
 * </ul>
 */
public class LockPattern extends View {
//	private Context mContext;
	private Point[][] initPoint = new Point[3][3];
	private ArrayList<Point> inputPoins = new ArrayList<Point>();
	private ArrayList<Integer> inputPassword = new ArrayList<Integer>();
	
	private int originalPointResId;
	private Bitmap originalPointBm;
	private int selectedPointResId;
	private Bitmap selectedPointBm;
	private int traceLineResId;
	private Bitmap traceLineBm;
	
	private Paint mPaint;
	
	//the point radius
	private float r;
	
	private float movingX,movingY;
	
	private boolean isInitCache;

	public LockPattern(Context context) {
		this(context,null,0);
	}
	
	public LockPattern(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public LockPattern(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		mContext = context;
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs,
					R.styleable.LocusPattern);
			originalPointResId = ta.getResourceId(
					R.styleable.LocusPattern_originalPoint,
					R.drawable.locus_round_original);
			selectedPointResId = ta.getResourceId(
					R.styleable.LocusPattern_selectedPoint,
					R.drawable.locus_round_click);
			traceLineResId = ta.getResourceId(
					R.styleable.LocusPattern_traceLineDrawable,
					R.drawable.locus_line);
			ta.recycle();
		}
	}
	
	private void init() {
		initSetting();
		initResource();
		isInitCache = true;
	}
	
	private void initSetting() {
		int width = getWidth();
		int height = getHeight();
		if(height > width) {
			r = width / 16;
		}else {
			r = height / 16;
		}
		
		//init point attribution
		float centerY = 5 * r;
		float increase = 6 * r;
		for(int i = 0 ; i < 3; i ++ ) {
			float centerX = 2 * r;
			initPoint[i] = new Point[3];
			for(int j = 0 ; j < 3; j ++ ) {
				initPoint[i][j] = new Point();
				initPoint[i][j].state = Point.POINT_NO_SELECTED;
				initPoint[i][j].r = r;
				initPoint[i][j].x = centerX;
				initPoint[i][j].y = centerY;
				initPoint[i][j].value = i * 3 + j;
				centerX += increase;
			}
			centerY += increase;
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	
	private void initResource() {
		Options options = new Options();
		options.inInputShareable = true;
		options.inPurgeable = true;
		
		originalPointBm = BitmapUtils.zoomTo(BitmapFactory.decodeResource(getResources(),
				originalPointResId, options), 2 * r);
		selectedPointBm = BitmapUtils.zoomTo(BitmapFactory.decodeResource(getResources(),
				selectedPointResId,options), 2 * r);
		traceLineBm = BitmapUtils.zoomTo(BitmapFactory.decodeResource(getResources(),
			    traceLineResId,options), r / 4 );
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		movingX = event.getX();
		movingY = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			Point point = getInsidingPoint(movingX, movingY);
			if(point != null) {
				if(point.state == Point.POINT_NO_SELECTED) {
					inputPoins.add(point);
					inputPassword.add(point.value);
					point.state = Point.POINT_SELECTED;
				}
			}
			postInvalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(inputCompleteListener != null) {
				inputCompleteListener.onInputComplete(inputPassword);
			}
			clearTrace();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if( ! isInitCache) {
			init();
		}
		for(int i = 0 ; i < 3 ; i ++ ) {
			for(int j = 0 ; j < 3 ; j ++ ) {
				drawPoint(canvas, initPoint[i][j]);
			}
		}
		
		for(int j = 0 , len = inputPoins.size() - 1 ; j < len ; j ++ ) {
			drawLine(canvas, inputPoins.get(j), inputPoins.get(j + 1));
		}
		
		if(inputPoins.size() != 0) {
			drawLine(canvas, inputPoins.get(inputPoins.size() - 1), movingX, movingY);
		}
	}
	
	private void drawPoint(Canvas canvas,Point p) {
		if(p.state == Point.POINT_SELECTED) {
			canvas.drawBitmap(selectedPointBm, p.x - r , p.y - r , mPaint);
		}else {
			canvas.drawBitmap(originalPointBm, p.x - r , p.y - r , mPaint);
		}
	}
	
	private void drawLine(Canvas canvas,Point start,Point end) {
		drawLine(canvas, start, end.x, end.y);
	}
	
	private void drawLine(Canvas canvas,Point start,float endX,float endY) {
		float degree = getDegree(start.x , start.y , endX , endY);
		canvas.rotate(degree , start.x , start.y);
		
		Matrix matrix = new Matrix();
		matrix.setScale(getDistance(start.x, start.y, endX, endY) / traceLineBm.getWidth() , 1);
		matrix.postTranslate(start.x, start.y - traceLineBm.getHeight() / 2.0f);
		canvas.drawBitmap(traceLineBm, matrix, mPaint);
		canvas.rotate(-degree , start.x , start.y);
	}
	
	private float getDegree(float startX,float startY,float endX,float endY) {
		return (float) Math.toDegrees(Math.atan2(endY - startY, endX - startX));
	}
	
	private float getDistance(float startX,float startY,float endX,float endY) {
		return (float) Math.sqrt( (startX - endX) * (startX - endX) + (startY - endY) * (startY - endY) ); 
	}
	
	//get the point that current location located inside
	private Point getInsidingPoint(final float x,final float y) {
		for(int i = 0 ; i < 3 ; i ++ ) {
			for(int j = 0 ; j < 3 ; j ++ ) {
				if( initPoint[i][j].isInside(x, y) ) {
					return initPoint[i][j];
				}
			}
		}
		return null;
	}
	
	private void clearTrace() {
		inputPoins.clear();
		inputPassword.clear();
		for(int i = 0 ; i < 3 ; i ++ ) {
			for(int j = 0 ; j < 3 ; j ++ ) {
				initPoint[i][j].state = Point.POINT_NO_SELECTED;
			}
		}
		movingX = 0;
		movingY = 0;
		postInvalidate();
	}
	
	private class Point {
		public static final int POINT_SELECTED = 1;
		public static final int POINT_NO_SELECTED = 0;
		public int value;
		public float x,y;
		public float r;
		public int state;
		
		public boolean isInside(float x,float y) {
			return (x - this.x)*(x - this.x) + (y - this.y)*(y - this.y) < r * r; 
		}
	}
	
	public interface InputCompleteListener {
		public void onInputComplete(ArrayList<Integer> input);
	}
	
	private InputCompleteListener inputCompleteListener;
	
	public void setInputCompleteListner(InputCompleteListener l) {
		inputCompleteListener = l;
	}

}
