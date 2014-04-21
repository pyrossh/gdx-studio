package gdxstudio.panel;

import gdxstudio.Content;
import gdxstudio.Export;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import scene2d.Scene;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectSettingsPanel extends BaseTable {
	private static final long serialVersionUID = 1L;
	String[] screenSizes = {
			"320x240", "480x320", "800x480", "852x480", "960x540", "1280x720", "1980x1080"
	};
	String[] targetSizes = {
			"800x480"
	};
	JComboBox<String> screenCombo = createComboBox(screenSizes);
	JComboBox<String> targetCombo = createComboBox(targetSizes);

	public ProjectSettingsPanel(){
		super("Settings");
		editors.add(createTextFieldEditor());
		editors.add(new DefaultCellEditor(targetCombo));
		editors.add(new DefaultCellEditor(screenCombo));
		editors.add(createNumberField());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		editors.add(createCheckBoxEditor());
		if(Content.projectExists())
			update();
		screenCombo.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				//String val[] = screenCombo.getSelectedItem().toString().split("x");
				//Content.studioPanel.setGdxScreenSize(Integer.parseInt(val[0]), Integer.parseInt(val[1]));
			}
		});
	}
	
	@Override
	public void clear(String... items){
		super.clear("", "" ,"" ,"" ,"", "", "", "", "", "", "", "", "");
	}
	
	private static JsonValue jsonValue;
	JsonReader jsonReader = new JsonReader();
	@Override 
	public void update(String... items){
		super.update();
    	String text = Export.readFile("config");
    	if(text.isEmpty())
    		return;
    	jsonValue = jsonReader.parse(text);
    	addRow("Title", jsonValue.getString("title"));
    	addRow("TargetSize", jsonValue.getString("targetSize"));
    	addRow("ScreenSize", jsonValue.getString("screenSize"));
    	addRow("AudioBufferCount", jsonValue.getString("audioBufferCount"));
    	addRow("Resize", jsonValue.getBoolean("resize"));
    	addRow("ForceExit", jsonValue.getBoolean("forceExit"));
    	addRow("FullScreen", jsonValue.getBoolean("fullScreen"));
    	addRow("UseGL20", jsonValue.getBoolean("useGL20"));
    	addRow("VSync", jsonValue.getBoolean("vSync"));
    	addRow("DisableAudio", jsonValue.getBoolean("disableAudio"));
    	addRow("KeepAspectRatio", jsonValue.getBoolean("keepAspectRatio"));
    	addRow("ShowFPS", jsonValue.getBoolean("showFPS"));
    	addRow("LoggingEnabled", jsonValue.getBoolean("loggingEnabled"));
    	ProjectPanel.updateProperty("Version", jsonValue.getString("version"));
    	ProjectPanel.updateProperty("Target", jsonValue.getString("target"));
    	/*createRow(content3, "Use Accelerometer", new WebSwitch());
    	createRow(content3, "Use Compass", new WebSwitch());
    	createRow(content3, "Use WakeLock", new WebSwitch());
    	createRow(content3, "Hide Status Bar", new WebSwitch());*/
	}

	@Override
	public void setProperty(String key, String value) {
		if(key.isEmpty() && value.isEmpty()){
			return;
		}
		jsonValue.get("hasIcon").set(new File(Content.getProject()+"icon.png").exists());
		jsonValue.get(Scene.uncapitalize(key)).set(value);
		Export.writeFile("config", jsonValue.toString());
	}
	
	public void updateVersion(String version){
		jsonValue.get("version").set(version);
		Export.writeFile("config", jsonValue.toString());
	}
	
	public void updateTarget(String target){
		jsonValue.get("target").set(target);
		Export.writeFile("config", jsonValue.toString());
	}
	
	static String jsonizeArray(Object[] array){
		StringBuilder sb = new StringBuilder();
		for(Object s: array){
			sb.append(s.toString().trim());
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}
}