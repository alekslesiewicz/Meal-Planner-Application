package application;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * MealData is used to keep track of an individual Meal's food items
 * @author Max Huddleston
 *
 */
public class MealData {
    HashMap<String, FoodItem> mealList;

    /**
     * Initialize the meal data object
     */
    public MealData() {
        mealList = new HashMap<String, FoodItem>();
    }

    /**
     * Add a food item to the meal
     *
     * @param foodItem the foodItem to add to the meal
     */
    public void addFoodToMeal(FoodItem foodItem) {
        mealList.put(foodItem.getID(), foodItem);
    }

    /**
     * Delete a food item from the meal
     *
     * @param foodItem the foodItem to delete from the meal
     */
    public void removeFoodItem(FoodItem foodItem) {
        mealList.remove(foodItem.getID());
    }

    /**
     * Get the current meal list
     *
     * @return the meal list
     */
    public List<FoodItem> getMealList() {
        return new ArrayList<FoodItem>(mealList.values());
    }

    /**
     * Reset the meal list and clear all items
     */
    public void resetMealList() {
        mealList = new HashMap<String, FoodItem>();
    }
}
