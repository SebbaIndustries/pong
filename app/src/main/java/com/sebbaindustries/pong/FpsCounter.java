package com.sebbaindustries.pong;

import android.annotation.SuppressLint;
import android.widget.TextView;

/**
 * <b>This class is used for showing fps</b>
 * @author sebbaindustries
 * @version 1.0
 */
public class FpsCounter {

    private static final int MIN_FPS_DRAW_PERIOD_MILLIS = 200;
    // kinda makes look like app is stable and not falling apart like android development :p
    private static final double SMOOTHING_COEFFICIENT = 0.1;

    private final TextView textView;
    private final ScalarExpSmoother fpsSmoother = new ScalarExpSmoother(
            SMOOTHING_COEFFICIENT);

    private long lastFpsDrawTime;

    /**
     * Sets text view for fps counter
     * @param textView Element for text
     */
    public FpsCounter(TextView textView) {
        this.textView = textView;
    }

    /**
     * Updates and stabilises fps
     * @param fps Frames per second
     */
    public void updateFps(double fps) {
        final double smoothedFps = fpsSmoother.smooth(fps);
        long now = System.currentTimeMillis();

        boolean canDrawFps = now - lastFpsDrawTime > MIN_FPS_DRAW_PERIOD_MILLIS;
        if (canDrawFps) {
            textView.post(new Runnable() {
                // this is why i hate android
                @SuppressLint("DefaultLocale")
                public void run() {
                    textView.setText(String.format("FPS: %.2f", smoothedFps));
                }
            });
            lastFpsDrawTime = now;
        }
    }
}
