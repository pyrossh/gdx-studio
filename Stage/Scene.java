import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;

abstract public class Scene extends Actor {
	/* The array map containing all the scenes data of the game*/
	public static final ArrayMap<String, String> scenesMap = new ArrayMap<String, String>();
	
	private String sceneName = "";
	public static String sceneBackground = "";
	public static String sceneMusic = "";
	public static String sceneTransition = "";
	public static float sceneDuration = 0;
	public static InterpolationType sceneInterpolationType = InterpolationType.Linear;
	
	public Scene(){
		setPosition(0, 0);
		setSize(0, 0);
		setBounds(0,0, 0, 0);
		Stage.addActor(this);
		sceneName = this.getClass().getName();
		Stage.log("Current Scene: "+sceneName);
		load();
	}
	
	public abstract void onClick(Actor actor);
	public abstract void onTouchDown(Actor actor);
	public abstract void onTouchUp();
	public abstract void onDragged();
	public abstract void onGesture(GestureType gestureType);
	public abstract void onPause();
	public abstract void onResume();
	public abstract void onDispose();
	
	private void load(){
		String[] lines = Scene.scenesMap.get(sceneName).split("\n");
		for(String line: lines){
			if(line.trim().isEmpty())
				continue;
			JsonValue jv = Stage.jsonReader.parse(line);
			Serializer.deserialize(jv.get("class").asString(), line);
		}
	}
	
	public void save(){
		StringBuilder sb = new StringBuilder();
		sb.append("{class:SceneJson,");
		sb.append("background:\""+sceneBackground+"\",");
		sb.append("music:\""+sceneMusic+"\",");
		sb.append("transition:\""+sceneTransition+"\",");
		sb.append("duration:"+sceneDuration+",");
		sb.append("interpolation:"+sceneInterpolationType.toString()+"}");
		sb.append("\n");
		Stage.removeActor("Shape");
		for(Actor actor: Stage.getChildren()){
			if(Stage.isValidActor(actor)){
				//log(actor.getName());
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
