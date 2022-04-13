package ui;
import java.io.File;
import java.io.FileFilter;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Alexander Bolte - Bolte Consulting (2010 - 2014).
 * 
 *         This class shall be a simple implementation of a TreeItem for
 *         displaying a file system tree.
 * 
 *         The idea for this class is taken from the Oracle API docs found at
 *         http
 *         ://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html.
 * 
 *         Basically the file sytsem will only be inspected once. If it changes
 *         during runtime the whole tree would have to be rebuild. Event
 *         handling is not provided in this implementation.
 */
public class FileTreeItem extends TreeItem<NameFile> {

	/**
	 * Calling the constructor of super class in oder to create a new
	 * TreeItem<File>.
	 * 
	 * @param f
	 *            an object of type File from which a tree should be build or
	 *            which children should be gotten.
	 */
	public FileTreeItem(NameFile f) {
		super(f);
	}
	static int test = 0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.TreeItem#getChildren()
	 */
	@Override
	public ObservableList<TreeItem<NameFile>> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;
			/*
			 * First getChildren() call, so we actually go off and determine the
			 * children of the File contained in this TreeItem.
			 */
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.TreeItem#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		if (isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			File f = (File) getValue();
			isLeaf = f.isFile();
		}

		return isLeaf;
	}

	/**
	 * Returning a collection of type ObservableList containing TreeItems, which
	 * represent all children available in handed TreeItem.
	 * 
	 * @param TreeItem
	 *            the root node from which children a collection of TreeItem
	 *            should be created.
	 * @return an ObservableList<TreeItem<File>> containing TreeItems, which
	 *         represent all children available in handed TreeItem. If the
	 *         handed TreeItem is a leaf, an empty list is returned.
	 */
	private ObservableList<TreeItem<NameFile>> buildChildren(TreeItem<NameFile> TreeItem) {
		NameFile f = TreeItem.getValue();
		if (f != null && f.isDirectory()) {
			NameFile[] files = f.listFiles((FileFilter)pathname -> !pathname.isHidden());
			if (files != null) {
				ObservableList<TreeItem<NameFile>> children = FXCollections.observableArrayList();
				for (NameFile childFile : files) {
					FileTreeItem item = new FileTreeItem(childFile);
					Icon icon = FileSystemView.getFileSystemView().getSystemIcon(childFile);
					item.setGraphic(new ImageView(jswingIconToImage(icon)));
					children.add(item);
				}

				return children;
			}
		}

		return FXCollections.emptyObservableList();
	}

	public static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;
	private boolean isLeaf;
}