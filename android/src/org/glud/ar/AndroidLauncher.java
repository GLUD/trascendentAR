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
	public void configureARScene() {
		loadMarker("hiroMarker",MarkerType.SINGLE,"Data/hiro.patt",8);
		loadMarker("kokoMarker",MarkerType.SINGLE,"Data/koko.patt",8);
		loadMarker("wolfMarker",MarkerType.SINGLE,"Data/lobo.patt",8);
		loadMarker("watercraftMarker",MarkerType.SINGLE,"Data/barco.patt",8);
		loadMarker("logoMarker",MarkerType.NFT,"Data/icon",0);
	}
}
