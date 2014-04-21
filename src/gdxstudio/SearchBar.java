package gdxstudio;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.utils.UIUtils;

final public class SearchBar extends JTextField {
	private static final long serialVersionUID = 1L;
	static int index = 0;
	
	public SearchBar(){
		UIUtils.setRound(this, 2);
		UIUtils.setDrawFocus(this, false);
		UIUtils.setInputPrompt(this, "Search");
		UIUtils.setShadeWidth(this, 0);
        setPreferredSize(new Dimension(280, 22));
        
        JButton searchUpBtn = Style.createToolButton("SearchUP", "up", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Content.currentView.equals("Editor")){
					Editor.context.setSearchForward(false);
					Content.editor.find(getText());
				}
			}
        });
        UIUtils.setLeftRightSpacing(searchUpBtn, 0);
        UIUtils.setUndecorated(searchUpBtn, true);
        JButton searchDownBtn = Style.createToolButton("SearchDown", "down", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Content.currentView.equals("Editor")){
					Editor.context.setSearchForward(true);
					Content.editor.find(getText());
				}
			}
        });
        UIUtils.setLeftRightSpacing(searchDownBtn, 0);
        UIUtils.setUndecorated(searchDownBtn, true);
        
        JPanel updownPanel = new JPanel(new HorizontalFlowLayout(0));
        updownPanel.add(searchUpBtn);
        updownPanel.add(searchDownBtn);
        updownPanel.setOpaque(false);
        UIUtils.setUndecorated(updownPanel, true);
        UIUtils.setTrailingComponent(this, updownPanel);
	}
}