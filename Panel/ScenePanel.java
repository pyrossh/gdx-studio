import java.awt.Cursor;
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

import web.laf.lite.widget.CenterPanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer.Task;

final public class ScenePanel extends BaseList {
	private static final long serialVersionUID = 1L;

	public static JComboBox<String> stateComboBox = BaseTable.createComboBox("Scenes", "Maps", "Actors");
	private static JButton addscene, updown ,delete, resume, pause;
	
	private static SceneState currentState = SceneState.SceneEditor;
	private final Timer saveTimer = new Timer(10000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			save();
		}
	});
	
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
			listModel.addElement(s);
		if(Content.sceneFileExists()){
			if(listModel.contains(Content.getSceneFile()))
				list.setSelectedIndex(listModel.indexOf(Content.getSceneFile()));
		}
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
		String text = JOptionPane.showInputDialog(Frame.getInstance(), "Create a New Scene", "New Scene", 
				JOptionPane.OK_CANCEL_OPTION);
		if(text == null || text.isEmpty())
			return;
		String name = text.replace(".json", "").replace(".java", "");
		name = Stage.capitalize(name);
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
			Frame.actorPanel.setHeaderText(list.getSelectedValue());
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
		Stage.clearAllHud();
		Stage.followActor(null);
		Stage.resetCamera();
		if(Content.sceneFileExists()){
			//load(Content.getSceneFile());
			Stage.getScene().load(Content.getSceneFile());
			for(Actor child: Stage.getChildren()){
				if(Stage.isValidActor(child)){
					Frame.actorPanel.addActor(child.getName());
					for(EventListener l: child.getListeners())
						child.removeListener(l);
				}
			}
		}
		Frame.sceneEffectPanel.update();
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
	
	private void save(){
		if(currentState != SceneState.SceneEditor)
			return;
		if(!SceneEditor.isDirty)
			return;
		Stage.getScene().save(Content.getSceneFile());
		SceneEditor.isDirty = false;
	}
}

enum SceneState{
	SceneEditor, MapEditor, ActorEditor, GameRunning
}

class SceneEditor extends Scene {
	public static Actor selectedActor = null;
	public static boolean reloadAssets = false;
	static AddField addField;

	public SceneEditor(){
		super();
		if(reloadAssets){
			Asset.loadBlocking();  // this is the first time this Scene is created by the Stage
			Content.assetPanel.updateAsset();
			com.badlogic.gdx.utils.Timer.schedule(new Task(){
				@Override
				public void run() {
					Frame.scenePanel.showStudio();
					Scene.scenesMap.removeKey("SceneEditor");
					Frame.scenePanel.update();
				}
			}, 1f);
			reloadAssets = false;
			addField = new AddField();
		}
		else{
			Frame.scenePanel.showStudio();
			Scene.scenesMap.removeKey("SceneEditor");
			Stage.addActor(this);
		}
		Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	public static void doClick(Actor actor){
		addField.remove();
		Frame.actorPanel.lock();
		Frame.propertyPanel.clear();
		Frame.effectPanel.clear();
		Frame.eventPanel.clear();
		selectedActor = actor;
		Stage.outline(actor);
		Frame.actorPanel.list.setSelectedIndex(Frame.actorPanel.indexOf(actor.getName()));
		StatusBar.updateSelected(actor.getName());
		Frame.dashPanel.update();
		Frame.propertyPanel.update();
		Frame.effectPanel.update();
		Frame.eventPanel.update();
		Frame.actorPanel.unlock();
		if(selectedActor instanceof List || selectedActor instanceof SelectBox){
			addField.setPosition(actor.getX(), actor.getY() - addField.getHeight());
			Stage.addActor(addField);
		}
	}

	
	@Override
	public void onClick(Actor actor) {
		doClick(actor);
	}
	
	boolean dragging;
	int edge;
	float startX, startY, lastX, lastY;
	public static boolean isDirty = false;
	@Override
	public void onTouchDown(Actor actor) {
		if (Stage.button == 0) {
			edge = 0;
			float x = Stage.mouse.x;
			float y = Stage.mouse.y;
			if (x > actor.getX() + actor.getWidth() - 10) edge |= Align.right;
			if (y > actor.getY() + actor.getHeight() - 10) edge |= Align.top;
			if (x < actor.getX() + 20 && y < actor.getY() + 20) edge = Align.left;
			dragging = edge != 0;
			startX = x;
			startY = y;
			lastX = x;
			lastY = y;
		}
	}

	@Override
	public void onTouchUp() {
		dragging = false;
		Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void onDragged() {
		if(Stage.isValidActor(selectedActor)){
			if(!dragging){
				selectedActor.setPosition(Stage.mouse.x, Stage.mouse.y);
				Frame.propertyPanel.updateProperty("X", ""+selectedActor.getX(), 0);
				Frame.propertyPanel.updateProperty("Y", ""+selectedActor.getY(), 0);
				StatusBar.updateXY(Stage.mouse.x, Stage.mouse.y);
				
			}
			else
			{
				float x = Stage.mouse.x;
				float y = Stage.mouse.y;
				float width = selectedActor.getWidth(), height = selectedActor.getHeight();
				float windowX = selectedActor.getX(), windowY = selectedActor.getY();
				if ((edge & Align.right) != 0) {
					width += x - lastX;
				}
				if ((edge & Align.top) != 0) {
					height += y - lastY;
				}
				if ((edge & Align.top) != 0 && (edge & Align.right) != 0) {
					width += x - lastX;
					height += y - lastY;
				}
				if (edge == Align.left){
					float rot = Stage.getAngle(selectedActor.getX()+selectedActor.getOriginX(), 
							selectedActor.getY()+selectedActor.getOriginY(), x, y) 
							- selectedActor.getRotation();
					selectedActor.rotate(rot);
				}
				lastX = x;
				lastY = y;
				selectedActor.setBounds(Math.round(windowX), Math.round(windowY), Math.round(width), Math.round(height));
				Frame.propertyPanel.updateProperty("Width", ""+width, 0);
				Frame.propertyPanel.updateProperty("Height", ""+height, 0);
			}
			SceneEditor.isDirty = true;
		}
	}

	@Override
	public void onGesture(GestureType type) {
	}
	
	private boolean isHand = true;
	private boolean isRight = false;
	private boolean isTop = false;

	@Override
	public void act(float delta){
		if(Stage.isValidActor(selectedActor)){
			isRight = false;
			isTop = false;
			//Stage.log("my"+Stage.mouse.y+"top"+selectedActor.getTop());
			if (Stage.mouse.x > selectedActor.getRight() - 10 && Stage.mouse.x < selectedActor.getRight()) {
				Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				isHand = false;
				isRight = true;
			}
			if(Stage.mouse.y > selectedActor.getTop() - 10 && Stage.mouse.y < selectedActor.getTop()){
				Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				isHand = false;
				isTop = true;
			}
			if (isRight && isTop) {
				Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				isHand = false;
			}
			if (!isRight && !isTop){
				if(!isHand){
					Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					isHand = true;
				}
			}
		}
	}
	
	@Override
	public void onKeyTyped(char key) {};
	@Override
	public void onKeyUp(int keycode){};
	@Override
	public void onKeyDown(int keycode){};
	@Override
	public void onPause(){};
	@Override
	public void onResume(){};
	@Override
	public void onDispose(){};
}

class AddField extends Table {
	TextField tf;
	TextButton addBtn;
	TextButton removeBtn;
	
	public AddField(){
		super(Asset.skin);
		setBackground(Asset.skin.getDrawable("dialogDim"));
		tf = new TextField("", Asset.skin);
		addBtn = new TextButton("Add", Asset.skin);
		removeBtn = new TextButton("Remove", Asset.skin);
		add(tf);
		add(addBtn);
		add(removeBtn);
		pack();
		addBtn.addListener(new ClickListener(){
			@Override
	    	public void clicked(InputEvent event, float x, float y){
	    		super.clicked(event, x, y);
	    		if(!tf.getText().isEmpty()){
	    			if(SceneEditor.selectedActor instanceof List){
	    				List list = (List) SceneEditor.selectedActor;
	    				Array<String> arr = new Array<String>(list.getItems());
	    				arr.add(tf.getText());
	    				list.setItems(arr.toArray());
	    				list.pack();
	    				AddField.this.setPosition(list.getX(), list.getY()-AddField.this.getHeight());
	    			}
	    			if(SceneEditor.selectedActor instanceof SelectBox){
	    				SelectBox list = (SelectBox) SceneEditor.selectedActor;
	    				Array<String> arr = new Array<String>(list.getItems());
	    				arr.add(tf.getText());
	    				list.setItems(arr.toArray());
	    				list.pack();
	    				AddField.this.setPosition(list.getX(), list.getY()-AddField.this.getHeight());
	    			}
	    				
	    		}
			}
		});
		removeBtn.addListener(new ClickListener(){
			@Override
	    	public void clicked(InputEvent event, float x, float y){
	    		super.clicked(event, x, y);
	    		if(SceneEditor.selectedActor instanceof List){
		    		List list = (List) SceneEditor.selectedActor;
		    		if(list.getItems().length == 0)
		    			return;
		    		Array<String> arr = new Array<String>(list.getItems());
		    		list.setSelectedIndex(list.getItems().length-1);
		    		arr.removeIndex(list.getSelectedIndex());
		    		list.setItems(arr.toArray());
		    		list.pack();
		    		AddField.this.setPosition(list.getX(), list.getY()-AddField.this.getHeight());
	    		}
	    		if(SceneEditor.selectedActor instanceof SelectBox){
	    			SelectBox list = (SelectBox) SceneEditor.selectedActor;
		    		if(list.getItems().length == 0)
		    			return;
		    		Array<String> arr = new Array<String>(list.getItems());
		    		list.setSelection(list.getItems().length-1);
		    		arr.removeIndex(list.getSelectionIndex());
		    		list.setItems(arr.toArray());
		    		list.pack();
		    		AddField.this.setPosition(list.getX(), list.getY()-AddField.this.getHeight());
	    		}
			}
		});
	}
}