package application;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilterRulesForm {
	
	/**
	 * QueryLine interface. Used to store the two different query lines in the MultiForm
	 * @author Aleks Lesiewicz
	 *
	 */
	interface QueryLine extends NodeWrapperADT {
		/**
		 * Get the rule this query line contains
		 * @return
		 */
		public String getRule();
	}
	
	/**
	 * A class representing a nutrient query. A two comboboxes and a textfield
	 * @author Aleks Lesiewicz
	 *
	 */
	class NutrientQueryLine implements QueryLine {
		HBox container;
		ComboBox<String> nutrientBox;
		TextField rightInput;
		ComboBox<String> cBox;
		
		/**
		 * Construct the query line and set up the HBox
		 */
		public NutrientQueryLine(EventHandler<ActionEvent> removeEvent) {
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
						rightInput.setText("");
					}
				}
			};
			
			
			nutrientBox = new ComboBox<String>(FXCollections.observableArrayList(
					"calories", "fat", "carbohydrate", "fiber", "protein"
			));
			cBox = new ComboBox<String>(FXCollections.observableArrayList(
					"<=", "==", ">="
			));
			rightInput = new TextField();
			cBox.getSelectionModel().selectFirst();
			nutrientBox.getSelectionModel().selectFirst();

			Button removeButton = new Button("Remove");
			
			rightInput.textProperty().addListener(validationListener);
			removeButton.setOnAction(removeEvent);
			
			container.getChildren().addAll(nutrientBox, cBox, rightInput, removeButton);
		}
		
		@Override
		public String getRule() {
			return nutrientBox.getValue() + " " + 
					cBox.getValue() + " " + rightInput.getText();
		}
		
		@Override
		public Node toNode() {
			return container;
		}
	}
	
	/**
	 * Wrapper for a TextField corresponding to a string query
	 * @author Aleks Lesiewicz
	 *
	 */
	class StringQueryField implements QueryLine {
		HBox root;
		TextField input;
		
		public StringQueryField() {		
			root = new HBox();
			input = new TextField();	
			root.getChildren().add(input);
		}
		
		@Override
		public String getRule() {
			return input.getText();
		}
		
		@Override
		public Node toNode() {
			return root;
		}
	}
	
	// The root of the scene
	private BorderPane root;
	private MultiForm<QueryLine> queryList;
	
	/**
	 * Construct the filter query view
	 * 
	 * @param cancelEvent event that fires when the cancel button is clicked
	 * @param applyQuery event that fires when the submit button is clicked
	 */
	public FilterRulesForm(EventHandler<ActionEvent> cancelEvent, EventHandler<ActionEvent> applyQuery) {
		root = new BorderPane();
		
		// Set up the main view of queries
		queryList = new MultiForm<QueryLine>(new VBox());
		
		StringQueryField sQuery = new StringQueryField();
		queryList.add(sQuery);
		
		root.setCenter(queryList.toNode());
		
		
		// Set up the add and reset buttons on top of the pane
		HBox top = new HBox();
		
		Button addNutrientQueryButton  = new Button("Add Nutrient Query");
		Button resetButton = new Button("Reset");
		
		addNutrientQueryButton.setPrefWidth(200);
		resetButton.setPrefWidth(200);
		
		EventHandler<ActionEvent> removeQueryEvent = actionEvent -> {
			Node rmButton = (Button) actionEvent.getSource();
			List<QueryLine> queries = queryList.getChildren();
			List<QueryLine> newQueryList = new ArrayList<QueryLine>();
			
			for (int i = 0; i < queries.size(); i++) {
				HBox root = (HBox) queries.get(i).toNode();
				if (!root.getChildren().contains(rmButton))
					newQueryList.add(queries.get(i));
			}
			
			queryList.reset();
			
			for (QueryLine line : newQueryList)
				queryList.add(line);
		};
		
		addNutrientQueryButton.setOnAction(event -> {
			queryList.add(new NutrientQueryLine(removeQueryEvent));
		});
		resetButton.setOnAction(event -> {
			queryList.reset();
			queryList.add(sQuery);
		});
		
		top.getChildren().addAll(addNutrientQueryButton, resetButton);
		root.setTop(top);
		
		// Set up the cancel and submit buttons
		HBox bottom = new HBox();
		Button cancelButton = new Button("Cancel");
		Button submitButton = new Button("Submit");
		
		cancelButton.setPrefWidth(300);
		submitButton.setPrefWidth(300);
		cancelButton.setOnAction(cancelEvent);
		submitButton.setOnAction(applyQuery);
		
		bottom.getChildren().addAll(cancelButton, submitButton);
		root.setBottom(bottom);
	}
	
	/**
	 * Get the view of the querys
	 * @return the BorderPane root consisting of the query view
	 */
	public BorderPane getFilterRuleView() { return root; } 
	
	/**
	 * Get the name query
	 * 
	 * @return the string corresponding to the name filter
	 */
	public String getNameFilterRule() {
		// We know the string query will always be at index 0
		List<QueryLine> rulesGUI = queryList.getChildren();
		return rulesGUI.get(0).getRule();
	}
	
	/**
	 * Get the filter rules from the form
	 * 
	 * Nutrient query rules: ["leftNum", "<,>,==,etc", "rightNum"]
	 * Name query rules: ["name"]
	 * @return the list of filter rules
	 */
	public List<String> getNutrientFilterRules() {
		List<String> filterRules = new ArrayList<String>();
		List<QueryLine> rulesGUI = queryList.getChildren();
		
		for (int i = 1; i < rulesGUI.size(); i++)
			filterRules.add(rulesGUI.get(i).getRule());
		
		return filterRules;
	}
}
