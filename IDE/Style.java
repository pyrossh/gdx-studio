

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import web.laf.lite.layout.HorizontalFlowLayout;
import web.laf.lite.layout.ToolbarLayout;
import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.popup.ButtonPopup;
import web.laf.lite.popup.PopupWay;
import web.laf.lite.utils.ColorUtils;
import web.laf.lite.utils.UIUtils;
import web.laf.lite.widget.CenterPanel;

public enum Style {
	 BLUE, DARK;
	 private static Style currentStyle = Style.BLUE;
	 public static Color topColor;
	 public static Color botColor;
	 public static Color headerTopLine;
	 public static Color headerBg;
	 public static Color headerBgBot;
	 public static Color headerFg;
	 public static Color border;
	 public static Color focus;
	 public static Color font;
	 public static Color listBg;

	 public static Color topLineColor = new Color(0x4580c8);
	 public static Color bottomColor = new Color(0x1956ad);
	 
	 private static HashMap<Style, ArrayList<Color>> stylesMap = new HashMap<Style, ArrayList<Color>>();
	 /* top, bot, headerTopline, headerBg, headerBgBot, headerFg, border, focus, font, listBg*/
	 private static String blueColors = "e4e4e4,d1d1d1,e8eaeb,2a3b57,1a2b47,ffffff,"
			 + "808080,ef8e39,000000,ffffff";
	 private static String darkColors = "213134,272b2f,6C788C,20272a,0b0e20,93c705,"
			 + "404040,ef8e39,e4e0e2,272b2f";
	 static {
		 stylesMap.put(Style.BLUE, getColor(blueColors));
		 stylesMap.put(Style.DARK, getColor(darkColors));
		 setStyle(Style.BLUE);
	 }

	 public static void setStyle(Style style){
		 currentStyle = style;
		 topColor = stylesMap.get(currentStyle).get(0);
		 botColor = stylesMap.get(currentStyle).get(1);
		 headerTopLine = stylesMap.get(currentStyle).get(2);
		 headerBg = stylesMap.get(currentStyle).get(3);
		 headerBgBot = stylesMap.get(currentStyle).get(4);
		 headerFg = stylesMap.get(currentStyle).get(5);
		 border = stylesMap.get(currentStyle).get(6);
		 focus = stylesMap.get(currentStyle).get(7);
		 font = stylesMap.get(currentStyle).get(8);
		 listBg = stylesMap.get(currentStyle).get(9);
	 }

	 private static ArrayList<Color> getColor(String colorsString){
		 ArrayList<Color> listColor = new ArrayList<Color>();
		 for(String c: colorsString.split(","))
			 listColor.add(ColorUtils.parseHexColor(c));
		 return listColor;
	 }
	 
	 public static Color canvasBg = new Color(108, 108, 108);
	 public static Color canvasScreen = new Color(28, 28, 28);
	 public static Color canvasShadowTop = new Color(108, 108, 108);
	 public static Color canvasShadowBot = new Color(155, 155, 155);
	 public static Color canvasBox = new Color(83, 82, 82);
	 
	 private static Color scrollBg = new Color ( 245, 245, 245 );
	 private static Color scrollBorder = new Color ( 230, 230, 230 );
	 
	 private static Color scrollBarBorder = new Color ( 201, 201, 201 );
	 private static Color scrollGradientLeft = new Color ( 239, 239, 239 );
	 private static Color scrollSelGradientLeft = new Color ( 203, 203, 203 ); //33
	 private static Color scrollGradientRight = new Color ( 211, 211, 211 );        
	 private static Color scrollSelGradientRight = new Color ( 175, 175, 175 );//-45
	 
	 public static HashMap<String, JButton> btnMap = new HashMap<String, JButton>();
	 public static ArrayList<JButton> viewGroup = new ArrayList<JButton>();
	 private static GradientPaint headerBgPaint;
	 
	 final public static void drawBottomBorder(Graphics g, int width, int height){
		 g.setColor(Style.border);
		 g.drawLine(0, height-1, width, height-1);
	 }
	 
	 final public static void drawTopBorder(Graphics g, int width){
		 g.setColor(Style.border);
		 g.drawLine(0, 0, width, 0);
	 }
	 
	 final public static void drawRightBorder(Graphics g, int width, int height){
		 g.setColor(Style.border);
		 g.drawLine(width-1, 0, width-1, height);
	 }
	 
	 final public static void drawLeftBorder(Graphics g, int width, int height){
		 g.setColor(Style.border);
		 g.drawLine(0, 0, 0, height);
	 }
	 
	 private static final int margin = 15;
	 private static final int marginsub5 = 10;
	 private static final int margin5 = 20;
	 private static final int targetW = 800+55;
	 private static final int targetH = 480;
	 
	 final public static void drawScreen(Graphics g, int width, int height){
		 g.setColor(Style.canvasBg);
		 g.fillRect(0, 0, width, height); //bg
		 g.setColor(Color.black);
		 g.fillRoundRect(5, marginsub5, targetW+marginsub5, targetH+10, 10, 10);
		 g.setColor(Style.canvasShadowBot);
		 g.drawLine(margin, targetH+margin5, targetW+marginsub5, targetH+margin5);//hoz shadow y same
		 g.drawLine(targetW+margin, margin, targetW+margin, targetH+margin);// vert shadow x same
		 g.setColor(Style.canvasBox);
		 g.fillRect(0, targetH+3*margin, width, 2);
		 g.fillRect(0, targetH+3*margin+3, width, height-targetH+3*margin-3);
	 }
	 
	 public static Color tableHeaderTopLineColor = new Color ( 232, 234, 235 );
	 public static Color tableHeaderTopBgColor = new Color ( 226, 226, 226 );
	 public static Color tableHeaderBotBgColor = new Color ( 201, 201, 201 );
	 public static Color tableHeaderBotLineColor = new Color ( 104, 104, 104 );
	 public static void drawTableHeader(Graphics g, int width, int height){
		 Graphics2D g2d = ( Graphics2D ) g;
		 // Table header background
		 GradientPaint bgPaint = new GradientPaint (0, 1, topColor, 0, height - 1, botColor);
		 g2d.setPaint(bgPaint);
		 g2d.fillRect(0, 1, width, height - 1);

		 // Header top and bottom lines
		 //g2d.setColor(headerTopLine);
		// g2d.drawLine( 0, 0, width, 0);
	 }
	 
	 final public static void drawHorizontalBar(Graphics g, int width, int height){
		 Graphics2D g2d = ( Graphics2D ) g;
	     GradientPaint paint = new GradientPaint(0, 1, Style.topColor, 0, height, Style.botColor);
	     g2d.setPaint(paint);
	     g2d.fillRect(0, 0, width, height);
	 }
	 
	 final public static void drawLightHorizontalBar(Graphics g, int width, int height){
		 Graphics2D g2d = ( Graphics2D ) g;
		 g2d.setPaint ( new GradientPaint ( 0, 1, Color.WHITE, 0, height, new Color ( 223, 223, 223 ) ) );
	     g2d.fillRect(0, 0, width, height);
	 }
	 
	 final public static void drawDarkHorizontalBar(Graphics g, int width, int height){
		 Graphics2D g2d = ( Graphics2D ) g;
	     GradientPaint paint = new GradientPaint(0, 1, Style.topColor.darker(), 0, height, Style.botColor.darker());
	     g2d.setPaint(paint);
	     g2d.fillRect(0, 0, width, height);
	 }
	 
	 final public static void drawTreeSelection(Graphics g, int height, Rectangle rect){
		 Graphics2D g2d = ( Graphics2D ) g;
		 g2d.setPaint ( new GradientPaint ( 0, 1, Style.topLineColor, 0, height, Style.bottomColor ) );
         g2d.fill(rect);
         g2d.setPaint(Style.topLineColor);
         g2d.draw(rect);
	 }
	 
	 final public static void drawVerticalBar(Graphics g, int width, int height){
		 Graphics2D g2d = ( Graphics2D ) g;
		 GradientPaint paint = new GradientPaint(0, 1, Style.botColor, 0, height, Style.topColor);
	     g2d.setPaint(paint);
	     g2d.fillRect(0, 0, width, height);
	 }
	 
	 final public static void drawVerticalScrollTrack( Graphics g, Rectangle thumbRect, int width,int height,
			 boolean drawBorder ){
		 if (drawBorder){
	         Graphics2D g2d = ( Graphics2D ) g;
	         g2d.setPaint ( scrollBg );
	         g2d.fillRect ( 0, 0, width, height );
	         int vBorder = width - 1; //maybe 0
	         g2d.setColor ( scrollBorder );
	         g2d.drawLine ( vBorder, 0, vBorder, height - 1 );
	      }
	 }
	 
	 final public static void drawHorizontalScrollTrack( Graphics g, Rectangle thumbRect, int width,int height,
			 boolean drawBorder ){
		 if (drawBorder){
	         Graphics2D g2d = ( Graphics2D ) g;
	         g2d.setPaint ( scrollBg );
	         g2d.fillRect ( 0, 0, width, height );
	         g2d.setColor ( scrollBorder );
	         g2d.drawLine ( 0, 0, width, 0 );
	      }
	 }
	 
	 final public static void drawVerticalScrollBar( Graphics g, Rectangle thumbRect, int width, boolean isDragging){
		 Graphics2D g2d = ( Graphics2D ) g;
		 Color leftColor = isDragging ? scrollSelGradientLeft : scrollGradientLeft;
	     Color rightColor = isDragging ? scrollSelGradientRight : scrollGradientRight;
		 g2d.setPaint ( new GradientPaint ( 3, 0, leftColor, width - 4, 0, rightColor ) );
         g2d.fillRoundRect ( thumbRect.x + 2, thumbRect.y + 1, thumbRect.width - 4, thumbRect.height - 3, 0, 0);
         g2d.setPaint ( scrollBarBorder );
         g2d.drawRoundRect ( thumbRect.x + 2, thumbRect.y + 1, thumbRect.width - 4, thumbRect.height - 3, 0, 0);
	 }
	 
	 final public static void drawHorizontalScrollBar( Graphics g, Rectangle thumbRect, int width, boolean isDragging){
		 Graphics2D g2d = ( Graphics2D ) g;
		 Color leftColor = isDragging ? scrollSelGradientLeft : scrollGradientLeft;
	     Color rightColor = isDragging ? scrollSelGradientRight : scrollGradientRight;
	     g2d.setPaint ( new GradientPaint ( 0, thumbRect.y + 2, leftColor, 0, thumbRect.y + 2 + thumbRect.height - 4, rightColor ) );
         g2d.fillRoundRect ( thumbRect.x + 1, thumbRect.y + 2, thumbRect.width - 3, thumbRect.height - 4, 0, 0);
         g2d.setPaint ( scrollBarBorder );
         g2d.drawRoundRect ( thumbRect.x + 1, thumbRect.y + 2, thumbRect.width - 3, thumbRect.height - 4, 0, 0);
	 }
	 
	 public static class TitleLabel extends JLabel {
			private static final long serialVersionUID = 1L;
			public TitleLabel(String text){
				super(text.toUpperCase());
				UIUtils.setBoldFont(this);
				setVerticalTextPosition(JLabel.CENTER);
				setHorizontalTextPosition(JLabel.CENTER);
				setHorizontalAlignment(JLabel.CENTER);
				setForeground(Style.headerFg);
			}
			@Override
			public void paint ( Graphics g){
				Graphics2D g2d = ( Graphics2D ) g;
				if(headerBgPaint == null)
					headerBgPaint = new GradientPaint ( 0, 1, Style.headerBg, 
							0, getHeight()-1, Style.headerBgBot);
				g2d.setPaint ( headerBgPaint );
				g2d.fillRect ( 0, 1, getWidth(), getHeight() - 1 );
				g2d.setColor ( Style.headerTopLine);
				g2d.drawLine ( 0, 0, getWidth (), 0 );
				super.paint(g);
			}
	 }
	 
	 public static class TitleButton extends JButton {
		 private static final long serialVersionUID = 1L;
		 public TitleButton(String text){
			 super(text.toUpperCase());
			 //btn.setOpaque(true);
			 //btn.setBackground(headerBg);
			 setForeground(Style.headerFg);
			 UIUtils.setBoldFont(this);
			 UIUtils.setRolloverDecoratedOnly(this,true);
			 UIUtils.setLeftRightSpacing(this, 4);
			 UIUtils.setRound(this, 0);
			 UIUtils.setUndecorated(this, true);
			 UIUtils.setShadeWidth(this, 2);
			 setVerticalTextPosition(JLabel.CENTER);
			 setHorizontalTextPosition(JLabel.CENTER);
			 setHorizontalAlignment(JLabel.CENTER);
		 }

		 @Override
		 public void paint( Graphics g){
			 Graphics2D g2d = ( Graphics2D ) g;
			 if(headerBgPaint == null)
				 headerBgPaint = new GradientPaint ( 0, 1, Style.headerBg, 
						 0, getHeight()-1, Style.headerBgBot);
			 g2d.setPaint ( headerBgPaint );
			 g2d.fillRect ( 0, 1, getWidth (), getHeight () - 1 );

			 // Header top and bottom lines
			 g2d.setColor ( Style.headerTopLine );
			 g2d.drawLine ( 0, 0, getWidth (), 0 );
			 super.paint(g);
		 }
	 }

	/*
	 * Creates a plain flat JButton/Header Button with current Style background color
	 */
	 final public static JButton createHeaderButton(String text, ActionListener al){
		final TitleButton btn = new TitleButton(text);
		if(al != null)
			btn.addActionListener(al);
		return btn;
	}
	 /* ToolBar Related Methods */
	// This is for Menu Items
	final public static JButton createMenuButton(String title){
		JButton btn = new JButton(title);
		btn.setFocusable(false);
		btn.setOpaque(false);
	    UIUtils.setRolloverDecoratedOnly(btn, true);
	    UIUtils.setRound(btn, 0);
	    UIUtils.setLeftRightSpacing(btn, 5);
	    UIUtils.setShadeWidth(btn, 0);
	    UIUtils.setInnerShadeWidth(btn, 0);
		return btn;
	} 
	
	// This is for ToolButtons without Popup
	final public static JPanel createToolPanel(String text, String iconname,final ActionListener onClick){
		final JPanel pan = new JPanel(new VerticalFlowLayout(FlowLayout.CENTER, 0, 0));
		pan.setOpaque(false);
		UIUtils.setRound(pan, 0);
		UIUtils.setShadeWidth(pan, 0);
		JButton btn = createMenuButton(text);
		btn.setIcon(Icon.icon(iconname));
		if(onClick != null)
			btn.addActionListener(onClick);
		btnMap.put(iconname, btn);
		pan.add(btn);
		return pan;
	}

	 /* SideBar Related Methods */
	// This is for ToolButtons with Popup
	final public static JButton createToolButton(String iconname){
		final JButton btn = new JButton(Icon.icon(iconname));
		btn.setFocusable(false);
		btn.setToolTipText(iconname.toUpperCase());
		btn.setOpaque(false);
	    UIUtils.setLeftRightSpacing(btn, 5);
	    UIUtils.setRound(btn, 0);
	    UIUtils.setShadeWidth(btn, 0);
	    UIUtils.setUndecorated(btn, true);
		return btn;
	}
	
	// This is for ToolButtons without Popup
	public static JButton createPopUpToolButton(String iconname){
		JButton btn = new JButton(Icon.icon(iconname));
		btn.setFocusable(false);
		btn.setToolTipText(iconname.toUpperCase());
		btn.setOpaque(false);
	    UIUtils.setLeftRightSpacing(btn, 4);
	    UIUtils.setRound(btn, 0);
	    UIUtils.setUndecorated(btn, true);
		return btn;
	}
	
	static int currentIndex = 1;
	public static void viewButton(String text, String ic){
		final int id = Integer.valueOf(currentIndex);
		currentIndex += 1;
		JButton btn = new JButton(text,Icon.icon(ic));
		UIUtils.setDrawFocus(btn, false);
        UIUtils.setShadeWidth(btn, 0);
        //UIUtils.setRound(btn, 0);
		viewGroup.add(btn);
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Content.toggleView(id);
			}
        });
	}
	
	final public static JPanel createButtonToolBarPanel(){
		JPanel tools = new JPanel(new ToolbarLayout(ToolbarLayout.HORIZONTAL)){
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g){
		    	Style.drawHorizontalBar(g, getWidth (), getHeight ());
		        g.setColor(Color.GRAY);
		        g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
			}
		};
		return tools;
	}
	
	public static JPanel createButtonToolBar(ActionListener al, String[] list){
		JPanel tools = createButtonToolBarPanel();
		for(int i=0;i< list.length-1; i++){
			if(i != 0)
				if(i%2 != 0) 
					continue;
			final JButton btn = new JButton(Icon.icon(list[i+1]));
		    UIUtils.setRolloverDecoratedOnly(btn,true);
		    UIUtils.setLeftRightSpacing(btn, 0);
		    btn.setFocusable(false);
		    btn.setToolTipText(list[i]);
		    btn.addActionListener(al);
		    tools.add(btn);
		}
		return tools;
	}
	
	/* Explorer Related Methods */
	final public static JButton createExplorerToolButton(String text,String iconname, ActionListener al){
		final JButton btn = new JButton(Icon.icon(iconname));
	    UIUtils.setRolloverDecoratedOnly(btn,true);
	    UIUtils.setLeftRightSpacing(btn, 0);
	    btn.setFocusable(false);
	    btn.setToolTipText(text);
	    if(al != null)
			btn.addActionListener(al);
		return btn;
	}
	
	final private static ButtonPopup createExplorerToolMenu(JButton btn, String title){
		final ButtonPopup menu = new ButtonPopup(btn, PopupWay.upRight);
		menu.setRound(0);
		menu.setShadeWidth(7);
		final JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		UIUtils.setRound(btn, 0);
		UIUtils.setLeftRightSpacing(btn, 0);
	    content.add(new TitleLabel(title), BorderLayout.NORTH);
	    menu.setContent(content);
		return menu;
	}
	
	final public static JButton createExplorerToolPopButton(final String text, final String icon,
			final JPanel panel, final String finishText, final ActionListener onClick){
		 final JButton btn = createExplorerToolButton(text, icon, null);
		 final ButtonPopup menu = createExplorerToolMenu(btn, text);
		 final JButton finish = createHeaderButton(finishText, null);
		 finish.setToolTipText(text);
		 final JPanel hoz = new JPanel(new VerticalFlowLayout(10));
		 panel.setOpaque(false);
		 UIUtils.setMargin(hoz, new Insets(10,10,10,10));
		 hoz.setOpaque(false);
		 hoz.add(new CenterPanel(panel));
		 Box box = Box.createHorizontalBox();
		 box.add(Box.createHorizontalGlue());
		 box.add(finish);
		 hoz.add(box);
		 menu.getContentPanel().add(new CenterPanel(hoz), BorderLayout.CENTER);
	     finish.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				menu.hidePopup();
				if(onClick != null)
					onClick.actionPerformed(event);
			}
	     });
		return btn;
	}
	
	final public static JButton createExplorerToolPopButton(final String text, final String icon,
			JTextField field, final ActionListener onClick){
		final JButton btn = createExplorerToolButton(text, icon, null);
		final ButtonPopup menu = createExplorerToolMenu(btn, text);
		final JButton finish = createHeaderButton("OK", null);
		finish.setToolTipText(text);
	    final JPanel hoz = new JPanel(new HorizontalFlowLayout(10));
		field.setPreferredSize(new Dimension(240,25));
		UIUtils.setMargin(hoz, new Insets(10,10,10,10));
		hoz.setOpaque(false);
		hoz.add(new CenterPanel(field));
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(finish);
		hoz.add(box);
		menu.getContentPanel().add(new CenterPanel(hoz), BorderLayout.CENTER);
		finish.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(onClick != null)
					onClick.actionPerformed(arg0);
				menu.hidePopup();
			}
	    });
		return btn;
	}
	
	public static JButton TreeButton(String iconName){
		 JButton btn = new JButton(Icon.icon(iconName));
		 btn.setVisible(false);
		 btn.setOpaque(false);
		 btn.setFocusable(false);
		 UIUtils.setRound(btn, 0);
		 //UIUtils.setShadeWidth(btn, 0);
		 //UIUtils.setUndecorated(btn, true);
		 return btn;
	}
}