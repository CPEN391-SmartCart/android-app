package com.cpen391.smartcart.stats;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.cpen391.smartcart.HomeActivity;
import com.cpen391.smartcart.R;
import com.cpen391.smartcart.ui.shopping.ShoppingFragment;
import com.cpen391.smartcart.ui.shopping.ShoppingList;
import com.cpen391.smartcart.ui.shopping.ShoppingListItem;
import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;
import com.cpen391.smartcart.ui.stats.StatsFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.view.MotionEvent.BUTTON_PRIMARY;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class statsTest {
    private HomeActivity homeActivity;
    private StatsFragment statsFragment;
    private ShoppingViewModel shoppingViewModel;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule(HomeActivity.class);

    @Before
    public void setUp() {
        homeActivity = mActivityRule.getActivity();
        statsFragment = new StatsFragment();
        startFragment(statsFragment);
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
        Assert.assertNotNull(statsFragment);
        Assert.assertNotNull(shoppingViewModel);
    }

    @Test
    public void testHistory() {
        RecyclerView history = statsFragment.getView().findViewById(R.id.history);
        ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
        shoppingListItems.add(new ShoppingListItem(1, "Test", 5.11, 6.0));
        shoppingViewModel.addHistory(new ShoppingList(shoppingListItems, 7.7, "2018-09-03T03:45:24Z"));
        history.getAdapter().notifyDataSetChanged();

        onView(ViewMatchers.withId(R.id.history))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText("2018-09-03"))
                ));

        onView(ViewMatchers.withId(R.id.history))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText("2018-09-03"))
                )).perform(click()).check(matches(withText("Test")));
    }
}
