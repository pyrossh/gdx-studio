package gdxstudio.panel;

import gdxstudio.Content;
import gdxstudio.GdxStudio;
import gdxstudio.StatusBar;
import gdxstudio.Style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;









import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

public class ConsolePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static final JTextArea consoleArea = new JTextArea("");
	private static final JScrollPane consoleAreaPane = new JScrollPane(consoleArea);
	
	public static String classPath;
	public static int errorCount = 0;
	public static int warningCount = 0;

	public ConsolePanel(){
		super(new VerticalFlowLayout());
		UIUtils.setFontSize(consoleArea, 14);
		UIUtils.setFontName(consoleArea, Content.editor.getFont().getFontName());
		UIUtils.setMargin(this, new Insets(0,0,0,0));
	    UIUtils.setUndecorated(this, true);
        consoleAreaPane.setPreferredSize(new Dimension(500, 200));
		add(new Style.TitleButton("Console", this));
		setVisible(false);
        add(consoleAreaPane);
        if(Content.projectExists())
        	updateCompiler();
	}
	
	public static void updateCompiler(){
		String filename = ConsolePanel.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		GdxStudio.log(filename);
		if(filename.endsWith(".jar"))
			classPath = filename;
		else
			classPath = filename+File.pathSeparator+
			new File(filename).getParentFile().getPath()+"/libs/gdx.jar"+File.pathSeparator;
	}

	public static void redirectSystemStreams(){
		final OutputStream out = new OutputStream() {
		      @Override
		      public void write(int b) throws IOException {
		          ConsolePanel.updateConsoleArea(String.valueOf((char) b));
		      }
		
		      @Override
		      public void write(byte[] b, int off, int len) throws IOException {
		    	  ConsolePanel.updateConsoleArea(new String(b, off, len));
		      }
		
		      @Override
		      public void write(byte[] b) throws IOException {
		          write(b, 0, b.length);
		      }
		 };
		 final OutputStream err = new OutputStream() {
			      @Override
			      public void write(int b) throws IOException {
			    	  consoleArea.setText("");
			          ConsolePanel.updateConsoleArea(String.valueOf((char) b));
			      }
			
			      @Override
			      public void write(byte[] b, int off, int len) throws IOException {
			    	  consoleArea.setText("");
			    	  ConsolePanel.updateConsoleArea(new String(b, off, len));
			      }
			
			      @Override
			      public void write(byte[] b) throws IOException {
			        write(b, 0, b.length);
			      }
		};
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(err, true));
	}

	public static void updateConsoleArea(final String text) {
	    SwingUtilities.invokeLater(new Runnable() {
	      public void run() {
	    	  consoleArea.append(text);
	    	  consoleArea.setCaretPosition(consoleArea.getText().length());
	      }
	    });
	  }

	@Override
	public void actionPerformed(ActionEvent e) {
		setVisible(false);
	}
	
	static String errString = "";
	static Color warningColor = new Color(255, 240, 0);
	final static CompilationProgress prog = null;
	final static PrintWriter compilerWriter = new PrintWriter(new OutputStream() {
		@Override
		public void write(int b) throws IOException {
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			errString = new String(b, off, len);
			consoleArea.setText("");
			GdxStudio.log(errString);
			for(String line: errString.split(System.lineSeparator())){
				if(line.contains("warning")){
					String[] warningLine = line.split(":");
					warningCount++;
					Content.editor.addWarning(Integer.parseInt(warningLine[warningLine.length-3]),
							warningLine[warningLine.length-1]);
				}
				if(line.contains("error")){
					String[] errorLine = line.split(":");
					errorCount++;
					Content.editor.addError(Integer.parseInt(errorLine[errorLine.length-3]),
							errorLine[errorLine.length-1]);
				}
			}
			if(warningCount == 1){
				StatusBar.warning.setForeground(warningColor);
				StatusBar.warning.setText("1 Warning");
			}
			if(warningCount > 1){
				StatusBar.warning.setForeground(warningColor);
				StatusBar.warning.setText(warningCount+" Warnings");
			}
			if(errorCount == 1){
				StatusBar.error.setForeground(Color.red);
				StatusBar.error.setText("1 Error");
			}
			if(errorCount>1){
				StatusBar.error.setForeground(Color.red);
				StatusBar.error.setText(errorCount+" Errors");
			}
		}

		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}
	});

	public static void compile(final String file) {
		clear();
		Content.editor.clearIcons();
		StatusBar.error.setForeground(Color.black);
		StatusBar.error.setText("Errors");
		StatusBar.warning.setForeground(Color.black);
		StatusBar.warning.setText("Warnings");
		errorCount = 0;
		warningCount = 0;
		consoleArea.append("Compiling... "+file+".java"+System.lineSeparator());
		final String buildArgs = Content.getProject()+"source/"+ file+".java"
				+" -cp "+classPath
				+" -d "+Content.getProject()+"bin"
				+" -1.7"
				+" -Xemacs";
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				BatchCompiler.compile(buildArgs, compilerWriter, compilerWriter, prog);
				consoleArea.append("Finished Compiling... "+file+".java"+System.lineSeparator());
				System.gc();
			}
		});
		t.start();
	}
	
	public static void build() {
		clear();
		consoleArea.append("Building.. "+Content.getProject()+System.lineSeparator());
		final String buildArgs = Content.getProject()+"source/"
				+" -cp "+classPath
				+" -d "+Content.getProject()+"bin"
				+" -1.7"
				+" -Xemacs";
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				BatchCompiler.compile(buildArgs, compilerWriter, compilerWriter, prog);
				System.gc();
			}
		});
		t.start();
	}
	
	public static void clear(){
		consoleArea.setText("");
	}
}
