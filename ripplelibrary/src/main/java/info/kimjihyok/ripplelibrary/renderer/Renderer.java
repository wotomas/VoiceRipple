package info.kimjihyok.ripplelibrary.renderer;

import android.graphics.Canvas;

/**
 * Created by jkimab on 2017. 8. 30..
 */

public abstract class Renderer {
  public void render(Canvas canvas, int x, int y, int buttonRadius, int rippleRadius, int rippleBackgroundRadius) {

  }

  public abstract void changeColor(int color);
}

