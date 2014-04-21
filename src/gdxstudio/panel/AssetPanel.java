package gdxstudio.panel;
import gdxstudio.Style;

import java.awt.Dimension;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import scene2d.Asset;
import web.laf.lite.widget.CenterPanel;

final public class AssetPanel extends BaseList {
	private static final long serialVersionUID = 1L;
	
	private static String[] comboText = new String[]{"Font", "Texture", "Animation", "Music", "Sound", "Particle", 
		"Model"};
	private static JComboBox<String> combo = new JComboBox<String>(comboText);
	
	String[] btns = new String[]{
			"New File", "newfile", "Delete", "trash",
	};
	
	DragSource dragSource = new DragSource();

	public AssetPanel(){
		super("Assets", "");
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		JPanel pan = Style.createButtonToolBar(this, btns);
		combo.setPreferredSize(new Dimension(145, 16));
		pan.add(new CenterPanel(combo, false, true));
		add(pan);
		add(scrollPane);
	    combo.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateAsset();
			}
	    });
	}
	
	public void updateAsset(){
		listModel2.clear();
		switch(combo.getSelectedItem().toString()){
			case "Font": 
				for(String names: Asset.fontMap.keys()) 
					listModel2.addElement(names);
				break;
			case "Texture": 
				for(String names: Asset.texMap.keys()) 
					listModel2.addElement(names);
				break;
			case "Music": 
				for(String names: Asset.musicMap.keys()) 
					listModel2.addElement(names);
				break;
			case "Sound": 
				for(String names: Asset.soundMap.keys()) 
					listModel2.addElement(names);
				break;
				//case "Particle": 	
				//	for(String names: Asset.musicMap.keys()) 
				//		assetModel.addElement(names); 
				//	break;
			case "Model": 
				for(Object names: Asset.modelMap.toArray()) 
					listModel2.addElement(names.toString());
				break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		this.setVisible(false);
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}