import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Serializer {

	public static void deserialize(String className, String value){
		if(className.equals("SceneJson")){
			JsonValue jv = Stage.jsonReader.parse(value);
			Scene.sceneBackground = jv.getString("background");
			Scene.sceneMusic = jv.getString("music");
			Scene.sceneTransition = jv.getString("transition");
			Scene.sceneDuration = jv.getFloat("duration");
			Scene.sceneInterpolationType = InterpolationType.valueOf(jv.getString("interpolation"));
			if(!Scene.sceneBackground.equals("None"))
				Stage.setBackground(Scene.sceneBackground);
			if(!Scene.sceneMusic.equals("None"))
				Asset.musicPlay(Scene.sceneMusic);
			Effect.transition(TransitionType.valueOf(Scene.sceneTransition), 
					Stage.getRoot(),Scene.sceneDuration, Scene.sceneInterpolationType);
		}
		else{
			try {
				Class cc = Class.forName(className);
				Stage.addActor((Actor)Stage.json.fromJson(cc, value));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void registerSerializer(Class clazz, Json.Serializer serializer){
		Stage.json.setSerializer(clazz, serializer);
	}

	public static void initialize(){
		registerSerializer(Actor.class, new ActorSerializer());
		registerSerializer(ImageJson.class, new ImageJson());
		registerSerializer(Label.class, new LabelSerializer());
		registerSerializer(Button.class, new ButtonSerializer());
		registerSerializer(TextButton.class, new TextButtonSerializer());
		registerSerializer(Table.class, new TableSerializer());
		registerSerializer(CheckBox.class, new CheckBoxSerializer());
		registerSerializer(SelectBox.class, new SelectBoxSerializer());
		registerSerializer(List.class, new ListSerializer());
		registerSerializer(Slider.class, new SliderSerializer());
		registerSerializer(TextField.class, new TextFieldSerializer());
		registerSerializer(Dialog.class, new DialogSerializer());
		registerSerializer(Touchpad.class, new TouchpadSerializer());
		registerSerializer(Sprite.class, new SpriteSerializer());
	}

	public static class ActorSerializer implements Json.Serializer<Actor> {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Actor actor = new Actor();
			readActor(jv, actor);
			return actor;
		}

		@Override
		public void write(Json json, Actor actor, Class arg2) {
			json.writeObjectStart();
			writeActor(json, actor);
			json.writeObjectEnd();
		}

		public static void readActor(JsonValue jv, Actor actor){
			actor.setName(jv.getString("name"));
			actor.setX(jv.getFloat("x"));
			actor.setY(jv.getFloat("y"));
			actor.setWidth(jv.getFloat("width"));
			actor.setHeight(jv.getFloat("height"));
			actor.setColor(Color.valueOf(jv.getString("color")));
		}

		public static void writeActor(Json json, Actor actor){
			json.writeValue("class", actor.getClass().getName());
			json.writeValue("name", actor.getName());
			json.writeValue("x", actor.getX());
			json.writeValue("y", actor.getY());
			json.writeValue("width", actor.getWidth());
			json.writeValue("height", actor.getHeight());
			json.writeValue("color", actor.getColor().toString());
			json.writeValue("ox", actor.getOriginX());
			json.writeValue("oy", actor.getOriginY());
			json.writeValue("rotation", actor.getRotation());
		}

	}

	public static class GroupSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Group group = new Group();
			readActor(jv, group);
			return group;
		}

		@Override
		public void write(Json json, Actor actor, Class arg2) {
			json.writeObjectStart();
			writeActor(json, actor);
			json.writeObjectEnd();
		}
	}

	private static class LabelSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			LabelStyle ls = new LabelStyle();
			ls.font = Asset.font(jv.getString("fontName"));
			Label label = new Label(jv.getString("text"), ls);
			readActor(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Actor label, Class arg2) {
			json.writeObjectStart();
			writeActor(json, label);
			json.writeValue("text", ((Label)label).getText().toString());
			json.writeValue("fontName", Asset.fontMap.getKey(((Label)label).getStyle().font, false));
			json.writeObjectEnd();
		}
	}

	public static class ButtonSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Button btn = new Button(Asset.skin);
			readActor(jv, btn);
			return btn;
		}

		@Override
		public void write(Json json, Actor btn, Class arg2) {
			json.writeObjectStart();
			writeActor(json, btn);
			json.writeObjectEnd();
		}

	}

	private static class CheckBoxSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			CheckBox check = new CheckBox(jv.getString("text"), Asset.skin);
			ActorSerializer.readActor(jv, check);
			return check;
		}

		@Override
		public void write(Json json, Actor check, Class arg2) {
			json.writeObjectStart();
			ActorSerializer.writeActor(json, check);
			json.writeValue("text", ((CheckBox)check).getText().toString());
			json.writeObjectEnd();
		}

	}

	private static class SliderSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Slider slider = new Slider(jv.getFloat("min"),jv.getFloat("max"),jv.getFloat("step")
					, false, Asset.skin);
			slider.setValue(jv.getFloat("value"));
			readActor(jv, slider);
			return slider;
		}

		@Override
		public void write(Json json, Actor slider, Class arg2) {
			json.writeObjectStart();
			writeActor(json, slider);
			json.writeValue("min", ((Slider)slider).getMinValue());
			json.writeValue("max", ((Slider)slider).getMaxValue());
			json.writeValue("step", ((Slider)slider).getStepSize());
			json.writeValue("value", ((Slider)slider).getValue());
			json.writeObjectEnd();
		}
	}

	private static class SelectBoxSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			SelectBox select = new SelectBox(jv.getString("text").split(","), Asset.skin);
			readActor(jv, select);
			return select;
		}

		@Override
		public void write(Json json, Actor select, Class arg2) {
			json.writeObjectStart();
			writeActor(json, select);
			String items = "";
			for(String s: ((SelectBox)select).getItems())
				items+=s+",";
			json.writeValue("text", items);
			json.writeObjectEnd();
		}

	}

	private static class ListSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			List list = new List(jv.getString("text").split(","), Asset.skin);
			ActorSerializer.readActor(jv, list);
			return list;
		}

		@Override
		public void write(Json json, Actor list, Class arg2) {
			json.writeObjectStart();
			ActorSerializer.writeActor(json, list);
			String items = "";
			for(String s: ((List)list).getItems())
				items+=s+",";
			json.writeValue("text", items);
			json.writeObjectEnd();
		}

	}

	private static class DialogSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Dialog dialog = new Dialog(jv.getString("title"), Asset.skin);
			readActor(jv, dialog);
			dialog.setModal(jv.getBoolean("modal"));
			dialog.setMovable(jv.getBoolean("move"));
			dialog.setResizable(jv.getBoolean("resize"));
			return dialog;
		}

		@Override
		public void write(Json json, Actor dialog, Class arg2) {
			json.writeObjectStart();
			writeActor(json, dialog);
			json.writeValue("title", ((Dialog)dialog).getTitle());
			json.writeValue("modal", ((Dialog)dialog).isModal());
			json.writeValue("move", ((Dialog)dialog).isMovable());
			json.writeValue("resize", ((Dialog)dialog).isResizable());
			json.writeObjectEnd();
		}

	}

	public static class TouchpadSerializer extends ActorSerializer {
		public static float deadZoneRadius = 5f;
		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Touchpad tp = new Touchpad(jv.getFloat("deadzoneRadius"), Asset.skin);
			deadZoneRadius = jv.getFloat("deadzoneRadius");
			ActorSerializer.readActor(jv, tp);
			return tp;
		}

		@Override
		public void write(Json json, Actor tp, Class arg2) {
			json.writeObjectStart();
			ActorSerializer.writeActor(json, tp);
			json.writeValue("deadzoneRadius", deadZoneRadius);
			json.writeObjectEnd();
		}
	}

	private static class TextFieldSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			TextField tf = new TextField(jv.getString("text"), Asset.skin);
			readActor(jv, tf);
			return tf;
		}

		@Override
		public void write(Json json, Actor tf, Class arg2) {
			json.writeObjectStart();
			writeActor(json, tf);
			json.writeValue("text", ((TextField)tf).getText().toString());
			json.writeObjectEnd();
		}
	}

	private static class TextButtonSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			TextButton btn = new TextButton(jv.getString("text"), Asset.skin);
			ActorSerializer.readActor(jv, btn);
			return btn;
		}

		@Override
		public void write(Json json, Actor btn, Class arg2) {
			json.writeObjectStart();
			ActorSerializer.writeActor(json, btn);
			json.writeValue("text", ((TextButton)btn).getText().toString());
			json.writeObjectEnd();
		}
	}

	private static class SplitPaneSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			SplitPane label = new SplitPane(null, null, false, Asset.skin);
			readActor(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Actor label, Class arg2) {
			json.writeObjectStart();
			writeActor(json, label);
			json.writeObjectEnd();
		}
	}

	private static class ScrollPaneSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			ScrollPane label = new ScrollPane(null);
			readActor(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Actor label, Class arg2) {
			json.writeObjectStart();
			writeActor(json, label);
			json.writeObjectEnd();
		}
	}

	private static class StackSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Stack label = new Stack();
			readActor(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Actor label, Class arg2) {
			json.writeObjectStart();
			writeActor(json, label);
			json.writeObjectEnd();
		}
	}

	private static class TreeSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Tree label = new Tree(Asset.skin);
			readActor(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Actor label, Class arg2) {
			json.writeObjectStart();
			ActorSerializer.writeActor(json, label);
			json.writeObjectEnd();
		}
	}

	private static class TableSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Table table = new Table(Asset.skin);
			readActor(jv, table);
			return table;
		}

		@Override
		public void write(Json json, Actor table, Class arg2) {
			json.writeObjectStart();
			writeActor(json, table);
			json.writeObjectEnd();
		}

	}

	private static class SpriteSerializer extends ActorSerializer {

		@Override
		public Actor read(Json arg0, JsonValue jv, Class arg2) {
			Sprite sprite;
			if(jv.getInt("frameCount") != 1){
				sprite = new Sprite(jv.getString("textures").split(",")[0], jv.getInt("frameCount"), 
						jv.getFloat("duration"));
			}
			else{
				sprite = new Sprite(jv.getFloat("duration"), jv.getString("textures").split(","));
			}
			ActorSerializer.readActor(jv, sprite);
			sprite.isAnimationActive = jv.getBoolean("active");
			sprite.isAnimationLooping = jv.getBoolean("looping");
			return sprite;
		}


		@Override
		public void write(Json json, Actor sprite, Class arg2) {
			json.writeObjectStart();
			ActorSerializer.writeActor(json, sprite);
			json.writeValue("textures", sprite.toString());
			json.writeValue("duration", ((Sprite)sprite).getDuration());
			json.writeValue("active", ((Sprite)sprite).isAnimationActive);
			json.writeValue("looping", ((Sprite)sprite).isAnimationLooping);
			json.writeValue("frameCount", ((Sprite)sprite).getFrameCount());
			json.writeObjectEnd();
		}

	}

}