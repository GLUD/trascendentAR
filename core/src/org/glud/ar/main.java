package org.glud.ar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class main extends ApplicationAdapter {
	final static String TAG = "AR Application";
	SpriteBatch batch;
	Texture img;
	ARToolKitManager arToolKitManager;
	int marcadorId;

	public main(ARToolKitManager arToolKitManager){
		this.arToolKitManager = arToolKitManager;
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		//cargar macardor
		marcadorId = arToolKitManager.cargarMarcador("single;Data/hiro.patt;80");
		Gdx.app.debug(TAG,"Marcador ID = "+marcadorId);
		if(marcadorId < 0){
			Gdx.app.error(TAG,"marcador no cargado");
		}else {
			Gdx.app.debug(TAG,"marcador cargado");
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.app.debug(TAG,"Marcador visible?= "+arToolKitManager.marcadorVisible(marcadorId));
		if(arToolKitManager.marcadorVisible(marcadorId)) {
			Gdx.app.debug(TAG,"Marcador visible");
			batch.begin();
			batch.draw(img, 0, 0);
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
