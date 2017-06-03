package org.glud.ar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.*;
import org.glud.trascendentAR.ARCamera;
import org.glud.trascendentAR.ARToolKitManager;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import static com.badlogic.gdx.Gdx.gl;

public class main extends ApplicationAdapter {

	private final static String TAG = "AR Application";

	AssetManager manager;
	boolean assetsLoaded = false;

	ARToolKitManager arToolKitManager;
	ARCamera camera;
	ModelBatch batch_3d;
	Environment environment;
	ArrayMap<String,ModelInstance> instances;
	Array<AnimationController> animationControllers;

	Matrix4 transform = new Matrix4();

	Stage stage;
	Image splash_img;

	public main(ARToolKitManager arToolKitManager){
		this.arToolKitManager = arToolKitManager;
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		//Configurar cámara de libGDX
		camera = new ARCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(0f,0f,1f);
		camera.lookAt(0,0,0);
		camera.near = 0;
		camera.far = 1000f;
		camera.update();

		/*
		 * CARGAR RECURSOS
		 * LOAD ASSETS
		 */
		manager = new AssetManager();
		manager.load("splash.png", Texture.class);
		manager.finishLoading(); //Esperar hasta que carge la imagen de splash - Wait until splash image load
		/* Añade los modelos para ser cargados.
		 * Note que los modelos no se cargan inmediatamente, se cargan utilizando manager.update() en el metodo render
		 * Add the models for loading.
		 * Note that models are not loaded inmediately, they load using manager.update() on render method
		 */
		manager.load("wolf.g3db",Model.class);
		manager.load("koko.g3db",Model.class);
		manager.load("watercraft.g3db",Model.class);
		manager.load("landscape.g3db",Model.class);
//		manager.load("houses.g3db",Model.class);

		batch_3d = new ModelBatch();

		//Adding lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		//2D
		stage = new Stage(new ScreenViewport());
		splash_img = new Image(manager.get("splash.png",Texture.class));
		stage.addActor(splash_img);
	}

	private void createModelsinstances(){
		/*
		 * Crear instacias de los modelos.
		 * Los nombres que se asignen al modelo deben coincidir con los declarados en AndroidLauncher
		 *
		 * Create model instances.
		 * Names given her, must match with the names declared on AndroidLauncher
		 */
		instances = new ArrayMap<String, ModelInstance>();
		Model model;

		model = manager.get("wolf.g3db",Model.class);
		instances.put("wolfMarker",new ModelInstance(model));
		model = manager.get("koko.g3db",Model.class);
		instances.put("kokoMarker",new ModelInstance(model));
		model = manager.get("watercraft.g3db",Model.class);
		instances.put("watercraftMarker",new ModelInstance(model));
		model = manager.get("landscape.g3db",Model.class);
		instances.put("logoMarker",new ModelInstance(model));
//		model = manager.get("houses.g3db",Model.class);
//		instances.put("houseMarker",new ModelInstance(model));

		/*
		 * Crear controladores de animación si el modelo es animado
		 * Create animation controllers if the model is animated
		 */
		animationControllers = new Array<AnimationController>();
		AnimationController animationController;
		animationController = new AnimationController(instances.get("wolfMarker")); //Wolf
		animationController.setAnimation("Wolf_Skeleton|Wolf_Run_Cycle_",-1);
		animationControllers.add(animationController);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if(!assetsLoaded){
			assetsLoaded = manager.update();
			if(assetsLoaded){
				splash_img.remove();
				createModelsinstances();
			}
		}else {
		/*
		 * Actualizar los controladores de animación
		 */
			for (AnimationController controller : animationControllers) {
				controller.update(delta);
			}

		/* Actualizar la matriz de proyección de la  cámara
		 * Update camera projection matrix
		 */
			camera.projection.set(arToolKitManager.getProjectionMatrix());
		/*
		 * Renderizar los modelos si el marcador está activo
		 */

			for (String markerName : instances.keys()) {

				if (arToolKitManager.markerVisible(markerName)) {
					transform.set(arToolKitManager.getTransformMatrix(markerName));
				/* Actualizar Cámara
				 * Update camera
				 */
					transform.getTranslation(camera.position);
					camera.position.scl(-1);
					camera.update();

				/* Dependiendo de las coordenadas del modelo puede necesitar rotarlo
				 * Depending from model coordinates it may be desired to apply a rotation
				 */
					transform.rotate(1, 0, 0, 90);
					ModelInstance instance = instances.get(markerName);
					instance.transform.set(transform);
					batch_3d.begin(camera);
					batch_3d.render(instance, environment);
					batch_3d.end();
				}
			}
		}
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose () {
		batch_3d.dispose();
		manager.dispose();
		stage.dispose();
	}

}
