package gdxstudio.panel;
import gdxstudio.Content;
import gdxstudio.Export;
import gdxstudio.Frame;
import gdxstudio.SceneEditor;
import gdxstudio.Style;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;

import scene2d.Camera;
import scene2d.Scene;
import scene2d.Serializer;
import web.laf.lite.widget.CenterPanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

final public class ScenePanel extends BaseList {
	private static final long serialVersionUID = 1L;

	public static JComboBox<String> stateComboBox = BaseTable.createComboBox("Scenes", "Maps", "Actors", 
			"Assets");
	private static JButton addscene, updown ,delete, resume, pause;
	
	private final Timer saveTimer = new Timer(20000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Scene.getCurrentScene().save(getSelectedValue());
		}
	});
	private static File classPath;
	
	public ScenePanel(){
		super("Scenes", true);
		saveTimer.start();
		Serializer.registerSerializer(SceneEditor.class, new Serializer.SceneSerializer());
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
		addscene = Style.createToolButton("New Scene", "newfile", this);
		updown = Style.createToolButton("ScenePriority", "updown", this);
		delete = Style.createToolButton("Delete", "trash", this);
		resume = Style.createToolButton("Resume", "resume", this);
		pause = Style.createToolButton("Pause", "pause", this);
		pause.setEnabled(false);
		tools.add(addscene);
		tools.add(updown);
		tools.add(delete);
		tools.add(resume);
		tools.add(pause);
		stateComboBox.setPreferredSize(new Dimension(65, 17));
		tools.add(new CenterPanel(stateComboBox, false, true));
		add(tools);
	}
	
	
	public void update(){
		lock();
		listModel.clear();
		for(String s: Scene.scenesMap.keys())
			listModel.addElement(s.replace(Scene.basePackage, ""));
		if(listModel.getSize()>0 && listModel.get(0) != null || !listModel.get(0).isEmpty())
			list.setSelectedIndex(0);
		Frame.eventPanel.update();
		classPath = new File(Content.getProject()+"bin/");
		unlock();
	}
	
	private void createSceneFile(){
		String text = JOptionPane.showInputDialog(Frame.getInstance(), "Create a New Scene", "New Scene", 
				JOptionPane.OK_CANCEL_OPTION);
		if(text == null || text.isEmpty())
			return;
		String name = text.replace(".json", "").replace(".java", "");
		name = Scene.capitalize(name);
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
		list.setSelectedIndex(listModel.indexOf(name));
		Scene.isDirty = true;
		Scene.getCurrentScene().save(getSelectedValue());
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
						Content.editor.load();
						if(Content.currentView.equals("Editor"))
							Content.toggleView(1);
					}
					listModel.removeElement(list.getSelectedValue());
					Frame.eventPanel.update();
					list.setSelectedValue(listModel.elementAt(index), true);
					showStudio();
					Scene.isDirty = true;
					Scene.getCurrentScene().save(getSelectedValue());
					unlock();
				}
				break;
			case "Resume": 
				Scene.getCurrentScene().save(getSelectedValue());
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
				Content.widgetPanel.disable();
				Content.toggleView(2);
				runGame();
				break;
			case "Pause": 
				Scene.cl = null;
				Scene.debug = true;
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
				Content.widgetPanel.enable();
				Scene.scenesMap.put("gdxstudio.SceneEditor", "");
				Scene.setScene("gdxstudio.SceneEditor");
				Content.toggleView(2);
				showStudio();
				break;
		}
	}

	private String prevScene = "";
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			ConsolePanel.clear();
			Content.editor.clearIcons();
			Content.editor.save();
			Scene.getCurrentScene().save(prevScene);//Warning Only Change
			if(list.getSelectedValue() == null)
				return;	
			prevScene = list.getSelectedValue();
			if(resume.isEnabled())
				showStudio();
			Content.editor.load();
			Frame.actorPanel.setHeaderText(list.getSelectedValue());
		}
	}
	
	public void showStudio(){
		Frame.actorPanel.clear();
		Scene.getRoot().clearChildren();
		Scene.getCurrentScene().clear();
		Camera.clearAllHud();
		Camera.followActor(null);
		Camera.reset();
		if(selectedValueExists()){
			Scene.getCurrentScene().load(getSelectedValue());
			for(Actor child: Scene.getCurrentScene().getChildren()){
				Frame.actorPanel.addActor(child.getName());
				for(EventListener l: child.getListeners())
					child.removeListener(l);
			}
			Scene.getRoot().addActor(Scene.getCurrentScene());
		}
		Frame.sceneEffectPanel.update();
	}
	
	public void runGame() {
		try {
			ConsolePanel.clear();
			Frame.actorPanel.clear();
			URL url = classPath.toURI().toURL();
			URLClassLoader cl = URLClassLoader.newInstance(new URL[]{url});
			Scene.cl = cl;
			Scene.debug = false;
			Scene.setScene(list.getSelectedValue());
		} catch (MalformedURLException me) {
			me.printStackTrace();
		}
	}
	
	public boolean selectedValueExists(){
		if(list.getSelectedValue() != null && !list.getSelectedValue().isEmpty())
			return true;
		return false;
	}
	
	public String getSelectedValue(){
		return list.getSelectedValue();
	}
}