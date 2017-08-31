package info.kimjihyok.ripplelibrary.renderer;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;

/**
 * Created by jkimab on 2017. 8. 30..
 */

public abstract class Renderer {
  protected float[] points;
  protected double ampValue = 1.0;

  @CallSuper
  public void render(Canvas canvas, int , int y){

  }

  public abstract void changeColor(@ColorInt int color);

  public void setAmpValue(double ampValue) {
    this.ampValue = ampValue;
  }
}

