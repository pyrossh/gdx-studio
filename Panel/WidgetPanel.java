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

import javax.swing.event.ListSelectionEvent;

public class WidgetPanel extends BaseList implements DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1L;
	
	DragSource dragSource = new DragSource();
	String[] btns = new String[]{
			"New Widget", "new","Delete", "trash"
	};
	
	public WidgetPanel(){
		super("Widgets", "Label","Image", "Sprite", "Button","TextButton", "CheckBox", 
				"List", "SelectBox", "Slider", "TextField",
				"Table", "Dialog", "Touchpad", "Map");
		scrollPane.setPreferredSize(new Dimension(200, 100));
		dragSource.addDragSourceListener(this);
	    dragSource.createDefaultDragGestureRecognizer(list, DnDConstants.ACTION_MOVE, this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent arg0) {
	}


	@Override
	public void dragEnter(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dragExit(DragSourceEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dragOver(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dropActionChanged(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub
		
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
