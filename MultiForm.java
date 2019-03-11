package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class MultiForm<T extends NodeWrapperADT> implements NodeWrapperADT{
	List<T> children;
	ScrollPane root;
	Pane layout;
	
	/**
	 * Construct a new MultiForm with the given layout Pane
	 * @param layout the root element for the pane
	 */
	public MultiForm(Pane layout) {
		this.layout = layout;
		root = new ScrollPane(layout);
		root.setContent(layout);
		root.setFitToWidth(true);
		children = new ArrayList<T>();
	}
	
	/**
	 * Add a Node to the 
	 * @param node
	 */
	public void add(T node) {
		children.add(node);
		layout.getChildren().add(node.toNode());
	}
	
	/**
	 * Reset the children of the layout
	 */
	public void reset() {
		layout.getChildren().clear();
		children = new ArrayList<T>();
	}
	
	/**
	 * Get the children of the layout
	 * @return the chilren of the layout
	 */
	public List<T> getChildren() {
		return children;
	}

	@Override
	public Node toNode() {
		return root;
	}
}
