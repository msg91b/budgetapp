
// I need to clean my tesseract code from last semster but I have some code that
// allows a phone to take a picture, crop the picture and send the crop
// picture to a server that scans and sends the result to the phone

// AndroidManifest.xml stuff
// allows the app to be installed partially on interal storage and stores
// the rest on the sd card. Test app was around 40mb, with the preference
// the app is 7mb on interal storage and the rest is on the sd card.
// It also allows users to switch where the app is stored
<manifest ... android:installLocation="preferExternal">	

<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	

public class MainActivity extends AppCompatActivity{
	
	///....
	private byte [] imgByteArray;// cropped image as a array of bytes
	
	// Need to create thread to handle socket handling
	// AsyncTask is slower compared to using threads???
	private class ClientThread extends AsyncTask<Void,Void,String>{
		@Override
		protected String doInBackground(Void ... x){
			String result = "error";
			try{
				Client user = new Client();
				user.connect("136.168.201.100",43281);
				user.sendImage(imgByteArray);
				user.close();
				return user.getResult();
			} catch (Exception e){
				StringWriter s = new StringWriter();
				PrintWriter p = new PrintWriter(s);
				e.printStackTrace(p);
				Log.d("error:", s.toString());
				return result;
			}
		}
		protected void onProgressUpdate(){}
		@Override
		protected void onPostExecute(String result){
			textview.setText(result);
		}
	}

	// This code handles the default android activities for cropping and taking a picture
	@Override
	public void onActivityResult(int code, int result, Intent act)
	{
		// camera activity returned, start crop activity 
		if(code == 1 && result == Activity.RESULT_OK){
			//Toast.makeText(getApplicationContext(), "crop started", Toast.LENGTH_SHORT).show();
			Log.d("test", "crop!");
			cam.performCrop();
		}
		// crop completed, store result in sd card and send to server
		// creates thead and listens for server's reply
		else if(code == 2 && result == Activity.RESULT_OK) {
			Bundle extra = act.getExtras();
			Bitmap cropImg = extra.getParcelable("data");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			cropImg.compress(Bitmap.CompressFormat.PNG, 100, out);
			this.imgByteArray = out.toByteArray();

			String path = Environment.getExternalStorageDirectory().toString() + "/budgetapp/imgs/";
			File img = new File(path + "crop.jpg");
			try {
				FileOutputStream w = new FileOutputStream(img);
				w.write(imgByteArray);
				w.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			new ClientThread().execute();
		}
		// error!
		else if(code == 1 || code == 2)
			Log.d("error", "image not saved!");
	}
	
	//....
}



public class Camera{
	private MainActivity main;
	private Uri outputFileUri;
	private Image pic;

	// MainActivity class must be the activity that launches camera/crop
	Camera(MainActivity main){
		this.main = main;
	}

	public void performCrop() {
		try {

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(outputFileUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 2);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 256);//image output resolution
			cropIntent.putExtra("outputY", 256);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			//cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile);
			// start the activity - we handle returning in onActivityResult
			main.startActivityForResult(cropIntent, 2);
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			Log.d("error", "This device doesn't support the crop action!");
		}
	}


	/* 	starts the default camera activity. 
	* - depending on the phone's manufacturer the image can be saved both in the
	* 	SD card or in the gallery. Need to handle cases were the image is sent
	* 	to the gallery
	* - This code assumes external storage is available 
	*/
	public void takePic()
	{
		// creating path to store image, SD card
		String path = Environment.getExternalStorageDirectory().toString() + "/budgetapp/imgs/";
		System.out.println(path);
		// creating directory for path
		File dir = new File(path);
		if(!dir.exists()) {
			if(!dir.mkdirs()) {
				Log.d("error", "Error with creating image dir/path!");
				return;
			}
		}
		else {
			//Toast.makeText(getApplicationContext(), "Image ready to go!", Toast.LENGTH_LONG).show();
		}
		
		// create image file name "pic.jpg" and tie to output URI
		outputFileUri = Uri.fromFile(new File(path + "/pic.jpg"));
		
		// create camera activity
		Intent camActivity = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// camera activity stores result in URI output
		camActivity.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); 
		if(camActivity.resolveActivity(main.getPackageManager()) != null) {
			main.startActivityForResult(camActivity, 1);
		}
		else
			Log.d("error", "phone cam support is not available");
	}
}



// socket connection class
// connects to a server running on sleipnir
// The class can send image data as a byte array to the server.
// If the server is able to scan the image it returns a the result
// else it returns an empty string.
public class Client {

	private Socket connection;
	private String result;

	Client(){}

	public void connect(String ip, int port){
		try {
			connection = new Socket(ip, port);
		} catch(Exception e){
			StringWriter s = new StringWriter();
			PrintWriter p = new PrintWriter(s);
			e.printStackTrace(p);
			Log.d("error:", s.toString());
		}
	}

	public void sendImage(byte [] data){
		if(connection == null)
			return;
		try {
			DataInputStream in = new DataInputStream(connection.getInputStream());
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeInt( data.length);
			out.write(data, 0, data.length);
			result = in.readUTF();
			System.out.println("result: " + result);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getResult(){
		if(connection == null)
			return null;
		return result;
	}

	public void close(){
		try {
			if(connection == null)
				return;
			connection.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

// non android code
// server class that creates a socket and listens for incoming connections
// each connection gets handled by a thread
public class Server {
	private ServerSocket listen;
	private int port;

	Server() {}
	Server(int port){
		this.port = port;
	}

	public void host(){
		try {
			listen = new ServerSocket(port);
			int numClients = 0;
			boolean run = true;
			while(run){
				Socket client = listen.accept();
				if(numClients == Integer.MAX_VALUE)
					numClients = -1;
				numClients++;
				Thread newclient = new Thread(new HandleClient(client, numClients));
				newclient.start();
			}
			this.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void close(){
		if(listen == null)
			return;
		try {
			listen.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}


// non android code
// HandleClient is used to handle individual client requests
// to scan an image they send over TCP the image is stored in a file "img<ClientNumber>"
// ClientNumber is used to distinguish the image file for each thread
public class HandleClient implements Runnable{

	private Socket client;
	private int number;

	// reads a process's stdout from input stream as a string
	//http://stackoverflow.com/questions/16714127/how-to-redirect-process-builders-output-to-a-string
	private static String StreamToString(InputStream in){
		StringBuilder result = new StringBuilder();
		try{
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String buffer = null;
			while((buffer = read.readLine()) != null)
				result.append(buffer);// + System.getProperty("line.separator"));
			read.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}

	// waits for the client to send its image then processes the image and
	// sends the Tesseract scan result back to the client
	// TODO -- add preprocessing blur, sharpening techniques
	public void run(){
		System.out.println("new client: " + number);
		try {

			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			// receive image from client
			int bsize = in.readInt();
			System.out.println("img size: " + bsize);

			byte [] buffer = new byte[bsize];
			// don't use read()!!! doesn't guarantee, readFully() does!
			in.readFully(buffer, 0, bsize);

			// save img for processing
			File local = new File("img" + number);

			FileOutputStream imgFile = new FileOutputStream(local.getPath());
			imgFile.write(buffer);
			imgFile.close();


			// preprocess image??
	

			// create tesseract process and send in new image
			ProcessBuilder buildtessproc = new ProcessBuilder(
					"tesseract", "img" + number, "stdout");//log"+number);
			Long time = System.nanoTime();
			Process tessproc = buildtessproc.start();
			int success = tessproc.waitFor();
			time = (System.nanoTime() - time)/1000000;
			if(success != 0) {
				System.out.print("--! Error! process failed");
				return;
			}

			String result = StreamToString(tessproc.getInputStream());
			System.out.println("process time: " + time + "ms Result: " + result);

			// send tesseract result to client
			out.writeUTF(result);
			
			// cleanup
			out.close();
			in.close();
			client.close();

			// delete img
			local.delete();

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	HandleClient(Socket client){
		this.client = client;
	}
	HandleClient(Socket client, int clientNum) {
		this.client = client;
		this.number = clientNum;
	}
}




