package info.kimjihyok.voiceripple;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.File;
import java.io.IOException;

import info.kimjihyok.ripplelibrary.Rate;
import info.kimjihyok.ripplelibrary.VoiceRippleView;
import info.kimjihyok.ripplelibrary.listener.RecordingListener;
import info.kimjihyok.ripplelibrary.renderer.Renderer;
import info.kimjihyok.ripplelibrary.renderer.TimerCircleRippleRenderer;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
  private static final String DIRECTORY_NAME = "AudioCache";

  private Button playButton;
  
  private MediaPlayer player = null;
  private File directory = null;
  private File audioFile = null;

  // Requesting permission to RECORD_AUDIO
  private boolean permissionToRecordAccepted = false;
  private String [] permissions = {Manifest.permission.RECORD_AUDIO};

  // Initialize Voice Ripple
  private VoiceRippleView voiceRipple;

  private Renderer currentRenderer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeControlPanel();

    // directory = new File(Environment.getExternalStorageDirectory(), DIRECTORY_NAME);
    directory = new File(getExternalCacheDir().getAbsolutePath(), DIRECTORY_NAME);

    if (directory.exists()) {
      deleteFilesInDir(directory);
    } else {
      directory.mkdirs();
    }

    audioFile = new File(directory + "/audio.mp3");

    voiceRipple = (VoiceRippleView) findViewById(R.id.voice_ripple_view);
    voiceRipple.setRecordingListener(new RecordingListener() {
      @Override
      public void onRecordingStopped() {
        Log.d(TAG, "onRecordingStopped()");
      }

      @Override
      public void onRecordingStarted() {
        Log.d(TAG, "onRecordingStarted()");
      }
    });

    // set view related settings for ripple view
    voiceRipple.setRippleSampleRate(Rate.LOW);
    voiceRipple.setRippleDecayRate(Rate.HIGH);
    voiceRipple.setBackgroundRippleRatio(1.4);

    // set recorder related settings for ripple view
    voiceRipple.setMediaRecorder(new MediaRecorder());
    voiceRipple.setOutputFile(audioFile.getAbsolutePath());
    voiceRipple.setAudioSource(MediaRecorder.AudioSource.MIC);
    voiceRipple.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    voiceRipple.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    // set inner icon
    voiceRipple.setRecordDrawable(ContextCompat.getDrawable(this, R.drawable.record), ContextCompat.getDrawable(this, R.drawable.recording));
    voiceRipple.setIconSize(30);

    voiceRipple.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (voiceRipple.isRecording()) {
          voiceRipple.stopRecording();
        } else {
          voiceRipple.startRecording();
        }
      }
    });

    currentRenderer = new TimerCircleRippleRenderer(getDefaultRipplePaint(), getDefaultRippleBackgroundPaint(), getButtonPaint(), getArcPaint(), 10000.0, 0.0);
    if (currentRenderer instanceof TimerCircleRippleRenderer) {
      ((TimerCircleRippleRenderer) currentRenderer).setStrokeWidth(20);
    }

    voiceRipple.setRenderer(currentRenderer);

    ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
  }

  private Paint getArcPaint() {
    Paint paint = new Paint();
    paint.setColor(ContextCompat.getColor(this, R.color.temp_color));
    paint.setStrokeWidth(20);
    paint.setAntiAlias(true);
    paint.setStrokeCap(Paint.Cap.SQUARE);
    paint.setStyle(Paint.Style.STROKE);
    return paint;
  }
  private Paint getDefaultRipplePaint() {
    Paint ripplePaint = new Paint();
    ripplePaint.setStyle(Paint.Style.FILL);
    ripplePaint.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
    ripplePaint.setAntiAlias(true);

    return ripplePaint;
  }

  private Paint getDefaultRippleBackgroundPaint() {
    Paint rippleBackgroundPaint = new Paint();
    rippleBackgroundPaint.setStyle(Paint.Style.FILL);
    rippleBackgroundPaint.setColor((ContextCompat.getColor(this, R.color.colorPrimary) & 0x00FFFFFF) | 0x40000000);
    rippleBackgroundPaint.setAntiAlias(true);

    return rippleBackgroundPaint;
  }

  private Paint getButtonPaint() {
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    return paint;
  }


  @Override
  protected void onStop() {
    super.onStop();
    try {
      voiceRipple.onStop();
    } catch (IllegalStateException e) {
      Log.e(TAG, "onStop(): ", e);
    }


    if (player != null) {
      player.release();
      player = null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    voiceRipple.onDestroy();
  }

  private boolean deleteFilesInDir(File path) {
    if(path.exists()) {
      File[] files = path.listFiles();
      if (files == null) {
        return true;
      }

      for(int i=0; i<files.length; i++) {
        if (files[i].isDirectory()) {
          files[i].delete();
        }
      }
    }
    return true;
  }

  private void initializeControlPanel() {
    playButton = (Button) findViewById(R.id.voice_play_button);
    playButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (directory != null && audioFile != null) {
          player = new MediaPlayer();
          try {
            player.setDataSource(audioFile.getAbsolutePath());
            player.prepare();
            player.start();
          } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
          }
        }
      }
    });

    ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
    picker.setShowOldCenterColor(false);
    picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
      @Override
      public void onColorChanged(int color) {
        voiceRipple.setRippleColor(color);
      }
    });

    SeekBar iconSizeSeekBar = (SeekBar) findViewById(R.id.icon_size_seekbar);
    iconSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        voiceRipple.setIconSize(i);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });


    SeekBar rippleSizeSeekbar = (SeekBar) findViewById(R.id.ripple_size_seekbar);
    rippleSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (i == 0 || i == 100) return;
        double ratio = 0.5 + ((double) i / 100.0);
        voiceRipple.setBackgroundRippleRatio(ratio);
        Log.d(TAG, "onBackgroundRippleRatio ChangedL " + ratio);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    final RadioButton low = (RadioButton) findViewById(R.id.option1);
    final RadioButton medium = (RadioButton) findViewById(R.id.option2);
    final RadioButton high = (RadioButton) findViewById(R.id.option3);
    RadioButton.OnClickListener optionOnClickListener = new RadioButton.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (low.isChecked()) {
          voiceRipple.setRippleDecayRate(Rate.LOW);
        } else if (medium.isChecked()) {
          voiceRipple.setRippleDecayRate(Rate.MEDIUM);
        } else if (high.isChecked()) {
          voiceRipple.setRippleDecayRate(Rate.HIGH);
        }
      }
    };

    low.setOnClickListener(optionOnClickListener);
    medium.setOnClickListener(optionOnClickListener);
    high.setOnClickListener(optionOnClickListener);
    high.setChecked(true);

    final RadioButton low1 = (RadioButton) findViewById(R.id.option4);
    final RadioButton medium1 = (RadioButton) findViewById(R.id.option5);
    final RadioButton high1 = (RadioButton) findViewById(R.id.option6);
    RadioButton.OnClickListener optionOnClickListener2 = new RadioButton.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (low1.isChecked()) {
          voiceRipple.setRippleSampleRate(Rate.LOW);
        } else if (medium1.isChecked()) {
          voiceRipple.setRippleSampleRate(Rate.MEDIUM);
        } else if (high1.isChecked()) {
          voiceRipple.setRippleSampleRate(Rate.HIGH);
        }
      }
    };

    low1.setOnClickListener(optionOnClickListener);
    medium1.setOnClickListener(optionOnClickListener);
    high1.setOnClickListener(optionOnClickListener);
    low1.setChecked(true);
  }



  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode){
      case REQUEST_RECORD_AUDIO_PERMISSION:
        permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        break;
    }
    if (!permissionToRecordAccepted ) finish();
  }
}
