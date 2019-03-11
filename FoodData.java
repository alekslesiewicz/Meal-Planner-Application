package application;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.io.File;
import java.lang.Double;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    
    /**
     * Public constructor
     */
    public FoodData() {
    	foodItemList = new ArrayList<FoodItem>();
    	indexes = new HashMap<String, BPTree<Double, FoodItem>>();
    }
    
    private void initIndices() {
    	indexes = new HashMap<String, BPTree<Double, FoodItem>>();
    	
    	String[] nutrients = {"calories", "fat", "carbohydrate", "fiber", "protein"};
    	
    	for (String nutrient : nutrients)
    		indexes.put(nutrient, new BPTree<Double, FoodItem>(3));
    }
    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
        Scanner sc;
        File file = new File(filePath);
        List<FoodItem> newFoodList = new ArrayList<FoodItem>();

        try {
            sc = new Scanner(file);
            
            initIndices();

            while (sc.hasNextLine()) {
                String[] foodLine = sc.nextLine().split(",");
                if (foodLine.length < 2 || foodLine.length > 12)
                	continue;
                
                String id = foodLine[0];
                String name = foodLine[1];

                FoodItem food = new FoodItem(id, name);
                for (int i = 2; i < foodLine.length; i +=2)
                    food.addNutrient(foodLine[i].trim().toLowerCase(),
                    		Double.parseDouble(foodLine[i + 1]));

                newFoodList.add(food);
                
                for (Map.Entry<String, Double> entry : food.getNutrients().entrySet()) {
            		indexes.get(entry.getKey()).insert(entry.getValue(), food);
            	}
            }
        } catch (IOException e) {
            System.out.println("An error occurred reading the food file.");
        } catch (Exception e) {
        	e.printStackTrace();
        }

        Collections.sort(newFoodList, 
        		(f1, f2) -> f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase()));
        foodItemList = newFoodList;
    }

    /**
     * Create a csv string representation of a FoodItem
     * 
     * @param foodItem the given food item
     * @return a csv string of the given food item
     */
    private String foodItemToCSV(FoodItem foodItem) {
    	 StringBuilder sb = new StringBuilder();
         
         sb.append(foodItem.getID());
         sb.append(',');
         sb.append(foodItem.getName());
         sb.append(',');

         for (Map.Entry<String, Double> nutrient : foodItem.getNutrients().entrySet()) {
             sb.append(nutrient.getKey());
             sb.append(',');
             sb.append(nutrient.getValue());
             sb.append(',');
         }

         // Get rid of the trailing comma
         sb.deleteCharAt(sb.length() - 1);
         sb.append('\n');

         return sb.toString();
    }
    
    /*
     * (non-Javadoc)
     * @see  skeleton.FoodDataADT#saveFoodItems(java.lang.String)
     */
    @Override
    public void saveFoodItems(String filePath) {
        FileWriter fw = null;
        File file = new File(filePath);

        try {
            fw = new FileWriter(file);

            for (FoodItem foodItem : foodItemList)
                fw.write(foodItemToCSV(foodItem));
            
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred when writing to the food file.");
        }
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        return foodItemList.stream()
        	.filter(foodItem -> foodItem.getName().contains(substring))
        	.collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
    	if (rules.size() == 0)
    		return foodItemList;
    	
        // get the corresponding BPTree from indexes
        ArrayList<FoodItem> filteredList = new ArrayList<FoodItem>();
    
        String[] rule = rules.get(0).split(" ");
        BPTree<Double, FoodItem> tree = indexes.get(rule[0]);
        filteredList.addAll(tree.rangeSearch(Double.valueOf(rule[2]), rule[1]));

        if (rules.size() == 1) {
            return filteredList;
        }

        for (int i = 1; i < rules.size(); i++) {
        	ArrayList<FoodItem> newFilterList = new ArrayList<FoodItem>();
            rule = rules.get(i).split(" ");
            tree = indexes.get(rule[0]);
            List<FoodItem> treeFilter = tree.rangeSearch(
                                                    Double.valueOf(rule[2]), rule[1]);
            for (FoodItem foodItem : filteredList) {
                if (treeFilter.contains(foodItem)) {
                    newFilterList.add(foodItem);
                }
            }
            
            filteredList = newFilterList;
        }

        return filteredList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
    	foodItemList.add(foodItem);
    	
    	for (Map.Entry<String, Double> entry : foodItem.getNutrients().entrySet()) {
    		indexes.get(entry.getKey()).insert(entry.getValue(), foodItem);
    	}
    	
    	Collections.sort(foodItemList, (obj1, obj2) -> 
    		obj1.getName().compareTo(obj2.getName()));	
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemList;
    }

}
