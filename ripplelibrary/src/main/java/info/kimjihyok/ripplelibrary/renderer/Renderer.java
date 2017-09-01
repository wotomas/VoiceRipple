package info.kimjihyok.ripplelibrary.renderer;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;

/**
 * Created by jkimab on 2017. 8. 30..
 */

public abstract class Renderer {
  @CallSuper
  public void render(Canvas canvas, int x, int y, int buttonRadius, int rippleRadius, int rippleBackgroundRadius) {

  }

  public abstract void changeColor(@ColorInt int color);
}

