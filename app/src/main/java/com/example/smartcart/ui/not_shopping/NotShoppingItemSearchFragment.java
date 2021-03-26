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

import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingQuantityDialogFragment;
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.search.SearchItemAdapter;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

public class NotShoppingItemSearchFragment extends Fragment {

    private NotShoppingViewModel notShoppingViewModel;
    private final ArrayList<SearchItem> item_list = new ArrayList<>();
    private SearchItemAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notShoppingViewModel = new ViewModelProvider(requireActivity()).get(NotShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        // recycler
        RecyclerView recycler = (RecyclerView) root.findViewById(R.id.recycler);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        View.OnClickListener add_item = v -> {
            TextView itemName = v.findViewById(R.id.itemName);
            notShoppingViewModel.setNextItemName(itemName.getText().toString());
            TextView price = v.findViewById(R.id.price);
            notShoppingViewModel.setNextPrice(new BigDecimal(price.getText().toString().substring(1)));

            NotShoppingQuantityDialogFragment dialog = new NotShoppingQuantityDialogFragment();
            dialog.show(getParentFragmentManager(), "NotShoppingQuantityDialogFragment");
        };
        adapter = new SearchItemAdapter(getActivity().getApplicationContext(), item_list, add_item);
        recycler.setAdapter(adapter);

        //TODO: call database to populate searchable items
        item_list.add(new SearchItem("ho", 1.00));
        item_list.add(new SearchItem("eo", 2.7));
        item_list.add(new SearchItem("3o", 3.0));
        item_list.add(new SearchItem("no", 1.0));
        item_list.add(new SearchItem("2o", 6.9));

        adapter.notifyDataSetChanged();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        MenuItem searchBar = menu.findItem(R.id.action_search);
        searchBar.setVisible(true);
        SearchView search = (SearchView) searchBar.getActionView();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_not_shopping_search_to_navigation_not_shopping);
        }
        return super.onOptionsItemSelected(item);
    }

}
