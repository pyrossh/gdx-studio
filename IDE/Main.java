import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import web.laf.lite.widget.Register;

public class Main {
	public static final String version = "0.7.1";
	
	public static void main(String[] args) {
		/*try {
			JavaLexer lexer = new JavaLexer(new ANTLRFileStream("C:/JavaParser.java"));
			JavaParser parser = new JavaParser(new CommonTokenStream(lexer));
			parser.addErrorListener(new DiagnosticErrorListener());
			parser.setErrorHandler(new DefaultErrorStrategy());
			parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
			parser.compilationUnit();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	 Register.init("gdxstudio");
        		 Content.initProjects();
            	 try{
            		 UIManager.setLookAndFeel("web.laf.lite.ui.WebLookAndFeelLite");
                 }
                 catch (Throwable e){
                     e.printStackTrace ();
                 }
            	createSplash();
            	//Frame.getInstance().pack();
            	Frame.getInstance().setVisible(true);
            	Frame.getInstance().setExtendedState(JFrame.MAXIMIZED_BOTH);
       	     	ConsolePanel.redirectSystemStreams();
       	     	if(Content.projectExists()){
       				Asset.loadAsynchronous = false;
       				Asset.setBasePath(Content.getProject());
       				SceneEditor.reloadAssets = true; // to load assets of new project
       				StudioPanel.updateAssets();
       	     		Content.toggleView(2);
       	     	}
       	     	else{
       	     		Frame.setDisabledProject();
       	     	}
            }
        });
		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Update().setVisible(true);
			}
		});*/
	}
	
    static void createFrameWithoutSplash(){
    	 Icon.loadIcons();
    	 Frame.getInstance().setIconImage(Icon.icon("icon").getImage());
    	 Frame.getInstance().setTitle("GdxStudio");
		 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 Frame.getInstance().setSize(screenSize);
		 Frame.getInstance().setLocation(0, 0);
		 Frame.getInstance().setLocationRelativeTo(null);
		 Frame.getInstance().initSideBar();
		 Frame.getInstance().initStatusBar();
		 Frame.getInstance().initContent();
		 Frame.getInstance().initToolBar();
    }
	
	static void createSplash(){
		Graphics2D g = null;
	    final SplashScreen splash = SplashScreen.getSplashScreen();
	    if (splash == null) createFrameWithoutSplash();
	    else {
	     g = splash.createGraphics();  
	     renderSplashFrame(g, 1, "Initializing");
	     splash.update();
	     renderSplashFrame(g, 2, "Loading Icons");
	     splash.update();
	     Icon.loadIcons();
	     renderSplashFrame(g, 3, "Loading Frame");
	     splash.update();
	     Frame.getInstance().setIconImage(Icon.icon("icon").getImage());
	     Frame.getInstance().setTitle("GdxStudio");
		 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 Frame.getInstance().setSize(screenSize);
		 Frame.getInstance().setLocation(0, 0);
		 Frame.getInstance().setLocationRelativeTo(null);
		 renderSplashFrame(g, 4, "Loading Explorer");
	     splash.update();
	     renderSplashFrame(g, 5, "Loading SideBar");
	     splash.update();
	     Frame.getInstance().initSideBar();
	     renderSplashFrame(g, 6, "Loading Status Bar");
	     splash.update();
	     Frame.getInstance().initStatusBar();
	     renderSplashFrame(g, 7, "Loading Content");
	     splash.update();
	     Frame.getInstance().initContent();
	     renderSplashFrame(g, 8, "Loading ToolBar");
	     splash.update();
	     Frame.getInstance().initToolBar();
	     renderSplashFrame(g, 9, "Finished");
	     splash.update();   
	    }
	}
	
	static int height = 200;
	static int width = 400;
	static int fontHeight = 12;
	private static int ind = 8;
	
	static void renderSplashFrame(Graphics2D g, int frame,  String content) {
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.BLACK);  
		g.setPaintMode();  
		g.fillRect(0, height - 29, width, 17);  
		g.setColor(Color.RED);
		ind--;
		if(ind == 0) ind = 1;
		for(int i= 0; i < frame; i ++)
			for(int j= 0; j < i*frame/ind; j ++){
				int x = 9 *j;
				/*if(x > 0 || x < 0) g.setColor(Color.RED);
				if(x > width/4) g.setColor(Color.ORANGE);
				if(x > width/2) g.setColor(Color.YELLOW);
				if(x > 3*width/4) g.setColor(Color.GREEN);*/
				g.fillRect(x , height - 28, 8, 8);
			}
		g.setColor(Color.ORANGE);
		for(int i= 0; i < frame; i ++)
			for(int j= 0; j < i*frame/1.1; j ++)
				g.fillRect(9 * j , height - 20, 8, 8);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, height - 12, width, fontHeight);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Purisa", Font.PLAIN, 13));
		g.drawString(content, 0, height - 2);	
	}

	public static void log(String text){
		System.out.println(text);
	}
	
	public static void error(String text){
		System.err.println(text);
	}
}