
import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;
import static org.opencv.android.Utils.matToBitmap;


// https://www.learn2crack.com/2016/03/setup-opencv-sdk-android-studio.html
// requires openCVLibrary 3.20

/* 	the main activity class must have the code below in order to load openCV
	else the openCV library may not be accessible when opencv methods are called

	static final String TAG = "mainActivity";
	static {

		if(!OpenCVLoader.initDebug()){
			Log.d(TAG, "OpenCV not loaded");
			// handle this here
		} else {
			Log.d(TAG, "OpenCV loaded");
		}
	}

/*

// IMPORTANT!! path must be where the tessdata directory is located at,
// path to trained data -> 		some_path_/budgetapp/tessdata/eng.traineddata
// path that is needed - >	 	some_path/budgetapp


// In order to read in an image the tesseract instance needs to be created.
// To create an instance you need to call the init function or use the two parameter constructor
// when making extract calls make sure that they are being called on a thread!
// the read type (readNum, readWord) call is set once. if a change is needed the tesseract object needs to be
// reinitalized. The reset method does this.

/*	example

	//  ocr extraction thread
	private class TessWorker extends AsyncTask<Void,Void,String>{
		@Override
		protected String doInBackground(Void ... x){
			String result = "error";
			try{
				// specify num or word reading outside thread
				return tes.extract(image);
			} catch (Exception e){
				return result;
			}
		}
		protected void onProgressUpdate(){}
		@Override
		protected void onPostExecute(String result){ // change where you place output result
			textview.setText(result);
		}
	}



	OCR x = new OCR();
	x.init(path, "eng");	// set new tesseract instance
	x.readNum();			// interpret somePic as containing characters only
	new TessWorker().execute();

	OR

	OCR x = new OCR(path, "eng");
	x.readWord();
	new TessWorker().execute();
*/


public class OCR {
	private TessBaseAPI tes = null;
	private String path;
	private String langs;
	private Bitmap image = null;
	private final int BLUR_TOLERANCE = 1; // needs tests
	private final boolean DEBUG = false;

	OCR(){}
	OCR(String p, String l){
		path = p;
		langs = l;
		init(p,l);
	}

	private void toGrayscale(){
		Mat m = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(image, m);
		Imgproc.cvtColor(m,m, Imgproc.COLOR_RGB2GRAY);
		matToBitmap(m, image);
	}

	//http://www.pyimagesearch.com/2015/09/07/blur-detection-with-opencv/
	//http://stackoverflow.com/questions/7765810/is-there-a-way-to-detect-if-an-image-is-blurry/7768918#7768918
	// needs changes not fully functional, may hang phone
	public double testBlur(){
		Mat m = new Mat(image.getWidth(), image.getHeight(), CvType.CV_64F);
		Utils.bitmapToMat(image, m);
		Mat test = new Mat();
		Imgproc.Laplacian(m, test, CvType.CV_64F);
		MatOfDouble mu = new MatOfDouble(), sig = new MatOfDouble();
		org.opencv.core.Core.meanStdDev(test, mu, sig);
		double t = sig.get(0,0)[0];
		return t * t;
	}
	public boolean isBlur(){
		return testBlur() < BLUR_TOLERANCE;
	}


	// resizes the bitmap if too large
	//http://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		//
		//	test if bm is already at an ok size???
		//
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		bm.recycle();
		return resizedBitmap;
	}

	// once a read type has been called, it is only allowed to use that read type
	// a reset is needed if you want read in words if tesseract as been set to read numbers
	public void reset(){
		init(path, langs);
	}

	// sets tesseract to read in words only
	public OCR readWord(){
		if(tes == null)
			return null;
		return tes.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "1234567890") ? this : null;
	}

	// sets tesseract to read in numbers only
	public OCR readNum(){
		if(tes == null)
			return null;
		return tes.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890,.$") ? this : null;
	}

	// this function initializes the tesseract object
	// set the path for the trained langage and speciy the language(s)
	public void init(String langPath, String langs){
		tes = new TessBaseAPI();
		tes.init(langPath, langs);
	}

	// interprets the bitmap as text, call on a thread!
	public String extract(Bitmap e){
		image = e;
		toGrayscale();
		tes.setImage(image);
		return tes.getUTF8Text();
	}

	public void getBoxes(){
		//tes.getBoxText();
	}

	// 100 - 0, 100 is very confident, 0 is very low confidence
	public int getConfidence(){
		return tes == null ? -1 : tes.meanConfidence();
	}
}
