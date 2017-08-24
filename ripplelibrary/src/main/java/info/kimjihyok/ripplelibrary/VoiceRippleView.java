package info.kimjihyok.ripplelibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jihyokkim on 2017. 8. 24..
 */

public class VoiceRippleView extends View implements VoiceRipple{
  public VoiceRippleView(Context context) {
    super(context);
  }

  public VoiceRippleView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public VoiceRippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void setTargetView(View parentView) {

  }

  @Override
  public void setRippleColor(int color) {

  }

  @Override
  public void setDampingAmplitude(int targetDampingAmplitude) {

  }

  @Override
  public void drop(int amplitude) {

  }
}
