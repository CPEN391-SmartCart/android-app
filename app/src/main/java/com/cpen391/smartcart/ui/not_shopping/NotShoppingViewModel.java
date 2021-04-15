package com.cpen391.smartcart.ui.not_shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cpen391.smartcart.ui.search.SearchItem;
import com.cpen391.smartcart.ui.shopping.ShoppingListItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import me.aflak.bluetooth.Bluetooth;

/**
 * Represents a shared collection of resources related to planning between fragments
 *
 */
public class NotShoppingViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ShoppingListItem>> shoppingList;
    private Set<String> itemNames;
    public  MutableLiveData<BigDecimal> total;
    private MutableLiveData<SearchItem> nextItem;
    private Set<String> scannedBarcodes;
    private Bluetooth bluetooth;

    public NotShoppingViewModel() {
        shoppingList = new MutableLiveData<>();
        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        scannedBarcodes = new HashSet<>();
        total = new MutableLiveData<>();
        total.setValue(new BigDecimal("0.00"));

        nextItem = new MutableLiveData<>();
    }

    public void setBluetooth(Bluetooth bluetooth) { this.bluetooth = bluetooth; }
    public Bluetooth getBluetooth() {return this.bluetooth; }

    public void setNextItem(SearchItem nextItem) {
        this.nextItem.setValue(nextItem);
    }
    public SearchItem getNextItem() {return nextItem.getValue(); }

    public LiveData<ArrayList<ShoppingListItem>> getShoppingList() { return shoppingList; }

    public void addScannedBarcode(String scannedBarcode)
    {
        this.scannedBarcodes.add(scannedBarcode);
    }

    public ShoppingListItem getNextPathedItem()
    {
        for (ShoppingListItem shoppingListItem : shoppingList.getValue())
        {
            if (!scannedBarcodes.contains(shoppingListItem.getBarcode()))
            {
                return shoppingListItem;
            }
        }

        return null;
    }

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
        total.setValue(total.getValue().add(item.getTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        itemNames.add(item.getItemName());
        shoppingList.setValue(temp_list);
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
        total.setValue(total.getValue().subtract(item.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        shoppingList.setValue(temp_list);
    }
}