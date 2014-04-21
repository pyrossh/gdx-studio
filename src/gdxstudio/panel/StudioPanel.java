package gdxstudio.panel;

import gdxstudio.Content;
import gdxstudio.Frame;
import gdxstudio.GdxStudio;
import gdxstudio.Style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.nio.ByteBuffer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;

import scene2d.Asset;
import scene2d.Effect;
import scene2d.EffectType;
import scene2d.ImageJson;
import scene2d.InterpolationType;
import scene2d.Map;
import scene2d.Scene;
import scene2d.Sprite;
import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;
import web.laf.lite.widget.CenterPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.badlogic.gdx.tools.hiero.HieroPanel;
import com.badlogic.gdx.tools.particleeditor.ParticlePanel;
import com.badlogic.gdx.tools.particleeditor.ParticleRenderer;
import com.badlogic.gdx.utils.Timer.Task;


final public class StudioPanel extends JPanel  {
	private static final long serialVersionUID = 1L;
	LwjglCanvas can;
	public LwjglAWTCanvas canvas;
	JPanel hbox;
	CenterPanel centerPanel;
	JScrollPane scrollPane;
	LwjglApplicationConfiguration config2 = new LwjglApplicationConfiguration();
	
	Image img;
	public StudioPanel(){
		super(new VerticalFlowLayout());
		hbox = new JPanel(new HorizontalFlowLayout());
		hbox.add(new HeaderLabel("Screen Size"));
		hbox.add(new HeaderLabel("Target Size"));
		UIUtils.setMargin(this, new Insets(15,15,15,15));
		config2.width = 640;
		config2.height = 320;
		config2.fullscreen = false;
		config2.useGL20 = false;
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
		GdxStudio.log("Creating Studio Canvas");
		try{
			Scene.configJson = Scene.jsonReader.parse(config);
			Scene.debug = true;
			Scene.scenesMap.clear();
			Scene.scenesMap.put("gdxstudio.SceneEditor", "");
			setOpaque(false);
			canvas = new  LwjglAWTCanvas(Scene.app , false);
			scrollPane = new JScrollPane(canvas.getCanvas());
			scrollPane.setPreferredSize(new Dimension(800, 480));
			UIUtils.setDrawBorder(scrollPane, false);
			Style.setScreenSize(scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height);
			centerPanel = new CenterPanel(scrollPane, true, false);
			centerPanel.setBackground(Color.black);
			UIUtils.setUndecorated(centerPanel, true);
			UIUtils.setMargin(centerPanel, new Insets(0,0,0,0));
			add(centerPanel);
			validate();
			Style.setScreenPosition(scrollPane.getLocation().x, scrollPane.getLocation().y);
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
			Asset.save();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setGdxScreenSize(int width, int height){
		removeAll();
		canvas.getCanvas().setSize(width, height);
		scrollPane = new JScrollPane(canvas.getCanvas());
		scrollPane.setPreferredSize(new Dimension(width, height));
		UIUtils.setDrawBorder(scrollPane, false);
		Style.setScreenSize(scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height);
		scrollPane.setPreferredSize(new Dimension(width, height));
		centerPanel = new CenterPanel(scrollPane, true, false);
		centerPanel.setBackground(Color.black);
		UIUtils.setUndecorated(centerPanel, true);
		UIUtils.setMargin(centerPanel, new Insets(0,0,0,0));
		add(centerPanel);
		validate();
		Style.setScreenPosition(scrollPane.getLocation().x, scrollPane.getLocation().y);
		canvas.makeCurrent();
        try {
			GLContext.useContext(canvas);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void revalidateScreenPosition(){
		if(scrollPane != null){
			validate();
			Style.setScreenPosition(canvas.getCanvas().getLocation().x, canvas.getCanvas().getLocation().y);
		}
	}
	
	public void destroyCanvas(){
		GdxStudio.log("Destroying Scene Canvas");
		if(canvas != null){
			remove(canvas.getCanvas());
			canvas.stop();
			canvas = null;
		}
	}
	
	private int getNameCount(Actor actor){
		int count = 1;
		for(Actor child: Scene.getCurrentScene().getChildren())
			if(actor.getClass().equals(child.getClass())) count++;
		return count;
	}
	
	
	public void setName(final Actor actor){
		com.badlogic.gdx.utils.Timer.schedule(new Task(){
			@Override
			public void run() {
				String name = actor.getClass().getSimpleName()+getNameCount(actor);
				if(Scene.getCurrentScene().findActor(name) == null)
					actor.setName(name);
				else
					actor.setName(name+"_1");
				actor.setX(Scene.mouse.x-actor.getWidth()/2);
				actor.setY(Scene.mouse.y-actor.getHeight()/2);
				Scene.getCurrentScene().addActor(actor);
				Frame.actorPanel.addActor(actor.getName());
				Scene.isDirty = true;
				Effect.createEffect(actor, EffectType.ScaleInOut, 1.5f, 0.5f, InterpolationType.Linear);
			}
		}, 0.1f);
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
				GdxStudio.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("font", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"atlas").list()){
			if(filename.endsWith(".atlas")){
				GdxStudio.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("atlas", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"sound").list()){
			if(filename.endsWith(".mp3")){
				GdxStudio.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("sound", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"music").list()){
			if(filename.endsWith(".mp3")){
				GdxStudio.log("Loading :"+filename);
				sb.append(filename);
				sb.append(",");
			}
		}
		Asset.assetMap.put("music", sb.toString());
		sb = new StringBuilder();
		for(String filename: new File(Asset.basePath+"model").list()){
			if(filename.endsWith(".obj") || filename.endsWith(".g3db")){
				GdxStudio.log("Loading :"+filename);
				Asset.modelMap.add(filename);
			}
		}
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