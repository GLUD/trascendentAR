package org.glud.ar;


import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.glud.trascendentar_android.ARLauncher;

public class AndroidLauncher extends ARLauncher {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new main(this), config);
	}

	@Override
	public boolean configureARScene() {
		loadMarker("hiroMarker",MarkerType.SINGLE,"Data/hiro.patt",8);
		loadMarker("kanjiMarker",MarkerType.SINGLE,"Data/kanji.patt",8);
		return true;
	}
}
