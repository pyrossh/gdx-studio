import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.File;

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
	private static OptionsPanel optionsPanel;
	public static AssetPanel assetPanel;
	private static ConsolePanel consolePanel;
	
	private static String sceneFile = null;
	private static Register sceneRegister;
	
	private static Register viewRegister;
	
	public static void initProjects(){
		projectRegister = new Register(105);
		projectFile = Register.getString(projectRegister);
		sceneRegister = new Register(107);
		sceneFile = Register.getString(sceneRegister);
		viewRegister = new Register(109);
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
        //north.add(title);
        optionsPanel = new OptionsPanel();
        north.add(optionsPanel);
		add(north, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        
        JPanel vert = new JPanel(new VerticalFlowLayout());
        assetPanel = new AssetPanel();
        consolePanel = new ConsolePanel();
        assetPanel.setVisible(false);
        vert.add(assetPanel);
        vert.add(consolePanel);
        add(vert, BorderLayout.SOUTH);
	}
	
	@Override
	public void paintComponent(Graphics g){
		Style.drawRightBorder(g, getWidth(), getHeight());
		Style.drawLeftBorder(g, getWidth(), getHeight());
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
	
	public static void toggleOptions(){
		optionsPanel.setVisible(!optionsPanel.isVisible());
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
	
    private static void showContent(String contentName){
    	if(currentView.equals(contentName))
    		return;
		((CardLayout) card.getLayout()).show(card, contentName);
		currentView = contentName;
		Register.putString(viewRegister, contentName);
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
	
	public static boolean sceneFileExists(){
		if(getSceneFile() == null || getSceneFile().isEmpty())
			return false;
		return true;
	}
	
	public static String getSceneFile(){
		return sceneFile;
	}
	
	public static void setSceneFile(String sceneName){
		Register.putString(sceneRegister, sceneName);
		sceneFile = sceneName;
	}
}