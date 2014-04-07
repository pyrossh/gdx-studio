import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class SceneEffectPanel extends BaseTable {
	private static final long serialVersionUID = 1L;
	
	
	static String[] durations = 
	{
		"0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9",
		"1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9",
		"2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9",
		"3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9",
		"4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9",
		"5.0"
	};
	
	static JComboBox<String> bgComboBox = createComboBox();
	static JComboBox<String> musicComboBox = createComboBox();
	static JComboBox<String> transitionComboBox = createComboBox();
	static JComboBox<String> durationComboBox = createComboBox(durations);
	static JComboBox<String> interpolationComboBox = createComboBox();
	
	public SceneEffectPanel(){
		super("Properties");
		editors.add(new DefaultCellEditor(bgComboBox));
		editors.add(new DefaultCellEditor(musicComboBox));
		editors.add(new DefaultCellEditor(transitionComboBox));
		editors.add(new DefaultCellEditor(durationComboBox));
		editors.add(new DefaultCellEditor(interpolationComboBox));
		for(TransitionType transition: TransitionType.values())
			transitionComboBox.addItem(transition.toString());
		for(InterpolationType interpolation: InterpolationType.values())
			interpolationComboBox.addItem(interpolation.toString());
	}
	
	@Override
	public void clear(String... names){
		super.clear("Background", "Music", "Transition", "Duration", "Interpolation");
	}
	
	@Override
	public void update(String... values){
		if(Stage.getScene() == null)
			return;
		Scene scene = Stage.getScene();
		super.update("Background", scene.sceneBackground, "Music", scene.sceneMusic,
				"Transition", scene.sceneTransition, "Duration", ""+scene.sceneDuration,
				"Interpolation", scene.sceneInterpolationType.toString());
		bgComboBox.removeAllItems();
		musicComboBox.removeAllItems();
		bgComboBox.addItem("None");
		musicComboBox.addItem("None");
		for(String tex: Asset.texMap.keys())
			bgComboBox.addItem(tex);
		for(String tex: Asset.musicMap.keys())
			musicComboBox.addItem(tex);
	}
	
	@Override
	public void setProperty(String key, String value){
		if(key.isEmpty() || value.isEmpty())
			return ;
		if(Stage.getScene() == null)
			return;
		Scene scene = Stage.getScene();
		switch(key){
			case "Background": 
				scene.sceneBackground = value;
				if(!scene.sceneBackground.equals("None"))
					Stage.setBackground(scene.sceneBackground);
				else
					Stage.removeBackground();
				break;
			case "Music": 
				scene.sceneMusic = value;
				if(!scene.sceneMusic.equals("None"))
					Asset.musicPlay(scene.sceneMusic);
				break;
			case "Transition": 
				scene.sceneTransition = value;
				Effect.transition(TransitionType.valueOf(scene.sceneTransition), 
						Stage.getRoot(),scene.sceneDuration, scene.sceneInterpolationType);
				break;
			case "Duration": 
				scene.sceneDuration = Float.parseFloat(value);
				Effect.transition(TransitionType.valueOf(scene.sceneTransition), 
						Stage.getRoot(),scene.sceneDuration, scene.sceneInterpolationType);
				break;
			case "Interpolation":
				scene.sceneInterpolationType = InterpolationType.valueOf(value);
				Effect.transition(TransitionType.valueOf(scene.sceneTransition), 
						Stage.getRoot(),scene.sceneDuration, scene.sceneInterpolationType);
				break;
		}
		SceneEditor.isDirty = true;
	}
}