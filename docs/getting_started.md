---
layout: doc_page
title: Aplicación simple
description: Crear una aplicación simple con trascendentAR y libGDX
---

1. Generar el projecto con libGDX solo con Android
2. Modificar el minSdkVersion=15 en AndroidManifest.xml y en build.gradle del módulo de android
3. Añadir permisos para usar la cámara
3. Si ves algún diálogo acerca de actualizar gradle no lo actualizes, podría romper los archivos build
4. Incluir los archivos .so en android/libs
5. en gradle.build añadir bajo el módulo de Android en dependencies:

	`compile 'org.glud.trascendentAR:trascendentAR-android:1.0-beta.1'`

	Y bajo el módulo core:

	`compile 'org.glud.trascendentAR:trascendentAR-core:1.0-beta.1'`

6. Hacer que AndroidLauncher.java herede de ARLauncher.java

	`public class AndroidLauncher extends ARLauncher`

7. Crear carpeta _Data_ con marcadores dentro del directorio de  _assets_
8. Cargar marcadores
	`@Override
	public boolean configureARScene() {
		loadMarker("hiroMarker", MarkerType.SINGLE, "hiro.patt",8)
		return true;
	}`

9. Usar ARToolKitManager desde core para llamar a métodos de ARToolKit
