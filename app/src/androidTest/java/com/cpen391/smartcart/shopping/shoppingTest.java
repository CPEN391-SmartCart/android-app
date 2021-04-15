package com.cpen391.smartcart.shopping;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.cpen391.smartcart.HomeActivity;
import com.cpen391.smartcart.R;
import com.cpen391.smartcart.ui.shopping.ShoppingCheckoutDialogFragment;
import com.cpen391.smartcart.ui.shopping.ShoppingFragment;
import com.cpen391.smartcart.ui.shopping.ShoppingListItem;
import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.view.MotionEvent.BUTTON_PRIMARY;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class shoppingTest {
    private HomeActivity homeActivity;
    private ShoppingFragment shoppingFragment;
    private ShoppingViewModel shoppingViewModel;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule(HomeActivity.class);

    @Before
    public void setUp() {
        homeActivity = mActivityRule.getActivity();
        shoppingFragment = new ShoppingFragment();
        startFragment(shoppingFragment);
        shoppingViewModel = new ViewModelProvider(homeActivity).get(ShoppingViewModel.class);
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
        Assert.assertNotNull(shoppingFragment);
        Assert.assertNotNull(shoppingViewModel);
    }

    @Test
    public void testShoppingList() {
        RecyclerView shoppingList = shoppingFragment.getView().findViewById(R.id.recycler);
        shoppingViewModel.addShoppingListItem(new ShoppingListItem(1, "test", 6.9, 4.20));
        shoppingList.getAdapter().notifyDataSetChanged();

        onView(ViewMatchers.withId(R.id.recycler))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText("test"))
                ));

        onView(ViewMatchers.withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItem(withText("test"), click(R.id.remove, BUTTON_PRIMARY)
                ));
    }

    @Test
    public void testCheckoutNoItems() {
        ShoppingCheckoutDialogFragment shoppingCheckoutDialogFragment = new ShoppingCheckoutDialogFragment();
        shoppingFragment.getParentFragmentManager().beginTransaction().add(shoppingCheckoutDialogFragment, null).commit();
        Assert.assertNotNull(shoppingCheckoutDialogFragment);
        onView(ViewMatchers.withId(R.id.checkout)).perform(click()).check(ViewAssertions.matches(withText("Error")));
    }

    @Test
    public void testCheckoutHasItems() {
        ShoppingCheckoutDialogFragment shoppingCheckoutDialogFragment = new ShoppingCheckoutDialogFragment();
        shoppingViewModel.addShoppingListItem(new ShoppingListItem(1, "test", 6.9, 4.20));
        shoppingFragment.getParentFragmentManager().beginTransaction().add(shoppingCheckoutDialogFragment, null).commit();
        Assert.assertNotNull(shoppingCheckoutDialogFragment);
        onView(ViewMatchers.withId(R.id.checkout)).perform(click()).check(ViewAssertions.doesNotExist());
    }

}
