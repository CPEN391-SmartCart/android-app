package com.example.smartcart.ui.shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartcart.ui.search.SearchItem;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ShoppingViewModel extends ViewModel {

    // used for communicating between fragments/dialogs
    private MutableLiveData<ArrayList<ShoppingListItem>> shoppingList;
    private Set<String> itemNames;
    public  MutableLiveData<BigDecimal> total;
    private MutableLiveData<SearchItem> nextItem;
    private MutableLiveData<ArrayList<ShoppingList>> history;
    private MutableLiveData<Boolean> sessionActive;

    public ShoppingViewModel() {
        shoppingList = new MutableLiveData<>();
        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        total = new MutableLiveData<>();
        total.setValue(new BigDecimal("0.00"));

        nextItem = new MutableLiveData<>();

        history = new MutableLiveData<>();
        history.setValue(new ArrayList<>());

        sessionActive = new MutableLiveData<>();
        sessionActive.setValue(false);
        initShoppingList();
    }

    public LiveData<Boolean> isSessionActive() { return this.sessionActive; }
    public void startSession() { this.sessionActive.setValue(true); }
    public void stopSession() { this.sessionActive.setValue(false); }

    public void setNextItem(SearchItem nextItem) {
        this.nextItem.setValue(nextItem);
    }
    public SearchItem getNextItem() {return nextItem.getValue(); }

    public LiveData<ArrayList<ShoppingListItem>> getShoppingList() { return shoppingList; }
    public LiveData<ArrayList<ShoppingList>> getHistory() { return history; }

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
        shoppingList.setValue(new ArrayList<>(temp_list));
    }

    public void clearShoppingListAndAddToHistory() {
        ArrayList<ShoppingList> temp_history = new ArrayList<>(history.getValue());
        temp_history.add(new ShoppingList(shoppingList.getValue(), total.getValue(), String.format("Shopping List #%s", temp_history.size())));
        history.setValue(new ArrayList<>(temp_history));

        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        total.setValue(new BigDecimal("0.00"));
    }

    public void setHistory(ArrayList<ShoppingList> shoppingList) {
        history.setValue(shoppingList);
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
        addShoppingListItem(new ShoppingListItem(6.9, "Fiji Apples", 1.29));
    }
}