package gdxstudio.panel;
import gdxstudio.Content;
import gdxstudio.Frame;
import gdxstudio.Icon;
import gdxstudio.Style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;

public class ProjectPanel extends JPanel implements ActionListener, TableModelListener {
	private static final long serialVersionUID = 1L;
	static DefaultTableModel propModel = new DefaultTableModel(){
		private static final long serialVersionUID = 1L;

		@Override
         public Class<?> getColumnClass(int columnIndex) {
             return String.class;
         }
		 
		 public boolean isCellEditable(int row, int column) {
	            if (column == 1 && row != 0)
	                return true;
	            return false;
			}
	};
	
	static JTable propTable = new JTable(propModel){
		private static final long serialVersionUID = 1L;

		public TableCellEditor getCellEditor(int row, int column)
        {
            int modelColumn = convertColumnIndexToModel( column );

            if (modelColumn == 1)
                return editors.get(row);
            else
                return super.getCellEditor(row, column);
        }
	};
	
	JButton moreBtn;
	static JLabel prjIcon = new JLabel();
	static List<TableCellEditor> editors = new ArrayList<TableCellEditor>(3);
	static JTextField versionField = new JTextField();
	public static JComboBox<String> targetComboBox = BaseTable.createComboBox("Desktop", "Android", "iOS");
	static{
		editors.add(BaseTable.createTextFieldEditor());
		editors.add(new DefaultCellEditor(versionField));
		editors.add(new DefaultCellEditor(targetComboBox));
	}
	
	public ProjectPanel(){
		super(new VerticalFlowLayout());
		UIUtils.setUndecorated(this, true);
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		UIUtils.setDrawSides(this, false, false, false, false);
		propTable.setTableHeader(null);
		propTable.setDefaultRenderer(String.class, new BaseRenderer());
		propTable.setShowGrid(true);
		propTable.setColumnSelectionAllowed(false);
		propTable.setRowSelectionAllowed(false);
		propTable.setCellSelectionEnabled(true);
		propTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		propModel.setColumnCount(2);
		propModel.addRow(new String[]{"Project", "Name"});
		propModel.addRow(new String[]{"Sink", ""});
		propModel.addRow(new String[]{"Target", "Desktop"});
		JScrollPane scrollPane = new JScrollPane(propTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(168, 48));
		UIUtils.setDrawBorder(scrollPane, false);
		add(new Style.TitleButton("Project", this));
		JPanel hoz1 = new JPanel(new HorizontalFlowLayout(0));
		hoz1.setBackground(Color.black);
		hoz1.add(prjIcon);
		hoz1.add(scrollPane);
		add(hoz1);
		moreBtn = new JButton("More");
		UIUtils.setUndecorated(moreBtn, true);
		UIUtils.setShadeWidth(moreBtn, 0);
		UIUtils.setDrawFocus(moreBtn, false);
		UIUtils.setRound(moreBtn, 0);
		UIUtils.setRolloverDecoratedOnly(moreBtn, true);
		moreBtn.setOpaque(false);
		//moreBtn.setPreferredSize(new Dimension(100, 20));
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.border));
		//p.setPreferredSize(new Dimension(200, 20));
		UIUtils.setUndecorated(p, true);
		UIUtils.setMargin(p, new Insets(0,0,0,0));
		UIUtils.setMargin(moreBtn, new Insets(0,0,0,0));
		p.add(moreBtn);
		add(p);
		if(Content.projectExists())
			update();
		propModel.addTableModelListener(this);
		moreBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Frame.projectSettingsPanel.isVisible())
					moreBtn.setText("More");
				else
					moreBtn.setText("Less");
				Frame.projectSettingsPanel.setVisible(!Frame.projectSettingsPanel.isVisible());
				Frame.projectSettingsPanel.repaint();
			}
		 });
	}
	
	public void update(){
		TableModelListener tl = propModel.getTableModelListeners()[0];
		propModel.removeTableModelListener(tl);
		propModel.getDataVector().clear();
		File prjFile = new File(Content.getProject()+"icon.png");
		if(prjFile.exists()){
			prjIcon.setIcon(new ImageIcon(prjFile.getPath()));
			propModel.addRow(new String[]{"Project", prjFile.getParentFile().getName()});
		}
		else{
			prjIcon.setIcon(Icon.icon("help"));
			propModel.addRow(new String[]{"Project", "No Project"});
		}
		propModel.addRow(new String[]{"Version", ""});
		propModel.addRow(new String[]{"Target", "Desktop"});
		propModel.addTableModelListener(tl);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if(e.getLastRow() < propModel.getRowCount() && e.getType() == TableModelEvent.UPDATE){
			String key = (String)propModel.getValueAt(e.getLastRow(), e.getColumn()-1);
			String value = (String)propModel.getValueAt(e.getLastRow(), e.getColumn());
			setProperty(key, value);
		}
	}
	
	public void setProperty(String key, String value){
		if(key.isEmpty() || value.isEmpty())
			return ;
		switch(key){
			case "Version": Frame.projectSettingsPanel.updateVersion(value);break;
			case "Target": Frame.projectSettingsPanel.updateTarget(value);break;
		}
	}
	
	public static void updateProperty(String key, String value){
		TableModelListener tl = propModel.getTableModelListeners()[0];
		propModel.removeTableModelListener(tl);
		if(key.isEmpty() || value.isEmpty())
			return ;
		switch(key){
			case "Version": propModel.setValueAt(value, 1, 1);break;
			case "Target":propModel.setValueAt(value, 2, 1);break;
		}
		propModel.addTableModelListener(tl);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		propTable.setVisible(!propTable.isVisible());
	}
}