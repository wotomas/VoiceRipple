package info.kimjihyok.ripplelibrary.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;

/**
 * Created by jkimab on 2017. 9. 1..
 */

public class TimerCircleRippleRenderer extends CircleRippleRenderer {
  private static final String TAG = "TimerCircleRippleRenderer";

  private int strokeWidth;
  private Paint timerPaint;
  private RectF rect;
  private double maxTimeMilliseconds;
  private double currentTimeMilliseconds;

  public TimerCircleRippleRenderer(Paint ripplePaint, Paint rippleBackgroundPaint, Paint buttonPaint, Paint timerPaint, double maxTimeMilliseconds, double currentTimeMilliseconds) {
    super(ripplePaint, rippleBackgroundPaint, buttonPaint);
    this.timerPaint = timerPaint;
    this.maxTimeMilliseconds = maxTimeMilliseconds;
    this.currentTimeMilliseconds = currentTimeMilliseconds;
    init();
  }

  private void init() {
    rect = new RectF();
  }

  @Override
  public void render(Canvas canvas, int x, int y, int buttonRadius, int rippleRadius, int rippleBackgroundRadius) {
    super.render(canvas, x, y, buttonRadius, rippleRadius, rippleBackgroundRadius);
    rect.set(x - buttonRadius + strokeWidth / 2, y - buttonRadius + strokeWidth / 2, x+ buttonRadius - strokeWidth / 2, y+ buttonRadius - strokeWidth / 2);
    canvas.drawArc(rect, -90, (float) (360.0 * (currentTimeMilliseconds / maxTimeMilliseconds)), false, timerPaint);
  }

  @Override
  public void changeColor(@ColorInt int color) {
    super.changeColor(color);
    timerPaint.setColor(color);
  }

  public void setCurrentTimeMilliseconds(int currentTimeMilliseconds) {
    this.currentTimeMilliseconds = currentTimeMilliseconds;
  }

  public void setStrokeWidth(int strokeWidth) {
    if (strokeWidth % 2 != 0) {
      throw new IllegalArgumentException("Stroke Width should be an even number!");
    }

    this.strokeWidth = strokeWidth;
    timerPaint.setStrokeWidth(strokeWidth);
  }
}
