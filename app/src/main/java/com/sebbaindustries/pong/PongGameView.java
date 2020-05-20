package com.sebbaindustries.pong;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * <b>This class is an actual game</b>
 * @author sebbaindustries
 * @version 1.0
 */
public class PongGameView extends GameView {

    public static final float HALF_PI = (float) (Math.PI * 0.5f);

    protected static final int MIN_MOVEMENT = 2;

    private Racket leftRacket;
    private Racket rightRacket;
    private Racket activeRacket;
    private Ball ball;
    private int leftPoints;
    private int rightPoints;

    private int pixelSize = 10;
    private Vector2d dimens;
    private Vector2d dimensHalf;

    private final Paint paint = new Paint();
    private Paint textPaint = new Paint();

    private Random random = new Random();

    /**
     * Main constructor, creates game and starts it
     * @param context App context for main screen
     */
    public PongGameView(Context context) {
        super(context);

        Vector2d racketSize = new Vector2d(pixelSize, 4 * pixelSize);
        float ballRadius = pixelSize;
        leftRacket = new Racket(racketSize);
        rightRacket = new Racket(racketSize);
        activeRacket = leftRacket;

        ball = new Ball(ballRadius);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setTextSize(64);
        textPaint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

        // listener for touch, tbh not completely sure how this thing works
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {

                    /*
                    This is for moving racket up( opposite gameplay)
                     */
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        int pointerIndex = event.getActionIndex();

                        float x = event.getX(pointerIndex);
                        float y = event.getY(pointerIndex);

                        Racket racket = (x < getWidth() * 0.5f) ? leftRacket
                                : rightRacket;
                        racket.setCenterY(y - dimensHalf.y);

                        break;
                    }

                    /*
                    This is for moving racket down (opposite gameplay)
                     */
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP: {
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        for (int idx = 0; idx < event.getPointerCount(); idx++) {
                            float x = event.getX(idx);
                            float y = event.getY(idx);
                            Racket racket = (x < getWidth() * 0.5f) ? leftRacket
                                    : rightRacket;

                            Vector2d center = racket.getCenter();
                            if (Math.abs(center.x + dimensHalf.x - x) > MIN_MOVEMENT
                                    || Math.abs(center.y + dimensHalf.y - y) > MIN_MOVEMENT) {
                                racket.setCenterY(y - dimensHalf.y);
                            }
                        }

                        break;
                    }

                    default:
                        Log.i(LOG_TAG, "unhandled action");
                }

                return true;
            }
        });
    }

    /**
     * Updates GameView
     */
    @Override
    protected void onUpdate() {
        float dt = getTimeDelta();
        ball.update(dt);
        activeRacket = (ball.getVelocity().x > 0) ? rightRacket : leftRacket;
    }

    /**
     * renders this 10px ball that flies away
     * @param canvas Game canvas
     */
    @Override
    protected void onRender(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        canvas.save();

        // for dimensions
        canvas.translate(dimensHalf.x, dimensHalf.y);

        drawPoints(canvas);
        drawNet(canvas);
        drawBall(canvas);
        drawRackets(canvas);

        canvas.restore();
    }

    /**
     * creates new surface for game
     * @param holder surface holder for game
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        dimens = new Vector2d(getWidth(), getHeight());
        dimensHalf = new Vector2d(0.5f * dimens.x, 0.5f * dimens.y);

        leftRacket.setCenter(new Vector2d(-dimensHalf.x * 0.8f, 0));
        rightRacket.setCenter(new Vector2d(dimensHalf.x * 0.8f, 0));

        ball.resetRandomly();

        super.surfaceCreated(holder);
    }

    /**
     * Draws that line that splits the screen
     * @param canvas App
     */
    void drawNet(Canvas canvas) {
        canvas.drawLine(0, -dimensHalf.y, 0, dimensHalf.y, paint);
    }

    /**
     * creates scoreboard and displays scores
     * @param canvas app
     */
    void drawPoints(Canvas canvas) {
        float quarterWidth = dimens.x * 0.25f;
        float quarterHeight = dimens.y * 0.25f;
        canvas.drawText(String.valueOf(leftPoints),
                -quarterWidth,
                -quarterHeight,
                textPaint);
        canvas.drawText(String.valueOf(rightPoints),
                quarterWidth,
                -quarterHeight,
                textPaint);
    }

    /**
     * draws a ball
     * @param canvas app
     */
    void drawBall(Canvas canvas) {
        ball.draw(canvas);
    }

    void drawRackets(Canvas canvas) {
        leftRacket.draw(canvas);
        rightRacket.draw(canvas);
    }


    private class Racket {
        private static final float SPEED = 1000f;
        private Vector2d center = new Vector2d(0, 0);
        private Vector2d size;

        /**
         * Size of a racket
         * @param size in vector
         */
        public Racket(Vector2d size) {
            this.size = size;
        }

        /**
         * gets center for collision purposes with the ball
         * @return center of the racket
         */
        public Vector2d getCenter() {
            return center;
        }

        /**
         * use with caution, this can brake tha game
         * @param center
         */
        public void setCenter(Vector2d center) {
            this.center = center;
        }

        /**
         * sets y center
         * @param y coordinate
         */
        public void setCenterY(float y) {
            center.y = y;
        }

        /**
         * calculates if ball and rackets intersect
         * @param otherRect rectangel of the ball
         * @return true/false
         */
        public boolean intersects(Rectangle otherRect) {
            Rectangle thisRect = Rectangle.fromCentered(center, size);
            return thisRect.intersects(otherRect);
        }

        /**
         * draws rackets
         * @param canvas app
         */
        public void draw(Canvas canvas) {
            float left = center.x - size.x;
            float top = center.y - size.y;
            float right = center.x + size.x;
            float bottom = center.y + size.y;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }


    private class Ball {
        Vector2d center;
        Vector2d velocity;
        float radius;

        static final float SPEED = 500f;

        /**
         * constructor for ball
         * @param radius radius with the ball
         */
        public Ball(float radius) {
            this.radius = radius;
        }

        /**
         * Updates location of the ball
         * @param dt position
         */
        public void update(float dt) {
            float dx = dt * velocity.x;
            float dy = dt * velocity.y;
            float margin = radius - Math.abs(velocity.x);

            if (center.x - margin < -dimensHalf.x) {
                resetRandomly();
            } else if (center.x + margin > dimensHalf.x) {
                resetRandomly();
            }

            if (Math.abs(center.y + dy) + radius > dimensHalf.y) {
                dy *= -1;
                velocity.y *= -1;
            } else if (activeRacketIntersects(new Vector2d(center.x + dx,
                    center.y + dy))) {
                dx *= -1;
                velocity.x *= -1;
            }

            if (center.x + dx - radius < -dimensHalf.x && center.x - radius >= -dimensHalf.x) {
                rightPoints++;
            } else if (center.x + dx + radius > dimensHalf.x && center.x + radius <= dimensHalf.x) {
                leftPoints++;
            }

            center.x += dx;
            center.y += dy;
        }

        /**
         * active check for intersection
         * @param center center for the game
         * @return
         */
        private boolean activeRacketIntersects(Vector2d center) {
            Rectangle rect = Rectangle.fromCentered(center, new Vector2d(
                    radius, radius));
            return activeRacket.intersects(rect);
        }

        public Vector2d getVelocity() {
            return velocity;
        }

        /**
         * Yeets tha ball in some direction, always into the loosing side
         */
        public void resetRandomly() {
            center = new Vector2d(0, (random.nextFloat() * dimens.y)
                    - dimensHalf.y);

            float angle = ((int) random.nextInt(4) + 0.5f) * HALF_PI;
            velocity = new Vector2d(SPEED * (float) Math.cos(angle), SPEED
                    * (float) Math.sin(angle));
        }

        public void draw(Canvas canvas) {
            float left = center.x - radius;
            float top = center.y - radius;
            float right = center.x + radius;
            float bottom = center.y + radius;
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private static class Rectangle {
        private Vector2d corner;
        private Vector2d size;

        public Rectangle(Vector2d corner, Vector2d size) {
            this.corner = corner;
            this.size = size;
        }

        public static Rectangle fromCentered(Vector2d center, Vector2d radius) {
            return new Rectangle(new Vector2d(center.x - radius.x, center.y
                    - radius.y), new Vector2d(2 * radius.x, 2 * radius.y));
        }

        public boolean intersects(Rectangle other) {
            return (this.corner.x + this.size.x >= other.corner.x)
                    && (other.corner.x + other.size.x >= this.corner.x)
                    && (this.corner.y + this.size.y >= other.corner.y)
                    && (other.corner.y + other.size.y >= this.corner.y);
        }
    }

    public static class Vector2d {
        public float x;
        public float y;

        public Vector2d(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
