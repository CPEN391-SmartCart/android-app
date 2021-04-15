package com.cpen391.smartcart.shopping;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.cpen391.smartcart.HomeActivity;
import com.cpen391.smartcart.R;
import com.cpen391.smartcart.ui.shopping.ShoppingCheckoutDialogFragment;
import com.cpen391.smartcart.ui.shopping.ShoppingFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.FragmentTestUtil;

@RunWith(AndroidJUnit4.class)
public class shoppingTest {
    private static final String LAST_ITEM_ID = "item: 99";
    private HomeActivity homeActivity;
    private ShoppingFragment shoppingFragment;

    @Before
    public void setUp() {
        homeActivity = Robolectric.setupActivity(HomeActivity.class);
        shoppingFragment = new ShoppingFragment();
        startFragment(shoppingFragment);
    }

    private void startFragment( Fragment fragment ) {
        FragmentManager fragmentManager = homeActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null );
        fragmentTransaction.commit();
    }

    @Test
    public void testNotNull() {
        Assert.assertNotNull(homeActivity);
    }

    @Test
    public void testCheckoutNoItems() {
        ShoppingCheckoutDialogFragment shoppingCheckoutDialogFragment = new ShoppingCheckoutDialogFragment();
        shoppingFragment.getParentFragmentManager().beginTransaction().add(shoppingCheckoutDialogFragment, null).commit();
        Assert.assertNotNull(shoppingCheckoutDialogFragment);
        onView(shoppingCheckoutDialogFragment).clickButton("checkout").expectDialog("Error");
    }

    @Test
    public void testCheckoutHasItems() {

    }

}
