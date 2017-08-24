package info.kimjihyok.voiceripple;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import info.kimjihyok.ripplelibrary.VoiceRipple;
import info.kimjihyok.ripplelibrary.VoiceRippleView;

public class MainActivity extends AppCompatActivity implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener{
  private static final String TAG = "MainActivity";
  private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
  private static final String DIRECTORY_NAME = "AudioCache";

  private MediaRecorder recorder;
  private Button voiceRecordButton;
  private Button playButton;
  
  private MediaPlayer player = null;
  private File directory = null;
  private File audioFile = null;

  // Requesting permission to RECORD_AUDIO
  private boolean permissionToRecordAccepted = false;
  private String [] permissions = {Manifest.permission.RECORD_AUDIO};
  private static boolean isRecording = false;

  // Handler for updating ripple effect
  private Handler handler;

  // Initialize Voice Ripple
  private VoiceRipple voiceRipple;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    voiceRecordButton = (Button) findViewById(R.id.voice_record_button);
    voiceRecordButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isRecording) {
          stopRecording();
          voiceRecordButton.setText(R.string.start_recording);
        } else {
          startRecording();
          voiceRecordButton.setText(R.string.stop_recording);
        }
      }
    });

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

    // Record to the external cache directory for visibility
    // directory = new File(Environment.getExternalStorageDirectory(), DIRECTORY_NAME);
    directory = new File(getExternalCacheDir().getAbsolutePath(), DIRECTORY_NAME);

    if (directory.exists()) {
      deleteFilesInDir(directory);
    } else {
      directory.mkdirs();
    }


    ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

    handler = new Handler();


//    voiceRipple = new VoiceRippleView(this);
//    voiceRipple.setTargetView(voiceRecordButton);
    voiceRipple = (VoiceRipple) findViewById(R.id.voice_ripple_view);
    voiceRipple.setRippleColor(ContextCompat.getColor(this, R.color.colorPrimary));
    voiceRipple.setDampingAmplitude(4000);
  }

  public boolean deleteFilesInDir(File path) {
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

  @Override
  protected void onStop() {
    super.onStop();
    if (recorder != null) {
      recorder.release();
      recorder = null;
    }

    if (player != null) {
      player.release();
      player = null;
    }
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

  private void startRecording() {
    recorder = new MediaRecorder();
    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    audioFile = new File(directory + "/audio.mp3");
    recorder.setOutputFile(audioFile.getAbsolutePath());

    recorder.setOnErrorListener(this);
    recorder.setOnInfoListener(this);

    try {
      recorder.prepare();
      recorder.start();
      isRecording = true;
      handler.post(updateRipple);
    } catch (IOException e) {
      Log.e(TAG, "prepare() failed");
    }
  }

  // updates the visualizer every 50 milliseconds
  // this will be moved in the VoiceRipple
  Runnable updateRipple = new Runnable() {
    @Override
    public void run() {
      if (isRecording) {
        voiceRipple.drop(recorder.getMaxAmplitude());

        handler.postDelayed(this, 40);
      }
    }
  };

  private void stopRecording() {
    isRecording = false;
    recorder.stop();
    recorder.release();
    handler.removeCallbacks(updateRipple);
    recorder = null;
  }

  @Override
  public void onError(MediaRecorder mediaRecorder, int i, int i1) {
    Log.d(TAG, "onError ");
  }

  @Override
  public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
    Log.d(TAG, "onInfo ");
  }
}
