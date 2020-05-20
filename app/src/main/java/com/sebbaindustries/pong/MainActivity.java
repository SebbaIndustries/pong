package com.sebbaindustries.pong;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <b>MIT License</b><br>
 * <br>
 * <b>Copyright (c) 2020 SebbaIndustries</b><br>
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br>
 * of this software and associated documentation files (the "Software"), to deal <br>
 * in the Software without restriction, including without limitation the rights <br>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br>
 * copies of the Software, and to permit persons to whom the Software is <br>
 * furnished to do so, subject to the following conditions: <br>
 * <br>
 * The above copyright notice and this permission notice shall be included in all <br>
 * copies or substantial portions of the Software. <br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE <br>
 * SOFTWARE. <br>
 * <br>
 * @author <b>sebbaindustries</b>
 * @version <b>1.0</b>
 */
public class MainActivity extends Activity {

    // idk why I created this, I don't remember
    static final String LOG_TAG = "Game";

    private GameView gameView;

    /**
     * Creates instance of game or crashes, I think its fixed tho
     * @param savedInstanceState android instance from app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView fpsTextView = (TextView) findViewById(R.id.fpsTextView);
        FpsCounter fpsCounter = new FpsCounter(fpsTextView);

        LinearLayout layout = (LinearLayout) findViewById(R.id.frame);


        gameView = new PongGameView(getApplicationContext());
        gameView.setFpsCounter(fpsCounter);

        layout.addView(gameView);
    }

    /**
     * <b>This function does work... kinda, needs more work</b>
     */
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

}
