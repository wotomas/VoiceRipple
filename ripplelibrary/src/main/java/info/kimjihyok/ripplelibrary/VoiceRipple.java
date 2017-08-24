package info.kimjihyok.ripplelibrary;

import android.view.View;
import android.widget.Button;

/**
 * Created by jihyokkim on 2017. 8. 24..
 */

public interface VoiceRipple {
  void setTargetView(View parentView);
  void setRippleColor(int color);
  void setDampingAmplitude(int dampingAmplitude);
  void drop(int amplitude);
}
