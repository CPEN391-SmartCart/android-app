package com.example.smartcart.ui.not_shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.search.SearchItemAdapter;
import com.example.smartcart.ui.shopping.ShoppingListItem;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This fragment represents search items to be added the notShoppingFragment planning list
 */
public class NotShoppingItemSearchFragment extends Fragment {

    private NotShoppingViewModel notShoppingViewModel;
    private ArrayList<SearchItem> item_list = new ArrayList<>();
    private SearchItemAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notShoppingViewModel = new ViewModelProvider(requireActivity()).get(NotShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        // Setups recyclerView for displaying the searchable items
        RecyclerView recycler = (RecyclerView) root.findViewById(R.id.recycler);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);

        // Adds a listener for adding items to the planning shopping list
        // For Quantity determined price items we instead open a quantity selection dialog
        View.OnClickListener add_item = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView itemName = v.findViewById(R.id.itemName);
                TextView price = v.findViewById(R.id.price);
                TextView barcode = v.findViewById(R.id.barcode);
                notShoppingViewModel.setNextItem(new SearchItem(itemName.getText().toString(), new BigDecimal(price.getText().toString().replace("/kg", "").substring(1)), barcode.getText().toString(), false));
                if (price.getText().toString().endsWith("/kg")) {
                    notShoppingViewModel.addShoppingListItem(new ShoppingListItem(1, notShoppingViewModel.getNextItem()));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_not_shopping_search_to_navigation_not_shopping);
                } else {
                    NotShoppingQuantityDialogFragment dialog = new NotShoppingQuantityDialogFragment();
                    dialog.show(getParentFragmentManager(), "NotShoppingQuantityDialogFragment");
                }
            }
        };
        item_list = ((HomeActivity) getActivity()).getSearchableItems();
        adapter = new SearchItemAdapter(getActivity().getApplicationContext(), item_list, add_item);
        recycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        MenuItem searchBar = menu.findItem(R.id.action_search);
        searchBar.setVisible(true);
        SearchView search = (SearchView) searchBar.getActionView();
        // Setup listener for filtering text
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(search.getQuery().toString().toLowerCase(Locale.getDefault()));
                return true;
            }
        });
    }

    /**
     * Acts as a onClickListener for the back button (It is called home by android)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_not_shopping_search_to_navigation_not_shopping);
        }
        return super.onOptionsItemSelected(item);
    }

}
