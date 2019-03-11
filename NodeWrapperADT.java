package application;

import javafx.scene.Node;

public interface NodeWrapperADT {
	/**
	 * Used to get the usable javafx node for a complex wrapper class
	 * @return the root level Node for a wrapper class
	 */
	public Node toNode();
}
