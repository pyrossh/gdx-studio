import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import web.laf.lite.layout.ToolbarLayout;
import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;
import web.laf.lite.widget.NumberTextField;

public abstract class BaseTable extends JPanel implements ActionListener, TableModelListener {
	private static final long serialVersionUID = 1L;
	
	protected DefaultTableModel tableModel = new DefaultTableModel(){
		private static final long serialVersionUID = 1L;

		 @Override
         public Class<?> getColumnClass(int columnIndex) {
             return String.class;
         }
		 
		 public boolean isCellEditable(int row, int column) {
	            if (column == 1)
	                return true;
	            return false;
			}
	};
	protected final JTable table = new JTable(tableModel){
		private static final long serialVersionUID = 1L;

		public TableCellEditor getCellEditor(int row, int column)
        {
            int modelColumn = convertColumnIndexToModel( column );
            if (modelColumn == 1){
            	if(row<editors.size())
            		return editors.get(row);
            }
            return super.getCellEditor(row, column);
        }
	};
	protected final JScrollPane scrollPane;
	protected final List<TableCellEditor> editors = new ArrayList<TableCellEditor>(3);
	public final JButton headerButton;
	
	public BaseTable(String title, DefaultTableModel model, DefaultTableCellRenderer renderer){
		super(new VerticalFlowLayout());
		if(model != null)
			 tableModel = model;
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		//UIUtils.setDrawSides(this, false, false, false, false);
		table.setTableHeader(null);
		if(renderer != null)
			table.setDefaultRenderer(String.class, renderer);
		else
			table.setDefaultRenderer(String.class, new BaseRenderer());
		table.setShowGrid(true);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableModel.setColumnCount(2);
		tableModel.addTableModelListener(this);
		clear();
		headerButton = Style.createHeaderButton(title, this);
		if(!title.isEmpty()){
			add(headerButton);
		}
		scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(200, table.getRowCount()*table.getRowHeight()));
		UIUtils.setDrawBorder(scrollPane, false);
		add(scrollPane);
	}
	
	public BaseTable(String title){
		this(title, null, null);
	}
	
	public void lock(){
		tableModel.removeTableModelListener(this);
	}
	
	public void unlock(){
		tableModel.addTableModelListener(this);
	}
	
	public void enable(){
		table.setEnabled(true);
	}
	
	public void disable(){
		table.setEnabled(false);
	}
	
	public void addRow(String key, String value){
		tableModel.addRow(new String[]{key, value});
	}
	
	public void addRow(String key, boolean value){
		tableModel.addRow(new Object[]{key, new Boolean(value)});
	}
	
	public void update(String... propertyAndValueNames){
		if(table.isEditing())
			table.getCellEditor().stopCellEditing();
		tableModel.getDataVector().clear();
		for(int i=0;i<propertyAndValueNames.length;i+=2)
			addRow(propertyAndValueNames[i], propertyAndValueNames[i+1]);
		table.setEnabled(true);
		table.repaint();
	}
	
	public void clear(String... propertyNames){
		if(table.isEditing())
			table.getCellEditor().stopCellEditing();
		tableModel.getDataVector().clear();
		for(int i=0;i<propertyNames.length;i++)
			addRow(propertyNames[i], "");
		table.repaint();
		table.setEnabled(false);
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if(e.getType() == TableModelEvent.UPDATE && e.getLastRow() < tableModel.getRowCount()){
			String key = (String)tableModel.getValueAt(e.getLastRow(), e.getColumn()-1);
			String value = tableModel.getValueAt(e.getLastRow(), e.getColumn()).toString();
			setProperty(key, value);
		}
	}
	
	public void updateProperty(String key, String value, int row){
		lock();
		tableModel.setValueAt(value, row, 1);
		unlock();
	}
	
	public int getRowCount(){
		return table.getRowCount();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		scrollPane.setVisible(!scrollPane.isVisible());
		validate();
		repaint();
	}
	
	public static JSpinner createSpinnerInteger(){
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(-99999),
				new Integer(99999), new Integer(1)));
		UIUtils.setDrawBorder(spinner, false);
		UIUtils.setShadeWidth(spinner, 0);
		UIUtils.setRound(spinner, 0);
		UIUtils.setDrawFocus(spinner, false);
		return spinner;
	}
	
	public static JSpinner createSpinnerFloat(){
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(new Float(0), new Float(-99999),
				new Float(99999), new Float(0.1f)));
		UIUtils.setDrawBorder(spinner, false);
		UIUtils.setShadeWidth(spinner, 0);
		UIUtils.setRound(spinner, 0);
		UIUtils.setDrawFocus(spinner, false);
		return spinner;
	}
	
	public static DefaultCellEditor createTextFieldEditor(){
		JTextField tf = new JTextField();
		UIUtils.setShadeWidth(tf, 0);
		UIUtils.setRound(tf, 0);
		UIUtils.setDrawFocus(tf, false);
		return new DefaultCellEditor(tf);
	}
	
	public static JTextField createTextField(){
		JTextField tf = new JTextField();
		UIUtils.setShadeWidth(tf, 0);
		UIUtils.setRound(tf, 0);
		UIUtils.setDrawFocus(tf, false);
		return tf;
	}
	
	public static DefaultCellEditor createNumberField(){
		NumberTextField tf = new NumberTextField();
		UIUtils.setShadeWidth(tf, 0);
		UIUtils.setRound(tf, 0);
		UIUtils.setDrawFocus(tf, false);
		return new DefaultCellEditor(tf);
	}
	
	public static JComboBox<String> createComboBox(){
		JComboBox<String> combo = new JComboBox<String>();
		UIUtils.setShadeWidth(combo, 0);
		UIUtils.setRound(combo, 0);
		UIUtils.setDrawFocus(combo, false);
		return combo;
	}
	
	public static JComboBox<String> createComboBox(String... items){
		JComboBox<String> combo = new JComboBox<String>(items);
		UIUtils.setShadeWidth(combo, 0);
		UIUtils.setRound(combo, 0);
		UIUtils.setDrawFocus(combo, false);
		return combo;
	}
	
	public static DefaultCellEditor createBooleanEditor(){
		return new DefaultCellEditor(new JComboBox<String>(new String[]{"false", "true"}));
	}
	
	public static TableCellEditor createCheckBoxEditor(){
		return new CheckBoxEditor();
	}
	
	public void setProperty(String name, String value){
	}
}


class BaseRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	JLabel label = new JLabel("      ");
	JCheckBox checkBox = new JCheckBox();
	JSpinner spinnerFloat = BaseTable.createSpinnerFloat();
	JSpinner spinnerInteger = BaseTable.createSpinnerInteger();
	
	public BaseRenderer(){
		spinnerInteger.setBorder(BorderFactory.createEmptyBorder());
		spinnerFloat.setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setBorder(noFocusBorder);
		if(column == 0)
        	return new HeaderLabel(value.toString());
		else {
			if(value instanceof Boolean) { // Boolean
	    	    checkBox.setSelected(((Boolean) value).booleanValue());
	    	    checkBox.repaint();
	    	    checkBox.setHorizontalAlignment(JLabel.CENTER);
	    	    return checkBox;
	    	}
		}
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

class HeaderLabel extends JLabel{
	private static final long serialVersionUID = 1L;

	HeaderLabel(String text){
		super(text);
		setHorizontalAlignment ( JLabel.CENTER );
		UIUtils.setMargin(this, new Insets(0,0,0,0));
		setForeground(Style.font);
	}

	@Override
	public void paint ( Graphics g){
		Style.drawTableHeader(g, getWidth(), getHeight());
		super.paint(g);
	}
}

class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final long serialVersionUID = 1L;
	Color currentColor = Color.red;
	JButton button;
	JColorChooser colorChooser;
	JDialog dialog;
	JPanel pan;
	protected static final String EDIT = "edit";

	public ColorEditor() {
		//Set up the editor (from the table's point of view),
		//which is a button.
		//This button brings up the color chooser dialog,
		//which is the editor from the user's point of view.
		button = new JButton();
		button.setActionCommand(EDIT);
		button.addActionListener(this);
		button.setBorderPainted(false);
		button.setRolloverEnabled(false);
		button.setOpaque(true);
		pan = new JPanel(new ToolbarLayout());
		pan.setOpaque(false);
		UIUtils.setMargin(pan, new Insets(2,3,2,3));
		pan.add(button);
		//Set up the dialog that the button brings up.
		colorChooser = new JColorChooser();
		dialog = JColorChooser.createDialog(button,
				"Pick a Color",true,  //modal
				colorChooser,
				this,  //OK button handler
				null); //no CANCEL button handler
	}

	/**
	 * Handles events from the editor button and from
	 * the dialog's OK button.
	 */
	public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())) {
			//The user has clicked the cell, so
			//bring up the dialog.
			button.setBackground(currentColor);
			colorChooser.setColor(currentColor);
			dialog.setVisible(true);

			//Make the renderer reappear.
			fireEditingStopped();

		} else { //User pressed dialog's "OK" button.
			currentColor = colorChooser.getColor();
			dialog.setVisible(false);
		}
	}

	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	@Override
	public String getCellEditorValue() {
		return String.format("%02x%02x%02x%02x",currentColor.getRed(), 
				currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha());
	}

	//Implement the one method defined by TableCellEditor.
	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		String colorString = (String) value;
		if(colorString.length() == 8 && colorString.matches("[0-9A-Fa-f]+")){
			 com.badlogic.gdx.graphics.Color cl = com.badlogic.gdx.graphics.Color.valueOf(colorString);
			 currentColor = new Color(cl.r, cl.g, cl.b, cl.a);
		}
		return pan;
	}

	@Override
	public void addCellEditorListener(CellEditorListener arg0) {
		super.addCellEditorListener(arg0);
	}

	@Override
	public void cancelCellEditing() {
		dialog.setVisible(false);
		super.cancelCellEditing();
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener arg0) {
		super.removeCellEditorListener(arg0);
	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		return super.shouldSelectCell(arg0);
	}

	@Override
	public boolean stopCellEditing() {
		dialog.setVisible(false);
		return super.stopCellEditing();
	}
}

class CheckBoxEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final long serialVersionUID = 1L;
	private JCheckBox checkBox;
	
	public CheckBoxEditor() {
		checkBox = new JCheckBox();
		checkBox.setHorizontalAlignment(JLabel.LEFT);
		//checkBox.addActionListener(this);
	}
	
	public CheckBoxEditor(boolean value) {
		checkBox = new JCheckBox();
		checkBox.setSelected(value);
		checkBox.setHorizontalAlignment(JLabel.LEFT);
		//checkBox.addActionListener(this);
	}
	
	/**
	 * Handles events from the editor button and from
	 * the dialog's OK button.
	 */
	public void actionPerformed(ActionEvent e) {
	}

	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	@Override
	public Boolean getCellEditorValue() {
		return checkBox.isSelected();
	}

	//Implement the one method defined by TableCellEditor.
	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		checkBox.setSelected(((Boolean) value).booleanValue());
		checkBox.repaint();
		return checkBox;
	}

	@Override
	public void addCellEditorListener(CellEditorListener arg0) {
		super.addCellEditorListener(arg0);
	}

	@Override
	public void cancelCellEditing() {
		super.cancelCellEditing();
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener arg0) {
		super.removeCellEditorListener(arg0);
	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		return super.shouldSelectCell(arg0);
	}

	@Override
	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}
}

class SpinnerIntegerEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	JSpinner spinner;

    // Initializes the spinner.
    public SpinnerIntegerEditor() {
        spinner = BaseTable.createSpinnerInteger();
        spinner.setBorder(BorderFactory.createEmptyBorder());
       // ((DefaultEditor) spinner.getEditor()).getTextField().setEnabled(false);
        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
    }

    // Prepares the spinner component and returns it.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
    		int row, int column) {
        spinner.setValue((Integer)Integer.parseInt(value.toString()));
        return spinner;
    }

    public boolean isCellEditable( EventObject eo ) {
        return true;
    }

    // Returns the spinners current value.
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    public boolean stopCellEditing() {
        try {
            spinner.commitEdit();
        } catch ( java.text.ParseException e ) {
            JOptionPane.showMessageDialog(null,"Invalid value, discarding.");
        }
        return super.stopCellEditing();
    }
}

class SpinnerFloatEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	JSpinner spinner;

    // Initializes the spinner.
    public SpinnerFloatEditor() {
        spinner = BaseTable.createSpinnerFloat();
        spinner.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
    }

    // Prepares the spinner component and returns it.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
    		int row, int column) {
        spinner.setValue((Float)Float.parseFloat(value.toString()));
        return spinner;
    }

    public boolean isCellEditable( EventObject eo ) {
        return true;
    }

    // Returns the spinners current value.
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    public boolean stopCellEditing() {
        try {
            spinner.commitEdit();
        } catch ( java.text.ParseException e ) {
            JOptionPane.showMessageDialog(null, "Invalid value, discarding.");
        }
        return super.stopCellEditing();
    }
}