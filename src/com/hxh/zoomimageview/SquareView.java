/* 
 * HXH Create at 2015-3-9 下午2:50:45 
 */
package com.hxh.zoomimageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author HXH
 * @created 2015-3-9 下午2:50:45
 * @类说明 TODO
 * @version 2015-3-9 [V TODO] 新增
 */
public class SquareView extends View {
	private final static int OUT_SQUARE = 0;
	private final static int IN_SQUARE = 1;
	private final static int EDGE_SQUARE = 2;
	// private int width;
	private Paint mLinePaint;
	private int mLineColor = Color.RED;
	private Paint mDotPaint;
	private int mDotColor = Color.CYAN;
	private Paint mShadowPaint;
	private int mShadowColor = Color.argb(150, 60, 60, 60);
	private int mLineWidth = 5;
	private int left;
	private int top;
	private int width;
	private int height;
	private boolean init;
	private int sWidth;
	private int checkPos = OUT_SQUARE;
	private int rangeWidth;
	private float tempX;
	private float tempY;
	private boolean toSmall;

	/**
	 * @param context
	 */
	public SquareView(Context context) {
		super(context);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		mLinePaint = new Paint();
		mLinePaint.setColor(mLineColor);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(mLineWidth);

		mDotPaint = new Paint();
		mDotPaint.setColor(mDotColor);
		mDotPaint.setStyle(Style.FILL);
		mLinePaint.setAntiAlias(true);

		mShadowPaint = new Paint();
		mShadowPaint.setColor(mShadowColor);
		mShadowPaint.setStyle(Style.FILL);
		mShadowPaint.setAntiAlias(true);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (!init) {
			width = getWidth();
			height = getHeight();
			sWidth = width / 2;
			left = (width - sWidth) / 2;
			top = (height - sWidth) / 2;
			rangeWidth = 16;

			// System.err.println(width);
			// System.err.println(height);
			// System.err.println(sWidth);
			// System.err.println(left);
			// System.err.println(top);
			init = true;
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (left < 0) {
			left = 0;
		}
		if (top < 0) {
			top = 0;
		}

		if (left + sWidth > width) {
			left = width - sWidth;
		}
		if (top + sWidth > height) {
			top = height - sWidth;
		}

		drawShadow(canvas);

		drawLines(canvas);

		drawDots(canvas);

	}

	/**
	 * 画9点
	 * 
	 * @param canvas
	 * 
	 */
	private void drawDots(Canvas canvas) {
		if (toSmall) {
			// getLastDot();
		} else {
			canvas.drawCircle(left, top, rangeWidth, mDotPaint);
			canvas.drawCircle(left + sWidth / 2, top, rangeWidth, mDotPaint);
			canvas.drawCircle(left + sWidth, top, rangeWidth, mDotPaint);
			canvas.drawCircle(left, top + sWidth / 2, rangeWidth, mDotPaint);
			canvas.drawCircle(left + sWidth, top + sWidth / 2, rangeWidth,
					mDotPaint);
			canvas.drawCircle(left, top + sWidth, rangeWidth, mDotPaint);
			canvas.drawCircle(left + sWidth / 2, top + sWidth, rangeWidth,
					mDotPaint);
			canvas.drawCircle(left + sWidth, top + sWidth, rangeWidth,
					mDotPaint);
		}
	}

	/**
	 * 画阴影
	 * 
	 * @param canvas
	 */
	private void drawShadow(Canvas canvas) {
		canvas.drawRect(0, 0, left, height, mShadowPaint);// 左
		canvas.drawRect(left, 0, left + sWidth, top, mShadowPaint);// 上
		canvas.drawRect(left + sWidth, 0, width, height, mShadowPaint);// 右
		canvas.drawRect(left, top + sWidth, left + sWidth, height, mShadowPaint);// 下

	}

	/**
	 * 画线
	 * 
	 * @param canvas
	 * 
	 */
	private void drawLines(Canvas canvas) {
		// canvas.drawLine(left, top, left + width, top + width, mLinePaint);
		canvas.drawLine(left, top, left + sWidth, top, mLinePaint);// 上
		canvas.drawLine(left, top, left, top + sWidth, mLinePaint);// 左
		canvas.drawLine(left + sWidth, top, left + sWidth, top + sWidth,
				mLinePaint);// 右
		canvas.drawLine(left, top + sWidth, left + sWidth, top + sWidth,
				mLinePaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			tempX = event.getX();
			tempY = event.getY();
			checkPos = inSpuare(tempX, tempY);
			System.err.println(tempX);
			System.err.println(tempY);
			System.err.println(checkPos);
			if (checkPos == EDGE_SQUARE) {
				// getTouchedDot(tempX, tempY);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (checkPos == IN_SQUARE) {
				reDraw(event.getX(), event.getY());
			} else if (checkPos == EDGE_SQUARE) {
				// Math.min(f1, f2);
				// (event.getX() - left)*(event.getX() - left) + (event.getY() -
				// top)*(event.getY() - top)
			}
			break;

		default:
			break;
		}
		if (checkPos == OUT_SQUARE) {
			return super.onTouchEvent(event);
		} else {
			// TODO
			return true;
		}
	}

	/**
	 * 
	 */
	private void getTouchedDot() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param x
	 * @param y
	 * 
	 */
	private void reDraw(float x, float y) {
		left = (int) (left + x - tempX);
		top = (int) (top + y - tempY);
		tempX = x;
		tempY = y;
		invalidate();
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private int inSpuare(float x, float y) {
		if (x < left - rangeWidth || x > left + sWidth + rangeWidth
				|| y < top - rangeWidth || y > top + sWidth + rangeWidth) {
			return OUT_SQUARE;
		}
		if (x > left + rangeWidth && x < left + sWidth - rangeWidth
				&& y > top + rangeWidth && y < top + sWidth - rangeWidth) {
			return IN_SQUARE;
		}
		return EDGE_SQUARE;
	}

	public int getsWidth() {
		return sWidth;
	}

	public int getSquareLeft() {
		return left;
	}

	public int getSquareTop() {
		return top;
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// // TODO Auto-generated method stub
	// return true;
	// }

}
