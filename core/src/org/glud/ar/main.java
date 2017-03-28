package org.glud.ar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class main extends ApplicationAdapter {
	final static String TAG = "AR Application";
	SpriteBatch batch;
	Texture img;
	ARToolKitManager arToolKitManager;
	Music musica;
	float volumen;
	float delta;
	int marcadorId;
	Vector2 posicion;

	public main(ARToolKitManager arToolKitManager){
		this.arToolKitManager = arToolKitManager;
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		musica = Gdx.audio.newMusic(Gdx.files.internal("musica.ogg"));
		musica.setLooping(true);
		posicion = new Vector2(Gdx.graphics.getWidth()*0.5f - img.getWidth()*0.5f ,
				Gdx.graphics.getHeight()*0.5f - img.getHeight()*0.5f);
		//cargar macardor
//		marcadorId = arToolKitManager.cargarMarcador("single;Data/hiro.patt;80");
//		Gdx.app.debug(TAG,"Marcador ID = "+marcadorId);
//		if(marcadorId < 0){
//			Gdx.app.error(TAG,"marcador no cargado");
//		}else {
//			Gdx.app.debug(TAG,"marcador cargado");
//		}
	}

	@Override
	public void render () {
		delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.app.debug(TAG,"Volumen: "+delta);
		Gdx.app.debug(TAG,"Volumen: "+volumen);
		if(arToolKitManager.marcadorVisible(marcadorId)){
			if(!musica.isPlaying()) {
				musica.play();
			}
			if(volumen < 0.99) {
				volumen += 0.5*delta;
				musica.setVolume(volumen);
			}
			batch.begin();
			batch.draw(img,posicion.x,posicion.y);
			batch.end();
		}else{
			if(musica.isPlaying()) {
				volumen -= 0.5*delta;
				musica.setVolume(volumen);
				if(volumen < 0.001) {
					musica.pause();
				}
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
