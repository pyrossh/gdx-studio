import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class EventPanel extends BaseTable {
	private static final long serialVersionUID = 1L;
	
	JComboBox<String> eventComboBox = createComboBox();
	JComboBox<String> onEventComboBox = createComboBox();
	JComboBox<String> sceneComboBox = createComboBox();
	
	public EventPanel(){
		super("Events");
		editors.add(new DefaultCellEditor(eventComboBox));
		editors.add(new DefaultCellEditor(onEventComboBox));
		editors.add(new DefaultCellEditor(sceneComboBox));
		for(EventType event: EventType.values())
			eventComboBox.addItem(event.toString());
		for(OnEventType onEvent: OnEventType.values())
			 onEventComboBox.addItem(onEvent.toString());
		for(String scene: Scene.scenesMap.keys())
			 sceneComboBox.addItem(scene);
	}
	
	@Override
	public void clear(String... names){
		super.clear("Event", "onEvent", "Scene");
	}
	
	@Override
	public void update(String... propertyNames){
		if(SceneEditor.selectedActor instanceof ImageJson){
			ImageJson img = (ImageJson) SceneEditor.selectedActor;
			super.update("Event", img.evtType.toString(), "onEvent", ""+img.onEvtType.toString(), 
					"Scene", ""+img.eventScene);
			sceneComboBox.removeAllItems();
			for(String scene: Scene.scenesMap.keys())
				 sceneComboBox.addItem(scene);
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
			ImageJson img = (ImageJson)SceneEditor.selectedActor;
			switch(key){
				case "Event": 
					img.evtType = EventType.valueOf(value);
					if(img.evtType == EventType.None){
						onEventComboBox.setEnabled(false);
					}
					else if(img.evtType == EventType.SceneCreated){
						onEventComboBox.setSelectedItem(OnEventType.DoEffect.toString());
						onEventComboBox.setEnabled(false);
					}
					else{
						onEventComboBox.setEnabled(true);
					}
						
					break;
				case "onEvent": 
					img.onEvtType = OnEventType.valueOf(value);
					if(img.onEvtType == OnEventType.DoEffect){
						sceneComboBox.setEnabled(false);
					}
					else{
						sceneComboBox.setEnabled(true);
					}
					break;
				case "Scene": img.eventScene = value;break;
			}
			SceneEditor.isDirty = true;
		}
	}
}