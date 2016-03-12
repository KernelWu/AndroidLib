package wuzm.android.kframe.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import wuzm.android.kframe.R;


public class Indicator extends View{
	private static final String TAG = Indicator.class.getSimpleName();
	int indicatorColor = Color.BLUE;
	float indicatorWidth = -1f;
	float indicatorHeight = -1f;
	int indicatorCount = 0;
	
	float indicatorPaddingLeft = 0f;
	float indicatorPaddingRight = 0f;
	float indicatorPaddingTop = 0f;
	float indicatorPaddingBottom = 0f;
	
	private Paint mPaint;
	
	private IndicatorAdapter mAdapter;
	
	private int pageCurrent;
	private float percentScrolled;
	private float percentScrolledFirst;
	
	private static final int DIRECT_NONE = 0;
	private static final int DIRECT_LEFT = -1;
	private static final int DIRECT_RIGHT = 1;
	private int directScrolling = DIRECT_NONE;
	
	private boolean isScrolling = false;
	
	public Indicator(Context context) {
		this(context, null, 0);
	}

	public Indicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public Indicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Indicator);
			indicatorColor = ta.getColor(R.styleable.Indicator_indicatorColor, Color.BLUE);
			indicatorWidth = ta.getDimension(R.styleable.Indicator_indicatorWidth, -1);
			indicatorHeight = ta.getDimension(R.styleable.Indicator_indicatorHeight, -1);
			
			float indicatorPadding = ta.getDimension(R.styleable.Indicator_indicatorPadding, 0);
			indicatorPaddingLeft = indicatorPadding;
			indicatorPaddingRight = indicatorPadding;
			indicatorPaddingTop = indicatorPadding;
			indicatorPaddingBottom = indicatorPadding;
			
			indicatorPaddingLeft = ta.getDimension(R.styleable.Indicator_indicatorPaddingLeft,
					indicatorPadding);
			indicatorPaddingRight = ta.getDimension(R.styleable.Indicator_indicatorPaddingRight,
					indicatorPadding);
			indicatorPaddingTop = ta.getDimension(R.styleable.Indicator_indicatorPaddingTop,
					indicatorPadding);
			indicatorPaddingBottom = ta.getDimension(R.styleable.Indicator_indicatorPaddingBottom
					, indicatorPadding);
			ta.recycle();
		}
		init();
	}
	
	private void init() {
		mPaint = new Paint();
		mPaint.setColor(indicatorColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(indicatorWidth == -1) {
			indicatorWidth = getWidth() / indicatorCount;
			System.out.println(TAG + "->indicatorWidth->" + indicatorWidth);
		}
        if(indicatorHeight == -1) {
        	indicatorHeight = getHeight();
        	System.out.println(TAG + "->indicatorHeight->" + indicatorHeight);
        }
        float left = pageCurrent * indicatorWidth;
        if(directScrolling == DIRECT_LEFT) {
        	left += indicatorWidth * percentScrolled;
        }else if(directScrolling == DIRECT_RIGHT) {
        	left += indicatorWidth;
        	left -= indicatorWidth * (1 - percentScrolled);
        }
        float right = left + indicatorWidth;
        left += indicatorPaddingLeft;
        right -= indicatorPaddingRight;
        System.out.println(TAG + "->left border->" + left);
        canvas.drawRect(left, 0, right, indicatorHeight, mPaint);
	}
	
	
	public void setAdapter(IndicatorAdapter adapter) {
		this.mAdapter = adapter;
		if(adapter == null) {
			this.indicatorCount = 0;
		}else {
			this.indicatorCount = adapter.getIndicatorCount();
		}
	}
	
//	/**
//	 * @see {@link ViewPager#setCurrentItem(int)}
//	 */
//	public void setCurrentItem(int item) {
//		pageCurrent = item;
//		invalidate();
//	}
//	
//	/**
//	 * @see {@link ViewPager#setCurrentItem(int, boolean)}
//	 */
//	public void setCurrentItem(int item, boolean smoothScroll) {
//		if(smoothScroll) {
//			
//		}else {
//			pageCurrent = item;
//			invalidate();
//		}
//	}

	/**
	 * 
	 * @see {@link android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)}
	 */
	public void onPageScrollStateChanged(int state) {
		
	}

	/**
	 * @see {@link android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)}
	 */
	public void onPageScrolled(int pageBeforeScrolling, float percentScrolled,
			int pixelScrolled) {
		System.out.println(TAG + "->onPageScrolled\n"
				+ "pageBeforeScrolling->" + pageBeforeScrolling + "\n"
				+ "percentScrolled->" + percentScrolled + "\n");
		if(percentScrolled == 0f) {
			this.percentScrolledFirst = 0f;
			isScrolling = false;
			System.out.println(TAG + "->stop scrolling");
		}
		if(this.percentScrolledFirst == 0f && !isScrolling) {
			this.percentScrolledFirst = percentScrolled;
			System.out.println("write first scolled percent down");
		}
		if(this.percentScrolledFirst != 0f && !isScrolling) {
			this.directScrolling = this.percentScrolledFirst > percentScrolled ?
					DIRECT_RIGHT : DIRECT_LEFT;
			if(directScrolling == DIRECT_LEFT) {
				System.out.println(TAG + "->left");
			}else {
				System.out.println(TAG + "->right");
			}
		}
		this.pageCurrent = pageBeforeScrolling;
		this.percentScrolled = percentScrolled;
		invalidate();
	}

	/**
	 * 
	 * @see {@link android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)}
	 */
	public void onPageSelected(int pageSelected) {
		invalidate();
	}
	
	
//	class SmoothScrollHandler extends Handler implements Runnable{
//		private int targetPage = 0;
//		private int curPage = 0;
//		private float percentScrolled = 0f;
//		
//		public void setTargetPage(int page) {
//			this.targetPage = page;
//		}
//		
//		public void setCurPage(int page) {
//			this.curPage = page;
//		}
//
//		private float getPercentScrolled() {
//			return percentScrolled;
//		}
//
//		private void setPercentScrolled(float percentScrolled) {
//			this.percentScrolled = percentScrolled;
//		}
//
//		@Override
//		public void run() {
//			if(getPercentScrolled() == 0) {
//				return;
//			}
//			TestIndicator.this.onPageScrolled(curPage, percentScrolled,
//					(int)(TestIndicator.this.indicatorWidth * percentScrolled));
//		}
//		
//		public void start() {
//			setPercentScrolled(0.1f);
//			post(this);
//		}
//		
//	}

}
