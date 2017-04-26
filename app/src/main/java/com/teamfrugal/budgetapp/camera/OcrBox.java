package com.teamfrugal.budgetapp.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.database.DataAccess;
import com.teamfrugal.budgetapp.database.ListContent;
import com.teamfrugal.budgetapp.ui.AddTransactionActivity;
import com.teamfrugal.budgetapp.ui.CropActivity;
import com.teamfrugal.budgetapp.ui.box;
import com.teamfrugal.budgetapp.ui.quote.ListActivity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.view.MotionEvent.INVALID_POINTER_ID;
import static com.teamfrugal.budgetapp.database.ListContent.addItem;
import static java.lang.Math.pow;

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
    private Paint b[][] = new Paint[2][4];
    private static int offsetX[]=new int[2];
    private static int offsetY[]=new int[2];
    private float mLastTouchX=0;
    private float mLastTouchY=0;
    private int mActivePointerId=INVALID_POINTER_ID;
    private int centerX[][] = new int[2][4], centerY[][]=new int[2][4];
    private Bitmap image;
    private int active=-1;
    TextView sText, aText;
    OCR myOCR;
    int pos[][]=new int[2][2];

    private int scaleXdir[][] = new int[2][4];
    private int scaleYdir[][] = new int[2][4];

    private box boxes[] = new box[2];
    private Point corners[][] = new Point[2][4];

    int centerXScaled[][] = new int[2][4];
    int centerYScaled[][] = new int[2][4];

    int posCTL[][] = new int[2][2];
    int posCTR[][] = new int[2][2];
    int posCBL[][] = new int[2][2];
    int posCBR[][] = new int[2][2];

    int colorChange = -1;

    ProgressBar mp;
    int sPressed, aPressed;

    public OcrBox(final CropActivity context, box[] box, Bitmap image, OCR myOCR, final TextView sText,
                  final TextView aText, ProgressBar mProgressBar, FloatingActionButton sButton,
                  FloatingActionButton aButton) {
        super(context);

        mLastTouchX=0;
        mLastTouchY=0;


        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                b[i][j] = new Paint();
            }
        }
        this.boxes = box;

        for (int i = 0; i < 2; i++) {
            corners[i][0] = new Point(box[i].origin[0] - 50, box[i].origin[1] - 50);
            corners[i][1] = new Point(box[i].origin[0] + 50, box[i].origin[1] - 50);
            corners[i][2] = new Point(box[i].origin[0] - 50, box[i].origin[1] + 50);
            corners[i][3] = new Point(box[i].origin[0] + 50, box[i].origin[1] + 50);
        }
        this.image = image;
        this.myOCR = myOCR;
        this.sText = sText;
        this.aText = aText;

        this.mp = mProgressBar;

        sButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                sPressed = 1;
                if (aPressed == 1)
                    finish(context);
            }
        });
        aButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                aPressed = 1;
                if (sPressed == 1)
                    finish(context);
            }
        });
    }

    void finish(final CropActivity context) {
        //addItem(new DummyContent.DummyItem("6", R.drawable.p5, sText.getText().toString(), aText.getText().toString()));
        addItem(new ListContent.Item(0, ListContent.randPhotoId(), sText.getText().toString(), Double.parseDouble(aText.getText().toString())));
        //context.startActivity(new Intent(context, ListActivity.class));
        context.startActivity(new Intent(context, AddTransactionActivity.class));
        context.finish();
    }

    int isInsideCorner(int x, int y, int i) {
        if (pow(30, 2) > pow(x - corners[i][3].x, 2) + pow(y - corners[i][3].y, 2)) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                final int pIndex = 0;
                final float x = MotionEventCompat.getX(event, pIndex);
                final float y = MotionEventCompat.getY(event, pIndex);

                colorChange = isInsideCorner((int)x,(int)y, 0);
                if (colorChange==1) {
                    active = 0;
                } else {
                    colorChange = isInsideCorner((int)x,(int)y,1);
                    if(colorChange == 1) {
                        active = 1;
                    }
                }

                if(colorChange == 1) {
                    mLastTouchX = x;
                    mLastTouchY = y;

                    mActivePointerId = MotionEventCompat.getPointerId(event, pIndex);
                } else if (x > corners[0][0].x && x < corners[0][1].x
                        && y > corners[0][0].y && y < corners[0][2].y) {
                    mLastTouchX = x;
                    mLastTouchY = y;
                    mActivePointerId = MotionEventCompat.getPointerId(event, pIndex);
                    active=0;
                } else if (x > corners[1][0].x && x < corners[1][1].x
                        && y > corners[1][0].y && y < corners[1][2].y) {
                    mLastTouchX = x;
                    mLastTouchY = y;

                    mActivePointerId = MotionEventCompat.getPointerId(event, pIndex);
                    active=1;
                } else{
                    active=-1;
                    return false;
                }
            }

            case MotionEvent.ACTION_MOVE: {
                final int pIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

                final float x = MotionEventCompat.getX(event, pIndex);
                final float y = MotionEventCompat.getY(event, pIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                if (colorChange == 1) {
                    scaleXdir[active][3] += (int) dx;
                    scaleXdir[active][2] += (int) dx;
                    scaleXdir[active][1] += (int) dx;

                    scaleYdir[active][3] += (int) dy;
                    scaleYdir[active][2] += (int) dy;
                    scaleYdir[active][1] += (int) dy;
                }
                else if (active != -1) {
                    offsetX[active] += (int) dx;
                    offsetY[active] += (int) dy;
                }
                invalidate();

                mLastTouchX = x;
                mLastTouchY = y;

                return true;
            }

            case MotionEvent.ACTION_UP: {
                colorChange = -1;
                if (active != -1)
                    applyOCR();
                active=-1;
            }
        }
        return false;
    }


    protected void drawCorner(Canvas canvas, int i, int j, Point c) {
        if (j == 1 || j == 3) { //right
            canvas.drawLine((float) (c.x + 20), (float) (c.y - 22),
                    (float) (c.x + 20), (float) (c.y + 22), b[i][j]);
        }
        if (j == 0 || j == 2) {//left
            canvas.drawLine((float) (c.x-20), (float) (c.y-22),
                    (float) (c.x-20), (float) (c.y+22), b[i][j]);
        }
        if (j > 1) { //bottom
            canvas.drawLine((float) (c.x-20), (float)(c.y+20),
                    (float) (c.x+20), (float)(c.y+20), b[i][j]);


        }
        if (j < 2) { //top
            canvas.drawLine((float) (c.x - 20), (float) (c.y - 20),
                (float) (c.x + 20), (float) (c.y - 20), b[i][j]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < 2; i++) {
            if (boxes[i].once == 0) {
                System.out.println("boxes once");
                pos[i][0] = boxes[i].origin[0];
                pos[i][1] = boxes[i].origin[1];

                posCTL[i][0] = corners[i][0].x;
                posCTL[i][1] = corners[i][0].y;

                posCTR[i][0] = corners[i][1].x;
                posCTR[i][1] = corners[i][1].y;

                posCBL[i][0] = corners[i][2].x;
                posCBL[i][1] = corners[i][2].y;

                posCBR[i][0] = corners[i][3].x;
                posCBR[i][1] = corners[i][3].y;
                boxes[i].once++;
            }

            super.onDraw(canvas);

            int left=2, right=1;

            centerX[i][0] = posCTL[i][0] + offsetX[i];
            centerX[i][1] = posCTR[i][0] + offsetX[i];
            centerX[i][2] = posCBL[i][0] + offsetX[i];
            centerX[i][3] = posCBR[i][0] + offsetX[i];

            centerY[i][0] = posCTL[i][1] + offsetY[i];
            centerY[i][1] = posCTR[i][1] + offsetY[i];
            centerY[i][2] = posCBL[i][1] + offsetY[i];
            centerY[i][3] = posCBR[i][1] + offsetY[i];

            centerXScaled[i][0] = centerX[i][0] + scaleXdir[i][0];
            centerXScaled[i][1] = centerX[i][1] + scaleXdir[i][1];
            centerXScaled[i][2] = centerX[i][2] + scaleXdir[i][2];
            centerXScaled[i][3] = centerX[i][3] + scaleXdir[i][3];

            centerYScaled[i][0] = centerY[i][0] + scaleYdir[i][0];
            centerYScaled[i][1] = centerY[i][1] + scaleYdir[i][1];
            centerYScaled[i][2] = centerY[i][2] + scaleYdir[i][2];
            centerYScaled[i][3] = centerY[i][3] + scaleYdir[i][3];

            // only works for 1080p screen currently. Not exactly sure how to go about supporting
            // different resolutions just yet.
            if (centerX[i][0] > 25 && centerX[i][1] < 1055) {
                if (colorChange == 1) {
                    corners[i][3].x = centerXScaled[i][3];
                    corners[i][right].x = centerXScaled[i][1];
                } else if (active != -1) {
                    corners[i][0].x = centerXScaled[i][0];
                    corners[i][1].x = centerXScaled[i][3];
                    corners[i][2].x = centerXScaled[i][0];
                    corners[i][3].x = centerXScaled[i][3];
                }
            }

            if (centerY[i][0] > 100 && centerY[i][2] < 1500) {
                if (colorChange == 1) {
                    corners[i][3].y = centerYScaled[i][3];
                    corners[i][left].y = centerYScaled[i][2];
                } else if (active != -1) {
                    corners[i][0].y = centerYScaled[i][0];
                    corners[i][1].y = centerYScaled[i][0];
                    corners[i][2].y = centerYScaled[i][3];
                    corners[i][3].y = centerYScaled[i][3];
                }
            }

            for (int j = 0; j < 4; j++) {
                b[i][j].setStyle(Paint.Style.STROKE);
                b[i][j].setColor(boxes[i].color);
                b[i][j].setStrokeWidth(5);
                if (colorChange == 1)
                    b[active][3].setColor(Color.RED);

                drawCorner(canvas, i, j, corners[i][j]);
            }
        }
    }

    void applyOCR() {
        Bitmap storeClip;
        Point s = corners[active][0];
        Point e = corners[active][3];

        storeClip = Bitmap.createBitmap(image, s.x - 15, s.y + 25, e.x - s.x + 27, e.y - s.y + 23);

        File f = new File(Environment.getExternalStorageDirectory() + "/sub" + active + ".jpeg");
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            storeClip.compress(Bitmap.CompressFormat.JPEG, 100, b);
            FileOutputStream ff = new FileOutputStream(f);
            ff.write(b.toByteArray());
            ff.close();
        } catch (IOException err) {
            err.printStackTrace();
        }
        TessBaseAPI mTess = myOCR.getApi();
        myOCR.reset();
        TextView text;

        if (active == 0) {
            text = sText;
        } else {
            text = aText;
        }
        new ocrInterface(getContext(), mTess, storeClip,text, sText, aText, active, mp).execute();
    }
}