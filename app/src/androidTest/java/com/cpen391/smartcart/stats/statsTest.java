package com.cpen391.smartcart.stats;

import androidx.fragment.app.FragmentTransaction;
import androidx.test.rule.ActivityTestRule;

import com.cpen391.smartcart.HomeActivity;

import org.junit.Before;
import org.junit.Test;

public class statsTest {
    private static final String LAST_ITEM_ID = "item: 99";

    @Before
    public void setUp() {
        FragmentTransaction transaction = new ActivityTestRule<>(HomeActivity.class).getActivity().getSupportFragmentManager().beginTransaction();
        //transaction.add(R.id.fragment_shopping);
    }

    @Test
    public void checkoutSuccess() {

    }

    @Test
    public void checkoutFail() {

    }
}
