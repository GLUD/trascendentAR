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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.artoolkit.ar.base.*;
import org.artoolkit.ar.base.assets.AssetHelper;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;

import static org.artoolkit.ar.base.camera.CameraPreferencesActivity.TAG;

public class AndroidLauncher extends AndroidApplication implements CameraEventListener, ARToolKitManager {

	private FrameLayout mainLayout;
	private CaptureCameraPreview preview;
	private View gameView;
	private boolean firstUpdate = false;
	int marcadorId = -1;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Las siguientes dos lines son necesaria para poder añadir marcadores de la manera que ARToolKit lo maneja
		AssetHelper assetHelper = new AssetHelper(getAssets());
		assetHelper.cacheAssetFolder(this, "Data");

		mainLayout = new FrameLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;

		//Configuraciones basicas
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		gameView = initializeForView(new main(this), config);

		if(graphics.getView() instanceof SurfaceView){
			SurfaceView glView = (SurfaceView)graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}

		setContentView(mainLayout);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart(): Activity starting.");

		if (ARToolKit.getInstance().initialiseNative(this.getCacheDir().getAbsolutePath()) == false) { // Use cache directory for Data files.

			new AlertDialog.Builder(this)
					.setMessage("The native library is not loaded. The application cannot continue.")
					.setTitle("Error")
					.setCancelable(true)
					.setNeutralButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									finish();
								}
							})
					.show();

			return;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mainLayout.removeView(gameView);
		mainLayout.removeView(preview);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Crea la view de la camara
		preview = new CaptureCameraPreview(this, this);

		//Añade las views al layout
		mainLayout.addView(preview,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mainLayout.addView(gameView,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}

	@Override
	protected void onStop() {
		super.onStop();
		ARToolKit.getInstance().cleanup();
	}

	@Override
	public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
		if (ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) { // Expects Data to be already in the cache dir. This can be done with the AssetUnpacker.
			Log.i(TAG, "getGLView(): Camera initialised");
		} else {
			// Error
			Log.e(TAG, "getGLView(): Error initialising camera. Cannot continue.");
			finish();
		}

		Toast.makeText(this, "Camera settings: " + width + "x" + height + "@" + rate + "fps", Toast.LENGTH_SHORT).show();

		firstUpdate = true;
	}

	@Override
	public void cameraPreviewFrame(byte[] frame) {
		if(this.firstUpdate) {
			if(this.configureARScene()) {
				Log.i("ARActivity", "Scene configured successfully");
			} else {
				Log.e("ARActivity", "Error configuring scene. Cannot continue.");
				this.finish();
			}

			this.firstUpdate = false;
		}

		if(ARToolKit.getInstance().convertAndDetect(frame)) {
			Log.i("ARActivity", "Convert and detect frame");

			this.onFrameProcessed();
		}
	}

	public void onFrameProcessed() {
	}



	@Override
	public void cameraPreviewStopped() {
		ARToolKit.getInstance().cleanup();
	}


	@Override
	public boolean marcadorVisible(int marcadorId) {
		return ARToolKit.getInstance().queryMarkerVisible(marcadorId);
	}

	private boolean configureARScene() {
		marcadorId = ARToolKit.getInstance().addMarker("single;Data/hiro.patt;80");
		Log.d(TAG,"Marcador ID = "+marcadorId);
		if(marcadorId < 0){
			Log.e(TAG,"marcador no cargado");
		}else {
			Log.i(TAG,"marcador cargado");
			return true;
		}
		return false;
	}
	public int cargarMarcador(String config) {
		return ARToolKit.getInstance().addMarker(config);
	}

	@Override
	public int obtenerMarcador(){
		return marcadorId;
	}
}
