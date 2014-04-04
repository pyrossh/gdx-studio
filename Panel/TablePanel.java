import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.SpringUtils;
import web.laf.lite.utils.UIUtils;


public class TablePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	//protected final JScrollPane scrollPane;
	public final JButton headerButton;
	private static HashMap<String, JComponent> values = new HashMap<String, JComponent>();
	
	public TablePanel(String title){
		super(new VerticalFlowLayout());
		UIUtils.setUndecorated(this, false);
		headerButton = Style.createHeaderButton(title, this);
		if(!title.isEmpty()){
			add(headerButton);
		}
		//scrollPane = new JScrollPane(this);
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//scrollPane.setPreferredSize(new Dimension(200, 500));
		//UIUtils.setDrawBorder(scrollPane, false);
		//add(scrollPane);
		createProperty("Name", createTextField());
		createProperty("  X  ", createSpinner() , "  Y  ", createSpinner());
		//createProperty("OX ", createSpinner() ,  "OY ", createSpinner());
		createProperty("Width", createSpinner(), "Height", createSpinner());
		//createProperty("Height", createSpinner());
		Main.log("COMPK"+getProperty("Y"));
		setProperty("X", 500);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void createProperty(String name, JComponent comp){
		JPanel hoz = new JPanel(new SpringLayout());
   	 	UIUtils.setUndecorated(hoz, false);
   	 	UIUtils.setMargin(hoz, new Insets(0, 0, 0, 0));
   	 	UIUtils.setShadeWidth(hoz, 0);
   	 	UIUtils.setRound(hoz, 0);
   	 	UIUtils.setDrawSides(hoz, false, false, true, false);
   	 	HeaderLabel x = new HeaderLabel(name);
   	 	UIUtils.setMargin(x, new Insets(0, 0, 0, 0));
   	 	comp.setBorder(BorderFactory.createEmptyBorder());
   	 	hoz.add(x);
   	 	hoz.add(comp);
   	 	values.put(name, comp);
   	 	SpringUtils.makeGrid(hoz, 1, 2, 0, 0, 0, 0);
   	 	add(hoz);
	}

	public void createProperty(String name, JComponent comp, String name2, JComponent comp2){
		JPanel hoz = new JPanel(new SpringLayout());
		UIUtils.setUndecorated(hoz, false);
   	 	UIUtils.setMargin(hoz, new Insets(0, 0, 0, 0));
   	 	UIUtils.setShadeWidth(hoz, 0);
   	 	UIUtils.setRound(hoz, 0);
   	 	UIUtils.setDrawSides(hoz, false, false, true, false);
   	 	HeaderLabel x = new HeaderLabel(name);
   	 	UIUtils.setMargin(x, new Insets(0, 0, 0, 0));
   	 	HeaderLabel y = new HeaderLabel(name2);
   	 	hoz.add(x);
   	 	hoz.add(comp);
   	 	hoz.add(y);
	 	hoz.add(comp2);
		values.put(name, comp);
		values.put(name2, comp2);
	 	SpringUtils.makeGrid(hoz, 1, 4, 0, 0, 0, 0);
   	 	add(hoz);
	}
	
	public void createProperty(String name, JComponent comp, String name2, JComponent comp2, 
			String name3, JComponent comp3){
		JPanel hoz = new JPanel(new SpringLayout());
		UIUtils.setUndecorated(hoz, false);
   	 	UIUtils.setMargin(hoz, new Insets(0, 0, 0, 0));
   	 	UIUtils.setShadeWidth(hoz, 0);
   	 	UIUtils.setRound(hoz, 0);
   	 	UIUtils.setDrawSides(hoz, false, false, true, false);
   	 	
   	 	HeaderLabel x = new HeaderLabel(name);UIUtils.setMargin(x, new Insets(0, 0, 0, 0));
   	 	HeaderLabel y = new HeaderLabel(name2);UIUtils.setMargin(y, new Insets(0, 0, 0, 0));
	 	HeaderLabel z = new HeaderLabel(name3);UIUtils.setMargin(z, new Insets(0, 0, 0, 0));
	 	
   	 	hoz.add(x);
   	 	hoz.add(comp);
   	 	hoz.add(y);
	 	hoz.add(comp2);
	 	hoz.add(z);
	 	hoz.add(comp3);
		values.put(name, comp);
		values.put(name2, comp2);
		values.put(name3, comp3);
	 	SpringUtils.makeCompactGrid(hoz, 1, 6, 0, 0, 0, 0);
   	 	add(hoz);
	}
	
	public JSpinner createSpinner(){
		JSpinner spinner = new JSpinner();
		UIUtils.setDrawBorder(spinner, false);
		UIUtils.setShadeWidth(spinner, 0);
		UIUtils.setRound(spinner, 0);
		UIUtils.setDrawFocus(spinner, false);
		return spinner;
	}
	
	public JTextField createTextField(){
		JTextField tf = new JTextField();
		UIUtils.setShadeWidth(tf, 0);
		UIUtils.setRound(tf, 0);
		UIUtils.setDrawFocus(tf, false);
		return tf;
	}
	
	public String getProperty(String name){
		Component c = values.get(name);
		if(c instanceof JSpinner)
			return ((JSpinner)c).getValue().toString();
		if(c instanceof JTextField){
			return ((JTextField)c).getText();
		}
		return "";
	}
	
	public void setProperty(String name, String value){
		Component c = values.get(name);
		if(c instanceof JTextField)
			((JTextField)c).setText(value);
	}
	
	public void setProperty(String name, int value){
		Component c = values.get(name);
		if(c instanceof JSpinner)
			((JSpinner)c).setValue(value);
	}
}
