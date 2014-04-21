package gdxstudio.panel;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import scene2d.Asset;
import scene2d.Effect;
import scene2d.EffectType;
import scene2d.InterpolationType;
import scene2d.Scene;

public class SceneEffectPanel extends BaseTable {
	private static final long serialVersionUID = 1L;
	
	static JComboBox<String> bgComboBox = createComboBox();
	static JComboBox<String> musicComboBox = createComboBox();
	static JComboBox<String> transitionComboBox = createComboBox();
	static JComboBox<String> interpolationComboBox = createComboBox();
	
	public SceneEffectPanel(){
		super("Properties", null, new SceneEffectRenderer());
		editors.add(new DefaultCellEditor(bgComboBox));
		editors.add(new DefaultCellEditor(musicComboBox));
		editors.add(new DefaultCellEditor(transitionComboBox));
		editors.add(createFloatSpinner("Duration"));
		editors.add(new DefaultCellEditor(interpolationComboBox));
		for(EffectType effect: EffectType.values())
			transitionComboBox.addItem(effect.toString());
		for(InterpolationType interpolation: InterpolationType.values())
			interpolationComboBox.addItem(interpolation.toString());
	}
	
	@Override
	public void clear(String... names){
		super.clear("Background", "Music", "Transition", "Duration", "Interpolation");
	}
	
	@Override
	public void update(String... values){
		Scene scene = Scene.getCurrentScene();
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
		Scene scene = Scene.getCurrentScene();
		switch(key){
			case "Background": 
				scene.sceneBackground = value;
				if(!scene.sceneBackground.equals("None"))
					Scene.getCurrentScene().setBackground(scene.sceneBackground);
				else
					Scene.getCurrentScene().removeBackground();
				break;
			case "Music": 
				scene.sceneMusic = value;
				if(!scene.sceneMusic.equals("None"))
					Asset.musicPlay(scene.sceneMusic);
				break;
			case "Transition": 
				scene.sceneTransition = value;
				break;
			case "Duration": 
				scene.sceneDuration = Float.parseFloat(value);
				break;
			case "Interpolation":
				scene.sceneInterpolationType = InterpolationType.valueOf(value);
				break;
		}
		Effect.createEffect(Scene.getRoot(), EffectType.valueOf(scene.sceneTransition), 1f,
				scene.sceneDuration, scene.sceneInterpolationType);
		Scene.isDirty = true;
	}
}

class SceneEffectRenderer extends BaseRenderer {
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBorder(noFocusBorder);
        if(column == 0)
        	return new HeaderLabel(value.toString());
        else {
        	if(row == 3){
        		if(!value.toString().isEmpty())
        			spinnerFloat.setValue(Float.parseFloat(value.toString()));
        		else
        			spinnerFloat.setValue(new Float(0.0));
        		return spinnerFloat;
            }
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}