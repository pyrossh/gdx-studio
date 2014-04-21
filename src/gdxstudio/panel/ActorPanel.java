package gdxstudio.panel;
import gdxstudio.Content;
import gdxstudio.Frame;
import gdxstudio.SceneEditor;
import gdxstudio.StatusBar;
import gdxstudio.Style;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import scene2d.Scene;
import web.laf.lite.widget.CenterPanel;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorPanel extends BaseList {
	private static final long serialVersionUID = 1L;
	
	String[] btns = new String[]{
			"Cut", "cut", "Copy", "copy", "Paste", "paste", "Delete", "trash"
	};
	private JComboBox<String> groupCombo = BaseTable.createComboBox();
	
	public static Actor copiedActor = null;
	public static Actor cutActor = null;
	public Component paste;
	
	
	public ActorPanel(){
		super("Actors", "");
		JPanel tools = Style.createButtonToolBar(this, btns);
		groupCombo.addItem("Root");
		groupCombo.setPreferredSize(new Dimension(90, 17));
		tools.add(new CenterPanel(groupCombo, false, true));
		add(tools);
		add(scrollPane);
		if(Frame.scenePanel.selectedValueExists())
			setHeaderText(Frame.scenePanel.getSelectedValue());
		paste = tools.getComponent(2);
		paste.setEnabled(false);
	}

	public void addActor(String actorName){
		lock();
		listModel2.addElement(actorName);
		unlock();
	}
	
	public void renameActor(String actorName, String newName){
		lock();
		listModel2.removeElement(actorName);
		listModel2.addElement(newName);
		unlock();
	}
	
	public void addGroup(String groupName){
		groupCombo.addItem(groupName);
	}
	
	public void removeGroup(String groupName){
		// must check for root
		for(int i=0;i<groupCombo.getItemCount();i++)
			if(groupCombo.getItemAt(i).equals(groupCombo.getSelectedItem())){
				groupCombo.removeItem(groupName);
				break;
			}
	}
	
	@Override
	public void clear(){
		lock();
		listModel2.model.clear();
		unlock();
		StatusBar.updateSelected("None");
	}
	
	public boolean contains(String name){
		return listModel2.contains(name);
	}
	
	public int indexOf(String element){
		return listModel2.indexOf(element);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			SceneEditor.doClick(Scene.getCurrentScene().findActor(list.getSelectedValue()));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		lock();
		switch(((JButton)e.getSource()).getToolTipText()){
		case "Cut": 
			if(list.getSelectedIndex() != -1){
				cutActor = SceneEditor.selectedActor;
				copiedActor = null;
				listModel2.removeElement(SceneEditor.selectedActor.getName());
				Scene.getCurrentScene().removeActor(SceneEditor.selectedActor);
				paste.setEnabled(true);
			}
			break;
			
		case "Copy":
			if(list.getSelectedIndex() != -1){
				String line = Scene.json.toJson(SceneEditor.selectedActor);
				copiedActor = Scene.json.fromJson(Actor.class, line);
				cutActor = null;
				paste.setEnabled(true);
			}
			break;
			
		case "Paste":
			if(cutActor != null){
				Content.studioPanel.setName(cutActor);
				cutActor = null;
				paste.setEnabled(false);
			}
			if(copiedActor != null){
				copiedActor = Scene.json.fromJson(Actor.class, Scene.json.toJson(SceneEditor.selectedActor));
				Content.studioPanel.setName(copiedActor);
			}
			Scene.isDirty = true;
			break;
		
		case "Delete":
			if(list.getSelectedIndex() != -1){
				Scene.getCurrentScene().removeActor(SceneEditor.selectedActor);
				StatusBar.updateSelected("None");
				if(contains(SceneEditor.selectedActor.getName()))
					listModel2.removeElement(SceneEditor.selectedActor.getName());
				Scene.isDirty = true;
			}
			break;
		}
		unlock();
	}
	
	public void setHeaderText(String text){
		header.setText(text);
	}
}
