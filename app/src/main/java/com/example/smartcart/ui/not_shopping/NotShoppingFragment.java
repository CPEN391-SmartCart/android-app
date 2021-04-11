package com.example.smartcart.ui.not_shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingListItemAdapter;
import com.example.smartcart.ui.stats.StatsItem;
import com.example.smartcart.ui.stats.StatsItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class NotShoppingFragment extends Fragment {

    private NotShoppingViewModel notShoppingViewModel;
    private ShoppingListItemAdapter adapter;
    ArrayList<StatsItem> topItems = new ArrayList<>();

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

        //Knapsack
        Button knapsack = root.findViewById(R.id.knapsack);
        EditText budgetInput = root.findViewById(R.id.budgetInput);
        knapsack.setOnClickListener(v -> {
            try{
                Double budgetAmount  = Double.parseDouble(budgetInput.getText().toString());
                executeKnapsackAlgorithm(budgetAmount);
            }catch(NumberFormatException e){
                Toast.makeText(getContext(), "Invalid budget input", Toast.LENGTH_SHORT).show();
            }
        });
        getStats();
        return root;
    }

    public void getStats() {
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url ="https://cpen391-smartcart.herokuapp.com/stats/frequency?"+"googleId=" + ((HomeActivity) getActivity()).getGoogleId() + "&N=5" ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray items = new JSONArray(response);
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                topItems.add(new StatsItem(item.getString("name"), item.getInt("sum"), item.getDouble("cost")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void executeKnapsackAlgorithm(Double budgetAmount){
        BigDecimal budgetRemaining = new BigDecimal(budgetAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
        topItems.sort((StatsItem i1, StatsItem i2)->i2.getCount()-i1.getCount());
        for(StatsItem item : topItems){
            BigDecimal cost = item.getCost();
            if(cost.compareTo(budgetRemaining)<0){
                notShoppingViewModel.addShoppingListItem(new ShoppingListItem(1, item.getName(),item.getCost(),0.0));
                budgetRemaining = budgetRemaining.subtract(cost);
            }
        }
        adapter.refreshList(notShoppingViewModel.getShoppingList().getValue());
        adapter.notifyDataSetChanged();
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