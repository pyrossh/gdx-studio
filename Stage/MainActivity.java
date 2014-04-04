import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Stage.configJson = Stage.jsonReader.parse(this.getClass().getClassLoader().getResourceAsStream("config"));
		String[] target = Stage.configJson.getString("targetSize").split("x");
		Stage.targetWidth = Integer.parseInt(target[0]);
		Stage.targetHeight = Integer.parseInt(target[1]);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useGL20 = Stage.configJson.getBoolean("useGL20");
        cfg.useCompass = false;
        cfg.hideStatusBar = true;
        cfg.useWakelock =  true;
        initialize(Stage.getInstance(), cfg);
    }
}