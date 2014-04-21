package scene2d;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Serializer {

	public static void registerSerializer(Class<?> clazz, Json.Serializer serializer){
		Scene.json.setSerializer(clazz, serializer);
	}

	public static void setup(){
		registerSerializer(Actor.class, new ActorSerializer());
		registerSerializer(Scene.class, new SceneSerializer());
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
		registerSerializer(Touchpad.class, new TouchpadSerializer());
		registerSerializer(Sprite.class, new SpriteSerializer());
		
		registerSerializer(Dialog.class, new DialogSerializer());
		registerSerializer(SplitPane.class, new SplitPaneSerializer());
		registerSerializer(ScrollPane.class, new ScrollPaneSerializer());
		registerSerializer(Stack.class, new StackSerializer());
		registerSerializer(Tree.class, new TreeSerializer());
		registerSerializer(Table.class, new TableSerializer());
		registerSerializer(ButtonGroup.class, new ButtonGroupSerializer());
		registerSerializer(HorizontalGroup.class, new HorizontalGroupSerializer());
		registerSerializer(VerticalGroup.class, new VerticalGroupSerializer());
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
			actor.setOriginX(jv.getFloat("ox"));
			actor.setOriginY(jv.getFloat("oy"));
			actor.setRotation(jv.getFloat("rotation"));
			actor.setColor(Color.valueOf(jv.getString("color")));
			actor.setTouchable(Touchable.valueOf(jv.getString("touchable")));
			actor.setVisible(jv.getBoolean("visible"));
			Scene.getCurrentScene().addActor(actor);
			actor.setZIndex(jv.getInt("zindex"));
		}

		public static void writeActor(Json json, Actor actor){
			json.writeValue("class", actor.getClass().getName());
			json.writeValue("name", actor.getName());
			json.writeValue("x", actor.getX());
			json.writeValue("y", actor.getY());
			json.writeValue("width", actor.getWidth());
			json.writeValue("height", actor.getHeight());
			json.writeValue("ox", actor.getOriginX());
			json.writeValue("oy", actor.getOriginY());
			json.writeValue("rotation", actor.getRotation());
			json.writeValue("zindex", actor.getZIndex());
			json.writeValue("color", actor.getColor().toString());
			json.writeValue("touchable", actor.getTouchable().toString());
			json.writeValue("visible", actor.isVisible());
		}

	}
	
	public static class SceneSerializer extends ActorSerializer {
		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Scene scene = Scene.getCurrentScene();
			scene.sceneBackground = jv.getString("background");
			scene.sceneMusic = jv.getString("music");
			scene.sceneTransition = jv.getString("transition");
			scene.sceneDuration = jv.getFloat("duration");
			scene.sceneInterpolationType = InterpolationType.valueOf(jv.getString("interpolation"));
			Scene.log(Scene.getCurrentScene().getName());
			Scene.getRoot().addActor(scene);
			return scene;
		}

		@Override
		public void write(Json json, Actor label, Class arg2) {
			json.writeObjectStart();
			Scene scene = (Scene) label;
			json.writeValue("class", Scene.class.getName());
			json.writeValue("background", scene.sceneBackground);
			json.writeValue("music", scene.sceneMusic);
			json.writeValue("transition", scene.sceneTransition);
			json.writeValue("duration", scene.sceneDuration);
			json.writeValue("interpolation", scene.sceneInterpolationType.toString());
			json.writeObjectEnd();
		}
	}

	public static class LabelSerializer extends ActorSerializer {

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
			readActor(jv, check);
			return check;
		}

		@Override
		public void write(Json json, Actor check, Class arg2) {
			json.writeObjectStart();
			writeActor(json, check);
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
			readActor(jv, list);
			return list;
		}

		@Override
		public void write(Json json, Actor list, Class arg2) {
			json.writeObjectStart();
			writeActor(json, list);
			String items = "";
			for(String s: ((List)list).getItems())
				items+=s+",";
			json.writeValue("text", items);
			json.writeObjectEnd();
		}

	}

	public static class TouchpadSerializer extends ActorSerializer {
		public static float deadZoneRadius = 5f;
		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Touchpad tp = new Touchpad(jv.getFloat("deadzoneRadius"), Asset.skin);
			deadZoneRadius = jv.getFloat("deadzoneRadius");
			readActor(jv, tp);
			return tp;
		}

		@Override
		public void write(Json json, Actor tp, Class arg2) {
			json.writeObjectStart();
			writeActor(json, tp);
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
			
			sprite.isAnimationActive = jv.getBoolean("active");
			sprite.isAnimationLooping = jv.getBoolean("looping");
			ActorSerializer.readActor(jv, sprite);
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
	
	public static class GroupSerializer implements Json.Serializer<Group> {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			Group group = new Group();
			readGroup(jv, group);
			return group;
		}

		@Override
		public void write(Json json, Group group, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, group);
			json.writeObjectEnd();
		}
		
		public void writeGroup(Json json, Group group){
			ActorSerializer.writeActor(json, group);
			//Array<String> actorsJson = new Array<String>();
			//for(Actor actor: group.getChildren()){
			//	actorsJson.add(Stage.json.toJson(actor));
			//}
			//json.writeValue("children", actorsJson, Array.class, String.class);
			//not working
		}
		
		public void readGroup(JsonValue jv, Group group){
			ActorSerializer.readActor(jv, group);
			//Array<String> array = (Array<String>)Stage.json.readValue(Array.class, String.class, 
			//		jv.get("children"));
			//for(String actorJson: array.items){
			//	Stage.log(actorJson);
			//	//group.addActor(Stage.json.fromJson(actor));
			//}
		}
		
		public void iterateActors(){
			
		}
	}
	
	private static class DialogSerializer extends ActorSerializer {

		@Override
		public Actor read(Json json, JsonValue jv, Class arg2) {
			Dialog dialog = new Dialog(jv.getString("title"), Asset.skin);
			dialog.setModal(jv.getBoolean("modal"));
			dialog.setMovable(jv.getBoolean("move"));
			dialog.setResizable(jv.getBoolean("resize"));
			readActor(jv, dialog);
			return dialog;
		}

		@Override
		public void write(Json json,  Actor dialog, Class arg2) {
			json.writeObjectStart();
			writeActor(json, dialog);
			json.writeValue("title", ((Dialog)dialog).getTitle());
			json.writeValue("modal", ((Dialog)dialog).isModal());
			json.writeValue("move", ((Dialog)dialog).isMovable());
			json.writeValue("resize", ((Dialog)dialog).isResizable());
			json.writeObjectEnd();
		}

	}

	private static class SplitPaneSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			SplitPane label = new SplitPane(null, null, false, Asset.skin);
			readGroup(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Group label, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, label);
			json.writeObjectEnd();
		}
	}

	private static class ScrollPaneSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			ScrollPane label = new ScrollPane(null);
			readGroup(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Group label, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, label);
			json.writeObjectEnd();
		}
	}

	private static class StackSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			Stack label = new Stack();
			readGroup(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Group label, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, label);
			json.writeObjectEnd();
		}
	}

	private static class TreeSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			Tree label = new Tree(Asset.skin);
			readGroup(jv, label);
			return label;
		}

		@Override
		public void write(Json json, Group label, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, label);
			json.writeObjectEnd();
		}
	}

	private static class TableSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			Table table = new Table(Asset.skin);
			//table.getPadBottom()
			//table.getCell(null).get
			readGroup(jv, table);
			return table;
		}

		@Override
		public void write(Json json, Group table, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, table);
			json.writeObjectEnd();
		}

	}
	
	private static class ButtonGroupSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			Table table = new Table(Asset.skin);
			readGroup(jv, table);
			return table;
		}

		@Override
		public void write(Json json, Group table, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, table);
			json.writeObjectEnd();
		}

	}
	
	private static class HorizontalGroupSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			HorizontalGroup hg = new HorizontalGroup();
			readGroup(jv, hg);
			return hg;
		}

		@Override
		public void write(Json json, Group table, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, table);
			json.writeObjectEnd();
		}

	}
	
	private static class VerticalGroupSerializer extends GroupSerializer {

		@Override
		public Group read(Json json, JsonValue jv, Class arg2) {
			VerticalGroup vg = new VerticalGroup();
			readGroup(jv, vg);
			return vg;
		}

		@Override
		public void write(Json json, Group vg, Class arg2) {
			json.writeObjectStart();
			writeGroup(json, vg);
			json.writeObjectEnd();
		}
	}
}