import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import web.laf.lite.widget.CenterPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;
import com.sun.xml.internal.ws.util.StringUtils;

final public class ScenePanel extends BaseList {
	private static final long serialVersionUID = 1L;
	
	private final static JTextField addField = new JTextField ();

	public static JComboBox<String> stateComboBox = new JComboBox<String>(new String[]{
			"Scenes", "Maps", "Actors"
	});	
	private static JButton addscene, updown ,delete, resume, pause;
	
	private static SceneState currentState = SceneState.SceneEditor;
	
	public ScenePanel(){
		super("Scenes", true);
		initToolBar();
		add(scrollPane);
		stateComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				clear();
				switch(stateComboBox.getSelectedIndex()){
					case 0: header.setText("SCENES");break;
					case 1: header.setText("MAPS");break;
					case 2: header.setText("ACTORS");break;
				}
				update();
			}
	    });
	}
	
	void initToolBar(){
		JPanel tools = Style.createButtonToolBarPanel();
		addscene = Style.createExplorerToolPopButton("New Scene", "newfile", addField, this);
		updown = Style.createExplorerToolButton("ScenePriority", "updown", this);
		delete = Style.createExplorerToolButton("Delete", "trash", this);
		resume = Style.createExplorerToolButton("Resume", "resume", this);
		pause = Style.createExplorerToolButton("Pause", "pause", this);
		pause.setEnabled(false);
		tools.add(addscene);
		tools.add(updown);
		tools.add(delete);
		tools.add(resume);
		tools.add(pause);
		tools.add(new CenterPanel(stateComboBox, false, true));
		add(tools);
	}
	
	
	public void update(){
		lock();
		listModel.clear();
		for(String s: Scene.scenesMap.keys())
			listModel.addElement(s);
		if(Content.sceneFileExists())
			if(listModel.contains(Content.getSceneFile()))
				list.setSelectedIndex(listModel.indexOf(Content.getSceneFile()));
		if(!Content.sceneFileExists())
			if(listModel.get(0) != null || !listModel.get(0).isEmpty()){
				Content.setSceneFile(listModel.get(0));
				list.setSelectedIndex(0);
			}
		Frame.eventPanel.update();
		classPath = new File(Content.getProject()+"bin");
		unlock();
	}
	
	private void createSceneFile(){
		if(addField.getText() == null || addField.getText().isEmpty())
			return;
		String name = addField.getText().replace(".json", "").replace(".java", "");
		name = StringUtils.capitalize(name);
		if(listModel.contains(name)){
			JOptionPane.showConfirmDialog(null, "Error: File already exists: "+name, "Error", 
					JOptionPane.OK_OPTION);
			return;
		}
		else{
			Export.writeFile("source/"+name+".java",
					Export.readFileFromClassPath("SceneTemplate.txt").replace("$$$", name));
			listModel.addElement(name);
			Scene.scenesMap.put(name, "");
			Frame.eventPanel.update();
		}
		addField.setText("");
		list.setSelectedIndex(listModel.indexOf(name));
		SceneEditor.isDirty = true;
		save();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch(((JButton)event.getSource()).getToolTipText()){
			case "New Scene": 
				createSceneFile();
				break;
			case "ScenePriority": 
				int sceneindex = list.getSelectedIndex();
				if(sceneindex+1 < listModel.size()){
					lock();
					String selected = listModel.getElementAt(sceneindex);
					String selectedValue = Scene.scenesMap.getValueAt(sceneindex);
					String down = listModel.getElementAt(sceneindex+1);
					String downValue = Scene.scenesMap.getValueAt(sceneindex+1);
					
					/* Changing the model of List */
					listModel.setElementAt(selected, sceneindex+1);
					listModel.setElementAt(down, sceneindex);
					
					/* Changing the sceneMap of Stage */
					Scene.scenesMap.setKey(sceneindex+1, selected);
					Scene.scenesMap.setValue(sceneindex+1, selectedValue);
					Scene.scenesMap.setKey(sceneindex, down);
					Scene.scenesMap.setValue(sceneindex, downValue);
					
					list.setSelectedIndex(sceneindex+1);
					unlock();
				}
				break;
			case "Delete":
				int confirm = JOptionPane.showConfirmDialog(null, 
						"Are you Sure you want to delete the Scene?", "Delete Scene", 
						JOptionPane.YES_NO_OPTION);
				if(confirm == JOptionPane.YES_OPTION){
					lock();
					Export.deleteFile("source/"+list.getSelectedValue()+".java");
					Export.deleteFile("bin/"+list.getSelectedValue()+".class");
					Scene.scenesMap.removeKey(list.getSelectedValue());
					int index = listModel.indexOf(list.getSelectedValue())-1;
					if(index != -1){
						Content.setSceneFile(listModel.elementAt(index));
						Content.editor.load();
						if(Content.currentView.equals("Editor"))
							Content.toggleView(1);
					}
					listModel.removeElement(list.getSelectedValue());
					Frame.eventPanel.update();
					list.setSelectedValue(Content.getSceneFile(), true);
					showStudio();
					SceneEditor.isDirty = true;
					save();
					unlock();
				}
				break;
			case "Resume": 
				save();
				currentState = SceneState.GameRunning;
				list.setEnabled(false);
				addscene.setEnabled(false);
				updown.setEnabled(false);
				delete.setEnabled(false);
				resume.setEnabled(false);
				pause.setEnabled(true);
				Frame.sceneEffectPanel.disable();
				Frame.propertyPanel.disable();
				Frame.effectPanel.disable();
				Frame.eventPanel.disable();
				Frame.widgetPanel.disable();
				Content.toggleView(2);
				runGame();
				break;
			case "Pause": 
				currentState = SceneState.SceneEditor;
				Stage.cl = null;
				Stage.debug = true;
				list.setEnabled(true);
				addscene.setEnabled(true);
				updown.setEnabled(true);
				delete.setEnabled(true);
				resume.setEnabled(true);
				pause.setEnabled(false);
				Frame.sceneEffectPanel.enable();
				Frame.propertyPanel.enable();
				Frame.effectPanel.enable();
				Frame.eventPanel.enable();
				Frame.widgetPanel.enable();
				Scene.scenesMap.put("SceneEditor", "");
				Stage.setScene("SceneEditor");
				Content.toggleView(2);
				runStudio();
				break;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if(list.getSelectedValue() == null)
				return;	
			if(resume.isEnabled())
				runStudio();
		}
	}
	
	public void runStudio(){
		ConsolePanel.clear();
		Content.editor.clearIcons();
		Content.editor.save();
		save();
		Content.setSceneFile(list.getSelectedValue());
		showStudio();
		Content.editor.load();
	}
	
	public void showStudio(){
		Frame.actorPanel.clear();
		Stage.getRoot().clearChildren();
		Stage.clearAllListeners();
		Stage.followActor(null);
		Stage.resetCamera();
		if(Content.sceneFileExists())
			load(Content.getSceneFile());
		for(Actor child: Stage.getChildren()){
			if(child.getName() != null)
				Frame.actorPanel.addActor(child.getName());
			for(EventListener l: child.getListeners())
				child.removeListener(l);
		}
		Frame.sceneEffectPanel.update();
		if(Stage.getScene() != null)
			Stage.addActor(Stage.getScene());
	}
	
	private static File classPath = new File(Content.getProject()+"bin/");
	public void runGame() {
		try {
			ConsolePanel.clear();
			Frame.actorPanel.clear();
			URL url = classPath.toURI().toURL();
			URLClassLoader cl = URLClassLoader.newInstance(new URL[]{url});
			Stage.cl = cl;
			Stage.debug = false;
			Stage.setScene(list.getSelectedValue());
		} catch (MalformedURLException me) {
			me.printStackTrace();
		}
	}
	
	public static void load(String sceneName){
		Main.log("Load");
		String[] lines = Scene.scenesMap.get(sceneName).split("\n");
		for(String line: lines){
			if(line.trim().isEmpty())
				continue;
			JsonValue jv = Stage.jsonReader.parse(line);
			Serializer.deserialize(jv.get("class").asString(), line);
		}
	}
	
	public static void save(){
		if(currentState != SceneState.SceneEditor)
			return;
		if(!SceneEditor.isDirty)
			return;
		Main.log("Save");
		StringBuilder sb = new StringBuilder();
		sb.append("{class:SceneJson,");
		sb.append("background:\""+Scene.sceneBackground+"\",");
		sb.append("music:\""+Scene.sceneMusic+"\",");
		sb.append("transition:\""+Scene.sceneTransition+"\",");
		sb.append("duration:"+Scene.sceneDuration+",");
		sb.append("interpolation:"+Scene.sceneInterpolationType.toString()+"}");
		sb.append("\n");
		Stage.removeActor("Shape");
		for(Actor actor: Stage.getChildren()){
			if(Stage.isValidActor(actor)){
				//log(actor.getName());
				sb.append(Stage.json.toJson(actor));
				sb.append("\n");
			}
		}
		Scene.scenesMap.put(Content.getSceneFile(), sb.toString());
		Gdx.files.local(Asset.basePath+"scene").writeString(Stage.json.toJson(Scene.scenesMap, ArrayMap.class, String.class), false);
		sb = null;
		SceneEditor.isDirty = false;
	}
}

enum SceneState{
	SceneEditor, MapEditor, ActorEditor, GameRunning
}