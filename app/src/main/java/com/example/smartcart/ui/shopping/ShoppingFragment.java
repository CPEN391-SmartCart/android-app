package com.example.smartcart.ui.shopping;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;

import java.math.BigDecimal;
import java.util.ArrayList;

import cdflynn.android.library.checkview.CheckView;

/**
 * Represents the shopping list functionality of the app
 */
public class ShoppingFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
    private ShoppingListItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopping, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Setups the recyclerView showing the shopping list
        RecyclerView recycler = root.findViewById(R.id.recycler);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);

        // We create an onClickListener here so that we can use shoppingViewModel
        View.OnClickListener remove_item = v -> {
            LinearLayout layout = (LinearLayout) v.getParent();
            TextView itemName = layout.findViewById(R.id.itemName);
            shoppingViewModel.removeShoppingListItem(itemName.getText().toString());
            adapter.refreshList(shoppingViewModel.getShoppingList().getValue());
            adapter.notifyDataSetChanged();
        };
        adapter = new ShoppingListItemAdapter(getActivity().getApplicationContext(), shoppingViewModel.getShoppingList().getValue(), remove_item);
        recycler.setAdapter(adapter);

        // UI for displaying the subtotal, gst, total cost of the cart
        TextView cart_subtotal = root.findViewById(R.id.cart_subtotal);
        TextView cart_gst = root.findViewById(R.id.cart_gst);
        TextView cart_total = root.findViewById(R.id.cart_total);
        shoppingViewModel.subtotal.observe(getActivity(), new Observer<BigDecimal>() {
            @Override
            public void onChanged(BigDecimal bigDecimal) {
                BigDecimal subtotal = shoppingViewModel.subtotal.getValue();
                BigDecimal total = subtotal.multiply(new BigDecimal("1.05")).setScale(2, BigDecimal.ROUND_HALF_UP);
                cart_subtotal.setText(String.format("Subtotal: $%s", subtotal.toString()));
                cart_gst.setText(String.format("GST: $%s", total.subtract(subtotal).toString()));
                cart_total.setText(String.format("Total: $%s", total.toString()));
            }
        });

        // Setups checkout button
        Button checkout = (Button) root.findViewById(R.id.checkout);
        checkout.setOnClickListener(v -> {
            if (shoppingViewModel.subtotal.getValue().compareTo(new BigDecimal(0)) == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                        .setTitle("Error")
                        .setMessage("Can't checkout with 0 items");
                builder.setPositiveButton("Understood", null);
                builder.create().show();
            } else {
                ShoppingCheckoutDialogFragment dialog = new ShoppingCheckoutDialogFragment();
                dialog.show(getParentFragmentManager(), "");
            }
        });

        // Update recyclerView on value change
        shoppingViewModel.getShoppingList().observe(getActivity(), new Observer<ArrayList<ShoppingListItem>>() {
            @Override
            public void onChanged(ArrayList<ShoppingListItem> shoppingListItems) {
                adapter.refreshList(shoppingListItems);
                adapter.notifyDataSetChanged();
            }
        });

        // Displays a checkmark animation on successful checkout
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
}