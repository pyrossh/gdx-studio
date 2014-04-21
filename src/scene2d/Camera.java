package scene2d;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

public class Camera extends OrthographicCamera {
	private static Camera instance;
	private static float duration;
	private static float time;
	private static Interpolation interpolation;
	private static boolean moveCompleted;
	private static float lastPercent;
	private static float percentDelta;
	private static float panSpeedX, panSpeedY;
	private static final Array<Actor> hudActors = new Array<Actor>();
	
	
	private static Actor followedActor = null;
	private static float followSpeed = 1f;
	/*
	 *  This is to set the offsets of camera position when following the actor
	 *  When the camera follows the actor its (x,y) position is set to actor's (x,y) position
	 *  based on followSpeed. The offsets are used to position the camera in such a way that the actor
	 *  doesn't need to be at the center of the camera always
	 */
	public static Rectangle followOffset = new Rectangle(10,70,10,60);
	private static boolean followContinous = false;

	
	public static boolean usePan = false;
    public static boolean useDrag = false;
	/*
	 * Sets the speed at which the camera pans. By default it moves 1px for a duration a 1f
	 * so its speed is 1px/f. So reduce the duration to increase its speed.
	 * ex: setPanSpeed(0.5) will change its speed to 2px/f
	 * Here: f can/maybe also indicate seconds 
	 */
	public static float panSpeed = 5f;
	public static Rectangle panSize;
	
	/*
	 *  This sets the boundary of the camera till what position can it move or pan in the
	 *  directions left, right, top, down. This is to prevent is from panning overboard the game area.
	 *  Usually the bounds of the camera is like a rectangle. This must be calculated carefully
	 *  as the camera's position is based on its center.
	*/
	public static Rectangle bounds = new Rectangle(0,0,999,999);


	Camera(){
		setToOrtho(false, Scene.targetWidth, Scene.targetHeight);
		position.set(Scene.targetWidth/2, Scene.targetHeight/2, 0);
		instance = this;
		panSize = new Rectangle(10, 10, Scene.targetWidth-10, Scene.targetHeight - 10);
	}

	/*
	 * Moves the camera to x,y over a time duration
	 */
	public void moveTo(float x, float y, float duration) {
		moveBy(x-position.x, y-position.y, duration);
	}

	/*
	 * Moves the camera by amountX, amountY over a time duration
	 */
	public static void moveBy (float amountX, float amountY, float duration) {
		moveBy(amountX, amountY, duration, null);
	}

	/*
	 * Moves the camera by amountX, amountY over a time duration and interpolation interp
	 */
	public static void moveBy (float amountX, float amountY, float dur, Interpolation interp) {
		duration = dur;
		interpolation = interp;
		panSpeedX = amountX;
		panSpeedY = amountY;
		lastPercent = 0;
		time = 0;
		moveCompleted = false;
	}

	private static Rectangle cullRect = new Rectangle();
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
		if(Scene.cullingEnabled){
			cullRect.set(getXLeft(), getYBot(), Scene.targetWidth, Scene.targetHeight);
			Scene.getCurrentScene().setCullingArea(cullRect);
		}
		translate(panSpeedX * percentDelta, panSpeedY * percentDelta, 0);
		for(Actor actor: hudActors) 
			actor.setPosition(actor.getX()+panSpeedX * percentDelta, actor.getY()+panSpeedY * percentDelta);
		lastPercent = percent;
		if (moveCompleted) interpolation = null;
	}

	public void resetCamera(){
		position.set(Scene.targetWidth/2, Scene.targetHeight/2, 0);
	}

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
		if(position.x < followedActor.getX() - followOffset.x) moveBy(1f, 0, followSpeed);
		else if(position.x > followedActor.getX() + followOffset.width) moveBy(-1f, 0, followSpeed);
		else if(position.y < followedActor.getY() - followOffset.y) moveBy(0, 1f, followSpeed);
		else if(position.y > followedActor.getY() - followOffset.height) moveBy(0, -1f, followSpeed);
		else {
			if(!followContinous)
				followedActor = null;
		}
	}

	@Override
	public void update(){
		super.update();
		Scene.mouse.x = Gdx.input.getX();
		Scene.mouse.y = Gdx.graphics.getHeight() - Gdx.input.getY();
		if(!moveCompleted)
			moveByAction(Scene.stateTime);//FIXME
		if(usePan) 
			panCameraWithMouse();
		if(followedActor != null)
			follow();
	}

	private void panCameraWithMouse(){
		 if(Scene.mouse.x > panSize.width && getXLeft() < bounds.width) moveBy(1f, 0, panSpeed);
		 else if(Scene.mouse.x < panSize.x  && getXLeft() > bounds.x)  moveBy(-1f, 0, panSpeed);
		 else if(Scene.mouse.y < panSize.y && getYBot() > bounds.y) moveBy(0, -1f, panSpeed);
		 else if(Scene.mouse.y > panSize.height && getYBot() < bounds.height) moveBy(0, 1f, panSpeed);
	}
	
	private final static Vector3 curr = new Vector3();
	private final static Vector3 last = new Vector3(-1, -1, -1);
	private final static Vector3 deltaDrag = new Vector3();
	private static float deltaCamX = 0;
	private static float deltaCamY = 0;
	public static void dragCam(int x, int y){
		instance.unproject(curr.set(x, y, 0));
    	if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
    		instance.unproject(deltaDrag.set(last.x, last.y, 0));
    		deltaDrag.sub(curr);
    		deltaCamX = deltaDrag.x + instance.position.x;
    		deltaCamY = deltaDrag.y + instance.position.y;
    		if(deltaCamX > bounds.x && deltaCamX < bounds.x+bounds.width)
    			moveBy(deltaDrag.x, 0f, 0f);
    		if(deltaCamY > bounds.y && deltaCamY < bounds.y+bounds.height)
    			moveBy(0f, deltaDrag.y, 0f);		
    	}
    	last.set(x, y, 0);
    }
	
	public static void resetDrag(){
		last.set(-1, -1, -1);
	}
	
	public static void reset(){
		usePan = false;
		followActor(null);
		clearAllHud();
		instance.position.set(Scene.targetWidth/2, Scene.targetHeight/2, 0);
	}

	/* If you want to make any elements/actors to move along with the camera 
	 * like HUD's add them using this method */
	public static void addHud(Actor actor){
		if(actor != null){
			Scene.getCurrentScene().addActor(actor);
			hudActors.add(actor);
		}
	}
	
	/* If you want to make any elements/actors to move along with the camera 
	 * like HUD's add them using this method */
	public static void addHud(String actorName){
		if(actorName != null && !actorName.isEmpty()){
			Actor actor = Scene.getCurrentScene().findActor(actorName);
			Scene.getCurrentScene().addActor(actor);
			hudActors.add(actor);
		}
	}


	/* If you want to any elements/actors which was a Hud the use this */
	public static void removeHud(Actor actor){
		Scene.getCurrentScene().removeActor(actor);
		hudActors.removeValue(actor, true);
	}
	
	/* If you want to any elements/actors which was a Hud the use this */
	public static void removeHud(String actorName){
		if(actorName != null && !actorName.isEmpty()){
			Actor actor = Scene.getCurrentScene().findActor(actorName);
			Scene.getCurrentScene().removeActor(actor);
			hudActors.removeValue(actor, true);
		}
	}
	
	

	/*
	 * Clears all hud's registered with the camera
	 */
	public static void clearAllHud(){
		hudActors.clear();
	}
	
	/*
	 * Returns the x position of the camera
	 */
	public static float getX(){
		return instance.position.x;
	}
	
	/*
	 * Returns the y position of the camera
	 */
	public static float getY(){
		return instance.position.y;
	}
	
	public static float getXLeft(){
		return instance.position.x - Scene.targetWidth/2;
	}
	
	public static float getYBot(){
		return instance.position.y - Scene.targetHeight/2;
	}
	
	public static float getWidth(){
		return instance.viewportWidth;
	}
	
	public static float getHeight(){
		return instance.viewportHeight;
	}
}