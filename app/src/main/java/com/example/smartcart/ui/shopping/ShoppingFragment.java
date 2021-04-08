package com.example.smartcart.ui.shopping;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cdflynn.android.library.checkview.CheckView;

public class ShoppingFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
    private ShoppingListItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopping, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // recycler
        RecyclerView recycler = root.findViewById(R.id.recycler);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        // create onClickListener here so that we can use shoppingViewModel
        View.OnClickListener remove_item = v -> {
            LinearLayout layout = (LinearLayout) v.getParent();
            TextView itemName = layout.findViewById(R.id.itemName);
            shoppingViewModel.removeShoppingListItem(itemName.getText().toString());
            adapter.refreshList(shoppingViewModel.getShoppingList().getValue());
            adapter.notifyDataSetChanged();
        };
        adapter = new ShoppingListItemAdapter(getActivity().getApplicationContext(), shoppingViewModel.getShoppingList().getValue(), remove_item);
        recycler.setAdapter(adapter);

        // for adding App Bar button
        setHasOptionsMenu(true);

        // cart_total
        TextView cart_total = root.findViewById(R.id.total_cart);
        shoppingViewModel.subtotal.observe(getActivity(), new Observer<BigDecimal>() {
            @Override
            public void onChanged(BigDecimal bigDecimal) {
                cart_total.setText(String.format("Total: $%s", shoppingViewModel.subtotal.getValue().toString()));
            }
        });

        // checkout
        Button checkout = (Button) root.findViewById(R.id.checkout);
        checkout.setOnClickListener(v -> {
            ShoppingCheckoutDialogFragment dialog = new ShoppingCheckoutDialogFragment();
            dialog.show(getParentFragmentManager(), "");
        });

        // update recycler view on value change
        shoppingViewModel.getShoppingList().observe(getActivity(), new Observer<ArrayList<ShoppingListItem>>() {
            @Override
            public void onChanged(ArrayList<ShoppingListItem> shoppingListItems) {
                adapter.refreshList(shoppingListItems);
                adapter.notifyDataSetChanged();
            }
        });

        shoppingViewModel.getHistory().observe(getActivity(), new Observer<ArrayList<ShoppingList>>() {
            @Override
            public void onChanged(ArrayList<ShoppingList> shoppingLists) {
                if (!shoppingLists.isEmpty() && isResumed()) {
                    CheckView checkAnimation = root.findViewById(R.id.check);
                    checkAnimation.bringToFront();
                    checkAnimation.check();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            checkAnimation.uncheck();
                        }
                    }, 1000);
                }
            }
        });

        adapter.notifyDataSetChanged();
        return root;
    }

    /**
     * Adds a button to the menu bar
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This functions as a onclick listener for the button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_item_button) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_shopping_to_navigation_shopping_search);
        }
        return super.onOptionsItemSelected(item);
    }
}