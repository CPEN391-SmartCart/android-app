package com.example.smartcart.ui.shopping;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartcart.ui.search.SearchItem;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import me.aflak.bluetooth.Bluetooth;

public class ShoppingViewModel extends ViewModel {

    // used for communicating between fragments/dialogs
    private MutableLiveData<ArrayList<ShoppingListItem>> shoppingList;
    private Set<String> itemNames;
    public  MutableLiveData<BigDecimal> subtotal; //TODO: add gst
    private MutableLiveData<SearchItem> nextItem;
    private MutableLiveData<ArrayList<ShoppingList>> history;
    private MutableLiveData<Boolean> sessionActive;
    private Bluetooth bluetooth;
    private Button bluetoothButton;

    public ShoppingViewModel() {
        shoppingList = new MutableLiveData<>();
        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        subtotal = new MutableLiveData<>();
        subtotal.setValue(new BigDecimal("0.00"));

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
    public void setBluetooth(Bluetooth bluetooth) { this.bluetooth = bluetooth; }
    public void setBluetoothButton(Button bluetoothButton) { this.bluetoothButton = bluetoothButton; }
    public Bluetooth getBluetooth() {return this.bluetooth; }
    public Button getBluetoothButton() {return this.bluetoothButton; }

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
        subtotal.postValue(subtotal.getValue().add(item.getTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        itemNames.add(item.getItemName());
        shoppingList.postValue(temp_list);
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
        subtotal.setValue(subtotal.getValue().subtract(item.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        shoppingList.setValue(new ArrayList<>(temp_list));
    }

    public void clearShoppingListAndAddToHistory() {
        ArrayList<ShoppingList> temp_history = new ArrayList<>(history.getValue());
        temp_history.add(new ShoppingList(shoppingList.getValue(), subtotal.getValue(), String.format("Shopping List #%s", (new Random()).nextInt(10))));
        history.setValue(new ArrayList<>(temp_history));

        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        subtotal.setValue(new BigDecimal("0.00"));
    }

    public void addHistory(ShoppingList shoppingList) {
        ArrayList<ShoppingList> temp_history = new ArrayList<>(history.getValue());
        temp_history.add(shoppingList);
        history.setValue(new ArrayList<>(temp_history));
    }
    public ShoppingList getRecentShoppingList() {
        return history.getValue().get(history.getValue().size() - 1);
    }

    private void initShoppingList() {

//        addShoppingListItem(new ShoppingListItem(1, "Pickles", 9.90));
//        addShoppingListItem(new ShoppingListItem(1, "Mayo", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Bread", 1.99));
//        addShoppingListItem(new ShoppingListItem(2, "Cheese", 1.99));
//        addShoppingListItem(new ShoppingListItem(3, "Mountain Dew", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Doritos", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Chug Jug", 9.90));
//        addShoppingListItem(new ShoppingListItem(1, "Mayo", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Bread", 1.99));
//        addShoppingListItem(new ShoppingListItem(1, "Cheese", 1.99));
//        addShoppingListItem(new ShoppingListItem(1, "Mountain Dew", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Doritos", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Pickles", 9.90));
//        addShoppingListItem(new ShoppingListItem(1, "Mayo", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Bread", 1.99));
//        addShoppingListItem(new ShoppingListItem(1, "Cheese", 1.99));
//        addShoppingListItem(new ShoppingListItem(1, "Mountain Dew", 3.99));
//        addShoppingListItem(new ShoppingListItem(1, "Doritos", 3.99));
//        addShoppingListItem(new ShoppingListItem(6.9, "Fiji Apples", 1.29));
    }
}