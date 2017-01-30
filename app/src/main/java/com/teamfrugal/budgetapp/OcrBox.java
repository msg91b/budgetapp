package com.teamfrugal.budgetapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Class:           OcrBox.java
 * Desc:            This class is used to draw a box on the screen and will support functions to
 *                  move and scale the box. Currently only move is implemented, but it has some
 *                  bugs that need to be worked out.
 * Related Layout:  N/A
 * Called from:     cameraActivity.java <---- Need to remove from cameraActivity.java and put inside
 *                                            cropImage.java instead.
 * Calls:           N/A
 */

public class OcrBox extends View {
    private Paint box = new Paint();
    private static int n = 0;
    private static int offsetX=0;
    private static int offsetY=0;
    private float mLastTouchX=0;
    private float mLastTouchY=0;
    private int mActivePointerId;
    private int leftx, rightx, bottomy, topy;
    OcrBox(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                final int pIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pIndex);
                final float y = MotionEventCompat.getY(event, pIndex);


                if (x > leftx && x < rightx && y < topy && y > bottomy) {
                    System.out.println("inside");
                    mLastTouchX = x;
                    mLastTouchY = y;

                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                }
                else {
                    return false;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

                final float x = MotionEventCompat.getX(event, pIndex);
                final float y = MotionEventCompat.getY(event, pIndex);

                System.out.println("mLastTouchX = " + mLastTouchX);
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                offsetX = (int) dx;
                offsetY = (int) dy;

                invalidate();


               // if (mLastTouchX != 0) {
                 //   mLastTouchX = x;
                   // mLastTouchY = y;
               // }

                break;
            }
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        box.setStyle(Paint.Style.STROKE);
        box.setColor(Color.WHITE);
        box.setStrokeWidth(10);

        //center of ocrBox
        int centerX = canvas.getWidth()/2 + offsetX;
        int centerY = canvas.getHeight()/2 + offsetY;

        //System.out.println("offsetX: " + offsetX);

        //corners of ocrBox

        leftx = centerX - canvas.getWidth()/4;
        rightx = centerX + canvas.getWidth()/4;
        topy = centerY + canvas.getHeight()/30;
        bottomy = centerY - canvas.getHeight()/30;

        //Top left
        canvas.drawLine(leftx, bottomy, leftx+50, bottomy, box);
        canvas.drawLine(leftx, bottomy-5, leftx, bottomy+50, box);

        //Top right
        canvas.drawLine(rightx, bottomy, rightx-50, bottomy, box);
        canvas.drawLine(rightx, bottomy-5, rightx, bottomy+50, box);

        //Bottom left
        canvas.drawLine(leftx, topy, leftx+50, topy, box);
        canvas.drawLine(leftx, topy+5, leftx, topy-50, box);

        //Bottom right
        canvas.drawLine(rightx, topy, rightx-50, topy, box);
        canvas.drawLine(rightx, topy+5, rightx, topy-50, box);
    }

    public void getCorners(int corners[]) {
        corners[0] = leftx;
        corners[1] = rightx;
        corners[2] = topy;
        corners[3] = bottomy;
    }
}
