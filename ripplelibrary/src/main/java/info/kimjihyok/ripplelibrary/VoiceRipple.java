package info.kimjihyok.ripplelibrary;

import android.view.View;
import android.widget.Button;

/**
 * Created by jihyokkim on 2017. 8. 24..
 */

public interface VoiceRipple {
  void setTargetView(View parentView);
  void setRippleColor(int color);
  void setThresholdRate(Threshold threshold);
  void drop(int amplitude);


  enum Threshold {
    LOW, MEDIUM, HIGH
  }
}
