import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorPanel extends BaseList {
	private static final long serialVersionUID = 1L;
	
	String[] btns = new String[]{
			"Cut", "cut", "Copy", "copy", "Paste", "paste", "Delete", "trash"
	};
	
	public static Actor copiedActor = null;
	
	public ActorPanel(){
		super("Actors", "");
		add(Style.createButtonToolBar(this, btns));
		add(scrollPane);
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
			copiedActor = SceneEditor.selectedActor;
			listModel2.removeElement(SceneEditor.selectedActor.getName());
			Stage.removeActor(SceneEditor.selectedActor);
			break;
			
		case "Copy":
			//need to make new copy
			//copiedActor = selectedActor;
			break;
			
		case "Paste":
			if(copiedActor != null){
				copiedActor.setName(copiedActor.getName()+"1");
				Stage.addActor(copiedActor, Stage.mouse.x, Stage.mouse.y);
				addActor(copiedActor.getName());
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
}
