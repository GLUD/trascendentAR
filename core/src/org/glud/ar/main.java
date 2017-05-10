package org.glud.ar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static com.badlogic.gdx.Gdx.gl;

public class main extends ApplicationAdapter {

	private final static String TAG = "AR Application";

	AssetManager manager;
	ARToolKitManager arToolKitManager;
	AR_Camera camera;
	ModelBatch batch_3d;
	Environment environment;

	ArrayMap<Integer,ARModelInstance> instances;
	Array<AnimationController> animationControllers;

	public main(ARToolKitManager arToolKitManager){
		this.arToolKitManager = arToolKitManager;
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		//Configurar cámara de libGDX
		camera = new AR_Camera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
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
		manager.load("adventurer.g3db",Model.class);
		manager.finishLoading(); //Esperar hasta que los recursos  se hayan cargado - Wait until all resources are loaded

		/*
		 * Crear instacias de los modelos
		 * Create model instances
		 */
		instances = new ArrayMap<Integer, ARModelInstance>();
		Model model;

		model = manager.get("wolf.g3db",Model.class);
		instances.put(0,new ARModelInstance(model));
		model = manager.get("adventurer.g3db",Model.class);
		instances.put(1,new ARModelInstance(model));

		/*
		 * Crear controladores de animación si el modelo es animado
		 * Create animation controllers if the model is animated
		 */
		animationControllers = new Array<AnimationController>();
		AnimationController animationController;
		animationController = new AnimationController(instances.get(0)); //Wolf
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
		float delta = Gdx.graphics.getDeltaTime();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if(arToolKitManager.marcadorVisible(marcadorId)){
			matriz_transformacion.set(arToolKitManager.getTransformMatrix(marcadorId));
			matriz_proyeccion.set(arToolKitManager.getProjectionMatrix());
//			matriz_transformacion.row_switch();
			if(!musica.isPlaying()) {
				musica.play();
			}
			if(volumen < 0.99) {
				volumen += 0.5*delta;
				musica.setVolume(volumen);
			}
			//Render
			matriz_transformacion.getTranslation(tmp);
			tmp.scl(-1);
			camera.projection.set(matriz_proyeccion);
			if(Gdx.input.isTouched())tmp.add(1,0,0);
			camera.position.set(tmp);
			camera.update();
			matriz_transformacion.rotate(1,0,0,90);
			if(animationController!=null)animationController.update(delta);
			for(ModelInstance instance : instanceArray){
				instance.transform.set(matriz_transformacion);
//				instance.transform.setTranslation(0,0,z);
//				instance.calculateTransforms();
			}
			batch_3d.begin(camera);
			batch_3d.render(instanceArray,environment);
			batch_3d.end();

			if(!music_img.isVisible()) music_img.setVisible(true);
		}else{
			if(music_img.isVisible()) music_img.setVisible(false);
			if(musica.isPlaying()) {
				volumen -= 0.5*delta;
				musica.setVolume(volumen);
				if(volumen < 0.001) {
					musica.pause();
				}
			}
		}
		renderMan();
		print_info();
		stage.act();
		stage.draw();
	}

	private void done_loading(){
		model = manager.get(model_name);
		modelInstance = new ModelInstance(model);

		//Crear controlador de la animación
		animationController = new AnimationController(modelInstance);
		animationController.setAnimation("Wolf_Skeleton|Wolf_Run_Cycle_",-1);


		instanceArray.add(modelInstance);
		modelInstance = new ModelInstance(manager.get("adventurer.g3db",Model.class));
		tmpArray.add(modelInstance);
		loading=false;
	}
	
	@Override
	public void dispose () {
		batch_3d.dispose();
	}

}
