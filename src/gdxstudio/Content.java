package gdxstudio;
import gdxstudio.panel.AssetPanel;
import gdxstudio.panel.ConsolePanel;
import gdxstudio.panel.ReplacePanel;
import gdxstudio.panel.StudioPanel;
import gdxstudio.panel.WidgetPanel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;
import web.laf.lite.widget.Register;

final public class Content extends JPanel {
	private static final long serialVersionUID = 1L;
	private static JPanel card;
    public static String currentView = "Project";
    
    private static String projectFile = null;
    private static Register projectRegister;
   
    public static Theme theme = Icon.loadTheme(Register.getTheme());
    
    public static Editor editor;
    public static RTextScrollPane editorScroll;
    public static StudioPanel studioPanel;
    private static ReplacePanel replacePanel;
	public static AssetPanel assetPanel;
	public static WidgetPanel widgetPanel;
	private static ConsolePanel consolePanel;
	
	public static void initProjects(){
		projectRegister = new Register(105);
		projectFile = Register.getString(projectRegister);
	}
	
	public Content(){
		super(new BorderLayout());
		UIUtils.setMargin(this, new Insets(1,2,0,2));
		UIUtils.setUndecorated(this, true);
		editor = new Editor();
		theme.apply(editor);
		editorScroll = new RTextScrollPane(editor);
		editorScroll.getGutter().setBookmarkIcon(Icon.icon("bookmark"));
		editorScroll.getGutter().setBookmarkingEnabled(true);
		editorScroll.setVerticalScrollBarPolicy(RTextScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		UIUtils.setDrawBorder(editorScroll, false);
		studioPanel = new StudioPanel();
        // CardLayout
        card = new JPanel(new CardLayout());
        card.add(editorScroll, "Editor");
        card.add(studioPanel, "Studio");
        
        JPanel north = new JPanel(new VerticalFlowLayout());
        replacePanel = new ReplacePanel();
        north.add(replacePanel);
		add(north, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        
        JPanel vert = new JPanel(new VerticalFlowLayout());
        assetPanel = new AssetPanel();
        consolePanel = new ConsolePanel();
        assetPanel.setVisible(false);
        widgetPanel = new WidgetPanel();
        widgetPanel.setVisible(false);
        
        vert.add(widgetPanel);
        vert.add(assetPanel);
        vert.add(consolePanel);
        add(vert, BorderLayout.SOUTH);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Style.border));
	}
	
	private static boolean canvasChanged = true;
	public static void toggleView(int index) {
		for(JButton b: Style.viewGroup){
			if(index-1 == Style.viewGroup.indexOf(b))
				b.setSelected(true);
			else
				b.setSelected(false);
		}
    	switch(index){
	    	case 1: showContent("Editor");
	    			break;
	    	case 2: showContent("Studio");
	    			if(canvasChanged){
		    			SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run() {
								studioPanel.createStudioCanvas();
							}
		    			});
		    			canvasChanged = false;
	    			}
	    			break;
	    	case 3: showContent("Studio"); 
	    			SwingUtilities.invokeLater(new Runnable(){
						@Override
						public void run() {
							studioPanel.createHieroCanvas(); 
						}
	    			});
	    			canvasChanged = true;
	    			break;
	    	case 4:	showContent("Studio"); 
			    	SwingUtilities.invokeLater(new Runnable(){
						@Override
						public void run() {
							studioPanel.createParticleCanvas(); 
						}
					});
			    	canvasChanged = true;
	    			break;
	    	default: break;
    	}
    }
	
	public static void toggleReplace(){
		replacePanel.setVisible(!replacePanel.isVisible());
	}
	
	public static void toggleAsset(){
		assetPanel.setVisible(!assetPanel.isVisible());
	}
	
	public static void toggleConsole(){
		consolePanel.setVisible(!consolePanel.isVisible());
	}
	
	public static void toggleWidget(){
		widgetPanel.setVisible(!widgetPanel.isVisible());
	}
	
    private static void showContent(String contentName){
    	if(currentView.equals(contentName))
    		return;
		((CardLayout) card.getLayout()).show(card, contentName);
		currentView = contentName;
    }
	
	public static boolean projectExists(){
		if(getProject() == null || getProject().isEmpty() || !(new File(getProject()).exists()))
			return false;
		return true;
	}
	
	public static String getProject() {
		return projectFile;
	}
	
	public static void setProject(String prjName) {
		prjName = prjName.replace("\\",  "/");
		Register.putString(projectRegister, prjName);
		projectFile = prjName;
	}
}