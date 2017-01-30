package com.teamfrugal.budgetapp;

/**
 * Created by Admin on 9/23/2016.
 */
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class:           OCR
 * Desc:            This class is used to interface with the OCR library. It also uses functions
 *                  found on a tutorial to put the eng.traineddata in place if it's missing.
 * Related layout:  N/A
 * Called from:     cameraOCR.java
 * Calls:           N/A
 */


public class OCR {
    private TessBaseAPI mTess;
    private String datapath = "";
    private AssetManager assets;
    public OCR(File location, AssetManager assets) {
        this.assets = assets;
        mTess = new TessBaseAPI();
        datapath = location + "/tesseract/";
        String language = "eng";

        //checkFile(new File(datapath + "tessdata/"));
        File tessdata = new File(datapath + "tessdata/");
        checkFile(tessdata);

        mTess.init(datapath, language);
    }

    void setBitmap(Bitmap b) {
        mTess.setImage(b);
    }

    void setImage(byte[] data, int width, int height, int bpp) {
        mTess.setImage(data, width, height, bpp, bpp*width);
    }

    String getOCRResult() {
        return mTess.getUTF8Text();
    }

    int getOCRConfidence() {
        return mTess.meanConfidence();
    }

    /* The following two methods are from this website.
     * http://imperialsoup.com/2016/04/29/simple-ocr-android-app-using-tesseract-tutorial/
     */
    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = assets;

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
