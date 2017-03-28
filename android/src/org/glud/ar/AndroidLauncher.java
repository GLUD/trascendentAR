package org.glud.ar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.artoolkit.ar.base.*;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;

import static org.artoolkit.ar.base.camera.CameraPreferencesActivity.TAG;

public class AndroidLauncher extends AndroidApplication implements CameraEventListener {

	private FrameLayout mainLayout;
	private CaptureCameraPreview preview;
	private View gameView;
	private boolean firstUpdate = false;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainLayout = new FrameLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.g = 8;
		config.r = 8;
		config.b = 8;
		config.a = 8;

		//Configuraciones basicas
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		gameView = initializeForView(new main(), config);
//		initialize(new main(),config);
		if(graphics.getView() instanceof SurfaceView){
			SurfaceView glView = (SurfaceView)graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}

		setContentView(mainLayout);
	}
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//		Log.i(TAG, "onStart(): Activity starting.");
//
//		if (ARToolKit.getInstance().initialiseNative(this.getCacheDir().getAbsolutePath()) == false) { // Use cache directory for Data files.
//
//			new AlertDialog.Builder(this)
//					.setMessage("The native library is not loaded. The application cannot continue.")
//					.setTitle("Error")
//					.setCancelable(true)
//					.setNeutralButton(android.R.string.cancel,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int whichButton) {
//									finish();
//								}
//							})
//					.show();
//
//			return;
//		}
//	}
//
	@Override
	protected void onPause() {
		super.onPause();
		mainLayout.removeView(gameView);
		mainLayout.removeView(preview);
	}
//
//	@Override
	protected void onResume() {
		super.onResume();
//		//Crea la view de la camara
		preview = new CaptureCameraPreview(this, this);

//		//Añade las views al layout

//		FrameLayout.LayoutParams params =
//				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//						FrameLayout.LayoutParams.WRAP_CONTENT);
//		params.addRule(FrameLayout.ALIGN_PARENT_TOP);
//		params.addRule(FrameLayout.ALIGN_PARENT_RIGHT);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);

		mainLayout.addView(gameView,params);
		mainLayout.addView(preview,params);
//
	}

	@Override
	public void cameraPreviewStarted(int i, int i1, int i2, int i3, boolean b) {

	}

	@Override
	public void cameraPreviewFrame(byte[] bytes) {

	}

	@Override
	public void cameraPreviewStopped() {

	}
//
//	@Override
//	public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
//		if (ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) { // Expects Data to be already in the cache dir. This can be done with the AssetUnpacker.
//			Log.i(TAG, "getGLView(): Camera initialised");
//		} else {
//			// Error
//			Log.e(TAG, "getGLView(): Error initialising camera. Cannot continue.");
//			finish();
//		}
//
//		Toast.makeText(this, "Camera settings: " + width + "x" + height + "@" + rate + "fps", Toast.LENGTH_SHORT).show();
//
//		firstUpdate = true;
//	}
//
//	@Override
//	public void cameraPreviewFrame(byte[] bytes) {
//	}
//
//	@Override
//	public void cameraPreviewStopped() {
//		ARToolKit.getInstance().cleanup();
//	}
}
