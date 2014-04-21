package gdxstudio.panel;

import gdxstudio.Content;
import gdxstudio.Icon;
import gdxstudio.SceneEditor;
import gdxstudio.Style;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.badlogic.gdx.Gdx;

import web.laf.lite.layout.VerticalFlowLayout;
import web.laf.lite.utils.UIUtils;


public class DashPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static JLabel label;
	private static JScrollPane scrollPane;
	private static Rectangle screenRect = new Rectangle();
	private static Robot robot;
	
	public DashPanel(){
		super(new VerticalFlowLayout());
		UIUtils.setUndecorated(this, true);
		UIUtils.setShadeWidth(this, 0);
		UIUtils.setRound(this, 0);
		UIUtils.setDrawSides(this, false, false, false, false);
		setBackground(Color.black);
		add(new Style.TitleButton("DashBoard", null));
		label = new JLabel();
		label.setBackground(Color.black);
		label.setVerticalTextPosition(JLabel.CENTER);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.CENTER);
		scrollPane = new JScrollPane(label);
		scrollPane.setBackground(Color.black);
		scrollPane.getViewport().setBackground(Color.black);
		scrollPane.setPreferredSize(new Dimension(200, 100));
		UIUtils.setDrawBorder(scrollPane, false);
		add(scrollPane);
		setIcon("snuke");
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void update(){
		screenRect.x = Content.studioPanel.getLocationOnScreen().x+(int)SceneEditor.selectedActor.getX();
		screenRect.y = Gdx.graphics.getHeight()-(int)SceneEditor.selectedActor.getY()-15;
		screenRect.width = (int)SceneEditor.selectedActor.getWidth();
		screenRect.height = (int)SceneEditor.selectedActor.getHeight();
		if(screenRect.width < 200){
			screenRect.x -= (200 - screenRect.width)/4;
			screenRect.width = 200;
		}
		if(screenRect.height < 100){
			screenRect.y -= (100 - DashPanel.screenRect.height)/4;
			screenRect.height = 100;
		}
		setIcon(robot.createScreenCapture(screenRect).getScaledInstance(200, 100, Image.SCALE_DEFAULT));
	}
	
	public void setIcon(String iconname){
		label.setIcon(Icon.icon(iconname));
	}
	
	public void setIcon(BufferedImage image){
		label.setIcon(new ImageIcon(image));
	}
	
	public void setIcon(Image image){
		label.setIcon(new ImageIcon(image));
	}
	
	public void setTextureRegion(String texName){
	}
}
