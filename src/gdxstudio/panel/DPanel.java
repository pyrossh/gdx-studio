package gdxstudio.panel;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import scene2d.Asset;

public class DPanel extends JTree {
	private static final long serialVersionUID = 1L;
	DefaultTreeModel treeModel;
	DefaultMutableTreeNode root;
	DefaultMutableTreeNode atlas;
	DefaultMutableTreeNode font;
	DefaultMutableTreeNode music;
	DefaultMutableTreeNode sound;
	DefaultMutableTreeNode model;
	DefaultMutableTreeNode skin;
	
	DPanel() {
		root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);
		
		for(File file: new File(Asset.basePath+"font").listFiles())
			root.add(new DefaultMutableTreeNode(file.getName()));
	}

	
}
