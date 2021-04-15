package com.cpen391.smartcart.not_shopping;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.cpen391.smartcart.HomeActivity;
import com.cpen391.smartcart.R;
import com.cpen391.smartcart.ui.home.HomeFragment;
import com.cpen391.smartcart.ui.not_shopping.NotShoppingFragment;
import com.cpen391.smartcart.ui.not_shopping.NotShoppingViewModel;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class notShoppingTest {
    private HomeActivity homeActivity;
    private NotShoppingFragment notShoppingFragment;
    private NotShoppingViewModel notShoppingViewModel;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule(HomeActivity.class);

    @Before
    public void setUp() {
        homeActivity = mActivityRule.getActivity();
        notShoppingFragment = new NotShoppingFragment();
        startFragment(notShoppingFragment);
        notShoppingViewModel = new ViewModelProvider(homeActivity).get(NotShoppingViewModel.class);
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
        Assert.assertNotNull(notShoppingFragment);
        Assert.assertNotNull(notShoppingViewModel);
    }

    @Test
    public void testPlanningList() {
        RecyclerView shoppingList = notShoppingFragment.getView().findViewById(R.id.recycler);
        notShoppingViewModel.addShoppingListItem(new ShoppingListItem(1, "test", 6.9, 4.20));
        shoppingList.getAdapter().notifyDataSetChanged();

        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText("test"))
                ));

        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItem(withText("test"), click(R.id.remove, BUTTON_PRIMARY)
                ));
    }

    @Test
    public void testSearchButton() {
        onView(withId(R.id.add_item_button))
                .perform(click());

        onView(ViewMatchers.withId(R.id.recycler)).perform(RecyclerViewActions.actionOnItem(withText("Test"), click(R.layout.search_item, BUTTON_PRIMARY)));
        onView(withText("Select quantity")).check(matches(isDisplayed()));
    }

    // Knapsack is tested in unit test
}
