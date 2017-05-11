package org.glud.ar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

import static com.badlogic.gdx.Gdx.gl;

public class main extends ApplicationAdapter {

	private final static String TAG = "AR Application";

	AssetManager manager;
	ARToolKitManager arToolKitManager;
	ARCamera camera;
	ModelBatch batch_3d;
	Environment environment;

	ArrayMap<String,ModelInstance> instances;
	Array<AnimationController> animationControllers;

	Matrix4 transform = new Matrix4();

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
		 * Cargar recursos
		 * Load assets
		 */
		manager = new AssetManager();
		manager.load("wolf.g3db",Model.class);
		manager.load("koko.g3db",Model.class);
		manager.finishLoading(); //Esperar hasta que los recursos  se hayan cargado - Wait until all resources are loaded

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
		instances.put("wolf",new ModelInstance(model));
		model = manager.get("koko.g3db",Model.class);
		instances.put("kokopelli",new ModelInstance(model));

//		model.dispose();

		/*
		 * Crear controladores de animación si el modelo es animado
		 * Create animation controllers if the model is animated
		 */
		animationControllers = new Array<AnimationController>();
		AnimationController animationController;
		animationController = new AnimationController(instances.get("wolf")); //Wolf
		animationController.setAnimation("Wolf_Skeleton|Wolf_Run_Cycle_",-1);
		animationControllers.add(animationController);

		batch_3d = new ModelBatch();

		//Adding lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void render () {
		Gdx.app.debug(TAG,"Rendering");

		float delta = Gdx.graphics.getDeltaTime();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		/*
		 * Actualizar los controladores de animación
		 */
		for (AnimationController controller:animationControllers) {
			controller.update(delta);
		}

		/* Actualizar la matriz de proyección de la  cámara
		 * Update camera projection matrix
		 */
		camera.projection.set(arToolKitManager.getProjectionMatrix());
		/*
		 * Renderizar los modelos si el marcador está activo
		 */

		for (String markerName:instances.keys()) {

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
				transform.rotate(1,0,0,90);
				ModelInstance instance = instances.get(markerName);
				instance.transform.set(transform);
				batch_3d.begin(camera);
				batch_3d.render(instance,environment);
				batch_3d.end();
			}
		}
	}
	
	@Override
	public void dispose () {
		batch_3d.dispose();
	}

}
