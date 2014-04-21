package scene2d.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import scene2d.Scene;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
		public static void main(String[] argc) {
			final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			Scene.configJson = Scene.jsonReader.parse(Main.class.getClassLoader().getResourceAsStream("config"));
			if(Scene.configJson.getBoolean("hasIcon"))
				cfg.addIcon("icon.png", FileType.Internal);
			String[] screen = Scene.configJson.getString("screenSize").split("x");
			String[] target = Scene.configJson.getString("targetSize").split("x");
			cfg.width = Integer.parseInt(screen[0]);
			cfg.height = Integer.parseInt(screen[1]);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			cfg.x = (int) ((dimension.getWidth() - cfg.width) / 2);
			cfg.y = (int) ((dimension.getHeight() - cfg.height) / 2);
			cfg.resizable = Scene.configJson.getBoolean("resize");
			cfg.forceExit =  Scene.configJson.getBoolean("forceExit");
			cfg.fullscreen =  Scene.configJson.getBoolean("fullScreen");
			cfg.useGL20 = Scene.configJson.getBoolean("useGL20");
			cfg.vSyncEnabled = Scene.configJson.getBoolean("vSync");
			cfg.audioDeviceBufferCount = Scene.configJson.getInt("audioBufferCount");
			LwjglApplicationConfiguration.disableAudio = Scene.configJson.getBoolean("disableAudio");
			Scene.targetWidth = Integer.parseInt(target[0]);
			Scene.targetHeight = Integer.parseInt(target[1]);
			new LwjglApplication(Scene.app, cfg);
		}
}

