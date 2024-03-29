package com.cpen391.smartcart.ui.shopping;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cpen391.smartcart.ui.search.SearchItem;

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
    public  MutableLiveData<BigDecimal> subtotal;
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
    }

    public LiveData<Boolean> isSessionActive() { return this.sessionActive; }
    public void startSession() { this.sessionActive.setValue(true); }
    public void stopSession() { this.sessionActive.setValue(false); }

    public void setBluetooth(Bluetooth bluetooth) { this.bluetooth = bluetooth; }
    public void setBluetoothButton(Button bluetoothButton) { this.bluetoothButton = bluetoothButton; }
    public Bluetooth getBluetooth() {return this.bluetooth; }
    public Button getBluetoothButton() {return this.bluetoothButton; }

    public LiveData<ArrayList<ShoppingListItem>> getShoppingList() { return shoppingList; }
    public LiveData<ArrayList<ShoppingList>> getHistory() { return history; }

    /**
     * Adds an shopping list item to the internal array
     * @param item - the item to add
     */
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

    /**
     * Removes a shopping list item to the internal array
     * @param itemName - the name of the item to remove
     */
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

    /**
     * Clears the current shopping list and add a new ShoppingList object to history
     */
    public void clearShoppingListAndAddToHistory() {
        ArrayList<ShoppingList> temp_history = new ArrayList<>(history.getValue());
        temp_history.add(new ShoppingList(shoppingList.getValue(), subtotal.getValue(), String.format("Shopping List #%s", (new Random()).nextInt(10))));
        history.setValue(new ArrayList<>(temp_history));

        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        subtotal.setValue(new BigDecimal("0.00"));
    }

    /**
     * Adds a shopping list to history
     */
    public void addHistory(ShoppingList shoppingList) {
        ArrayList<ShoppingList> temp_history = new ArrayList<>(history.getValue());
        temp_history.add(shoppingList);
        history.setValue(new ArrayList<>(temp_history));
    }

    /**
     * Returns the most recent shopping list in history
     */
    public ShoppingList getRecentShoppingList() {
        return history.getValue().get(history.getValue().size() - 1);
    }

    /**
     * Sort history with most recent first
     */
    public void sortHistory() {
        ArrayList<ShoppingList> temp_history = new ArrayList<>(history.getValue());
        temp_history.sort((o1, o2) -> {
            String[] date1 = o1.getPurchaseDate().substring(0, 10).split("-");
            String[] date2 = o2.getPurchaseDate().substring(0, 10).split("-");
            for (int i = 0; i < 3; i++) {
                if (Integer.parseInt(date1[i]) > Integer.parseInt(date2[i])) {
                    return -1;
                } else if (Integer.parseInt(date2[i]) > Integer.parseInt(date1[i])) {
                    return 1;
                }
            }

            String[] time1 = o1.getPurchaseDate().substring(11, 16).split(":");
            String[] time2 = o2.getPurchaseDate().substring(11, 16).split(":");
            for (int i = 0; i < 2; i++) {
                if (Integer.parseInt(time1[i]) > Integer.parseInt(time2[i])) {
                    return -1;
                } else if (Integer.parseInt(time2[i]) > Integer.parseInt(time1[i])) {
                    return 1;
                }
            }

            return 0;
        });
        history.setValue(new ArrayList<>(temp_history));
    }

}