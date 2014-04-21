package gdxstudio.panel;
import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.utils.UIUtils;

public class WidgetPanel extends BaseList implements DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1L;
	
	DragSource dragSource = new DragSource();
	String[] btns = new String[]{
			"New Widget", "new","Delete", "trash"
	};
	private JPanel hbox = new JPanel(new HorizontalFlowLayout());
	StyleList sl = new StyleList();
	StyleTable st = new StyleTable();
	
	
	public WidgetPanel(){
		super("Widgets", "Label","Image", "Sprite", "Button","TextButton", "CheckBox", 
				"List", "SelectBox", "Slider", "TextField",
				"Table", "Dialog", "Touchpad", "Model");
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		dragSource.addDragSourceListener(this);
	    dragSource.createDefaultDragGestureRecognizer(list, DnDConstants.ACTION_MOVE, this);
	    hbox.add(list);
	    hbox.add(sl);
	    hbox.add(st);
	    //this.remove(scrollPane);
	    UIUtils.setDrawBorder(scrollPane, true);
	    scrollPane.setPreferredSize(new Dimension(200, 150));
	    scrollPane.setViewportView(hbox);
	    add(scrollPane);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent arg0) {
	}


	@Override
	public void dragEnter(DragSourceDragEvent arg0) {
	}


	@Override
	public void dragExit(DragSourceEvent arg0) {
	}


	@Override
	public void dragOver(DragSourceDragEvent arg0) {
	}


	@Override
	public void dropActionChanged(DragSourceDragEvent arg0) {
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
	    StringSelection text = new StringSelection(list.getSelectedValue());
	    dragSource.startDrag(event, DragSource.DefaultMoveDrop, text, this);
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}

class StyleList extends BaseList {
	private static final long serialVersionUID = 1L;

	public StyleList(){
		super("Styles", "LabelStyle", "ButtonStyle");
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
	}
}

class StyleTable extends BaseTable {
	private static final long serialVersionUID = 1L;

	public StyleTable(){
		super("Properties");
	}
	
	@Override
	public void clear(String... items){
		super.clear("StyleName", "", "", "", "");
	}
}
