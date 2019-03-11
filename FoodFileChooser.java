package application;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FoodFileChooser {
	Stage stage;
	FileChooser fileChooser;
	
	/**
	 * Construct a new FoodFileChooser
	 * Used to choose the files for reading and writing
	 */
	public FoodFileChooser() {
		stage = new Stage();
		fileChooser = new FileChooser();
		fileChooser.setTitle("Open File");
	}
	
	/**
	 * Show the FileChooser to the user and get the file path
	 * @return the absolute file path of the read/write file
	 */
	public String getFileName() {
		stage.show();
		File file = fileChooser.showOpenDialog(stage);
		stage.close();
		if (file != null)
			return file.getAbsolutePath();
		else 
			return null;
	}
}
