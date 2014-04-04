import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/** The Main Entry Point for the Game is the Stage class
 * <p>
 * It consists of a single Stage2d, Stage3d, Camera2d, and Camera3d which are all initialized based on the config file.
 * The root of the stage can be accessed in a statically {@link #getRoot()} and methods related to camera like moveTo, moveBy,
 * are also accessed the same way.<br>
 * It has extra things like stateTime, gameUptime, pauseState, PauseListeners, ResumeListeners, DisposeListeners.<br>
 * 
 * It has automatic asset unloading and disposing and you can use {@link #exit()} to quit your game safely
 * 
 * Note: Your TMX maps have to be unloaded manually as they can be huge resources needing to be freed early.
 * 
 * It has static methods which can be used for panning the camera using mouse, keyboard, drag.. etc.
 * It can also automatically follow a actor by using followActor(Actor actor)<br>
 * 
 * This class will register all your scenes based on your scene.json file and then you can switch you scenes by using {@link #setScene}
 * method with the sceneClassName.<br>
 * 
 * Run the Desktop Game by using Stage class as it contains the static main declaration.<br>
 * Your first sceneName in the config.json file gets shown first automatically and once and all your assets <br>
 * are loaded in the background(asynchronously) in the first scene and then automatically the next scene in the list is set.
 * You can stop the stage from switching to the next scene by setting Asset.loadAsynchronous = false in your first scene but then
 * you have to load all the assets by using the blocking call Asset.loadBlocking()
 * 
 * If you want to display your splash screen for more time after the assets have loaded then you can change
 * Stage.splashDuration to the amount of time you want you splash screen to show. By default the splash screen's
 * lifetime is finished once the assets are loaded and then the next scene is set.
 * 
 * <p>
 * @ex
 * <pre>
 * <code>
    //This is our first Scene and it shows the libgdx logo until all the assets are loaded 
    //then it automatically switches to the Menu scene
    public class  Splash extends Scene {
		
		public Splash() {
			Stage.splashDuration = 5f; // This will make my splash scene to wait 5 seconds after assets are all loaded
			final Texture bg1 = new Texture("splash/libgdx.png");
			final Image imgbg1 = new Image(bg1);
			imgbg1.setFillParent(true);
			Stage.addActor(imgbg1);
	    } 
   }
   
    //This is Scene gets called once the assets are loaded
    public class  Menu extends Scene {
    
		public Menu() {
			//create some actors
			// if you used sink studio and create a scene like Menu.json then
			// it will automatically call load("Menu") it will populate your scene after parsing the json file
			
			//you can access these objects like this
			TextButton btn = (TextButton) Stage.findActor("TextButton1");
			Image img = (Image) Stage.findActor("Image5");
			
			// these actors are loaded from the json file and are give names which allows
			// easy access to them
		}
	}
 </code>
 </pre>
 * @author pyros2097 */

public final class Stage implements ApplicationListener {
	private static Stage instance;
	public static float stateTime = 0;
	public static float gameUptime = 0;
	private float startTime = System.nanoTime();
	
	public static final Json json = new Json();
	public static final JsonReader jsonReader = new JsonReader();
	public static JsonValue configJson = null;
	
	private static com.badlogic.gdx.scenes.scene2d.Stage stage2d;
	private static OrthographicCamera camera;
	private static Label fpsLabel;
	private static Stage3d stage3d;
	private static CameraInputController camController;
	public static boolean use3d = true;
	
	private static Scene currentScene = null;
	private static int sceneIndex = 0;
	private static final Array<Actor> hudActors = new Array<Actor>();
	
	/*Important:
	 *  The Target Width  and Target Height refer to the nominal width and height of the game for the
	 *  graphics which are created  for this width and height, this allows for the Stage to scale this
	 *  graphics for all screen width and height. Therefore your game will work on all screen sizes 
	 *  but maybe blurred or look awkward on some devices.
	 *  ex:
	 *  My Game targetWidth = 800 targetHeight = 480
	 *  Then my game works perfectly for SCREEN_WIDTH = 800 SCREEN_HEIGHT = 480
	 *  and on others screen sizes it is just zoomed/scaled but works fine thats all
	 */
	public static float targetWidth = 800;
	public static float targetHeight  = 480;
	public static boolean pauseState = false;
	public static float splashDuration = 0f;
	public static Color bgColor = new Color(1f,1f,1f,1f);
	
	//Studio Related Stuff
	/* Selected Actor can never be null as when the stage is clicked it may return an actor or the root actor */
	private static ShapeRenderer shapeRenderer;
	public static boolean debug = false;
	public static ClassLoader cl = null;
	
	public static int dots = 40;
	public static int xlines;
	public static int ylines;
	
	public final static Vector2 mouse = new Vector2();
	public static int pointer = 0;
	public static int button = 0;
	private final static Actor selectionBox = new Actor(){
		@Override
		public void draw(Batch batch, float alpha){
			batch.end();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(getX(), getY(), 
						getWidth(),getHeight());
			shapeRenderer.end();
			batch.begin();
		}
	};
	static {
		selectionBox.setTouchable(Touchable.disabled);
		selectionBox.setName("Shape");
	};
	
	/** The Main Launcher for Stage Game
	 * <p>
	 * Just specify the Stage class as the Main file and when you export your game to jar add
	 * the manifest entry Main-Class: Stage for it to work. 
	 */
	public static void main(String[] argc) {
		final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		configJson = jsonReader.parse(Stage.class.getClassLoader().getResourceAsStream("config"));
		if(configJson.getBoolean("hasIcon"))
			cfg.addIcon("icon.png", FileType.Internal);
		String[] screen = configJson.getString("screenSize").split("x");
		String[] target = configJson.getString("targetSize").split("x");
		cfg.width = Integer.parseInt(screen[0]);
		cfg.height = Integer.parseInt(screen[1]);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		cfg.x = (int) ((dimension.getWidth() - cfg.width) / 2);
		cfg.y = (int) ((dimension.getHeight() - cfg.height) / 2);
		cfg.resizable = configJson.getBoolean("resize");
		cfg.forceExit =  configJson.getBoolean("forceExit");
		cfg.fullscreen =  configJson.getBoolean("fullScreen");
		cfg.useGL20 = configJson.getBoolean("useGL20");
		cfg.vSyncEnabled = configJson.getBoolean("vSync");
		cfg.audioDeviceBufferCount = configJson.getInt("audioBufferCount");
		LwjglApplicationConfiguration.disableAudio = configJson.getBoolean("disableAudio");
		targetWidth = Integer.parseInt(target[0]);
		targetHeight = Integer.parseInt(target[1]);
		new LwjglApplication(Stage.getInstance(), cfg);
	}
	
	/*
	 * Singleton Pattern
	 */
	private Stage(){}
	
	public static Stage getInstance(){
		if(instance == null)
			instance = new Stage();
		return instance;
	}
	
	/*
	 * This is where the stage and camera are created and the scenes are created
	 * dynamically
	*/
	@Override
	public final void create() {
		log("Created");
		Config.setup();
		stage2d = new com.badlogic.gdx.scenes.scene2d.Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				configJson.getBoolean("keepAspectRatio"));
		stage2d.getRoot().setName("Root");
		stage2d.getRoot().setTouchable(Touchable.childrenOnly);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, targetWidth, targetHeight);
		camera.position.set(targetWidth/2, targetHeight/2, 0);
		stage2d.setCamera(camera);
		Gdx.input.setCatchBackKey(true);
 		Gdx.input.setCatchMenuKey(true);
 		Gdx.input.setInputProcessor(stage2d);
 		stage2d.addListener(touchInput);
 		if(use3d){
 			stage3d = new Stage3d();
	 		camController = new CameraInputController(stage3d.getCamera());
	 		//Gdx.input.setInputProcessor(camController);
 		}
 		shapeRenderer = new ShapeRenderer();
 		Serializer.initialize();
 		JsonValue sv = jsonReader.parse(Gdx.files.internal(Asset.basePath+"scene"));
 		for(JsonValue jv: sv.iterator())
 			Scene.scenesMap.put(jv.name, jv.asString());
 		setScene(Scene.scenesMap.firstKey());
 		xlines = (int)Gdx.graphics.getWidth()/dots;
 		ylines = (int)Gdx.graphics.getHeight()/dots;
	}
	
	/*
	 * This is the main rendering call that updates the time, updates the stage,
	 * loads assets asynchronously, updates the camera and FPS text.
	 */
	@Override
	public final void render(){
		if (System.nanoTime() - startTime >= 1000000000) {
			gameUptime +=1 ;
			startTime = System.nanoTime();
		}
		stateTime += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT);
		Asset.load();
		stage2d.act();//Gdx.graphics.getDeltaTime();
		stage2d.draw();
		updateController();
		if(use3d){
			stage3d.act();
	    	stage3d.draw();
	    	camController.update();
		}
		if(debug){
			shapeRenderer.begin(ShapeType.Point);
			shapeRenderer.setColor(Color.BLACK);
			for(int i = 0; i<=xlines; i++)
				for(int j = 0; j<=ylines; j++)
					shapeRenderer.point(2+i*dots, j*dots, 0);
			shapeRenderer.end();
		}
		if (fpsLabel != null && configJson.getBoolean("showFps"))
			fpsLabel.setText("Fps: " + Gdx.graphics.getFramesPerSecond());
 	}
	
	public static void outline(Actor actor){
		selectionBox.setPosition(actor.getX(), actor.getY());
		selectionBox.setSize(actor.getWidth(), actor.getHeight());
		Stage.addActor(selectionBox);
	}
	/*
	 * This will resize the stage accordingly to fit to your target width and height
	 */
	@Override
	public final void resize(int width, int height) {
		log("Resize");
		stage2d.setViewport(targetWidth, targetHeight, configJson.getBoolean("keepAspectRatio"));
		if(use3d)
			stage3d.setViewport(targetWidth, targetHeight, configJson.getBoolean("keepAspectRatio"));
	}

	/*
	 * This will pause any music and stop any sound being played
	 * and will fire the Pause event
	 */
	@Override
	public final void pause() {
		log("Pause");
		Asset.musicPause();
		Asset.soundStop();
		pauseState = true;
		currentScene.onPause();
	}

	/*
	 * This will resume any music currently being played
	 * and will fire the Resume event
	 */
	@Override
	public final void resume() {
		log("Resume");
		Asset.musicResume();
		pauseState = false;
		currentScene.onResume();
	}

	/*
	 * When disposed is called
	 * It will automatically unload all your assets and dispose the stage
	 */
	@Override
	public final void dispose() {
		log("Disposing");
		currentScene.onDispose();
		stage2d.dispose();
		Asset.unloadAll();
		Config.writeTotalTime(gameUptime);
		Gdx.app.exit();
	}
	
	/*
	 * Use this to exit your game safely
	 * It will automatically unload all your assets and dispose the stage
	*/
	public static final void exit(){
		log("Disposing and Exiting");
		currentScene.onDispose();
		stage2d.dispose();
		Asset.unloadAll();
		Config.writeTotalTime(gameUptime);
		Gdx.app.exit();
	}

	public static void log(String log) {
		if(configJson.getBoolean("loggingEnabled"))
			Gdx.app.log("Stage ", log);
	}
	
	public static Group getRoot(){
		return stage2d.getRoot();
	}
	
	public static void clearAllListeners(){
		hudActors.clear();
	}
	
	/**
	 * Get screen time from start in format of HH:MM:SS. It is calculated from
	 * "secondsTime" parameter.
	 * */
	public static String toScreenTime(float secondstime) {
		int seconds = (int)(secondstime % 60);
		int minutes = (int)((secondstime / 60) % 60);
		int hours =  (int)((secondstime / 3600) % 24);
		return new String(addZero(hours) + ":" + addZero(minutes) + ":" + addZero(seconds));
	}
	
	private static String addZero(int value){
		String str = "";
		if(value < 10)
			 str = "0" + value;
		else
			str = "" + value;
		return str;
	}
	
/***********************************************************************************************************
* 					Scene Related Functions											   		   	           *
************************************************************************************************************/	
	/**
	 * Set the current scene to be displayed
	 * @param className The scene's Class Name
	 **/
	public static void setScene(String className){
		if(!Scene.scenesMap.containsKey(className)){
			log(className+": Scene Does not Exist");
			return;
		}
		/*
		 *  Every time the scene Changes disable panning/following/listening as it can cause the 
		 *  camera to move around aimlessly, thus abrupting the scene and clear 
		 *  all the listeners/huds that were registered in the previous scene
		 */
		sceneIndex = Scene.scenesMap.keys().toArray().indexOf(className, false);
		if(currentScene != null)
			currentScene.onDispose();
		clear();
		clear3d();
		stage2d.addListener(touchInput);
		stage2d.getRoot().setPosition(0, 0);
		stage2d.getRoot().setSize(targetWidth, targetHeight);
		stage2d.getRoot().setBounds(0,0,targetWidth,targetHeight);
		stage2d.getRoot().setColor(1f, 1f, 1f, 1f);
		stage2d.getRoot().setVisible(true);
		stage3d.getRoot().setPosition(0, 0, 0);
		stage3d.getRoot().setVisible(true);
		try {
			if(cl != null)
				currentScene = (Scene) cl.loadClass(className).newInstance();
			else
				currentScene = (Scene) Class.forName(className).newInstance();
			//addActor(currentScene);
		} catch (InstantiationException e) {
			log("Scene cannot be created , Check if scene class constructor is empty");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log("Scene cannot be created , Check if scene class can be found");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log("Scene cannot be created , Check if scene class can be found");
			e.printStackTrace();
		}
		if (fpsLabel != null && configJson.getBoolean("showFPS")){
			fpsLabel.setPosition(targetWidth - 80, targetHeight - 20);
			addHud(fpsLabel);
		}
	}
	
	/**
	 * Set the current scene to be displayed with an amount of delay
	 * @param className The registered scene's name
	 **/
	public static void setSceneWithDelay(final String className, float delay){
		Timer.schedule(new Task(){
			@Override
			public void run() {
				setScene(className);
			}
		}, delay);
	}
	
	/**
	 * Returns the current scene being Displayed on stage
	 **/
	public static Scene getScene(){
		return currentScene;
	}
	
	/**
	 * Changes to the next scene in the scnesList
	 **/
	public static void nextScene(){
		if(sceneIndex <= Scene.scenesMap.size)
			sceneIndex++;
		setScene(Scene.scenesMap.getKeyAt(sceneIndex));
	}
	
	/**
	 * Changes to the previous scene in the scnesList
	 **/
	public static void prevScene(){
		if(sceneIndex >= 0)
			sceneIndex--;
		setScene(Scene.scenesMap.getKeyAt(sceneIndex));
	}
	
	/**
	 * Changes to the next scene in the scnesList
	 **/
	public static void nextSceneWithDelay(float delay){
		if(sceneIndex <= Scene.scenesMap.size)
			sceneIndex++;
		setSceneWithDelay(Scene.scenesMap.getKeyAt(sceneIndex), delay);
	}
	
	/**
	 * Changes to the previous scene in the scnesList
	 **/
	public static void prevSceneWithDelay(float delay){
		if(sceneIndex >= 0)
			sceneIndex--;
		setSceneWithDelay(Scene.scenesMap.getKeyAt(sceneIndex), delay);
	}
	
	/**
	 * This loads the fonts for fps and logPane from the skin file. This is called by Asset once the
	 * assets are done loading
	 * */
	static void setup(){
		fpsLabel = new Label("", Asset.skin);
		fpsLabel.setName("Fps");
	}
	
	public static void addActor(Actor actor){
		stage2d.addActor(actor);
	}
	
	public static void addActor(Actor actor, float x, float y){
		actor.setPosition(x, y);
		stage2d.addActor(actor);
	}
	
	public static void addActorWithDelay(final Actor actor, float delay){
		Timer.schedule(new Task(){
			@Override
			public void run() {
				addActor(actor);
			}
		}, delay);
	}
	
	/* If you want to make any elements/actors to move along with the camera 
	* like HUD's add them using this method */
	public static void addHud(Actor actor){
		addActor(actor);
		hudActors.add(actor);
	}
	
	/* If you want to any elements/actors which was a Hud the use this */
	public static void removeHud(Actor actor){
		removeActor(actor);
		hudActors.removeValue(actor, false);
	}
	
	public static void clearAllHud(){
		hudActors.clear();
	}
	
	public static boolean removeActor(Actor actor){
		return stage2d.getRoot().removeActor(actor);
	}
	
	public static boolean removeActor(String actorName){
		return removeActor(findActor(actorName));
	}
	
	public static void removeActorWithDelay(Actor actor, float delay){
		addAction(Actions.sequence(Actions.delay(delay), Actions.removeActor(actor)));
	}
	
	public static void addAction(Action action) {
		stage2d.addAction(action);
	}
	
	public static void removeAction(Action action) {
		stage2d.getRoot().removeAction(action);
	}
	
	public static Actor findActor(String actorName){
		return stage2d.getRoot().findActor(actorName);
	}
	
	/*
	 * This method makes sure the actor is not null and actors's name is not null
	 * or actor is not root/fps/shape
	 */
	public static boolean isValidActor(Actor actor){
		if(actor != null){
			String name = actor.getName();
			if(name != null){
				if(name.equals("Root") && name.equals("Fps") && name.equals("Shape")){
					return false;
				}
				else{
					return true;
				}
			}
		}
		return false;
	}
	
	public static SnapshotArray<Actor> getChildren(){
		return stage2d.getRoot().getChildren();
	}
	
	public static Actor hit(float x, float y){
		return stage2d.getRoot().hit(x, y, true);
	}
	
	public static void clear(){
		if(currentScene != null)
			currentScene.onDispose();
		disablePanning();
		followActor(null);
		hudActors.clear();
		resetCamera();
		stage2d.clear();
	}
	
	private static Image imgbg = null;
	public static void setBackground(String texName) {
		if(imgbg != null)
			removeBackground();
		if(Asset.tex(texName) != null){
			imgbg = new Image(new TextureRegionDrawable(Asset.tex(texName)), Scaling.stretch);
			imgbg.setFillParent(true);
			stage2d.addActor(imgbg);
			imgbg.toBack();
		}
	}
	
	public static void removeBackground() {
		stage2d.getRoot().removeActor(imgbg);
	}
	
	public static void showToast(String message, float duration){
		Table table = new Table(Asset.skin);
		table.add("   "+message+"   ");
		table.setBackground(Asset.skin.getDrawable("dialogDim"));
		table.pack();
		table.setPosition(targetWidth/2 - table.getWidth(), targetHeight/2 - table.getHeight());
		addActor(table);
		table.addAction(Actions.sequence(Actions.delay(duration), Actions.removeActor(table)));
	}
	
	public static void showMessageDialog(String title, String message){
		Dialog dialog = new Dialog(title, Asset.skin);
		dialog.getContentTable().add(message);
		dialog.button("OK", "OK");
		dialog.pack();
		dialog.show(stage2d);
	}
	
	public static boolean showConfirmDialog(String title, String message){
		Dialog dialog = new Dialog(title, Asset.skin);
		dialog.button("Yes", "Yes");
		dialog.button("No", "No");
		dialog.pack();
		dialog.show(stage2d);
		//if(dialog.result().equals("Yes")) //update Gdx
		//	return true;
		return false;
	}
    
/***********************************************************************************************************
* 					Camera Related Functions											   		   	       *
************************************************************************************************************/	
    private static float duration;
    private static float time;
	private static Interpolation interpolation;
    private static boolean moveCompleted;
    private static float lastPercent;
    private static float percentDelta;
    private static float panSpeedX, panSpeedY;
    //private static final Vector3 mousePos = new Vector3();
    
    public void moveTo(float x, float y) {
        moveTo(x, y, 0f);
    }
    
    public void moveTo(float x, float y, float duration) {
        moveBy(x-camera.position.x, y-camera.position.y, duration);
    }
    
    public static void moveBy (float amountX, float amountY) {
         moveBy(amountX, amountY, 0, null);
    }

    public static void moveBy (float amountX, float amountY, float duration) {
         moveBy(amountX, amountY, duration, null);
    }

    public static void moveBy (float amountX, float amountY, float dur, Interpolation interp) {
    	duration = dur;
     	interpolation = interp;
     	panSpeedX = amountX;
     	panSpeedY = amountY;
     	lastPercent = 0;
     	time = 0;
        moveCompleted = false;
    }
    
    private void moveByAction(float delta){
        time += delta;
        moveCompleted = time >= duration;
        float percent;
        if (moveCompleted)
                percent = 1;
        else {
            percent = time / duration;
            if (interpolation != null) percent = interpolation.apply(percent);
        }
        percentDelta = percent - lastPercent;
    	camera.translate(panSpeedX * percentDelta, panSpeedY * percentDelta, 0);
    	for(Actor actor: Stage.hudActors) 
    		actor.setPosition(actor.getX()+panSpeedX * percentDelta, actor.getY()+panSpeedY * percentDelta);
        lastPercent = percent;
        if (moveCompleted) interpolation = null;
    }
    
    public static void resetCamera(){
    	camera.position.set(targetWidth/2, targetHeight/2, 0);
    }
    
	public static OrthographicCamera getCamera(){
		return camera;
	}
    
    public static float getCameraWidth(){
    	return camera.viewportWidth;
    }
    
    public static float getCameraHeight(){
    	return camera.viewportHeight;
    }
    
    public static float getCameraX(){
    	return camera.position.x;
    }
    
    public static float getCameraY(){
    	return camera.position.y;
    }
    
    private static Actor followedActor = null;
	private static float followSpeed = 1f;
	private static float followTopOffset = 60;
	private static float followLeftOffset = 10;
	private static float followBotOffset = 70;
	private static float followRightOffset = 10;
	private static boolean followContinous = false;
	
	/*
	 * This makes the camera follow the actor once and only once. Once the camera reaches its
	 * target, it stops following the actor.
	 */
	public static void followActor(Actor actor){
	    followedActor = actor;
	    followContinous = false;
	}
	
	/*
	 * This makes the camera follow the actor continuously, even after the camera reaches its
	 * target, it keeps following the if the actor changes its position.
	 */
	public static void followActorContinuously(Actor actor){
	    followedActor = actor;
	    followContinous = true;
	}
	
	/*
	 *  This is to set the offsets of camera position when following the actor
	 *  When the camera follows the actor its (x,y) position is set to actor's (x,y) position
	 *  based on followSpeed. The offsets are used to position the camera in such a way that the actor
	 *  doesn't need to be at the center of the camera always
	 */
    public static void setFollowActorOffset(float top, float left, float bot, float right){
    	followTopOffset = top;
    	followLeftOffset = left;
    	followBotOffset = bot;
    	followRightOffset = right;
    }
    
    /*
     * Sets the speed at which the camera follows the actor. By default it moves 1px for a duration of 1f
     * so its speed is 1px/f. So reduce the duration to increase its speed.
     * ex: setPanSpeed(0.5) will change its speed to 2px/f
     * Here: f can/maybe also indicate seconds
     */
    public static void setFollowSpeed(float duration){
    	followSpeed = duration;
    }
	
    private void follow(){
    	//if(camera.position.x == followedActor.getX()+followLeftOffset &&
    	//	camera.position.y == followedActor.getY()+followTopOffset)
    	//return;
    	//moveTo(followedActor.getX()+followLeftOffset, followedActor.getY()+followTopOffset, 100f);
    	if(camera.position.x < followedActor.getX() - followLeftOffset) moveBy(1f, 0, followSpeed);
		else if(camera.position.x > followedActor.getX() + followRightOffset) moveBy(-1f, 0, followSpeed);
		else if(camera.position.y < followedActor.getY() - followBotOffset) moveBy(0, 1f, followSpeed);
		else if(camera.position.y > followedActor.getY() - followTopOffset) moveBy(0, -1f, followSpeed);
		else {
			if(!followContinous)
				followedActor = null;
		}
    }
    
    private void updateController(){
    	mouse.x = Gdx.input.getX();
    	mouse.y = Gdx.graphics.getHeight() - Gdx.input.getY();
    	if(!moveCompleted)
    		moveByAction(stateTime);
    	if(hasControl){
			if(Config.usePan) panCameraWithMouse();
			if(Config.useKeyboard) panCameraWithKeyboard();
		}
		if(followedActor != null)
			follow();
    }
    
/***********************************************************************************************************
* 					2d Controller Related Functions												   	       *
************************************************************************************************************/	
    private static boolean hasControl = false;
    
    /*
	 * This will allow you to move the camera using the keyboard key up, left, right, down 
	 * when Config.useKeyboard is true.
	 * This will allow you to move the camera using the mouse when the mouse reaches the edges of the screen
	 * when Config.usePan is true.
	 * This will allow you to move the camera using the mouse when dragging the mouse
	 * when Config.useDrag is true.
	 */
    public static void enablePanning(){
    	hasControl = true;
    }
    
    /*
     * This disables all automatic panning and moving of camera
     */
    public static void disablePanning(){
    	hasControl = false;
    }
    
	
    
    private float panSpeed = 5f;
    private float panXLeftOffset = 100;
	private float panXRightOffset = 0;//Gdx.graphics.getWidth() - 100;
	private float panYUpOffset = 70;
	private float panYDownOffset = 0;//Gdx.graphics.getHeight() - 70;
	public static float camOffsetX = 160f;
	public static float camOffsetYTop = 110f;
	public static float camOffsetYBot = 65f;
	public static float mapOffsetX = 0;
	public static float mapOffsetY = 0;
	
	
	/*
     * Sets the speed at which the camera pans. By default it moves 1px for a duration a 1f
     * so its speed is 1px/f. So reduce the duration to increase its speed.
     * ex: setPanSpeed(0.5) will change its speed to 2px/f
     * Here: f can/maybe also indicate seconds 
     */
	public void setPanSpeed(float duration){
		panSpeed = duration;
	}
	
	/*
	 *  This sets the boundary of the camera till what position can it move or pan in the
	 *  directions left, right, top, down. This is to prevent is from panning overboard the game area.
	 *  Usually the bounds of the camera is like a rectangle. This mus be calculated carefully
	 *  as the camera's position is based on its center.
	 */
    public void setCameraBoundary(float xLeft, float xRight, float yUp, float dDown){
    	panXLeftOffset = xLeft;
    	panXRightOffset = xRight;
    	panYUpOffset = yUp;
    	panYDownOffset = dDown;
    }
    
    public void setCamOffset(float xOffset, float yOffsetTop, float yOffsetBot){
    	camOffsetX = xOffset;
    	camOffsetYTop = yOffsetTop;
    	camOffsetYBot = yOffsetBot;
    }
    
    private void panCameraWithMouse(){
    	if(mouse.x > panXRightOffset && camera.position.x < mapOffsetX - 5) moveBy(1f, 0, panSpeed);
    	else if(mouse.x < panXLeftOffset && camera.position.x > camOffsetX +5)  moveBy(-1f, 0, panSpeed);
    	else if(mouse.y < panYUpOffset && camera.position.y < mapOffsetY -5) moveBy(0, 1f, panSpeed);
    	else if(mouse.y > panYDownOffset && camera.position.y > camOffsetYBot +5) moveBy(0, -1f, panSpeed);
    }
    	
    private void panCameraWithKeyboard(){
    	if(Gdx.input.isKeyPressed(Keys.LEFT))
    		//if(camera.position.x > camOffsetX +5)
    			moveBy(-panSpeed, 0);
    	else if(Gdx.input.isKeyPressed(Keys.RIGHT))
    		//if(camera.position.x < mapOffsetX - 5)
    			moveBy(panSpeed, 0);
    	else if(Gdx.input.isKeyPressed(Keys.UP))
    		//if(camera.position.y < mapOffsetY -5)
    			moveBy(0, panSpeed);
    	else if(Gdx.input.isKeyPressed(Keys.DOWN))
    		//if(camera.position.y > camOffsetYBot +5)
    			moveBy(0, -panSpeed);
    }
	
	/* 
	 * Gesture related
	 */
	public static float touchDragIntervalRange = 200f; // 200px drag for a gesture event
	public static float touchInitialX = 0.0f, touchInitialY = 0.0f;
	public static float touchCurrentX = 0.0f, touchCurrentY = 0.0f;
	public static float difX;
	public static float difY;
	public static float prevDifX;
	public static float prevDifY;
	public static float touchDifX;
	public static float touchDifY;
	
	public boolean isTouchDragInterval() {
		difX = java.lang.Math.abs(touchInitialX - touchCurrentX);
		difY = java.lang.Math.abs(touchInitialY - touchCurrentY);
		if (difX > touchDragIntervalRange || difY > touchDragIntervalRange) 
			return true;
		else 
			return false;
	}

	public static GestureType getGestureDirection() {
		prevDifX = difX;
		prevDifY = difY;
		difX = java.lang.Math.abs(touchInitialX - touchCurrentX);
		difY = java.lang.Math.abs(touchInitialY - touchCurrentY);
		/**
		 * Get minimal changes on drag
		 * <p> checkMomentumChanges
		 * EXAMPLE<br>
		 * User drags finger to left, suddenly dragging to right without removing
		 * his finger from the screen
		 * */
		if (prevDifX > difX || prevDifY > difY) {
			touchInitialX = touchCurrentX;
			touchInitialY = touchCurrentY;
			//
			difX = 0.0f;
			difY = 0.0f;
			prevDifX = 0.0f;
			prevDifY = 0.0f;
			
			/**
			 * Set touch differences, optional, if you need amount of change from
			 * initial touch to drag, USE THIS: on touchDrag, pan or similar mthods
			 * */
			touchDifX = java.lang.Math.abs(touchInitialX - touchCurrentX);
			touchDifY = java.lang.Math.abs(touchInitialY - touchCurrentY);
		}
		if (touchInitialY < touchCurrentY && difY > difX) {
			return GestureType.Up;
		} else if (touchInitialY > touchCurrentY && difY > difX) {
			return GestureType.Down;
		} else if (touchInitialX < touchCurrentX && difY < difX) {
			return GestureType.Right;
		} else if (touchInitialX > touchCurrentX && difY < difX) {
			return GestureType.Left;
		} else {
			return GestureType.None;
		}
	}
	
	/* Dragging Camera Related */
	private final static Vector3 curr = new Vector3();
	private final static Vector3 last = new Vector3(-1, -1, -1);
	private final static Vector3 deltaDrag = new Vector3();
	private static float deltaCamX = 0;
	private static float deltaCamY = 0;
	
	private static void dragCam(int x, int y){
		camera.unproject(curr.set(x, y, 0));
    	if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
    		camera.unproject(deltaDrag.set(last.x, last.y, 0));
    		deltaDrag.sub(curr);
    		deltaCamX = deltaDrag.x + camera.position.x;
    		deltaCamY = deltaDrag.y + camera.position.y;
    		if(deltaCamX > camOffsetX && deltaCamX < mapOffsetX)
    			moveBy(deltaDrag.x, 0);
    		if(deltaCamY > camOffsetYBot && deltaCamY < mapOffsetY)
    			moveBy(0, deltaDrag.y);		
    	}
    	last.set(x, y, 0);
    }
    
	static boolean gestureStarted = false;
	static Actor validActor = null;
    private final static ClickListener touchInput = new ClickListener(){
    	@Override
    	public void clicked(InputEvent event, float x, float y){
    		super.clicked(event, x, y);
    		mouse.set(x, y);
    		validActor = hit(x,y);
    		if(isValidActor(validActor))
    			currentScene.onClick(validActor);
    	}
    	
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
			super.touchDown(event, x, y, pointer, button);
			mouse.set(x, y);
			Stage.pointer = pointer;
			Stage.button = button;
			touchInitialX = x;
			touchInitialY = y;
			gestureStarted = true;
			validActor = hit(x,y);
			if(isValidActor(validActor))
				currentScene.onTouchDown(validActor);
			return true;
		}
		
		
		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer){
			super.touchDragged(event, x, y, pointer);
			mouse.set(x, y);
			Stage.pointer = pointer;
			currentScene.onDragged();
			if(gestureStarted == true){
				touchCurrentX = x;
				touchCurrentY = y;
				if(getGestureDirection() != GestureType.None){
					gestureStarted = false;
					currentScene.onGesture(getGestureDirection());
				}
			}
			if(hasControl)
				if(Config.useDrag) dragCam((int)x, (int)-y);
		}
		
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button){
			super.touchUp(event, x, y, pointer, button);
			mouse.set(x, y);
			Stage.pointer = pointer;
			Stage.button = button;
			// reset Gesture
			difX = 0.0f;
			difY = 0.0f;
			touchInitialX = 0.0f;
			touchInitialY = 0.0f;
			touchCurrentX = 0.0f;
			touchCurrentY = 0.0f;
			touchDifX = 0.0f;
			touchDifY = 0.0f;
			prevDifX = 0.0f;
			prevDifY = 0.0f;
			// reset Gesture
			currentScene.onTouchUp();
			if(hasControl)
				last.set(-1, -1, -1);
			gestureStarted = false;
		}
	};
	
/***********************************************************************************************************
* 					3d Related Functions												   	       		   *
************************************************************************************************************/	
	
	public static void addActor3d(Actor3d actor3d){
		stage3d.addActor3d(actor3d);
	}
	
	public static void removeActor3d(Actor3d actor3d){
		stage3d.getRoot().removeActor3d(actor3d);
	}
	
	public static void clear3d(){
		stage3d.clear();
	}
	
	public static void getChildren3d(){
		stage3d.getActors3d();
	}
	
	public static void removeActor3d(String actor3dName){
		stage3d.getRoot().removeActor3d(stage3d.getRoot().findActor3d(actor3dName));
	}
	
	public static void resetCamera3d(){
		stage3d.getCamera().position.set(10f, 10f, 10f);
		stage3d.getCamera().lookAt(0,0,0);
		stage3d.getCamera().near = 0.1f;
		stage3d.getCamera().far = 300f;
    }
	
	public static PerspectiveCamera getCamera3d(){
		return stage3d.getCamera();
	}
	
/***********************************************************************************************************
* 					Utilities Related Functions												   	       		   *
************************************************************************************************************/
	
	public static float getAngle(float cx, float cy, float tx, float ty) {
	    float angle = (float) Math.toDegrees(MathUtils.atan2(tx - cx, ty - cy));
	    //if(angle < 0){
	    //    angle += 360;
	    //}
	    return angle;
	}
	
	public static final float getDistance(Actor a, Actor b){
		float dx = java.lang.Math.abs(a.getX() - b.getX());
		float dy = java.lang.Math.abs(a.getY() - b.getY());
		return (float)java.lang.Math.sqrt(dx*dx + dy*dy); 
	}
	
	public static final float getDistance(float x1, float y1, float x2, float y2){
		float dx = java.lang.Math.abs(x1 - x2);
		float dy = java.lang.Math.abs(y1 - y2);
		return (float)java.lang.Math.sqrt(dx*dx + dy*dy); 
	}
	
	/*
	 * Capitalizes the First Letter of a String
	 */
	public static String capitalize(String text){
		if(text != null && text != "")
			return (text.substring(0, 1)).toUpperCase() + text.substring(1);
		else
			return "";
	}

	/*
	 * UnCapitalizes the First Letter of a String
	 */
	public static String uncapitalize(String text){
		return text.substring(0, 1).toLowerCase()+text.substring(1);
	}

	public static Rectangle getBoundingBox(Actor actor) {
		float posX = actor.getX();
		float posY = actor.getY();
		float width = actor.getWidth();
		float height = actor.getHeight();
		return new Rectangle(posX, posY, width, height);
	}

	public static boolean collides(Actor actor, float x, float y) {
		Rectangle rectA1 = getBoundingBox(actor);
		Rectangle rectA2 = new Rectangle(x, y, 5, 5);
		// Check if rectangles collides
		if (Intersector.overlaps(rectA1, rectA2)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean collides(Actor actor1, Actor actor2) {
		Rectangle rectA1 = getBoundingBox(actor1);
		Rectangle rectA2 = getBoundingBox(actor2);
		// Check if rectangles collides
		if (Intersector.overlaps(rectA1, rectA2)) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * TEA Encryption and Decryption
	 */
	public static String teaKey = "Default";
	private static final int delta = 0x9E3779B9;
	private static final int MX(int sum, int y, int z, int p, int e, int[] k) {
		return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
	}
	
	public static final byte[] encrypt(String data) {
		return encrypt(data.getBytes(), teaKey.getBytes());
	}

	public static final byte[] decrypt(String data) {
		return decrypt(data.getBytes(), teaKey.getBytes());
	}

	public static final byte[] encrypt(String data, String key) {
		return encrypt(data.getBytes(), key.getBytes());
	}

	public static final byte[] decrypt(String data, String key) {
		return decrypt(data.getBytes(), key.getBytes());
	}

	private static final byte[] encrypt(byte[] data, byte[] key) {
		if (data.length == 0) 
			return data;
		return toByteArray(encrypt(toIntArray(data, true), toIntArray(key, false)), false);
	}


	private static final byte[] decrypt(byte[] data, byte[] key) {
		if (data.length == 0)
			return data;
		return toByteArray(decrypt(toIntArray(data, false), toIntArray(key, false)), true);
	}

	private static final int[] encrypt(int[] v, int[] k) {
		int n = v.length - 1;
		if (n < 1) {
			return v;
		}
		if (k.length < 4) {
			int[] key = new int[4];

			System.arraycopy(k, 0, key, 0, k.length);
			k = key;
		}
		int z = v[n], y = v[0], sum = 0, e;
		int p, q = 6 + 52 / (n + 1);

		while (q-- > 0) {
			sum = sum + delta;
			e = sum >>> 2 & 3;
	for (p = 0; p < n; p++) {
		y = v[p + 1];
		z = v[p] += MX(sum, y, z, p, e, k);
	}
	y = v[0];
	z = v[n] += MX(sum, y, z, p, e, k);
		}
		return v;
	}

	private static final int[] decrypt(int[] v, int[] k) {
		int n = v.length - 1;

		if (n < 1) {
			return v;
		}
		if (k.length < 4) {
			int[] key = new int[4];

			System.arraycopy(k, 0, key, 0, k.length);
			k = key;
		}
		int z = v[n], y = v[0], sum, e;
		int p, q = 6 + 52 / (n + 1);

		sum = q * delta;
		while (sum != 0) {
			e = sum >>> 2 & 3;
		for (p = n; p > 0; p--) {
			z = v[p - 1];
			y = v[p] -= MX(sum, y, z, p, e, k);
		}
		z = v[n];
		y = v[0] -= MX(sum, y, z, p, e, k);
		sum = sum - delta;
		}
		return v;
	}


	private static final int[] toIntArray(byte[] data, boolean includeLength) {
		int n = (((data.length & 3) == 0)
				? (data.length >>> 2)
						: ((data.length >>> 2) + 1));
		int[] result;

		if (includeLength) {
			result = new int[n + 1];
			result[n] = data.length;
		}
		else {
			result = new int[n];
		}
		n = data.length;
		for (int i = 0; i < n; i++) {
			result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
		}
		return result;
	}


	private static final byte[] toByteArray(int[] data, boolean includeLength) {
		int n = data.length << 2;

		if (includeLength) {
			int m = data[data.length - 1];

			if (m > n) {
				return null;
			}
			else {
				n = m;
			}
		}
		byte[] result = new byte[n];

		for (int i = 0; i < n; i++) {
			result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
		}
		return result;
	}
}