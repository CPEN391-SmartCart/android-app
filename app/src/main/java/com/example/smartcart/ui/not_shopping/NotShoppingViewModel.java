package com.example.smartcart.ui.not_shopping;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartcart.items.BarcodeUUID;
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.shopping.ShoppingList;
import com.example.smartcart.ui.shopping.ShoppingListItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;

public class NotShoppingViewModel extends ViewModel {

    // used for communicating between fragments/dialogs
    private MutableLiveData<ArrayList<ShoppingListItem>> shoppingList;
    private Set<String> itemNames;
    public  MutableLiveData<BigDecimal> total;
    private MutableLiveData<SearchItem> nextItem;
    private Set<BarcodeUUID> scannedItems;
    private Bluetooth bluetooth;

    public NotShoppingViewModel() {
        shoppingList = new MutableLiveData<>();
        shoppingList.setValue(new ArrayList<>());
        itemNames = new HashSet<>();
        scannedItems = new HashSet<>();
        total = new MutableLiveData<>();
        total.setValue(new BigDecimal("0.00"));

        nextItem = new MutableLiveData<>();
        initShoppingList();
    }

    public void setBluetooth(Bluetooth bluetooth) { this.bluetooth = bluetooth; }
    public Bluetooth getBluetooth() {return this.bluetooth; }

    public void setNextItem(SearchItem nextItem) {
        this.nextItem.setValue(nextItem);
    }
    public SearchItem getNextItem() {return nextItem.getValue(); }

    public LiveData<ArrayList<ShoppingListItem>> getShoppingList() { return shoppingList; }

    public void addScannedItem(BarcodeUUID item)
    {
        this.scannedItems.add(item);
    }

    public ShoppingListItem getNextPathedItem()
    {
        for (ShoppingListItem shoppingListItem : shoppingList.getValue())
        {
            if (!scannedItems.contains(new BarcodeUUID(shoppingListItem.getBarcode(), shoppingListItem.getUUID())))
            {
                return shoppingListItem;
            }
        }

        return null;
    }

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
    }
}