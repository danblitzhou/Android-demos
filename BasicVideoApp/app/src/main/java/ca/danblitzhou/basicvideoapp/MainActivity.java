package ca.danblitzhou.basicvideoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);

        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.demovideo);

        MediaController videoControl = new MediaController(this);

        videoControl.setAnchorView(videoView);

        videoView.setMediaController(videoControl);

        videoView.start();

    }
}
