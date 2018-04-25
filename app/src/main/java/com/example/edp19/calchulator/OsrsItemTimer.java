package com.example.edp19.calchulator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by eric on 4/24/18.
 */

public class OsrsItemTimer extends OsrsItem{
    private TextView tvName;

    private CountDownTimer timer;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private Drawable background;
    private long ticks = 0;
    private long totalTicks = 20000 / 100;
    private long totalTime = 20000;

    private boolean isRunning = false;

    private WeakReference<OsrsTableItem> item;

    public OsrsItemTimer(Context context, OsrsTableItem parent){
        super(parent);
        item = new WeakReference<>(parent);

        //10 seconds
        final long duration = 10 * 1000;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (ConstraintLayout) layoutInflater.inflate(R.layout.special_attack, null);

        progressBar = layout.findViewById(R.id.pbSpecialAttack);
        progressBar.setVisibility(View.INVISIBLE);
        background = layout.getBackground();
        layout.setBackgroundDrawable(null);

        tvName = layout.findViewById(R.id.tvSpecialAttack);
        tvName.setVisibility(View.VISIBLE);
    }

    interface OnItemTimerFinishedListener {
         void onItemTimerFinished(OsrsItem item);
    }

    OnItemTimerFinishedListener listener;

    public void setOnItemTimerFinishedListener(OnItemTimerFinishedListener listener){
        this.listener = listener;
    }

    public void startTimer(long timerStartTime){
        System.out.println("Timer start time: " + timerStartTime);
        long timeElapsed = new Date().getTime() - timerStartTime;

        System.out.println(timeElapsed + " millseconds have elapsed");
        long percentageTimeElapsed = (timeElapsed / totalTime);

        System.out.println("Percent: " + percentageTimeElapsed);
        ticks = totalTicks - (percentageTimeElapsed * totalTicks / 100);

        System.out.println("THE STARTING TICKS IS: " + ticks);

        startTimer();
    }

    public void startTimer(){
        tvName.setGravity(Gravity.CENTER);
        isRunning = true;
        this.show();
        timer = new CountDownTimer(totalTime, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (((float) ++ticks / (float) totalTicks) * 100);

                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                ticks = 0;
                isRunning = false;
                progressBar.setProgress(100);
                OsrsItemTimer.this.hide();
                tvName.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

                listener.onItemTimerFinished(OsrsItemTimer.this);
                System.out.println("Finished");
            }
        }.start();
    }

    //hides the progress bar and background
    public void hide(){
        progressBar.setVisibility(View.INVISIBLE);
        layout.setBackgroundDrawable(null);
    }

    //shows the progress bar and background
    public void show(){
        layout.setBackgroundDrawable(background);
        progressBar.setVisibility(View.VISIBLE);
    }

    public boolean isRunning() {
        return isRunning;
    }


    public TextView getTextView(){
        return tvName;
    }

    public ConstraintLayout getLayout(){
        return layout;
    }
}