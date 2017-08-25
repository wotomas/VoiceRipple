package info.kimjihyok.ripplelibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jihyokkim on 2017. 8. 24..
 */

public class VoiceRippleView extends View implements VoiceRipple {
  private static final String TAG = "VoiceRippleView";
  private static final double AMPLITUDE_REFERENCE = 32767.0;
  private static final int MIN_RADIUS = 200;
  private Context context;

  @ColorInt
  private int rippleColor;
  private Paint ripplePaint;
  private int thresholdRate;


  private int rippleRadius;

  public VoiceRippleView(Context context) {
    super(context);
    init(context, null);
  }

  public VoiceRippleView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public VoiceRippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }


  private void init(Context context, AttributeSet attrs) {
    this.context = context;

    TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.VoiceRippleView, 0, 0);
    try {
      rippleColor = a.getColor(R.styleable.VoiceRippleView_rippleColor, Color.BLACK);
      rippleRadius = a.getInt(R.styleable.VoiceRippleView_rippleRadius, 10);
    } finally {
      a.recycle();
    }

    setThresholdRate(Threshold.LOW);
    setupPaint();
  }

  private void setupPaint() {
    ripplePaint = new Paint();
    ripplePaint.setStyle(Paint.Style.FILL);
    ripplePaint.setColor(rippleColor);
    ripplePaint.setAntiAlias(true);
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int viewWidthHalf = this.getMeasuredWidth() / 2;
    int viewHeightHalf = this.getMeasuredHeight() / 2;

    canvas.drawCircle(viewWidthHalf, viewHeightHalf, rippleRadius, ripplePaint);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int minw =  getPaddingLeft() + getPaddingRight();
    int w = resolveSizeAndState(minw, widthMeasureSpec, 0);

    int minh =  getPaddingBottom() + getPaddingTop();
    int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

    setMeasuredDimension(w, h);
  }


  @Override
  public void setTargetView(View parentView) {
    Log.d(TAG, "setTargetView(): " + parentView.getX() + " " + parentView.getY());
  }

  @Override
  public void setRippleColor(int color) {
    this.rippleColor = color;
    invalidate();
  }

  @Override
  public void setThresholdRate(Threshold threshold) {
    switch (threshold) {
      case LOW:
        this.thresholdRate = 5;
        break;
      case MEDIUM:
        this.thresholdRate = 10;
        break;
      case HIGH:
        this.thresholdRate = 20;
        break;
    }
  }

  /**
   * Calculating decibels from amplitude requires the following: power_db = 20 * log10(amp / amp_ref);
   * 0db is the maximum, and everything else is negative
   * @param amplitude
   */
  @Override
  public void drop(int amplitude) {
    int powerDb = (int)(20.0 * Math.log10((double) amplitude / AMPLITUDE_REFERENCE));

    // clip if change is below threshold
    final int THRESHOLD = (-1 * powerDb) / thresholdRate;

    if (rippleRadius - THRESHOLD >= powerDb + MIN_RADIUS || powerDb + MIN_RADIUS >= rippleRadius + THRESHOLD) {
      this.rippleRadius = powerDb + MIN_RADIUS;
      invalidate();
    }


  }
}
