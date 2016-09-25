package ca.danblitzhou.timerdemo;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    protected static final int DEFAULT_TIME = 180000;
    protected static final int MAX_TIME = 600000;
    TextView count;
    CountDownTimer downTimer;
    SeekBar timeCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = (TextView) findViewById(R.id.counter);
        setTimeText(count, (long)DEFAULT_TIME);
        timeCtrl = (SeekBar) findViewById(R.id.seekBar);
        timeCtrl.setMax(MAX_TIME);
        timeCtrl.setProgress(DEFAULT_TIME);
        timeCtrl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser){

                setTimeText(count, (long)progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}

        });



    }

    public void controlCountDown(View view){

        final Button CtrlBtn = (Button)findViewById(R.id.startBtn);

        if (timeCtrl.isEnabled()) {
            downTimer = new CountDownTimer((long) timeCtrl.getProgress(), (long) 1000) {
                @Override
                public void onTick(long ms_till_done) {
                    setTimeText(count, ms_till_done);
                    timeCtrl.setProgress((int) ms_till_done);
                }

                @Override
                public void onFinish() {
                    timeCtrl.setEnabled(true);
                    count.setText("Finished");
                    CtrlBtn.setText("START");
                }
            };
            CtrlBtn.setText("STOP");
            downTimer.start();
            timeCtrl.setEnabled(false);
        }else {
            timeCtrl.setEnabled(true);
            downTimer.cancel();
            CtrlBtn.setText("START");
        }

    }

    public void setTimeText(TextView textView, long timeInMs) {
        if (timeInMs > 0) {
            textView.setText(String.format("%02d", timeInMs / 60000) + " : " + String.format("%02d", (timeInMs % 60000) / 1000));
        }else textView.setText("00 : 00");
    }

}
