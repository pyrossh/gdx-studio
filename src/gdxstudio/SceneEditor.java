package gdxstudio;

import java.awt.Cursor;

import scene2d.Asset;
import scene2d.Scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
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

public class SceneEditor extends Scene {
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
					Scene.scenesMap.removeKey("gdxstudio.SceneEditor");
					Frame.scenePanel.update();
				}
			}, 1f);
			reloadAssets = false;
			addField = new AddField();
		}
		else{
			Frame.scenePanel.showStudio();
			Scene.scenesMap.removeKey("gdxstudio.SceneEditor");
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
		Scene.getCurrentScene().outline(actor);
		Frame.actorPanel.list.setSelectedIndex(Frame.actorPanel.indexOf(actor.getName()));
		StatusBar.updateSelected(actor.getName());
		Frame.dashPanel.update();
		Frame.propertyPanel.update();
		Frame.effectPanel.update();
		Frame.eventPanel.update();
		Frame.actorPanel.unlock();
		if(selectedActor instanceof List || selectedActor instanceof SelectBox){
			addField.setPosition(actor.getX(), actor.getY() - addField.getHeight());
			Scene.getRoot().addActor(addField);
		}
	}

	
	@Override
	public void onClick(Actor actor) {
		doClick(actor);
	}
	
	boolean dragging;
	int edge;
	float startX, startY, lastX, lastY;
	@Override
	public void onTouchDown(Actor actor) {
		if (Scene.mouseButton == 0) {
			edge = 0;
			float x = Scene.mouse.x;
			float y = Scene.mouse.y;
			if (x > actor.getX() + actor.getWidth() - 10) edge |= Align.right;
			if (y > actor.getY() + actor.getHeight() - 10) edge |= Align.top;
			//if (x < actor.getX() + 20 && y < actor.getY() + 20) edge = Align.left; no rotation
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
		if(selectedActor != null){
			if(!dragging){
				selectedActor.setPosition(Scene.mouse.x, Scene.mouse.y);
				Frame.propertyPanel.updateProperty("X", ""+selectedActor.getX(), 0);
				Frame.propertyPanel.updateProperty("Y", ""+selectedActor.getY(), 0);
				StatusBar.updateXY(Scene.mouse.x, Scene.mouse.y);
				
			}
			else
			{
				float x = Scene.mouse.x;
				float y = Scene.mouse.y;
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
					float rot = getAngle(selectedActor.getX()+selectedActor.getOriginX(), 
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
			Scene.isDirty = true;
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
		if(selectedActor != null){
			isRight = false;
			isTop = false;
			//Stage.log("my"+Stage.mouse.y+"top"+selectedActor.getTop());
			if (Scene.mouse.x > selectedActor.getRight() - 10 && Scene.mouse.x < selectedActor.getRight()) {
				Content.studioPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				isHand = false;
				isRight = true;
			}
			if(Scene.mouse.y > selectedActor.getTop() - 10 && Scene.mouse.y < selectedActor.getTop()){
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