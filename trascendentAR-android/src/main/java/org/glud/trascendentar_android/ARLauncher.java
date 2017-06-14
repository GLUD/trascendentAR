package org.glud.trascendentar_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.ArrayMap;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.assets.AssetHelper;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.glud.trascendentAR.ARToolKitManager;
import static org.artoolkit.ar.base.camera.CameraPreferencesActivity.TAG;

/**
 * Created by juan on 20/05/17.
 */
public abstract class ARLauncher extends AndroidApplication implements CameraEventListener, ARToolKitManager{
    public static class MarkerType{
        public static final String SINGLE="single";
		public static final String MULTI="multi";
    }

    private FrameLayout mainLayout;
    private CaptureCameraPreview preview;
    private View gameView;
    private boolean firstUpdate = false;
    private ArrayMap<String,Integer> markers = new ArrayMap<>();

    @Override
    public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config){
        //Las siguientes dos lines son necesaria para poder añadir markers de la manera que ARToolKit lo maneja
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(this, "Data");

        mainLayout = new FrameLayout(this);
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

        gameView = initializeForView(listener, config);

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

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
		//Añade las views al layout
		mainLayout.addView(gameView,layoutParams);
		mainLayout.addView(preview,layoutParams);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			configureARScene();
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

	abstract public void configureARScene();

	protected void loadMarker(String name, String type, String path, float size){
		int markerID = -1;
		markerID = ARToolKit.getInstance().addMarker(type+";"+path+";"+size);
		if(markerID >= 0) markers.put(name,markerID);
		else Log.i("ARActivity","Marker"+name+" not loaded");
	}




	@Override
	public boolean markerVisible(String markerName) {
		return ARToolKit.getInstance().queryMarkerVisible(markers.get(markerName));
	}

	@Override
	public ArrayMap<String, Integer> getMarkers() {
		return markers;
	}

	@Override
	public float[] getProjectionMatrix() {
		return ARToolKit.getInstance().getProjectionMatrix();
	}


	@Override
	public float[] getTransformMatrix(String markerName) {
		return ARToolKit.getInstance().queryMarkerTransformation(markers.get(markerName));
	}

	@Override
	public boolean arIsRunning() {
		return ARToolKit.getInstance().isRunning();
	}
}
