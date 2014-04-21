package gdxstudio;
import gdxstudio.panel.ConsolePanel;
import gdxstudio.panel.OptionsPanel;
import gdxstudio.panel.ProjectPanel;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.layout.ToolbarLayout;
import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.popup.ButtonPopup;
import web.laf.lite.popup.PopupWay;
import web.laf.lite.utils.UIUtils;
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
        initPack();
        initExport();
        addSeparator();
        initOptions();
        addSeparator();
        add(Style.createToolPanel("Build", "esource", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConsolePanel.build();
			}
	    }));
        add(Style.createToolPanel("Compile", "esource", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Content.editor.save();
			}
	    }));
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
				popupContent.add(UIUtils.setBoldFont(new JLabel("GdxStudio v"+GdxStudio.version)));
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
	
	void initPack(){
		JButton packButton = Style.createMenuButton("Pack");
		packButton.setIcon(Icon.icon("epackage"));
    	packButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Settings settings = new Settings();
				settings.maxWidth = 2048;
		        settings.maxHeight = 2048;
		        settings.paddingX = 0;
		        settings.paddingY = 0;
				TexturePacker2.process(settings, Content.getProject()+File.separator+"pack",
						Content.getProject()+"atlas/", "pack.atlas");
				for(File f: new File(Content.getProject()+File.separator+"pack").listFiles()){
					if(f.isDirectory()){
						TexturePacker2.process(settings, f.getPath(), Content.getProject()+"atlas/", f.getName());
						JOptionPane.showMessageDialog(null, "Packed Texture: "+f.getName(), "Texture Packer", 
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
    	});
		add(packButton);
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
				dialog.setSize(525, 525);
				Dimension Size = Toolkit.getDefaultToolkit().getScreenSize();  
				dialog.setLocation(new Double((Size.getWidth()/2) - (dialog.getWidth()/2)).intValue(), 
						new Double((Size.getHeight()/2) - (dialog.getHeight()/2)).intValue());  
				dialog.setVisible(true);
			}
	    	
	    }));
	}
	
	void initView(){
        Style.viewButton("Editor", "editor");
        Style.viewButton("Studio", "studio");
        Style.viewButton("Hiero", "shiero");
        Style.viewButton("Particle", "sparticle");
        WebButtonGroup textGroup = new WebButtonGroup(true, Style.viewGroup.get(0), Style.viewGroup.get(1), 
        		Style.viewGroup.get(2), Style.viewGroup.get(3));
        textGroup.setButtonsDrawFocus(false);
        UIUtils.setUndecorated(textGroup, true);
        UIUtils.setShadeWidth(textGroup, 0);
        UIUtils.setMargin(textGroup, new Insets(0, 0, -1, 0));
        UIUtils.setRound(textGroup, 0);
        UIUtils.setDrawSides(textGroup, false, false, false, false);
        add(textGroup);
	}
    
    public void addSpace(){
		add(new JLabel("       "), ToolbarLayout.START);
	}
	
	public void addSeparator(){
		add(new JSeparator(SwingConstants.VERTICAL), ToolbarLayout.START);
	}
}