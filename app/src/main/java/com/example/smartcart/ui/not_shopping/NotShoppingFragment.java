package com.example.smartcart.ui.not_shopping;

import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingListItemAdapter;

public class NotShoppingFragment extends Fragment {

    private NotShoppingViewModel notShoppingViewModel;
    private ShoppingListItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notShoppingViewModel =
                new ViewModelProvider(requireActivity()).get(NotShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_not_shopping, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // recycler
        RecyclerView recycler = root.findViewById(R.id.not_shopping_list);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);

        //
        View.OnClickListener remove_item = v -> {
            LinearLayout layout = (LinearLayout) v.getParent();
            TextView itemName = layout.findViewById(R.id.itemName);
            notShoppingViewModel.removeShoppingListItem(itemName.getText().toString());
            adapter.refreshList(notShoppingViewModel.getShoppingList().getValue());
            adapter.notifyDataSetChanged();
        };
        adapter = new ShoppingListItemAdapter(getActivity().getApplicationContext(), notShoppingViewModel.getShoppingList().getValue(), remove_item);
        recycler.setAdapter(adapter);

        // for adding App Bar button
        setHasOptionsMenu(true);

        //display shortest path
        Button displayPath = root.findViewById(R.id.displayPath);
        displayPath.setOnClickListener(v -> {
            HomeActivity.btt.write("pp:"+notShoppingViewModel.getNextItem());
        });
        //Knapsack
        Button knapsack = root.findViewById(R.id.knapsack);
        knapsack.setOnClickListener(v -> {
            //TODO: bluetooth call de1 to hw accel knapsack
        });
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
            System.out.println("nav to search");
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_not_shopping_to_navigation_not_shopping_search);
        }
        return super.onOptionsItemSelected(item);
    }
}