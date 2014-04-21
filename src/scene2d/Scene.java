package scene2d;
import scene3d.Actor3d;
import scene3d.Group3d;
import scene3d.Stage3d;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

abstract public class Scene extends Group {
	/* The array map containing all the scenes data of the game*/
	public static final ArrayMap<String, String> scenesMap = new ArrayMap<String, String>();
	private String sceneName = "";
	public String sceneBackground = "None";
	public String sceneMusic = "None";
	public String sceneTransition = "None";
	public float sceneDuration = 0;
	public InterpolationType sceneInterpolationType = InterpolationType.Linear;
	
	public static Color bgColor = new Color(1f,1f,1f,1f);
	public static JsonValue configJson = null;
	public static final JsonReader jsonReader = new JsonReader();
	public static final Json json = new Json();
	public static float splashDuration = 0f;
	public static boolean pauseState = false;
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
	public static boolean debug = false;
	public final static Vector2 mouse = new Vector2();

	private static Scene currentScene = null;
	private static int sceneIndex = 0;
	
	public static ClassLoader cl = null;
	public static com.badlogic.gdx.scenes.scene2d.Stage stage2d;
	public static Stage3d stage3d;
	private static Label fpsLabel;
	private static InputMultiplexer inputMux;

	public static int mousePointer = 0;
	public static int mouseButton = 0;
	public static boolean cullingEnabled = false;
	public static boolean isDirty = false;
	public static String basePackage = "source.";

	public Scene(){
		setTouchable(Touchable.childrenOnly);
		Camera.reset();
		stage2d.clear();
		stage3d.clear();
		stage2d.addListener(touchInput);
		setSize(targetWidth, targetHeight);
		setBounds(0,0, targetWidth, targetHeight);
		setColor(1f, 1f, 1f, 1f);
		setVisible(true);
		stage2d.getRoot().setPosition(0, 0);
		stage2d.getRoot().setVisible(true);
		stage3d.getRoot().setPosition(0, 0, 0);
		stage3d.getRoot().setVisible(true);
		sceneName = this.getClass().getName();
		setName(sceneName);
		Scene.log("Current Scene: "+sceneName);
		currentScene = this;
		load(sceneName);
		cullingEnabled = true;
	}

	@Override
	public void act(float delta){
		super.act(delta);
		if (fpsLabel != null)
			fpsLabel.setText("Fps: " + Gdx.graphics.getFramesPerSecond());
	}

	public abstract void onClick(Actor actor);
	public abstract void onTouchDown(Actor actor);
	public abstract void onTouchUp();
	public abstract void onDragged();
	public abstract void onGesture(GestureType gestureType);
	public abstract void onKeyTyped(char key);
	public abstract void onKeyUp(int keycode);
	public abstract void onKeyDown(int keycode);
	/*
	 * This will pause any music and stop any sound being played
	 * and will fire the Pause event
	 */
	public abstract void onPause();
	/*
	 * This will resume any music currently being played
	 * and will fire the Resume event
	 */
	public abstract void onResume();
	public abstract void onDispose();

	public void load(String sceneName){
		log("Load "+sceneName);
		if(!sceneName.contains(basePackage) && !sceneName.contains("gdxstudio"))
			sceneName = basePackage+sceneName;
		String[] lines = scenesMap.get(sceneName).split("\n");
		for(String line: lines){
			if(line.trim().isEmpty())
				continue;
			json.fromJson(null, line);
			//log(line);
		}
		if(!sceneBackground.equals("None"))
			setBackground(sceneBackground);
		if(!sceneMusic.equals("None"))
			Asset.musicPlay(sceneMusic);
		if(!sceneTransition.equals("None"))
			Effect.createEffect(Scene.getRoot(), EffectType.valueOf(sceneTransition), 1f,
					sceneDuration, sceneInterpolationType);
	}

	protected void save(){
		save(sceneName);
	}

	public void save(String sceneName){
		if(!isDirty)
			return;
		if(sceneName == null || sceneName.isEmpty())
			return;
		Scene.log("Save "+sceneName);
		if(!sceneName.contains(basePackage) && !sceneName.contains("gdxstudio"))
			sceneName = basePackage+sceneName;
		StringBuilder sb = new StringBuilder();
		for(Actor actor: getChildren()){
			sb.append(json.toJson(actor));
			sb.append("\n");
		}
		sb.append(json.toJson(getRoot().findActor(this.sceneName)));//??Warning
		scenesMap.put(sceneName, sb.toString());
		Gdx.files.local(Asset.basePath+"scene").writeString(json.toJson(scenesMap, ArrayMap.class, 
				String.class), false);
		sb = null;
		isDirty = false;
	}
	
	/**
	 * Set the current scene to be displayed
	 * @param className The scene's Class Name
	 **/
	public static void setScene(String className){
		if(!className.contains(basePackage) && !className.contains("gdxstudio"))
			className = basePackage+className;
		if(!scenesMap.containsKey(className)){
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
		try {
			if(cl != null)
				cl.loadClass(className).newInstance();
			else
				Class.forName(className).newInstance();
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
		//if (fpsLabel != null && configJson.getBoolean("showFPS")){
		//	fpsLabel.setPosition(targetWidth - 80, targetHeight - 20);
		//	Camera.addHud(fpsLabel);
		//}
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
	public static Scene getCurrentScene(){
		return currentScene;
	}

	/**
	 * Changes to the next scene in the scnesList
	 **/
	public static void nextScene(){
		if(sceneIndex <= scenesMap.size)
			sceneIndex++;
		setScene(scenesMap.getKeyAt(sceneIndex));
	}

	/**
	 * Changes to the previous scene in the scnesList
	 **/
	public static void prevScene(){
		if(sceneIndex >= 0)
			sceneIndex--;
		setScene(scenesMap.getKeyAt(sceneIndex));
	}

	/**
	 * Changes to the next scene in the scnesList
	 **/
	public static void nextSceneWithDelay(float delay){
		if(sceneIndex <= scenesMap.size)
			sceneIndex++;
		setSceneWithDelay(Scene.scenesMap.getKeyAt(sceneIndex), delay);
	}

	/**
	 * Changes to the previous scene in the scnesList
	 **/
	public static void prevSceneWithDelay(float delay){
		if(sceneIndex >= 0)
			sceneIndex--;
		setSceneWithDelay(scenesMap.getKeyAt(sceneIndex), delay);
	}

	private static Image imgbg = null;
	public void setBackground(String texName) {
		if(imgbg != null)
			removeBackground();
		if(Asset.tex(texName) != null){
			imgbg = new Image(new TextureRegionDrawable(Asset.tex(texName)), Scaling.stretch);
			imgbg.setFillParent(true);
			stage2d.addActor(imgbg);
			imgbg.toBack();
		}
	}

	public void removeBackground() {
		getRoot().removeActor(imgbg);
	}
	
	public static Group getRoot(){
		return stage2d.getRoot();
	}
	
	public void addActor(Actor actor, float x, float y){
		if(actor != null){
			actor.setPosition(x, y);
			addActor(actor);
		}
	}

	public void addActorWithDelay(final Actor actor, float delay){
		Timer.schedule(new Task(){
			@Override
			public void run() {
				addActor(actor);
			}
		}, delay);
	}

	public boolean removeActor(String actorName){
		return removeActor(findActor(actorName));
	}

	public void removeActorWithDelay(Actor actor, float delay){
		addAction(Actions.sequence(Actions.delay(delay), Actions.removeActor(actor)));
	}

	public Actor hit(float x, float y){
		return hit(x, y, true);
	}


	/* Dragging Camera Related */
	static boolean gestureStarted = false;
	static Actor validActor = null;
	private final ClickListener touchInput = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			super.clicked(event, x, y);
			mouse.set(x, y);
			validActor = hit(x,y);
			if(validActor != null && validActor.getName() != null)
				Scene.getCurrentScene().onClick(validActor);
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
			mouse.set(x, y);
			mousePointer = pointer;
			mouseButton = button;
			touchInitialX = x;
			touchInitialY = y;
			gestureStarted = true;
			validActor = hit(x,y);
			if(validActor != null && validActor.getName() != null)
				Scene.getCurrentScene().onTouchDown(validActor);
			return super.touchDown(event, x, y, pointer, button);
		}


		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer){
			super.touchDragged(event, x, y, pointer);
			mouse.set(x, y);
			mousePointer = pointer;
			Scene.getCurrentScene().onDragged();
			if(gestureStarted == true){
				touchCurrentX = x;
				touchCurrentY = y;
				if(getGestureDirection() != GestureType.None){
					gestureStarted = false;
					Scene.getCurrentScene().onGesture(getGestureDirection());
				}
			}
			if(Camera.useDrag) 
				Camera.dragCam((int)x, (int)-y);
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button){
			super.touchUp(event, x, y, pointer, button);
			mouse.set(x, y);
			mousePointer = pointer;
			mouseButton = button;
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
			Scene.getCurrentScene().onTouchUp();
			Camera.resetDrag();
			gestureStarted = false;
		}

		@Override
		public boolean keyTyped(InputEvent event, char key) {
			Scene.getCurrentScene().onKeyTyped(key);
			return super.keyUp(event, key);
		}

		@Override
		public boolean keyUp(InputEvent event, int keycode) {
			Scene.getCurrentScene().onKeyUp(keycode);
			return super.keyUp(event, keycode);
		}
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			Scene.getCurrentScene().onKeyDown(keycode);
			return super.keyDown(event, keycode);
		}
	};

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

	/* 
	 * Dialog Methods
	 */
	public void showToast(String message, float duration){
		Table table = new Table(Asset.skin);
		table.add("   "+message+"   ");
		table.setBackground(Asset.skin.getDrawable("dialogDim"));
		table.pack();
		table.setPosition(Scene.targetWidth/2 - table.getWidth(), Scene.targetHeight/2 - table.getHeight());
		addActor(table);
		table.addAction(Actions.sequence(Actions.delay(duration), Actions.removeActor(table)));
	}

	public void showMessageDialog(String title, String message){
		Dialog dialog = new Dialog(title, Asset.skin);
		dialog.getContentTable().add(message);
		dialog.button("OK", "OK");
		dialog.pack();
		dialog.show(getStage());
	}

	public boolean showConfirmDialog(String title, String message){
		Dialog dialog = new Dialog(title, Asset.skin);
		dialog.button("Yes", "Yes");
		dialog.button("No", "No");
		dialog.pack();
		dialog.show(getStage());
		//if(dialog.result().equals("Yes")) FIXME update Gdx
		//	return true;
		return false;
	}
	
	public void outline(Actor actor){
		selectionBox.setPosition(actor.getX(), actor.getY());
		selectionBox.setSize(actor.getWidth(), actor.getHeight());
		stage2d.addActor(selectionBox);
	}

/***********************************************************************************************************
* 					3d Related Functions												   	       		   *
************************************************************************************************************/	

	public static void addActor3d(Actor3d actor3d){
		stage3d.addActor3d(actor3d);
	}

	public static void removeActor3d(Actor3d actor3d){
		stage3d.getRoot().removeActor3d(actor3d);
	}
	
	public static void removeActor3d(String actor3dName){
		getRoot3d().removeActor3d(stage3d.getRoot().findActor(actor3dName));
	}
	
	public static Group3d getRoot3d(){
		return stage3d.getRoot();
	}

	public static void getChildren3d(){
		stage3d.getActors3d();
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
	

	public static void log(String log) {
		if(Scene.configJson.getBoolean("loggingEnabled"))
			Gdx.app.log("Stage ", log);
	}

	
/***********************************************************************************************************
* 					Utilities Related Functions												   	       		   *
************************************************************************************************************/
	/*
	 * The the angle in degrees of the inclination of a line
	 * @param cx, cy The center point x, y
	 * @param tx, ty The target point x, y
	 */
	public static float getAngle(float cx, float cy, float tx, float ty) {
		float angle = (float) Math.toDegrees(MathUtils.atan2(tx - cx, ty - cy));
		//if(angle < 0){
		//    angle += 360;
		//}
		return angle;
	}

	private static Vector2 distVector = new Vector2();
	public static final float getDistance(Actor a, Actor b){
		distVector.set(a.getX(), a.getY());
		return distVector.dst(b.getX(), b.getY());
	}

	public static final float getDistance(float x1, float y1, float x2, float y2){
		distVector.set(x1, y1);
		return distVector.dst(x2, y2);
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

	public static Rectangle getBounds(Actor actor) {
		float posX = actor.getX();
		float posY = actor.getY();
		float width = actor.getWidth();
		float height = actor.getHeight();
		return new Rectangle(posX, posY, width, height);
	}

	public static boolean collides(Actor actor, float x, float y) {
		Rectangle rectA1 = getBounds(actor);
		Rectangle rectA2 = new Rectangle(x, y, 5, 5);
		// Check if rectangles collides
		if (Intersector.overlaps(rectA1, rectA2)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean collides(Actor actor1, Actor actor2) {
		Rectangle rectA1 = getBounds(actor1);
		Rectangle rectA2 = getBounds(actor2);
		// Check if rectangles collides
		if (Intersector.overlaps(rectA1, rectA2)) {
			return true;
		} else {
			return false;
		}
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
	
	/*
	 * TEA Encryption and Decryption
	 */
	public static String encryptKey = "Default";
	private static final int encryptDelta = 0x9E3779B9;
	private static final int MX(int sum, int y, int z, int p, int e, int[] k) {
		return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
	}
	
	public static final byte[] encrypt(String data) {
		return encrypt(data.getBytes(), encryptKey.getBytes());
	}

	public static final byte[] decrypt(String data) {
		return decrypt(data.getBytes(), encryptKey.getBytes());
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
			sum = sum + encryptDelta;
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

		sum = q * encryptDelta;
		while (sum != 0) {
			e = sum >>> 2 & 3;
		for (p = n; p > 0; p--) {
			z = v[p - 1];
			y = v[p] -= MX(sum, y, z, p, e, k);
		}
		z = v[n];
		y = v[0] -= MX(sum, y, z, p, e, k);
		sum = sum - encryptDelta;
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
	
	private static ShapeRenderer shapeRenderer;
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
	
	static void setupFps(){
		fpsLabel = new Label("", Asset.skin);
		fpsLabel.setName("Fps");
	}
	public static float stateTime = 0;
	public static float gameUptime = 0;
	private static float startTime = System.nanoTime();
	/*
	 *  The main application listener for the game
	 */
	public static ApplicationListener app = new ApplicationListener() {
		public int dots = 40;
		public int xlines;
		public int ylines;
		@Override
		public final void create() {
			log("Create");
			Config.setup();
			Serializer.setup();
			stage2d = new com.badlogic.gdx.scenes.scene2d.Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
					Scene.configJson.getBoolean("keepAspectRatio"));
			stage2d.getRoot().setName("Root");
			stage2d.getRoot().setTouchable(Touchable.childrenOnly);
			stage2d.setCamera(new Camera());
			inputMux = new InputMultiplexer();
			inputMux.addProcessor(stage2d);
			stage3d = new Stage3d();
			//camController = new CameraInputController(stage3d.getCamera());
			//inputMux.addProcessor(stage3d);
			//inputMux.addProcessor(camController);
			shapeRenderer = new ShapeRenderer();
			selectionBox.setTouchable(Touchable.disabled);
			selectionBox.setName("Shape");
			JsonValue sv = Scene.jsonReader.parse(Gdx.files.internal(Asset.basePath+"scene"));
			for(JsonValue jv: sv.iterator())
				scenesMap.put(jv.name, jv.asString());
			setScene(scenesMap.firstKey());
			Gdx.input.setCatchBackKey(true);
			Gdx.input.setCatchMenuKey(true);
			Gdx.input.setInputProcessor(inputMux);
			xlines = (int)Gdx.graphics.getWidth()/dots;
	 		ylines = (int)Gdx.graphics.getHeight()/dots;
		}
		/*
		 * This is the main rendering call that updates the time, updates the stage,
		 * loads assets asynchronously, updates the camera and FPS text.
		 */
		@Override
		public final void render(){
			Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT);
			if (System.nanoTime() - startTime >= 1000000000) {
				gameUptime +=1 ;
				startTime = System.nanoTime();
			}
			stateTime += Gdx.graphics.getDeltaTime();
			Asset.load();
			stage3d.act();
			stage3d.draw();
		    //camController.update();
			Scene.stage2d.act();//Gdx.graphics.getDeltaTime();
			Scene.stage2d.draw();
			if(debug){
				shapeRenderer.begin(ShapeType.Point);
				shapeRenderer.setColor(Color.BLACK);
				for(int i = 0; i<=xlines; i++)
					for(int j = 0; j<=ylines; j++)
						shapeRenderer.point(2+i*dots, j*dots, 0);
				shapeRenderer.end();
			}
	 	}
		@Override
		public final void resize(int width, int height) {
			log("Resize");
			stage2d.setViewport(targetWidth, targetHeight, configJson.getBoolean("keepAspectRatio"));
			stage3d.setViewport(targetWidth, targetHeight, configJson.getBoolean("keepAspectRatio"));
		}
		@Override
		public final void pause() {
			log("Pause");
			Asset.musicPause();
			Asset.soundStop();
			Scene.pauseState = true;
			Scene.getCurrentScene().onPause();
		}
		@Override
		public final void resume() {
			log("Resume");
			Asset.musicResume();
			Scene.pauseState = false;
			Scene.getCurrentScene().onResume();
		}
		@Override
		public final void dispose() {
			log("Dispose");
			Scene.getCurrentScene().onDispose();
			Scene.stage2d.dispose();
			Asset.unloadAll();
			Gdx.app.exit();
		}
	};
	
	public static final void exit(){
		log("Disposing and Exiting");
		getCurrentScene().onDispose();
		stage2d.dispose();
		stage3d.dispose();
		Asset.unloadAll();
		Gdx.app.exit();
	}

	
	public enum GestureType {
		None, Up, Down, Left, Right
	}
	
	public enum OnEventType {
		DoEffect,
		SetScene
	}
	
}