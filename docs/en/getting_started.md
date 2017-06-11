---
layout: doc_page
title: Simple App
description: Make a simple application with trascendentAR and libGDX
lang: en
ref: simple_app
---

1. Create project with libGDX
2. Modify in build.gradle fro android module minSdkVersion=15
3. Add permission to use the camera
4. Include ARToolKit native libraries en android/libs
5. Add the following lines on gradle.build for android module dependencies:

	`compile 'org.glud.trascendentAR:trascendentAR-android:1.0-beta.1'`

	The same on core module:

	`compile 'org.glud.trascendentAR:trascendentAR-core:1.0-beta.1'`

6. AndroidLauncher.java must inherits from ARLauncher.java

	`public class AndroidLauncher extends ARLauncher`

7. Create _Data_ folder inside  _assets_ directory. All markers must go inside and the folder name cannot be changed

8. Load markers: Add the markers inside your AndroidLauncher
	`@Override
	public boolean configureARScene() {
		loadMarker("hiroMarker", MarkerType.SINGLE, "hiro.patt",8)
		return true;
	}`

9. Use ARToolKitManager from core module to call ARToolKit methods
