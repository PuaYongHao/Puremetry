package com.example.puremetry;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class NoiseDetectController extends Activity {

    public static final int APP_PERMISSION_RECORD_AUDIO = 1;
    private static MediaRecorder mediaRecorder;
    private static Context context;

    public static void detectNoiseLevel(Context c) {

        context = c;

        if (checkPermission()) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null/");

            try {
                mediaRecorder.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
        }
        else
            requestPermission();

    }

    public void stop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public static int getAmplitude() {
        if (mediaRecorder != null)
            return (mediaRecorder.getMaxAmplitude());
        else
            return 0;
    }

    private static void requestPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(context, "Please grant permission for audio.",
                        Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, APP_PERMISSION_RECORD_AUDIO);
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
        }
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, APP_PERMISSION_RECORD_AUDIO);
    }

    public static boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

}
