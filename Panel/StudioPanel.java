import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.tools.hiero.HieroPanel;
import com.badlogic.gdx.tools.particleeditor.ParticlePanel;
import com.badlogic.gdx.tools.particleeditor.ParticleRenderer;
import com.badlogic.gdx.utils.Array;

class SceneEditor extends Scene {
	
	public static Actor selectedActor = null;
	public static boolean reloadAssets = false;
	static AddField addField;
	DragAndDrop dragAndDrop = new DragAndDrop();
	

	public SceneEditor(){
		super();
		if(reloadAssets){
			Asset.loadBlocking();  // this is the first time this Scene is created by the Stage
			Content.assetPanel.updateAsset();
			Frame.scenePanel.showStudio();
			Scene.scenesMap.removeKey("SceneEditor");
			Frame.scenePanel.update();
			reloadAssets = false;
			addField = new AddField();
			Stage.addActor(this);
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
					Main.log(""+rot);
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

final public class StudioPanel extends JPanel  {
	private static final long serialVersionUID = 1L;
	LwjglCanvas can;
	LwjglAWTCanvas canvas;
	
	final Timer saveTimer = new Timer(10000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ScenePanel.save();
		}
	});
	Image img;
	public StudioPanel(){
		super(new VerticalFlowLayout());
		try {
			img = ImageIO.read(Icon.getResourceAsStream("background.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		UIUtils.setMargin(this, new Insets(15,15,15,15));
	}

	
	
	@Override
	public void paint(Graphics g){
		if(!isOpaque())
			Style.drawScreen(g, getWidth(), getHeight());
		super.paint(g);	
		
	}
	
	String config = "{"
		+"title: GdxStudio,"
		+"keepAspectRatio: false,"
		+"showFPS: false,"
		+"loggingEnabled: true,"
		+"}";
	
	public void createHieroCanvas(){
		setOpaque(true);
		removeAll();
		HieroPanel panel = new HieroPanel();
		canvas = new  LwjglAWTCanvas(new HieroPanel.FontRenderer() , false);
		HieroPanel.gamePanel.add(canvas.getCanvas());
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(860, 650));
		UIUtils.setDrawBorder(scrollPane, false);
		add(scrollPane);
	}
	
	public void createParticleCanvas(){
		setOpaque(true);
		removeAll();
		ParticlePanel panel = new ParticlePanel();
		canvas = new  LwjglAWTCanvas(new ParticleRenderer(panel) , false);
		panel.initializeComponents(canvas);
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(860, 650));
		UIUtils.setDrawBorder(scrollPane, false);
		add(scrollPane);
	}
	
	public void createStudioCanvas(){
		removeAll();
		Main.log("Creating Studio Canvas");
		try{
			Stage.configJson = Stage.jsonReader.parse(config);
			Stage.targetWidth = 800; // Once designed in this don't change only change the screen width
			Stage.targetHeight = 480;// Once designed in this don't change only change the screen height
			Stage.debug = true;
			Scene.scenesMap.clear();
			Scene.scenesMap.put("SceneEditor", "");
			//can = new LwjglCanvas(stage, false);
			setOpaque(false);
			canvas = new  LwjglAWTCanvas(Stage.getInstance() , false);
			JScrollPane scrollPane = new JScrollPane(canvas.getCanvas());
			scrollPane.setPreferredSize(new Dimension(800, 480));
			UIUtils.setDrawBorder(scrollPane, false);
			add(scrollPane);
			canvas.getCanvas().setDropTarget(new DropTarget() {
				private static final long serialVersionUID = 1L;
				public synchronized void drop(DropTargetDropEvent event) {
					try{
						Transferable transferable = event.getTransferable();
						if( transferable.isDataFlavorSupported(DataFlavor.stringFlavor )){
							event.acceptDrop( DnDConstants.ACTION_MOVE );
							String s = (String)transferable.getTransferData(DataFlavor.stringFlavor);
							createActor(s);
							event.getDropTargetContext().dropComplete(true);
						}
						else{
							event.rejectDrop();
						}
					}
					catch( Exception exception ){
						System.err.println( "Exception" + exception.getMessage() );
						event.rejectDrop();
					}
				}
			});
			canvas.getCanvas().addFocusListener(new FocusListener(){
				@Override
				public void focusGained(FocusEvent arg0) {
					saveTimer.start();
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					ScenePanel.save();
					saveTimer.stop();
				}
			});
			Asset.save();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void destroyCanvas(){
		Main.log("Destroying Scene Canvas");
		if(canvas != null){
			remove(canvas.getCanvas());
			canvas.stop();
			canvas = null;
		}
	}
	
	private int getNameCount(Actor actor){
		int count = 1;
		for(Actor child: Stage.getChildren())
			if(actor.getClass().equals(child.getClass())) count++;
		return count;
	}
	
	
	public void setName(Actor actor){
		String name = actor.getClass().getSimpleName()+getNameCount(actor);
		if(Stage.findActor(name) == null)
			actor.setName(name);
		else
			actor.setName(name+"_1");
		actor.setX(Stage.mouse.x/2);
		actor.setY(Stage.mouse.y/2);
		Stage.addActor(actor);
		Frame.actorPanel.addActor(actor.getName());
		SceneEditor.isDirty = true;
		Effect.createEffect(actor, EffectType.ScaleInOut, 1.5f, 0.5f, InterpolationType.Linear);
	}
	
	public void createActor(String type){
		switch(type){
			case "Label":
				if(Asset.fontMap.size != 0){
					LabelStyle ls = new LabelStyle();
					ls.font = Asset.fontMap.firstValue();
					Label label = new Label("Text", ls);
					setName(label);
				}
				break;
			case "Image":
				if(Asset.texMap.size != 0){
					setName(new ImageJson(Asset.texMap.firstKey()));
				}
				break;
			case "Texture":setName(new ImageJson(Content.assetPanel.list.getSelectedValue()));break;
			case "Sprite":setName(new Sprite(1f, Asset.texMap.firstKey()));break;
			case "Particle":
				//ParticleEffect pe = new ParticleEffect();
				//pe.load(effectFile, imagesDir);
				break;
	
			case "Button":setName(new Button(Asset.skin));break;
			case "TextButton":setName(new TextButton("Text", Asset.skin));break;
			case "TextField":setName(new TextField("", Asset.skin));break;
			case "Table":setName(new Table(Asset.skin));break;
			case "CheckBox":setName(new CheckBox("Check", Asset.skin));break;
			case "SelectBox":setName(new SelectBox(new String[]{"First","Second","Third"}, Asset.skin));break;
			case "List":setName(new List(new String[]{"First","Second","Third"}, Asset.skin));break;
			case "Slider":setName(new Slider(0, 10, 1, false, Asset.skin));break;
			case "Dialog":setName(new Dialog("Title", Asset.skin));break;
			case "Touchpad":setName(new Touchpad(5, Asset.skin));break;
			case "Map":setName(new Map(1, 24));break;
			
			case "None":break;
			default:break;
		}
	}
	
	public static void updateAssets(){
		StringBuilder sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"font").list()){
			if(filename.endsWith(".fnt")){
				Main.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("font", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"atlas").list()){
			if(filename.endsWith(".atlas")){
				Main.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("atlas", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"sound").list()){
			if(filename.endsWith(".mp3")){
				Main.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("sound", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"music").list()){
			if(filename.endsWith(".mp3")){
				Main.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("music", sb.toString());
	}
	
	private static int counter = 1;
    public static void saveScreenshot(){
        try{
            FileHandle fh;
            do{
                fh = new FileHandle("shots/shot" + counter++ + ".png");
            }while (fh.exists());
            Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
        }catch (Exception e){           
        }
    }

    private static Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY){
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);

        final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);

        final int numBytes = w * h * 4;
        byte[] lines = new byte[numBytes];
        if (flipY){
            pixels.clear();
            pixels.get(lines);
        }else{
            final int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++){
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        }
        return pixmap;
    }
}