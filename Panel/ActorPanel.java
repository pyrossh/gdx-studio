import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

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
	
	public ActorPanel(){
		super("Actors", "");
		JPanel tools = Style.createButtonToolBar(this, btns);
		groupCombo.addItem("Root");
		groupCombo.setPreferredSize(new Dimension(90, 17));
		tools.add(new CenterPanel(groupCombo, false, true));
		add(tools);
		add(scrollPane);
		if(Content.sceneFileExists())
			setHeaderText(Content.getSceneFile());
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
			SceneEditor.doClick(Stage.findActor(list.getSelectedValue()));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		lock();
		switch(((JButton)e.getSource()).getToolTipText()){
		case "Cut": 
			cutActor = SceneEditor.selectedActor;
			copiedActor = null;
			listModel2.removeElement(SceneEditor.selectedActor.getName());
			Stage.removeActor(SceneEditor.selectedActor);
			break;
			
		case "Copy":
			String line = Stage.json.toJson(SceneEditor.selectedActor);
			copiedActor = Stage.json.fromJson(Actor.class, line);
			cutActor = null;
			break;
			
		case "Paste":
			if(cutActor != null){
				Content.studioPanel.setName(cutActor);
				cutActor = null;
			}
			if(copiedActor != null){
				copiedActor = Stage.json.fromJson(Actor.class, Stage.json.toJson(SceneEditor.selectedActor));
				Content.studioPanel.setName(copiedActor);
			}
			break;
		
		case "Delete":
			Stage.removeActor(Stage.findActor(list.getSelectedValue()));
			StatusBar.updateSelected("None");
			listModel2.removeElement(list.getSelectedValue());
			break;
		}
		unlock();
	}
	
	public void setHeaderText(String text){
		header.setText(text);
	}
}
