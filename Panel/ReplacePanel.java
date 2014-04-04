import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import web.laf.lite.layout.HorizontalFlowLayout;

public class ReplacePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton findButton = new JButton("Find");
	private JTextField replaceField = new JTextField();
	private JButton replaceButton = new JButton("Replace");
	private JButton replaceFindButton = new JButton("Replace/Find");
	private JButton replaceAllButton = new JButton("ReplaceAll");

	public ReplacePanel(){
		super(new HorizontalFlowLayout());
		add(new JLabel("Replace With:"));
		add(replaceField);
		add(replaceButton);
		add(replaceFindButton);
		add(replaceAllButton);
		setVisible(false);
	}
}
