package com.cpen391.smartcart;

import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class viewModelTest {

    @Test
    public void initializationTest() {
        ShoppingViewModel shoppingViewModel = new ShoppingViewModel();
        Assert.assertEquals(shoppingViewModel.getShoppingList().getValue().size(), 0);
    }


}
