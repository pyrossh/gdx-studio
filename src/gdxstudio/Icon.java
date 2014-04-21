package gdxstudio;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.fife.ui.rsyntaxtextarea.Theme;

import scene2d.Asset;

public class Icon {
	
	static String[] iconsList = new String[]{
		"icon", "vs", "eclipse", "dark", "idea",
		"trash", "help", "szoomout", "szoomin", 
		"sweblaf", "update", "stexturepacker", "sparticle", "snuke", 
		"slibGDX", "shierologo", "shiero", "search", "screen", "sabout", 
		"packer", "options", "newprj", "newpack", "newfile", "lib", "level", 
		"high", "font", "find", "esource", "epackage", 
		"eopen", "emusic", "empty", "eimage", "efile", "editor",  "console", 
		"color", "up","down", 
		"resume", "pause", "go", "stop", "export","new","copy", "cut", "paste", "refresh",
		"home", "properties",
		"java", "warning", "error", "updown", "studio", "bookmark"
	};
	
	// Icons
	private static HashMap<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();
	public static void loadIcons(){
		for(String f : iconsList){
			//SinkStudio.log(f);
			iconMap.put(f, loadIcon(f+".png"));
		}
	}
	
	public static ImageIcon icon(String name){
		return iconMap.get(name);
	}
	
	public static Theme loadTheme(String name){
		try{
			return Theme.load(getResourceAsStream(name+".xml"));
		}
		catch ( IOException e ){
             e.printStackTrace ();
         }
		return null;
	}

	public static ImageIcon loadIcon(String path){
		return new ImageIcon(Icon.getResource(path));
	}

	public static InputStream getResourceAsStream(String path){
		return Asset.class.getClassLoader().getResourceAsStream(path);
	}

	public static URL getResource(String path){
	    return Asset.class.getClassLoader().getResource(path);
	}
}