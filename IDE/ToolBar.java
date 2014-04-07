import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.layout.ToolbarLayout;
import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.popup.ButtonPopup;
import web.laf.lite.popup.PopupWay;
import web.laf.lite.utils.UIUtils;
import web.laf.lite.widget.Register;
import web.laf.lite.widget.WebButtonGroup;

final public class ToolBar extends JPanel {
	private static final long serialVersionUID = 1L;
	SearchBar searchBar;
	ProcessBuilder pb;
	Process runningProcess;
	OptionsPanel optionsPanel;
    
	public ToolBar(){
		super(new ToolbarLayout());
        initAbout();
        initProject();
        initOpen();
        initExport();
        addSeparator();
        initOptions();
        initStyle();
        addSeparator();
        add(Style.createToolPanel("Run", "go", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				run();
			}
	    }));
        add(Style.createToolPanel("Stop", "stop", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop();
			}
	    }));
        Style.btnMap.get("stop").setEnabled(false);
        addSeparator();
        addSpace();
        addSpace();
        initSearch();
        addSpace();
        addSpace();
        addSeparator();
        initView();
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.border));
	}
	
	@Override
	public void paintComponent(Graphics g){
		Style.drawHorizontalBar(g, getWidth (), getHeight ());
	}
	
	void initSearch(){
		searchBar = new SearchBar();
        final JPanel pan = new JPanel(new VerticalFlowLayout(FlowLayout.CENTER));
        pan.setOpaque(false);
        pan.add(searchBar);
        add(pan);
	}
	
	void initAbout(){
		add(Style.createToolPanel("", "sabout", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new JDialog(Frame.getInstance(), "About", false);
				dialog.setResizable(false);
				dialog.getContentPane().setLayout(new BorderLayout());
				final JPanel popupContent = new JPanel (new VerticalFlowLayout(5, 10));
				final JButton licenseBtn = new JButton("Apache License v2.0");
				final JPanel author = new JPanel(new HorizontalFlowLayout());
				final JPanel hoz = new JPanel(new HorizontalFlowLayout());
				popupContent.add(UIUtils.setBoldFont(new JLabel("GdxStudio v"+Main.version)));
				popupContent.add(new JSeparator(SwingConstants.HORIZONTAL));
				author.setOpaque(false);
				author.add(new JLabel("Created by: pyros2097"));
				author.add(licenseBtn);
				popupContent.add(author);
				popupContent.add(new JSeparator(SwingConstants.HORIZONTAL));
				popupContent.add(new JLabel("", Icon.icon("slibGDX"), JLabel.LEADING));
				popupContent.add(new JLabel("", Icon.icon("sweblaf"), JLabel.LEADING));
				hoz.setOpaque(false);
				hoz.add(new JLabel("", Icon.icon("shierologo"), JLabel.LEADING));
				hoz.add(new JLabel("", Icon.icon("stexturepacker"), JLabel.LEADING));
				popupContent.add(hoz);
				UIUtils.setUndecorated(popupContent, false);
				UIUtils.setMargin(popupContent, new Insets(0,0,0,0));
				UIUtils.setDrawSides(popupContent, false, false, false, false);
				dialog.getContentPane().add(popupContent);
				dialog.pack();
				dialog.setSize(525, 500);
				Dimension Size = Toolkit.getDefaultToolkit().getScreenSize();  
				dialog.setLocation(new Double((Size.getWidth()/2) - (dialog.getWidth()/2)).intValue(), 
						new Double((Size.getHeight()/2) - (dialog.getHeight()/2)).intValue());  
				dialog.setVisible(true);
			}
		}));
	}
	
	void initFile(){
		final JButton menuBtn1 = Style.createMenuButton("File");
        final ButtonPopup menu = new ButtonPopup(menuBtn1,PopupWay.downRight);
        menu.setRound(0);
        JPanel popupContent = new JPanel ( new VerticalFlowLayout ( 5, 5 ) );
        popupContent.setPreferredSize(new Dimension(200, 200));
        popupContent.add(UIUtils.setBoldFont(new JLabel("     ToolBar")));
        popupContent.add(new JSeparator(SwingConstants.HORIZONTAL));
        popupContent.add(UIUtils.setBoldFont(new JLabel("     Explorer")));
        popupContent.add(new JSeparator(SwingConstants.HORIZONTAL));
        popupContent.setOpaque(false);
        menu.setContent(popupContent);
        add(menuBtn1);
	}
        
    void initEdit(){
    	final JButton menuBtn2 = Style.createMenuButton("Edit");
        final ButtonPopup menu2 = new ButtonPopup(menuBtn2,PopupWay.downRight);
        menu2.setRound(0);
        JPanel popupContent2 = new JPanel ( new VerticalFlowLayout ( 5, 5 ) );
        popupContent2.setPreferredSize(new Dimension(200, 200));
        popupContent2.add(UIUtils.setBoldFont(new JLabel("     ToolBar")));
        popupContent2.add(new JSeparator(SwingConstants.HORIZONTAL));
        popupContent2.add(UIUtils.setBoldFont(new JLabel("     Explorer")));
        popupContent2.add(new JSeparator(SwingConstants.HORIZONTAL));
        popupContent2.setOpaque(false);
        menu2.setContent(popupContent2);
        add(menuBtn2);
	}
	
	void run(){
		if(!Style.btnMap.get("stop").isEnabled()){
			Style.btnMap.get("stop").setEnabled(true);
			Style.btnMap.get("go").setEnabled(false);
		}
		if(!new File(Content.getProject()+new File(Content.getProject()).getName()+".jar").exists())
			Export.createJar();
		pb = new ProcessBuilder("java", "-jar", Content.getProject()+new File(Content.getProject()).getName()+".jar");
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		pb.redirectInput(Redirect.INHERIT);
		try {
			runningProcess = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void stop(){
		if(!Style.btnMap.get("go").isEnabled()){
			Style.btnMap.get("stop").setEnabled(false);
			Style.btnMap.get("go").setEnabled(true);
		}
		if(runningProcess != null)
			runningProcess.destroy();
	}
	
	void initProject(){
		JButton prj = Style.createMenuButton("Project");
		prj.setIcon(Icon.icon("newprj"));
		prj.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileDialog fd = new FileDialog((Frame)null, "New GdxStudio Project", FileDialog.SAVE);
				fd.setVisible(true);
				String filename = fd.getDirectory()+fd.getFile();
				if(filename != null && !filename.isEmpty())
					if(new File(filename).exists())
						Export.createProject(fd.getDirectory());
			}
		});
		add(prj);
	}
	
	void initOpen(){
		JButton open = Style.createMenuButton("Open");
		open.setIcon(Icon.icon("eopen"));
		open.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog((Frame)null, "Open GdxStudio Project", FileDialog.LOAD);
				fd.setVisible(true);
				String filename = fd.getDirectory()+fd.getFile();
				if(filename != null)
					if(!filename.isEmpty() && filename.contains("config"))
						if(new File(filename).exists())
							Export.openProject(fd.getDirectory());
			}
		});
		add(open);
	}
	
	void initExport(){
		JButton export = Style.createMenuButton("Export");
		export.setIcon(Icon.icon("export"));
		export.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ProjectPanel.targetComboBox.getSelectedIndex() == 0)
					Export.createJar();
				else
					Export.createDex();
			}
		});
		add(export);
	}

	void initOptions(){
        optionsPanel = new OptionsPanel();
	    add(Style.createToolPanel("Options", "options", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(Frame.getInstance(), "Options", false);
				dialog.setResizable(false);
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(optionsPanel);
				dialog.pack();
				dialog.setSize(525, 500);
				Dimension Size = Toolkit.getDefaultToolkit().getScreenSize();  
				dialog.setLocation(new Double((Size.getWidth()/2) - (dialog.getWidth()/2)).intValue(), 
						new Double((Size.getHeight()/2) - (dialog.getHeight()/2)).intValue());  
				dialog.setVisible(true);
			}
	    	
	    }));
	}
	
	void initStyle(){
		add(Style.createToolPanel("Style", "style", null));
		UIUtils.setRound(Style.btnMap.get("style"), 10);
        final ButtonPopup wbp = new ButtonPopup(Style.btnMap.get("style"), PopupWay.downRight);
        wbp.setRound(0);
        UIUtils.setRound(wbp, 0);
        JPanel editorStyles = new JPanel ( new VerticalFlowLayout (5, 5) );
        editorStyles.setOpaque(false);
        UIUtils.setRound(editorStyles, 0);
        UIUtils.setUndecorated(editorStyles, true);
        ThemeButton[] themeButtons = {
        		createThemeItem("IntelliJ IDEA", "idea"),
        		createThemeItem("Dark", "dark"),
        		createThemeItem("Visual Studio", "vs"),
        		createThemeItem("Eclipse", "eclipse")
        };
        WebButtonGroup themeGroup = new WebButtonGroup(WebButtonGroup.VERTICAL, true, themeButtons);
        themeGroup.setButtonsDrawFocus(false);
        themeGroup.setButtonsDrawFocus(false);
        editorStyles.add(themeGroup);
        wbp.setContent (editorStyles);
	}
	
	ThemeButton createThemeItem(String title, String iconname){
		final ThemeButton themeButton = new ThemeButton(title, iconname);
		if(Register.getTheme().equals(iconname)) 
			themeButton.setSelected(true);
		themeButton .addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e){
            	Register.setTheme(themeButton.iconame);
            	Content.theme = Icon.loadTheme(themeButton.iconame);
            	Content.theme.apply(Content.editor);
           }
        });
		return themeButton;
	}
	
	void initView(){
		JPanel pan = new JPanel(new VerticalFlowLayout(FlowLayout.CENTER, 0, 0));
		UIUtils.setUndecorated(pan, true);
		UIUtils.setShadeWidth(pan, 0);
		UIUtils.setRound(pan, 0);
		UIUtils.setDrawSides(pan, false, false, false, false);
        Style.viewButton("Editor", "editor");
        Style.viewButton("Studio", "studio");
        Style.viewButton("Hiero", "shiero");
        Style.viewButton("Particle", "sparticle");
        WebButtonGroup textGroup = new WebButtonGroup(true,Style.viewGroup.toArray(new JButton[Style.viewGroup.size()]));
        textGroup.setButtonsDrawFocus(false);
        UIUtils.setUndecorated(textGroup, true);
        UIUtils.setShadeWidth(textGroup, 1);
        UIUtils.setMargin(textGroup, new Insets(1, 0, 1, 0));
        UIUtils.setRound(textGroup, 0);
        UIUtils.setDrawSides(textGroup, false, false, false, false);
        pan.setOpaque(false);
        pan.add(textGroup);
        add(pan, ToolbarLayout.MIDDLE);
	}
    
    public void addSpace(){
		add(new JLabel("       "), ToolbarLayout.START);
	}
	
	public void addSeparator(){
		add(new JSeparator(SwingConstants.VERTICAL), ToolbarLayout.START);
	}
}

final class ThemeButton extends JToggleButton{
	private static final long serialVersionUID = 1L;
	String iconame;
	ThemeButton(String text, String ic){
		super(text, Icon.icon(ic));
		iconame = ic;
		setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setFocusable(false);
	}
}