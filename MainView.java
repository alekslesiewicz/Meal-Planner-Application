package application;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The MainView of the program
 * @author Max Huddleston
 *
 */
public class MainView {
	BorderPane mainView;
	
	public MainView() {
		mainView = new BorderPane();
		mainView.setCenter(new Label("Herro"));
	}
	
	public BorderPane getMainView() {
		return mainView;
	}
}
