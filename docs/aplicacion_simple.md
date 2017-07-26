---
layout: doc_page
title: 1. Aplicación simple
description: Crear una aplicación simple con trascendentAR y libGDX
lang: es
ref: simple_app
---

Si has llegado aquí, significa que ya [configuraste un proyecto](aplicacion_simple.html) y estás listo para crear tu primera aplicación de realidad aumentada. Recuerda que el código de este tutorial lo puedes encontrar en [github](https://github.com/Juankz/arSimpleApp). Por si tienes alguna duda con algún paso, puedes ir al código fuente y revisar por tu cuenta.

## Alistando los recursos
Para cualquier aplicación de realidad aumentada con ARToolKit necesitamos 2 cosas: La primera es un marcador el cual es un archivo que describe un patrón o imagen que nuestra applicación deberá reconocer. La segunda es un modelo 3D para desplegar una vez que el marcador esté visible. Descargalos [aquí]({{site.github.url}}/downloads/simpleapp_assets.zip) <a href="{{site.github.url}}/downloads/simpleapp_assets.zip" class="icon fa-download"></a>

## Añadir código
Trascendentar incluye todos los métodos para inicializar una aplicación en android incluyendo la cámara, la capa de gráficos e implementando el manejo necesario cuando la aplicacion entra en pausa o cierra, así no tienes que preocuparte por esto.
Para acceder a los beneficios de trascendentAR, la clase AndroidLauncher.java dentro del módulo de android debe heredar de ARLauncher.java usando `public class AndroidLauncher extends ARLauncher`
Posteriormente agregar los métodos faltantes para cargar marcadores. El resultado final será:

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
	public void configureARScene() {
	}
}
```
Ahora cambia la línea `initialize(new main(), config);` por `initialize(new main(this), config);`.Esto nos ayudará a acceder métodos de realidad aumentada desde el módulo core

## Cargar marcadores

<span class="image left"><img src="images/simpleapp_patt.png" alt="Archivos a modificar" /></span>

Resultado:

``` java
@Override
public boolean configureARScene() {
    loadMarker("hiroMarker",MarkerType.SINGLE,"Data/hiro.patt",8);
    return true;
}
```
Este método añade un marcador para ser reconocido por ARToolKit, los parámetros de entrada son los siguientes: nombre del marcador, tipo de marcador, ruta del marcador (siempre debe estar en la carpeta _Data_) y tamáño del marcador, recomendado mayor a 4 unidades y menor a 100. EL tope máximo realmente depende de la distancia focal que se asigne a la cámara.

## Usar ARToolKit Manager para acceder métodos de realidad aumentada

Desde el módulo core, se maneja toda la lógica del juego o aplicación. El siguiente código está bien comentado y es suficientemente auto-explicativo:

``` java
package org.glud.arsimpleapp; //Esto cambia según como hallas llamado al paquete usualmente com.persona_empresa.nombre_app

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.glud.trascendentAR.ARCamera;
import org.glud.trascendentAR.ARToolKitManager;

public class main extends ApplicationAdapter {
	ARToolKitManager arManager; //Accede a los métodos de realidad aumentada
	AssetManager assetManager; //Se usa para cargar recursos
	ModelInstance koko; //La instancia del modelo que se va a usar
	ARCamera camera; //Cámara de realidad aumentada
	ModelBatch batch_3d; //Este objeto se encarga de pintar todos las instancias 3D en pantalla
	Environment environment; //Controla la iluminación del espacio
	Matrix4 transform = new Matrix4(); //Matriz auxiliar para manipular modelos si es necesesario
	Stage stage; //Dibuja todos los objetos 2D en pantalla y recibe entradas (Ej: Toque de un dedo)
	Button cameraPrefsButton; //Botón

	//¡Importante! this is how we can connect with Android side and artoolkit methods
	public main(ARToolKitManager arManager){
		this.arManager = arManager;
	}

	@Override
	public void create () {
		//Configurar cámara de libGDX
		camera = new ARCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(0f,0f,1f);
		camera.lookAt(0,0,0);
		camera.near = 0;
		camera.far = 1000f;
		camera.update();

		/* Cargar recursos -> Modelo 3D	e imágenes del botón	 */
		assetManager = new AssetManager();
		assetManager.load("koko.g3db", Model.class);
		assetManager.load("cam_button_down.png", Texture.class);
		assetManager.load("cam_button_up.png", Texture.class);
		assetManager.finishLoading();


		/* Crear una instancia del modelo		 */
		koko = new ModelInstance(assetManager.get("koko.g3db",Model.class));

		/* Agregar luces al espacio 3D		 */
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		batch_3d = new ModelBatch();

		stage = new Stage(new ScreenViewport());
		/* Create a button to open the camera preferences activity. First we define what images will be rendered when up and down. Usually this is made with a skin, but for this example we will do it using code
		 */
		Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
		buttonStyle.up = new Image(assetManager.get("cam_button_up.png",Texture.class)).getDrawable();
		buttonStyle.down = new Image(assetManager.get("cam_button_down.png",Texture.class)).getDrawable();
		cameraPrefsButton = new Button(buttonStyle);
		//Damos una posicion en la parte superior derecha de la pantalla
		cameraPrefsButton.setPosition(stage.getWidth() - 20 - cameraPrefsButton.getHeight(),stage.getHeight() - 20 - cameraPrefsButton.getHeight());

		// Recognize when button is clicked and open camera preferences using arToolKitManger
		cameraPrefsButton.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				arManager.openCameraPreferences();
			}
		});

		/* Let's add the button to the stage		 */
		stage.addActor(cameraPrefsButton);

		/* Finally as we have a button to be pressed, we need to make stage to receive inputs*/
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		/* (1)
		* Limpiar la pantalla con un color negro y alpha cero, esto es prácticamente un color transparente,
		* sin embargo de no ser un color negro, no funcionará adecuadamente
		*/
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		/* (2)
		* Antes de llamar cualquier método de ARToolkit,debemos revisar
		* si el módulo de realidad aumentada ya está listo, de lo contrario
		* la aplicación fallará
		*/
		if(!arManager.arRunning())return;

		/*(3)
		*	Actualizar la matriz de proyección de la cámara
		*/
		camera.projection.set(arManager.getProjectionMatrix());

		/*(4)
		*	Comprobar si el marcador está visible, el texto asignado a markerID debe coincidir con
		* el nombre dado al marcador cuando se cargó en la clase AndroidLauncher
		*/
		String markerID = "hiroMarker";
		if(arManager.markerVisible(markerID)){
			transform.set(arManager.getTransformMatrix(markerID));
			/* (5)
			 * Actualizar Cámara
			 */
			transform.getTranslation(camera.position);
			camera.position.scl(-1);
			camera.update();

			/* (6)
			 * Dependiendo de las coordenadas del modelo puede necesitar rotarlo
			 */
			transform.rotate(1, 0, 0, 90);
			koko.transform.set(transform);

			/* (7)
			 * Pintar objetos en pantalla
			 */
			batch_3d.begin(camera);
			batch_3d.render(koko, environment);
			batch_3d.end();
		}
	}

	/* Manejo apropiado de memoria	*/
	@Override
	public void dispose () {
		batch_3d.dispose();
		assetManager.dispose();
	}
}
```

**Explicación extendida:** Si no estás familiarizado con 3D en libGDX, te recomiendo los tutoriales de [Xoppa](https://xoppa.github.io/blog/basic-3d-using-libgdx/) (En inglés). Algunas cosas que debes tener en cuenta son:
* La cámara que se usa es ARCamera, una cámara propia de trascendentAR.
* La clase principal o _main_ como se llamó en este caso, acepta un objeto ARToolKitManager en el constructor, este se envía desde AndroidLauncher.
* Siempre revisar si el módulo de realidad aumentada está funcionando usando `arManager.arRunning()`
* G3DB es el formato de modelos 3D que maneja libGDX, se puede convertir desde otros formatos como FBX o OBJ a G3DB usando [esta herramienta](https://github.com/libgdx/fbx-conv) de libGDX. Pero hay que tener en cuenta que tiene limitaciones, mayor información [aquí](https://github.com/libgdx/libgdx/wiki/Importing-Blender-models-in-LibGDX).

## Correr la aplicacion

1. Generar APK usando intelliJ IDEA en la pestaña Build
2. Instalar la aplicación en el teléfono móvil. Desafortunadamente este paso debe ser ejecutado por consola, afortunadamente IntelliJ IDEA y Android Studio nos proveen una consola que podemos usar. Simplemente hay que escribir la siguente línea: `adb install -r android/build/outputs/apk/android-debug.apk` y la aplicación se instalará en tu dipositivo

NOTA: También puedes resumir los 2 pasos anteriores en una sola línea de consola:

`./gradlew assemble && adb install -r android/build/outputs/apk/android-debug.apk`

## Disfruta
Abre la aplicación, apunta la cámara al patrón y observa la magia.

<center>
<img src="images/simpleapp_output.png" alt="Bello resultado">
</center>
