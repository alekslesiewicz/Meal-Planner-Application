package application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;


	
	public class AddFoodButton extends ListCell<FoodItem> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("Add to meal");
        String lastItem;
        FoodItem item;
        MealData mealData;

        public AddFoodButton(MealData mealData, ListView<FoodItem> mealListView) {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            
            this.mealData = mealData;
            
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println(lastItem + " : " + event);
                    mealData.addFoodToMeal(item);
                    System.out.println(mealData.getMealList().size());
                    mealListView.setItems(FXCollections.observableArrayList(mealData.getMealList()));
                }
            });
        }

        @Override
        protected void updateItem(FoodItem item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            this.item = item;
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item.getName();
                label.setText(item!=null ? item.getName() : "<null>");
                setGraphic(hbox);
            }
        }
    }
	
	


