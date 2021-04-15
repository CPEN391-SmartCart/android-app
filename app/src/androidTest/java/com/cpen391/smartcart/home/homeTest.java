package com.cpen391.smartcart.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.cpen391.smartcart.R;
import com.cpen391.smartcart.HomeActivity;
import com.cpen391.smartcart.ui.home.HomeFragment;
import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;
import com.cpen391.smartcart.ui.stats.StatsFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class homeTest {
    private HomeActivity homeActivity;
    private HomeFragment homeFragment;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule(HomeActivity.class);

    @Before
    public void setUp() {
        homeActivity = mActivityRule.getActivity();
        homeFragment = new HomeFragment();
        startFragment(homeFragment);
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
        Assert.assertNotNull(homeFragment);
    }

    @Test
    public void testBTButtons() {
        onView(ViewMatchers.withId(R.id.smartcart_connect)).check(ViewAssertions.matches(withText("Connect to SmartCart")));

        onView(ViewMatchers.withId(R.id.start_session_button)).check(ViewAssertions.matches(withText("Start Shopping Session")));
    }
}
