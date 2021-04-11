package com.example.smartcart.ui.shopping;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.not_shopping.NotShoppingItemSearchFragment;
import com.example.smartcart.ui.not_shopping.NotShoppingQuantityDialogFragment;
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.search.SearchItemAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

public class ShoppingItemSearchFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
    private ArrayList<SearchItem> item_list = new ArrayList<>();
    private SearchItemAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        // recycler
        RecyclerView recycler = (RecyclerView) root.findViewById(R.id.recycler);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        item_list = ((HomeActivity) getActivity()).getSearchableItems();
        View.OnClickListener add_item = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView itemName = v.findViewById(R.id.itemName);
                TextView price = v.findViewById(R.id.price);
                TextView barcode = v.findViewById(R.id.barcode);
                shoppingViewModel.setNextItem(new SearchItem(itemName.getText().toString(), new BigDecimal(price.getText().toString().replace("/kg", "").substring(1)), barcode.getText().toString(), false));
                if (price.getText().toString().endsWith("/kg")) {
                    shoppingViewModel.addShoppingListItem(new ShoppingListItem(1, shoppingViewModel.getNextItem()));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_shopping_search_to_navigation_shopping);
                } else {
                    ShoppingQuantityDialogFragment dialog = new ShoppingQuantityDialogFragment();
                    dialog.show(getParentFragmentManager(), "ShoppingQuantityDialogFragment");
                }
            }
        };
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
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_shopping_search_to_navigation_shopping);
        }
        return super.onOptionsItemSelected(item);
    }

}
