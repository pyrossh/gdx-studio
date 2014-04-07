import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.StringBuilder;

abstract public class Scene extends Actor {
	/* The array map containing all the scenes data of the game*/
	public static final ArrayMap<String, String> scenesMap = new ArrayMap<String, String>();
	
	private String sceneName = "";
	public String sceneBackground = "None";
	public String sceneMusic = "None";
	public String sceneTransition = "None";
	public float sceneDuration = 0;
	public InterpolationType sceneInterpolationType = InterpolationType.Linear;
	
	public Scene(){
		setPosition(0, 0);
		setSize(0, 0);
		setBounds(0,0, 0, 0);
		sceneName = this.getClass().getName();
		setName(sceneName);
		Stage.log("Current Scene: "+sceneName);
		load(sceneName);
	}
	
	public abstract void onClick(Actor actor);
	public abstract void onTouchDown(Actor actor);
	public abstract void onTouchUp();
	public abstract void onDragged();
	public abstract void onGesture(GestureType gestureType);
	public abstract void onKeyTyped(char key);
	public abstract void onKeyUp(int keycode);
	public abstract void onKeyDown(int keycode);
	public abstract void onPause();
	public abstract void onResume();
	public abstract void onDispose();
	
	public void load(String sceneName){
		Stage.log("Load");
		String[] lines = Scene.scenesMap.get(sceneName).split("\n");
		for(String line: lines){
			if(line.trim().isEmpty())
				continue;
			Stage.addActor(Stage.json.fromJson(Actor.class, line));
		}
		if(!sceneBackground.equals("None"))
			Stage.setBackground(sceneBackground);
		if(!sceneMusic.equals("None"))
			Asset.musicPlay(sceneMusic);
		if(!sceneTransition.equals("None"))
			Effect.transition(TransitionType.valueOf(sceneTransition), 
					Stage.getRoot(), sceneDuration, sceneInterpolationType);
	}
	
	protected void save(){
		save(sceneName);
	}
	
	public void save(String sceneName){
		Stage.log("Save");
		StringBuilder sb = new StringBuilder();
		Stage.removeActor("Shape");
		for(Actor actor: Stage.getChildren()){
			if(Stage.isValidActor(actor)){
				//Stage.log(actor.getName());
				sb.append(Stage.json.toJson(actor));
				sb.append("\n");
			}
		}
		scenesMap.put(sceneName, sb.toString());
		Gdx.files.local(Asset.basePath+"scene").writeString(Stage.json.toJson(scenesMap, ArrayMap.class, 
				String.class), false);
		sb = null;
	}
	
	public String getSceneName(){
		return sceneName;
	}
}
