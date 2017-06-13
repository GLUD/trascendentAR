---
layout: doc_page
title: 1. Aplicación simple
description: Crear una aplicación simple con trascendentAR y libGDX
lang: es
ref: simple_app
---

Si has llegado aquí, significa que ya [configuraste un proyecto](aplicacion_simple.html) y estás listo para crear tu primera aplicación de realidad aumentada.

## Añadir código
Trascendentar incluye todos los métodos para inicializar una aplicación en android incluyendo la cámara, la capa de gráficos e implementando el manejo necesario cuando la aplicacion entra en pausa o cierra, así no tienes que preocuparte por esto.
Para acceder a los beneficios de trascendentAR, la clase AndroidLauncher.java dentro del módulo de android debe heredar de ARLauncher.java usando `public class AndroidLauncher extends ARLauncher`
Posteriormente agregar los métodos faltantes para crear marcadores. El resultado final será:

``` java

package org.glud.arsimpleapp;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.glud.arsimpleapp.main;
import org.glud.trascendentar_android.ARLauncher;

public class AndroidLauncher extends ARLauncher {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new main(), config);
	}

	@Override
	public boolean configureARScene() {
		return true;
	}
}
```
Asegúrate de que el método `configureARScene()` retorne **true** o el programa no cargará ningún marcador.

## Cargar marcadores

<span class="image left"><img src="images/simpleapp_patt.png" alt="Archivos a modificar" /></span>

1. Crear carpeta _Data_ con marcadores dentro del directorio de  _assets_ en el módulo android. El nombre de la carpeta no debe cambiar o trascendentAR no será capaz de localizar los marcadores.
2. Copiar el archivo hiro.patr a la carpeta _Data_ recién creada
3. Dentro del método `configureARScene` carga el marcador usando el método loadMarker:  `loadMarker(MarkerType.SINGLE,"Data/hiro.patt",8);`
4. Agregar archivo _camera_para.dat_ en la carpeta _Data_


Resultado:

``` java
@Override
public boolean configureARScene() {
    loadMarker(MarkerType.SINGLE,"Data/hiro.patt",8);
    return true;
}
```

Este método cargará el marcador con ID=0, si cargamos otro marcador, tendrá ID=1 y así sucesivamente. También se puede añadir un nombre al marcador para referenciarlo más fácil: `loadMarker("miMarcador",MarkerType.SINGLE,"Data/hiro.patt",8);`

## Usar ARToolKit Manager para acceder métodos de realidad aumentada

``` java
package org.glud.arsimpleapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import org.glud.trascendentAR.ARCamera;
import org.glud.trascendentAR.ARToolKitManager;

public class main extends ApplicationAdapter {
	ARToolKitManager arManager;
	AssetManager assetManager;
	ModelInstance koko;
	ARCamera camera;
	ModelBatch batch_3d;
	Environment environment;
	Matrix4 transform = new Matrix4();

	/*
	Usamos asset manager para cargar modelos 3D, tipografias,
	 imagenes 2D y demas
 	*/

	@Override
	public void create (ARToolKitManager arManager) {
		this.arManager = arManager;

		//Configurar cámara de libGDX
		camera = new ARCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(0f,0f,1f);
		camera.lookAt(0,0,0);
		camera.near = 0;
		camera.far = 1000f;
		camera.update();

		assetManager = new AssetManager();
		assetManager.load("koko.g3db", Model.class);
		assetManager.finishLoading();
		koko = new ModelInstance(assetManager.get("koko.g3db",Model.class));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.projection.set(arManager.getProjectionMatrix());
		int markerID = 0;
		if(arManager.markerVisible(markerID)){
			transform.set(arManager.getTransformMatrix(markerID));
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
			koko.transform.set(transform);
			batch_3d.begin(camera);
			batch_3d.render(koko, environment);
			batch_3d.end();
		}
	}

	@Override
	public void dispose () {
		batch_3d.dispose();
		assetManager.dispose();
	}
}
```

## Correr la aplicacion

1. Generar APK usando intelliJ IDEA en la pestaña Build
2. Instalar la aplicación en el teléfono móvil. Desafortunadamente este paso debe ser ejecutado por consola, afortunadamente IntelliJ IDEA y Android Studio nos proveen una consola que podemos usar. Simplemente hay que escribir la siguente línea: `adb install -r android/build/outputs/apk/android-debug.apk` y la aplicación se instalará en tu dipositivo

NOTA: También puedes resumir los 2 pasos anteriores en una sola línea de consola:

`./gradlew assemble && adb install -r android/build/outputs/apk/android-debug.apk`

## Disfruta
Apunta la cámara al patrón y observa la magia.
