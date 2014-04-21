package scene2d;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;


public class ImageJson extends Image implements Json.Serializer<ImageJson> {
	private String texName = "";
	public EffectType effectType = EffectType.None;
	public float effectValue = 0f;
	public float effectDuration = 0f;
	public InterpolationType interpolationType = InterpolationType.Linear;
	public float addActorDelay = 0f;
	public float addEffectDelay = 0f;
	
	public EventType evtType = EventType.None;
	public Scene.OnEventType onEvtType = Scene.OnEventType.DoEffect;
	public String eventScene = "";
	
	
	public ImageJson(){
		
	}
	
	/* 
	 * The texture name 
	 * */
	public ImageJson(String texName){
		super(Asset.tex(texName));
		this.texName = texName;
	}

	@Override
	public ImageJson read(Json arg0, JsonValue jv, Class arg2) {
		final ImageJson image = new ImageJson(jv.getString("texName"));
		Serializer.ActorSerializer.readActor(jv, image);
		image.effectType = EffectType.valueOf(jv.getString("effect"));
		image.effectDuration = jv.getFloat("duration");
		image.effectValue = jv.getFloat("value");
		image.interpolationType = InterpolationType.valueOf(jv.getString("interpolation"));
		image.addActorDelay = jv.getFloat("addActorDelay");
		image.addEffectDelay = jv.getFloat("addEffectDelay");
		
		image.evtType = EventType.valueOf(jv.getString("event"));
		image.onEvtType = Scene.OnEventType.valueOf(jv.getString("onEvent"));
		image.eventScene = jv.getString("eventScene");
		createEvent(image);
		return image;
	}
		
	void createEvent(final ImageJson image){
		switch(image.evtType){
			case SceneCreated:
				checkOnEventType(image);
				break;
			case Clicked:
				break;
			case Dispose:
				break;
			case Dragged:
				break;
			case GestureDown:
				break;
			case GestureLeft:
				break;
			case GestureRight:
				break;
			case GestureUp:
				break;
			case Moved:
				break;
			case Pause:
				break;
			case Resume:
				break;
			case TouchedDown:
				break;
			case TouchedUp:
				break;
			case None:
				break;
			default:
				break;
		}
	}
	
	void checkOnEventType(ImageJson image){
		switch(image.onEvtType){
			case DoEffect:Effect.createEffect(image);break;
			//case SetScene:Stage.setScene(image.eventScene);break;
			default:break;
		}
	}

	@Override
	public void write(Json json, ImageJson image, Class arg2) {
		json.writeObjectStart();
		Serializer.ActorSerializer.writeActor(json, image);
		json.writeValue("texName", image.getTexName());
		json.writeValue("effect", image.effectType.toString());
		json.writeValue("value", image.effectValue);
		json.writeValue("duration", image.effectDuration);
		json.writeValue("interpolation", image.interpolationType.toString());
		json.writeValue("addActorDelay", image.addActorDelay);
		json.writeValue("addEffectDelay", image.addActorDelay);
		
		json.writeValue("event", image.evtType.toString());
		json.writeValue("onEvent", image.onEvtType.toString());
		json.writeValue("eventScene", image.eventScene);
		json.writeObjectEnd();
	}
	
	public String getTexName(){
		return texName;
	}
}