import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;

public abstract class BaseList extends JPanel implements ListSelectionListener, ActionListener {
	private static final long serialVersionUID = 1L;
	protected JList<String> list;
	protected DefaultListModel<String> listModel;
	protected SortedListModel listModel2;
	protected JScrollPane scrollPane;
	protected final JLabel header;
	
	BaseList(String title){
		super(new VerticalFlowLayout());
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		UIUtils.setDecorateSelection(list, false);
		header = new Style.TitleLabel(title);
		add(header);
		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(200, 175));
		UIUtils.setDrawBorder(scrollPane, false);
		add(scrollPane);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.border));
	}
	
	BaseList(String title, boolean dontAddScroll){
		super(new VerticalFlowLayout());
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		header = new Style.TitleLabel(title);
		add(header);
		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(200, 175));
		UIUtils.setDrawBorder(scrollPane, false);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.border));
	}
	
	BaseList(String title, String... items){
		super(new VerticalFlowLayout());
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		//listModel = new DefaultListModel<String>();
		list = new JList<String>(items);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		header = new Style.TitleLabel(title);
		add(header);
		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(200, 175));
		UIUtils.setDrawBorder(scrollPane, false);
		add(scrollPane);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.border));
	}
	
	@SuppressWarnings("unchecked")
	BaseList(String title, String useSort){
		super(new VerticalFlowLayout());
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		listModel2 = new  SortedListModel();
		list = new JList<String>(listModel2);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		header = new Style.TitleLabel(title);
		add(header);
		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(200, 175));
		UIUtils.setDrawBorder(scrollPane, false);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.border));
	}
	
	public void enable(){
		list.setEnabled(true);
	}
	
	public void disable(){
		list.setEnabled(false);
	}
	
	public void lock(){
		list.removeListSelectionListener(this);
	}
	
	public void unlock(){
		list.addListSelectionListener(this);
	}
	
	public void clear(){
		lock();
		listModel.clear();
		unlock();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		scrollPane.setVisible(!scrollPane.isVisible());
		validate();
		repaint();
	}
}

@SuppressWarnings("rawtypes")
class SortedListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	SortedSet<Object> model;

	public SortedListModel() {
		model = new TreeSet<Object>();
	}

	public int getSize() {
		return model.size();
	}

	public Object getElementAt(int index) {
		return model.toArray()[index];
	}
	
	public int indexOf(Object element){
		int index = 0;
		for(Object e: model.toArray()){
			if(e.equals(element))
				break;
			index++;
		}
		return index;
	}
	

	public void addElement(Object element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}
	public void addAll(Object elements[]) {
		Collection<Object> c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public Object firstElement() {
		return model.first();
	}

	public Iterator iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		return model.last();
	}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}