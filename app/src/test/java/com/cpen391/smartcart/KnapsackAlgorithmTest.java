package com.cpen391.smartcart;

import com.cpen391.smartcart.ui.not_shopping.NotShoppingFragment;
import com.cpen391.smartcart.ui.shopping.ShoppingList;
import com.cpen391.smartcart.ui.shopping.ShoppingListItem;
import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;
import com.cpen391.smartcart.ui.stats.StatsItem;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;

public class KnapsackAlgorithmTest {
    @Test
    public void knapsackAlgorithmTestOverlappingItems() {
        BigDecimal budget = new BigDecimal(30.0).setScale(2, BigDecimal.ROUND_HALF_UP);
        ArrayList<StatsItem > topItems = new ArrayList<StatsItem>();
        ArrayList< ShoppingListItem > shoppingList = new ArrayList<ShoppingListItem>();
        ArrayList< StatsItem > itemsToAdd = new ArrayList<StatsItem>();

        topItems.add(new StatsItem("Cheese", 5, 5.99));
        topItems.add(new StatsItem("Milk", 2, 3.99));
        topItems.add(new StatsItem("Chocolate", 3, 10.99));

        shoppingList.add(new ShoppingListItem(2, "Cheese", 5.99, 0));
        shoppingList.add(new ShoppingListItem(1, "Cheesepuffs", 9.99, 0));

        NotShoppingFragment.BudgetAndItemsToAdd budgetAndItemsToAdd = NotShoppingFragment.calculateBudgetRemaining(budget,topItems, shoppingList);

        itemsToAdd.add(new StatsItem("Milk", 2, 3.99));
        itemsToAdd.add(new StatsItem("Chocolate", 3, 10.99));
        Assert.assertEquals(budgetAndItemsToAdd.itemsToAdd.size(),itemsToAdd.size());
        BigDecimal expectedBudgetRemaining = new BigDecimal(30.0 - 2*5.99- 9.99).setScale(2, BigDecimal.ROUND_HALF_UP);
        Assert.assertTrue(budgetAndItemsToAdd.budgetRemaining.equals(expectedBudgetRemaining));
    }

    @Test
    public void knapsackAlgorithmTestNoOverlappingItems() {
        BigDecimal budget = new BigDecimal(30.0).setScale(2, BigDecimal.ROUND_HALF_UP);
        ArrayList<StatsItem > topItems = new ArrayList<StatsItem>();
        ArrayList< ShoppingListItem > shoppingList = new ArrayList<ShoppingListItem>();
        ArrayList< StatsItem > itemsToAdd = new ArrayList<StatsItem>();

        topItems.add(new StatsItem("Cheese", 5, 5.99));
        topItems.add(new StatsItem("Milk", 2, 3.99));
        topItems.add(new StatsItem("Chocolate", 3, 10.99));

        shoppingList.add(new ShoppingListItem(1, "Cheesepuffs", 9.99, 0));

        NotShoppingFragment.BudgetAndItemsToAdd budgetAndItemsToAdd = NotShoppingFragment.calculateBudgetRemaining(budget,topItems, shoppingList);

        itemsToAdd.add(new StatsItem("Cheese", 5, 5.99));
        itemsToAdd.add(new StatsItem("Milk", 2, 3.99));
        itemsToAdd.add(new StatsItem("Chocolate", 3, 10.99));
        Assert.assertEquals(budgetAndItemsToAdd.itemsToAdd.size(),itemsToAdd.size());
        BigDecimal expectedBudgetRemaining = new BigDecimal(30.0 - 9.99).setScale(2, BigDecimal.ROUND_HALF_UP);
        Assert.assertTrue(budgetAndItemsToAdd.budgetRemaining.equals(expectedBudgetRemaining));
    }


    @Test
    public void knapsackAlgorithmTestCompleteOverlap() {
        BigDecimal budget = new BigDecimal(30.0).setScale(2, BigDecimal.ROUND_HALF_UP);
        ArrayList<StatsItem > topItems = new ArrayList<StatsItem>();
        ArrayList< ShoppingListItem > shoppingList = new ArrayList<ShoppingListItem>();
        ArrayList< StatsItem > itemsToAdd = new ArrayList<StatsItem>();

        topItems.add(new StatsItem("Cheese", 5, 5.99));
        topItems.add(new StatsItem("Milk", 2, 3.99));

        shoppingList.add(new ShoppingListItem(2, "Cheese", 5.99, 0));
        shoppingList.add(new ShoppingListItem(2, "Milk", 3.99, 0));

        NotShoppingFragment.BudgetAndItemsToAdd budgetAndItemsToAdd = NotShoppingFragment.calculateBudgetRemaining(budget,topItems, shoppingList);

        Assert.assertEquals(budgetAndItemsToAdd.itemsToAdd.size(),itemsToAdd.size());
        BigDecimal expectedBudgetRemaining = new BigDecimal(30.0 - 2*5.99 - 2*3.99).setScale(2, BigDecimal.ROUND_HALF_UP);
        Assert.assertTrue(budgetAndItemsToAdd.budgetRemaining.equals(expectedBudgetRemaining));
    }
}