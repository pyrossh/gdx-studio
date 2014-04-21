package scene2d.android;
import scene2d.Scene;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class Main extends AndroidApplication {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Scene.configJson = Scene.jsonReader.parse(this.getClass().getClassLoader().getResourceAsStream("config"));
		String[] target = Scene.configJson.getString("targetSize").split("x");
		Scene.targetWidth = Integer.parseInt(target[0]);
		Scene.targetHeight = Integer.parseInt(target[1]);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useGL20 = Scene.configJson.getBoolean("useGL20");
        cfg.useCompass = false;
        cfg.hideStatusBar = true;
        cfg.useWakelock =  true;
        initialize(Scene.app, cfg);
    }
}