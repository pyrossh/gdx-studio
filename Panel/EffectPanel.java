import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import web.laf.lite.utils.UIUtils;


public class EffectPanel extends BaseTable {
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
	
	JComboBox<String> effectComboBox = createComboBox();
	JComboBox<String> durationComboBox = createComboBox(durations);
	JComboBox<String> interpolationComboBox = createComboBox();
	
	public EffectPanel(){
		super("Effects");
		UIUtils.setUndecorated(this, true);
		editors.add(new DefaultCellEditor(effectComboBox));
		editors.add(createNumberField());
		editors.add(new DefaultCellEditor(durationComboBox));
		editors.add(new DefaultCellEditor(interpolationComboBox));
		for(EffectType effect: EffectType.values())
			effectComboBox.addItem(effect.toString());
		for(InterpolationType interpolation: InterpolationType.values())
			interpolationComboBox.addItem(interpolation.toString());
	}
	
	@Override
	public void clear(String... names){
		super.clear("Effect", "Value", "Duration", "Interpolation", "addEffectDelay", "addActorDelay");
	}
	
	@Override
	public void update(String... propertyNames){
		if(SceneEditor.selectedActor instanceof ImageJson){
			ImageJson img = (ImageJson) SceneEditor.selectedActor;
			super.update("Effect", img.effectType.toString(), "Value", ""+img.effectValue, 
					"Duration", ""+img.effectDuration,"Interpolation", img.interpolationType.toString(),
					"addEffectDelay", ""+img.addEffectDelay,
					"addActorDelay", ""+img.addActorDelay);
		}
		else{
			clear();
		}
	}
	
	@Override
	public void setProperty(String key, String value){
		if(key.isEmpty() || value.isEmpty())
			return ;
		if(SceneEditor.selectedActor instanceof ImageJson){
			ImageJson img = (ImageJson) SceneEditor.selectedActor;
			img.clearActions();
			switch(key){
				case "Effect": img.effectType = EffectType.valueOf(value);break;
				case "Value": img.effectValue = Float.parseFloat(value);break;
				case "Duration": img.effectDuration = Float.parseFloat(value);break;
				case "Interpolation":img.interpolationType = InterpolationType.valueOf(value);break;
			}
			Effect.createEffect(img);
			SceneEditor.isDirty = true;
		}
	}
}