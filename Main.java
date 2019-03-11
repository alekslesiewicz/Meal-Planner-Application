package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Filename:   Main.java
 * Project:    Food Query and Meal Analysis
 * Authors:    Aleks Lesiewicz 
 * 
 * This class represents the GUI. It sets the main view and calls the additional views when needed.
 */

public class Main extends Application {
	
	 ListView<FoodItem> listView;
	 ListView<FoodItem> mealsList;
	
	Scene primaryScene;
	 
	// The main view
	BorderPane root; 
	
	// All of the views used by the program
	FilterRulesForm queryView;
	AddFoodView addFoodView;
	FoodAnalysis foodAnalysisView;
	FoodFileChooser foodFileChooser;
	
	// Setup the data helpers 
	FoodData foodData;
	MealData mealData;
	
	final int SCENE_WIDTH = 1400;
	
	private void setMainView() {
		primaryScene.setRoot(root);
	}
	
	/**
	 * When a button is clicked, return to the main view
	 */
	EventHandler<ActionEvent> returnToMainView = event -> {
		setMainView();
	};
	
	/**
	 * Add a FoodItem to the foodList
	 */
	EventHandler<ActionEvent> addFoodEvent = event -> {
		FoodItem foodItem = addFoodView.getFoodItem();
		foodData.addFoodItem(foodItem);
		listView.setItems(FXCollections.observableArrayList(foodData.getAllFoodItems()));
		setMainView();
	};
	
	/**
	 * Query the foodList
	 */
	EventHandler<ActionEvent> queryFoodEvent = event -> {
		String strQuery = queryView.getNameFilterRule();
		List<String> nutrientQueries = queryView.getNutrientFilterRules();
		
		System.out.println("STRQUERY: " + strQuery);
		System.out.println("Nutrinet Query Size: " + nutrientQueries.size());
		
		List<FoodItem> strQueryItems = foodData.filterByName(strQuery);
		List<FoodItem> nutrQueryItems = foodData.filterByNutrients(nutrientQueries);
		
		HashMap<String, FoodItem> setIntersectionMap = new HashMap<String, FoodItem>();
		List<FoodItem> setIntersection = new ArrayList<FoodItem>();
		
		for (int i = 0; i < strQueryItems.size(); i++) {
			FoodItem foodItem = strQueryItems.get(i);
			setIntersectionMap.put(foodItem.getID(), foodItem);
		}
		
		for (int i = 0; i < nutrQueryItems.size(); i++) {
			FoodItem foodItem = nutrQueryItems.get(i);
			
			if (setIntersectionMap.containsKey(foodItem.getID()))
				setIntersection.add(foodItem);
		}		
		
		primaryScene.setRoot(root);
		listView.setItems(FXCollections.observableArrayList(setIntersection));
	};
	
	/**
	 * Analyze a meal
	 */
	EventHandler<ActionEvent> analyzeMealEvent = event -> {
		List<FoodItem> mealList = mealData.getMealList();
		BorderPane scene = foodAnalysisView.getFoodAnalysisView(mealList);
	};
	
	EventHandler<ActionEvent> loadFoodFileEvent = event -> {
		String fileName = foodFileChooser.getFileName();
		if (fileName != null) {
			foodData.loadFoodItems(fileName);
			listView.setItems(FXCollections.observableArrayList(foodData.getAllFoodItems()));
		}
	};
	
	EventHandler<ActionEvent> saveFoodFileEvent = event -> {
		String fileName = foodFileChooser.getFileName();
		if (fileName != null) {
			foodData.saveFoodItems(fileName);
		}
	};
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			primaryStage.setTitle("GUI");
			root = new BorderPane();
			
			primaryScene = new Scene(root, SCENE_WIDTH,800);

			
			//status bar for add, save, and load buttons
			BorderPane statusBarHolder = new BorderPane();
			HBox statusBar = new HBox();
			
			
			// Initialize all of the views
			queryView = new FilterRulesForm(returnToMainView, queryFoodEvent);
			addFoodView = new AddFoodView(returnToMainView, addFoodEvent);
			foodAnalysisView = new FoodAnalysis(returnToMainView);
			foodFileChooser = new FoodFileChooser();
			
			// Initialize the data helpers
			foodData = new FoodData();
			mealData = new MealData();
			
			//add button
			Button addButton = new Button();
			addButton.setText("Add");
			addButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					BorderPane addRoot = addFoodView.getAddFoodView();
					primaryScene.setRoot(addRoot);
				}
			});
			
			
			//buttons for status bar
			Button saveButton = new Button();
			saveButton.setText("Save");
			Button loadButton = new Button();
			loadButton.setText("Load");
			loadButton.setOnAction(loadFoodFileEvent);
			saveButton.setOnAction(saveFoodFileEvent);
			Label title = new Label("Food Analysis and Meals");		
			statusBar.getChildren().addAll(addButton, saveButton, loadButton);			
			statusBarHolder.setRight(statusBar);
			statusBarHolder.setCenter(title);
			
			
			//food lists
			listView = new ListView<>();
			mealsList = new ListView<FoodItem>();
		       
			
			listView.setPrefWidth(SCENE_WIDTH / 2);
			listView.setCellFactory(new Callback<ListView<FoodItem>, ListCell<FoodItem>>() {
				@Override
				public ListCell<FoodItem> call(ListView<FoodItem> param) {
					return new AddFoodButton(mealData, mealsList);
				}
			});
			
		    ScrollPane pane = new ScrollPane();
		    pane.setContent(listView);
		    pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		    Group group = new Group();
		    group.getChildren().add(pane);
		    
			
			mealsList.setPrefWidth(SCENE_WIDTH / 2);
			mealsList.setCellFactory(new Callback<ListView<FoodItem>, ListCell<FoodItem>>() {
				@Override
				public ListCell<FoodItem> call(ListView<FoodItem> param) {
					return new RemoveFoodButton(mealData, mealsList);
				}
			});
			ScrollPane pane2 = new ScrollPane();
		    pane2.setContent(mealsList);
		    pane2.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		    Group group2 = new Group();
		    group2.getChildren().add(pane2);
		    
		    
		    //holds food list and meal list
		    HBox listHolder = new HBox();
		    listHolder.getChildren().addAll(listView, mealsList);
		    
		    HBox bottomButtonHolder = new HBox();
			
			Button queryButton = new Button();
			queryButton.setText("Query");
			queryButton.setPrefWidth(SCENE_WIDTH / 2);
			queryButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					BorderPane queryRoot = queryView.getFilterRuleView();
					primaryScene.setRoot(queryRoot);
				}
			});
			
			Button analyzeButton = new Button("Analyze Meal");
			analyzeButton.setPrefWidth(SCENE_WIDTH / 2);
			analyzeButton.setOnAction(event -> {
				List<FoodItem> mealList = mealData.getMealList();
				BorderPane analysisView = 
						foodAnalysisView.getFoodAnalysisView(mealList);
				primaryScene.setRoot(analysisView);
			});
			
			bottomButtonHolder.getChildren().addAll(queryButton, analyzeButton);
									
			root.setTop(statusBarHolder);
			root.setCenter(listHolder);
			root.setBottom(bottomButtonHolder);
						
			primaryScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(primaryScene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
