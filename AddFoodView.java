package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddFoodView {
	
	class NutrientLine implements NodeWrapperADT {
		HBox container;
		Label nutrient;
		List<String> nutrients;
		TextField rightInput;
		
		/**
		 * Construct the query line and set up the HBox
		 */
		public NutrientLine(String nutrient) {
			container = new HBox();
			
			ChangeListener<String> validationListener = (obs, oldText, newText) -> {
				try {
					Double.parseDouble(newText);
				} catch (NumberFormatException e) {
					if (!newText.isEmpty()) {
						Alert alert = new Alert(Alert.AlertType.ERROR, 
								"Cannot enter a non-numeric or negative input.");
						alert.showAndWait()
					      .filter(response -> response == ButtonType.OK);
						
						Platform.runLater(() -> rightInput.clear());
					}
				}
			};
			
			
			this.nutrient = new Label(nutrient);
			this.nutrient.setMinWidth(115);
			rightInput = new TextField();
						
			rightInput.textProperty().addListener(validationListener);
			
			container.getChildren().addAll(this.nutrient, rightInput);
		}
		
		/**
		 * Get the current nutrient value
		 * 
		 * @return the double value associated with the nutrient
		 */
		public double getNutrientValue() {
			if (!rightInput.getText().isEmpty())
				return Double.parseDouble(rightInput.getText());
			else
				return 0;
		}
		@Override
		public Node toNode() {
			return container;
		}
	}
	
	private BorderPane root;
	private TextField idEntry;
	private TextField nameEntry;
	private String[] nutrients = {"calories", "fat", "carbohydrate", "fiber", "protein"};
	private MultiForm<NutrientLine> nutrientList;

	public AddFoodView(EventHandler<ActionEvent> cancelEvent, 
			EventHandler<ActionEvent> addEvent) {
		root = new BorderPane();
		nutrientList = new MultiForm<NutrientLine>(new VBox());
		
		// Set up the top of the pane
		HBox top = new HBox();
		idEntry = new TextField("ID");
		nameEntry = new TextField("Name");
		top.getChildren().addAll(idEntry, nameEntry);
		
		// Set up middle of the pane
		for (int i = 0; i < nutrients.length; i++) {
			nutrientList.add(new NutrientLine(nutrients[i]));
		}
		
		// Set up the bottom of the pane
		HBox bottom = new HBox();
		Button exitButton = new Button("Cancel");
		Button addFoodButton = new Button("Add Food");
		
		exitButton.setOnAction(cancelEvent);
		addFoodButton.setOnAction(addEvent);
		
		bottom.getChildren().addAll(exitButton, addFoodButton);
		
		root.setTop(top);
		root.setCenter(nutrientList.toNode());
		root.setBottom(bottom);
	}
	
	/**
	 * Get the AddFoodView view for the main GUI
	 * @return the BorderPane associated with the AddFoodView GUI
	 */
	public BorderPane getAddFoodView() {
		return root;
	}
	
	/**
	 * Get the food item corresponding to the given inputs
	 * 
	 * @return the added food item
	 */
	public FoodItem getFoodItem() {
		FoodItem foodItem = new FoodItem(idEntry.getText(), nameEntry.getText());
		List<NutrientLine> nutrientLines = nutrientList.getChildren();
		for (int i = 0; i < nutrients.length; i++) {
			foodItem.addNutrient(nutrients[i], 
					nutrientLines.get(i).getNutrientValue());
		}
		return foodItem;
	}
}
