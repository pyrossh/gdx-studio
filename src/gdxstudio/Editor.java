package gdxstudio;
import gdxstudio.panel.ConsolePanel;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

final public class Editor extends TextEditorPane {
	private static final long serialVersionUID = 1L;
	static DefaultCompletionProvider provider = new DefaultCompletionProvider();
	public static SearchContext context = new SearchContext(); 
	
	final private Timer saveTimer = new Timer(60000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			save();
		}
	});
	float zoomSize = 13;
	
	public Editor(){
		super(TextEditorPane.INSERT_MODE, true);
		saveTimer.start();
        setMargin(new Insets(0, 0, 0, 0));
        setMarginLineEnabled(true);
        setMarginLineColor(new Color(248,248,248)); //FIXME color not changing
        setAntiAliasingEnabled(true);
        setAutoIndentEnabled(true);
        setBracketMatchingEnabled(true);
        setUseFocusableTips(true);
        setTabSize(4);
        setLineWrap(true);
        setWrapStyleWord(true);
        setTabsEmulated(true);
		load();
		setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SYNTAX_STYLE_JAVA);
		LanguageSupportFactory.get().register(this);
		ToolTipManager.sharedInstance().registerComponent(this);
		JavaLanguageSupport jls = (JavaLanguageSupport)support;
		jls.getParser(this).setEnabled(false);
		try {
			jls.getJarManager().addCurrentJreClassFileSource();
			jls.setAutoActivationEnabled(true);
			jls.setAutoCompleteEnabled(true);
			jls.setAutoActivationDelay(50);
			String filename = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
			if(filename.endsWith(".jar"))
				jls.getJarManager().addClassFileSource(new File(filename));
			jls.install(this);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
        ((RSyntaxTextAreaHighlighter ) getHighlighter()).setDrawsLayeredHighlights(false);
        addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				saveTimer.start();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				saveTimer.stop();
				save();
			}
        });
        
        addCaretListener(new CaretListener(){
        	@Override
        	public void caretUpdate(CaretEvent e) {
        		StatusBar.updateCaret(getCaretLineNumber()+1, getCaretOffsetFromLineStart()+1);
        	}
        });
	}
	
	public void load(){
		if(Frame.scenePanel.selectedValueExists()){
			setText(Export.readFile("source/"+Frame.scenePanel.getSelectedValue()+".java"));
			setCaretPosition(0);
			setDirty(false);
		}
	}
	
	@Override
	public void save(){
		if(isDirty()){
			if(Frame.scenePanel.selectedValueExists()){
				Export.writeFile("source/"+Frame.scenePanel.getSelectedValue()+".java", getText());
				ConsolePanel.compile(Frame.scenePanel.getSelectedValue());
				setDirty(false);
			}
		}
	}
	
	public void zoomin(){
		zoomSize += 1;
		setFont(getFont().deriveFont(zoomSize));
	}
	
	public void zoomout(){
		zoomSize -= 1;
		setFont(getFont().deriveFont(zoomSize));
	}
	
	public void find(String text){
    	context.setSearchFor(text);
    	SearchEngine.find(this, context);
    }
    
    public void replace(String text, String replaceText){
    	context.setSearchFor(text);
    	context.setReplaceWith(replaceText);
    	SearchEngine.replace(this, context);
    }
    
    public void replaceAll(String text, String replaceText){
    	context.setSearchFor(text);
    	context.setReplaceWith(replaceText);
    	SearchEngine.replaceAll(this, context);
    }
    
    public void addWarning(int line, String tip){
    	if(getLineCount() >= line){
	    	try {
	    		Content.editorScroll.getGutter().addLineTrackingIcon(line-1, Icon.icon("warning"), tip);
	    	}
	    	catch (BadLocationException e){
				e.printStackTrace();
	    	}
    	}
    }
    
    public void addError(int line, String tip){
    	if(getLineCount() >= line){
	    	try {
	    		Content.editorScroll.getGutter().addLineTrackingIcon(line-1, Icon.icon("error"), tip);
	    	}
	    	catch (BadLocationException e){
				e.printStackTrace();
	    	}
    	}
    }
    
    public void clearIcons(){
    	Content.editorScroll.getGutter().removeAllTrackingIcons();
    }
}