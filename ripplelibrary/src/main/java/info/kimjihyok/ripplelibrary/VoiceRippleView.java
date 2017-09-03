package info.kimjihyok.ripplelibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

import info.kimjihyok.ripplelibrary.listener.RecordingListener;
import info.kimjihyok.ripplelibrary.renderer.Renderer;
import info.kimjihyok.ripplelibrary.renderer.TimerCircleRippleRenderer;

/**
 * Created by jihyokkim on 2017. 8. 24..
 */

public class VoiceRippleView extends View {
  private static final String TAG = "VoiceRippleView";
  private static final double AMPLITUDE_REFERENCE = 32767.0;
  private static int MIN_RADIUS;
  private static int MIN_ICON_SIZE;
  private static int MIN_FIRST_RIPPLE_RADIUS;
  private static final int INVALID_PARAMETER = -1;

  private int buttonRadius;
  private int rippleRadius;
  private int backgroundRadius;
  private int iconSize;
  private boolean isRecording;
  private boolean isPrepared;

  private int rippleDecayRate = INVALID_PARAMETER;
  private int thresholdRate = INVALID_PARAMETER;
  private double backgroundRippleRatio = INVALID_PARAMETER;
  private int audioSource = INVALID_PARAMETER;
  private int outputFormat = INVALID_PARAMETER;
  private int audioEncoder = INVALID_PARAMETER;

  private MediaRecorder recorder;
  private Drawable recordIcon;
  private Drawable recordingIcon;
  private OnClickListener listener;
  private Handler handler;  // Handler for updating ripple effect
  private RecordingListener recordingListener;
  private Renderer currentRenderer;
  private int currentRecordedTime = 0;

  public void setRenderer(Renderer currentRenderer) {
    this.currentRenderer = currentRenderer;
    invalidate();
  }

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

  private int minFirstRadius;

  private void init(Context context, AttributeSet attrs) {
    MIN_RADIUS = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
    MIN_ICON_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    MIN_FIRST_RIPPLE_RADIUS = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.VoiceRippleView, 0, 0);
    try {
      rippleRadius = a.getInt(R.styleable.VoiceRippleView_VoiceRippleView_rippleRadius, MIN_RADIUS);
      iconSize = a.getInt(R.styleable.VoiceRippleView_VoiceRippleView_iconSize, MIN_ICON_SIZE);
    } finally {
      a.recycle();
    }

    backgroundRadius = rippleRadius;
    buttonRadius = backgroundRadius;
    minFirstRadius =  MIN_FIRST_RIPPLE_RADIUS;
    handler = new Handler();

    this.setClickable(true);
    this.setEnabled(true);
    this.setFocusable(true);
    this.setFocusableInTouchMode(true);

    setBackgroundRippleRatio(1.1);
    setRippleDecayRate(Rate.MEDIUM);
    setRippleSampleRate(Rate.LOW);
  }

  public void setMediaRecorder(MediaRecorder recorder) {
    this.recorder = recorder;
  }

  public void onStop() throws IllegalStateException {
    if (isPrepared && recorder != null) {
      recorder.stop();
    }
  }

  public void onDestroy() {
    if (isPrepared && recorder != null) {
      recorder.release();
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if(event.getAction() == MotionEvent.ACTION_UP) {
      if(listener != null) listener.onClick(this);
    }
    return super.dispatchTouchEvent(event);
  }


  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if(event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
      if(listener != null) listener.onClick(this);
    }
    return super.dispatchKeyEvent(event);
  }

  @Override
  public void setOnClickListener(OnClickListener listener) {
    this.listener = listener;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int viewWidthHalf = this.getMeasuredWidth() / 2;
    int viewHeightHalf = this.getMeasuredHeight() / 2;

    currentRenderer.render(canvas, viewWidthHalf, viewHeightHalf, buttonRadius, rippleRadius, backgroundRadius);

    if (isRecording) {
      recordingIcon.setBounds(viewWidthHalf - iconSize, viewHeightHalf - iconSize, viewWidthHalf + iconSize, viewHeightHalf + iconSize);
      recordingIcon.draw(canvas);
    } else {
      recordIcon.setBounds(viewWidthHalf - iconSize, viewHeightHalf - iconSize, viewWidthHalf + iconSize, viewHeightHalf + iconSize);
      recordIcon.draw(canvas);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int minw =  getPaddingLeft() + getPaddingRight();
    int w = resolveSizeAndState(minw, widthMeasureSpec, 0);

    int minh =  getPaddingBottom() + getPaddingTop();
    int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

    setMeasuredDimension(w, h);
  }


  public boolean isRecording() {
    return isRecording;
  }

  public void setOutputFile(String absolutePath) {
    recorder.setOutputFile(absolutePath);
  }

  public void setAudioSource(int audioSource) {
    this.audioSource = audioSource;
  }

  public void setOutputFormat(int outputFormat) {
    this.outputFormat = outputFormat;
  }

  public void setAudioEncoder(int audioEncoder) {
    this.audioEncoder = audioEncoder;
  }

  public void setIconSize(int dpSize) {
    this.iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize / 2, getResources().getDisplayMetrics());
    invalidate();
  }

  public void setRippleColor(int color) {
    currentRenderer.changeColor(color);
  }

  public void setRippleSampleRate(Rate rate) {
    switch (rate) {
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
    invalidate();
  }

  public void setRippleDecayRate(Rate rate) {
    switch (rate) {
      case LOW:
        this.rippleDecayRate = 20;
        break;
      case MEDIUM:
        this.rippleDecayRate = 10;
        break;
      case HIGH:
        this.rippleDecayRate = 5;
        break;
    }
    invalidate();
  }


  public void setBackgroundRippleRatio(double ratio) {
    this.backgroundRippleRatio = ratio;
    minFirstRadius = (int) (MIN_FIRST_RIPPLE_RADIUS + (MIN_FIRST_RIPPLE_RADIUS * backgroundRippleRatio));
    invalidate();
  }

  /**
   * Calculating decibels from amplitude requires the following: power_db = 20 * log10(amp / amp_ref);
   * 0db is the maximum, and everything else is negative
   * @param amplitude
   */
  private void drop(int amplitude) {
    int powerDb = (int)(20.0 * Math.log10((double) amplitude / AMPLITUDE_REFERENCE));

    // clip if change is below threshold
    final int THRESHOLD = (-1 * powerDb) / thresholdRate;

    if (THRESHOLD >= 0) {
      if (rippleRadius - THRESHOLD >= powerDb + MIN_RADIUS + minFirstRadius || powerDb + MIN_RADIUS + minFirstRadius >= rippleRadius + THRESHOLD) {
        rippleRadius = powerDb + MIN_RADIUS + minFirstRadius;
        backgroundRadius = (int) (rippleRadius * backgroundRippleRatio);
      } else {
        // if decreasing velocity reached 0, it should simply match with ripple radius
        if (((backgroundRadius - rippleRadius) / rippleDecayRate) == 0) {
          backgroundRadius = rippleRadius;
          rippleRadius = buttonRadius;
        } else {
          backgroundRadius = backgroundRadius - ((backgroundRadius - rippleRadius) / rippleDecayRate);
          rippleRadius = rippleRadius - ((rippleRadius - buttonRadius) / rippleDecayRate);
        }
      }

      invalidate();
    }
  }

  public void stopRecording() {
    isRecording = false;
    if (isPrepared) {
      recorder.stop();
      recorder.reset();
      isPrepared = false;
      handler.removeCallbacks(updateRipple);
      currentRecordedTime = 0;
      invalidate();
      if (recordingListener != null) {
        recordingListener.onRecordingStopped();
      }
    } else {
      Log.i(TAG, "stopRecording(): ", new IllegalStateException("Recording should be stopped if recording has been called previously"));
    }
  }

  public void startRecording() {
    checkValidState();
    try {
      prepareRecord();
      recorder.start();
      isRecording = true;
      isPrepared = true;
      handler.post(updateRipple);
      invalidate();
      if (recordingListener != null) {
        recordingListener.onRecordingStarted();
      }
    } catch (Exception e) {
      Log.e(TAG, "startRecording(): ", e);
    }
  }

  private void checkValidState() {
    if (thresholdRate == INVALID_PARAMETER || backgroundRippleRatio == INVALID_PARAMETER || rippleDecayRate == INVALID_PARAMETER) {
      throw new IllegalStateException("Set rippleSampleRate, backgroundRippleRatio and rippleDecayRate before starting to record!");
    }

    if (audioSource == INVALID_PARAMETER || outputFormat == INVALID_PARAMETER || audioEncoder == INVALID_PARAMETER) {
      throw new IllegalStateException("You have to set audioSource, outputFormat, and audioEncoder before starting to record!");
    }
  }


  private void prepareRecord() throws IOException {
    recorder.setAudioSource(audioSource);
    recorder.setOutputFormat(outputFormat);
    recorder.setAudioEncoder(audioEncoder);
    recorder.prepare();
  }

  private Runnable updateRipple = new Runnable() {
    @Override
    public void run() {
      if (isRecording) {
        drop(recorder.getMaxAmplitude());
        currentRecordedTime = currentRecordedTime + 50;
        if (currentRenderer instanceof TimerCircleRippleRenderer) {
          ((TimerCircleRippleRenderer)currentRenderer).setCurrentTimeMilliseconds(currentRecordedTime);
        }
        handler.postDelayed(this, 50);  // updates the visualizer every 50 milliseconds
      }
    }
  };

  public void setRecordDrawable(Drawable recordIcon, Drawable recordingIcon) {
    this.recordIcon = recordIcon;
    this.recordingIcon = recordingIcon;

    invalidate();
  }

  public void setRecordingListener(RecordingListener recordingListener) {
    this.recordingListener = recordingListener;
  }
}
