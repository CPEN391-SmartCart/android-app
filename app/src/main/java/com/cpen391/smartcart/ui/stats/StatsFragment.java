package com.cpen391.smartcart.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.cpen391.smartcart.HomeActivity;
import com.cpen391.smartcart.R;
import com.cpen391.smartcart.ui.shopping.ShoppingList;
import com.cpen391.smartcart.ui.shopping.ShoppingListAdapter;
import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This fragment displays the information related to the users statistics and shopping habits
 */
public class StatsFragment extends Fragment {

    private ShoppingListAdapter historyAdapter;
    private ArrayList<StatsItem> topItems = new ArrayList<>();
    private BarChart barChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ShoppingViewModel shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stats, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Setup top 10 items in bargraph
        barChart = (BarChart) root.findViewById(R.id.bargraph);
        barChart.setTouchEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDescription("");

        // Setup recycler for shopping list histories
        RecyclerView historyRecycler = root.findViewById(R.id.history);
        historyRecycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager historyLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        historyRecycler.setLayoutManager(historyLayoutManager);
        historyAdapter = new ShoppingListAdapter(getActivity(), shoppingViewModel.getHistory().getValue());
        historyRecycler.setAdapter(historyAdapter);

        // Updates the UI on history changing
        shoppingViewModel.getHistory().observe(getActivity(), new Observer<ArrayList<ShoppingList>>() {
            @Override
            public void onChanged(ArrayList<ShoppingList> shoppingLists) {
                historyAdapter.refreshList(shoppingViewModel.getHistory().getValue());
                historyAdapter.notifyDataSetChanged();
            }
        });

        getStats();
        shoppingViewModel.sortHistory();
        return root;
    }

    /**
     * Fetches stats information from the backend database
     */
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

                            // bar graph
                            ArrayList<BarEntry> barEntries = new ArrayList<>();
                            ArrayList<String> barNames = new ArrayList<>();

                            for (int i = 0; i < topItems.size(); i++){
                                barEntries.add(new BarEntry(topItems.get(i).getCount(), i));
                                barNames.add(topItems.get(i).getName());
                            }

                            BarDataSet barDataSet = new BarDataSet(barEntries, "Item Names");
                            barDataSet.setColor(Color.rgb(115, 50, 207));

                            BarData theData = new BarData(barNames, barDataSet);
                            barChart.setData(theData);
                            barChart.invalidate();

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



}
