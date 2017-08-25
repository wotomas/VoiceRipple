package info.kimjihyok.voiceripple;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.File;
import java.io.IOException;

import info.kimjihyok.ripplelibrary.Rate;
import info.kimjihyok.ripplelibrary.VoiceRippleView;

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

    // set view related settings for ripple view
    voiceRipple.setRippleColor(ContextCompat.getColor(this, R.color.colorPrimary));
    voiceRipple.setRippleSampleRate(Rate.LOW);
    voiceRipple.setRippleDecayRate(Rate.HIGH);
    voiceRipple.setBackgroundRippleRatio(1.4);

    // set recorder related settings for ripple view
    voiceRipple.setMediaRecorder(new MediaRecorder());
    voiceRipple.setOutputFile(audioFile.getAbsolutePath());
    voiceRipple.setAudioSource(MediaRecorder.AudioSource.MIC);
    voiceRipple.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    voiceRipple.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    // inner icon
    voiceRipple.setRecordDrawable(ContextCompat.getDrawable(this, R.drawable.record), ContextCompat.getDrawable(this, R.drawable.recording));
    voiceRipple.setIconSize(30);

    voiceRipple.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (voiceRipple.isRecording()) {
          voiceRipple.stopRecording();
        } else {

          try {
            voiceRipple.startRecording();
          } catch (IOException e) {
            Log.e(TAG, "startRecording() error: ", e);
          }
        }
      }
    });

    ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
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

        voiceRipple.setBackgroundRippleRatio(1 + 2.0 * ((double) i / 100.0));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    //TODO: 요거 두개 추가 라디오 박스로
    //voiceRipple.setRippleDecayRate();
    //voiceRipple.setRippleSampleRate();


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
