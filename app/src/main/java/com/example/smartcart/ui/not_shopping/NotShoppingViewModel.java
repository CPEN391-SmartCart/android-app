package com.example.smartcart.ui.not_shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartcart.ui.shopping.ShoppingListItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotShoppingViewModel extends ViewModel {

    // used for communicating between fragments/dialogs
    private MutableLiveData<ArrayList<ShoppingListItem>> shoppingList;
    private Set<String> itemNames;
    public  MutableLiveData<BigDecimal> total;
    private MutableLiveData<String> nextItemName;
    private MutableLiveData<BigDecimal> nextPrice;

    public NotShoppingViewModel() {
        shoppingList = new MutableLiveData<>();
        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        total = new MutableLiveData<>();
        total.setValue(new BigDecimal("0.00"));

        nextItemName = new MutableLiveData<>();
        nextItemName.setValue("");
        nextPrice = new MutableLiveData<>();
        nextPrice.setValue(new BigDecimal("0.00"));
        initShoppingList();
    }

    public void setNextItemName(String nextItemName) {
        this.nextItemName.setValue(nextItemName);
    }
    public String getNextItemName() {return nextItemName.getValue(); }
    public void setNextPrice(BigDecimal nextPrice) { this.nextPrice.setValue(nextPrice);}
    public BigDecimal getNextPrice() { return this.nextPrice.getValue(); }

    public LiveData<ArrayList<ShoppingListItem>> getShoppingList() { return shoppingList; }

    public void addShoppingListItem(ShoppingListItem item) {
        ArrayList<ShoppingListItem> temp_list = new ArrayList<>(shoppingList.getValue());
        if (itemNames.contains(item.getItemName())) {
            for(ShoppingListItem i : temp_list) {
                if (i.getItemName().equals(item.getItemName())) {
                    i.setQuantity(i.getQuantity() + item.getQuantity());
                }
            }
        } else {
            temp_list.add(item);
        }
        total.setValue(total.getValue().add(item.getTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        itemNames.add(item.getItemName());
        shoppingList.setValue(temp_list);
    }

    public void removeShoppingListItem(String itemName) {
        ArrayList<ShoppingListItem> temp_list = new ArrayList<>(shoppingList.getValue());
        System.out.println(itemName);
        System.out.println(temp_list);
        ShoppingListItem item = temp_list.stream().filter(x -> x.getItemName().equals(itemName)).findAny().orElse(null);
        if (item.getQuantity() == 1) {
            temp_list.remove(item);
            itemNames.remove(item.getItemName());
        } else {
            item.setQuantity(item.getQuantity() - 1);
        }
        total.setValue(total.getValue().subtract(item.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        shoppingList.setValue(temp_list);
    }

    public void clearShoppingList() {
        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        total.setValue(new BigDecimal("0.00"));
    }

    private void initShoppingList() {

        addShoppingListItem(new ShoppingListItem(1, "Pickles", 9.90));
        addShoppingListItem(new ShoppingListItem(1, "Mayo", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Bread", 1.99));
        addShoppingListItem(new ShoppingListItem(2, "Cheese", 1.99));
        addShoppingListItem(new ShoppingListItem(3, "Mountain Dew", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Doritos", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Chug Jug", 9.90));
        addShoppingListItem(new ShoppingListItem(1, "Mayo", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Bread", 1.99));
        addShoppingListItem(new ShoppingListItem(1, "Cheese", 1.99));
        addShoppingListItem(new ShoppingListItem(1, "Mountain Dew", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Doritos", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Pickles", 9.90));
        addShoppingListItem(new ShoppingListItem(1, "Mayo", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Bread", 1.99));
        addShoppingListItem(new ShoppingListItem(1, "Cheese", 1.99));
        addShoppingListItem(new ShoppingListItem(1, "Mountain Dew", 3.99));
        addShoppingListItem(new ShoppingListItem(1, "Doritos", 3.99));
    }
}