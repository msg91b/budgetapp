package com.teamfrugal.budgetapp.camera;

import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.client.Client;
import com.teamfrugal.budgetapp.dummy.DummyContent;
import com.teamfrugal.budgetapp.ui.SettingsActivity;
import com.teamfrugal.budgetapp.ui.quote.ListActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.teamfrugal.budgetapp.camera.ImgUtil.createBinary;
import static com.teamfrugal.budgetapp.dummy.DummyContent.addItem;
import static com.teamfrugal.budgetapp.ui.SettingsActivity.KEY_OCR_MODE;

/**
 * Created by Matthew on 3/23/2017.
 */


public class ocrInterface extends AsyncTask<Void, Integer, String> {
    private TessBaseAPI mTess;
    private Bitmap inBitmap = null;
    private Bitmap outBitmap = null;
    private String result;
    private TextView text;
    private TextView sText;
    private TextView aText;
    private Context c;

    // Mode == 0 for Phone Processing
    // MODE == 1 for Server Processing
    //private int MODE = 0;
    private int mode;
    ProgressBar mp;
    int sPressed, aPressed;
    ocrInterface(Context cc, TessBaseAPI mTess, Bitmap bitmap, TextView text, TextView sText,
                 TextView aText, int reading, ProgressBar mp) {
        this.c = cc;
        this.mTess = mTess;
        this.inBitmap = bitmap;
        this.text = text;
        this.sText = sText;
        this.aText = aText;

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(cc);
        //SharedPreferences.Editor editor = mSharedPreferences.edit();
        //editor.putString("KEY_OCR_MODE", "grr");
        mode = Integer.parseInt(mSharedPreferences.getString(KEY_OCR_MODE, "0"));

        System.out.println("Mode <----> " + mode);

        if (reading == 0) readWord(); else readNum();
        this.mp = mp;
    }

    @Override
    protected String doInBackground(Void ... x) {
        int pro = 0;
        publishProgress(pro++);

        //pre-process bitmap in this thread
        outBitmap = createBinary(inBitmap);

        File f = new File(Environment.getExternalStorageDirectory() + "/binary.jpeg");
        try {
            f.createNewFile();
            FileOutputStream ff = new FileOutputStream(f);
            outBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ff);
            ff.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (mode == 0) {
            result = extract(outBitmap);
        }

        if (mode == 1) {
            try{
                Client user = new Client();
                user.connect("136.168.201.100",43281); // sleipnir host
                user.sendImage(ImgUtil.bitmap2byte(outBitmap));
                String serverReply = user.getResult();
                user.close();
                result = serverReply;
            } catch (Exception e){
                StringWriter s = new StringWriter();
                PrintWriter p = new PrintWriter(s);
                e.printStackTrace(p);
                Log.d("error:", s.toString());
                return null;
            }
        }
        publishProgress(pro);
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer ... x) {
        if (x[0] == 0)
            mp.setVisibility(View.VISIBLE);
        else
            mp.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onPostExecute(String result) {
        text.setText(result);
    }

    // interprets the bitmap as text, call on a thread!
    public String extract(Bitmap e){
        mTess.setImage(e);
        return mTess.getUTF8Text();
    }

    // returns the interpret image when scanned, useful for testing why results are bad
    public Bitmap GetThresholdedImage(Bitmap e) {
        if (mTess == null)
            return null;
        mTess.setImage(e);
        return WriteFile.writeBitmap(mTess.getThresholdedImage());
    }

    // 100 - 0, 100 is very confident, 0 is very low confidence
    public int getConfidence(){
        return mTess == null ? -1 : mTess.meanConfidence();
    }

    // sets tesseract to read in words only
    public ocrInterface readWord(){
        System.out.println("readword");
        if(mTess == null)
            return null;
        return mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "1234567890") ? this : null;
    }

    // sets tesseract to read in numbers only
    public ocrInterface readNum(){
        System.out.println("readnum");
        if(mTess == null)
            return null;
        return mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890.$") ? this : null;
    }


}





















