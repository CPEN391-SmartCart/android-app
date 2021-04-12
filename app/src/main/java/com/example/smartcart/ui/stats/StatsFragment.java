package com.example.smartcart.ui.stats;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.search.SearchItemAdapter;
import com.example.smartcart.ui.shopping.ShoppingList;
import com.example.smartcart.ui.shopping.ShoppingListAdapter;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingListItemAdapter;
import com.example.smartcart.ui.shopping.ShoppingViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StatsFragment extends Fragment {

    StatsItemAdapter topItemsAdapter;
    ShoppingListAdapter historyAdapter;
    ArrayList<StatsItem> topItems = new ArrayList<>();

    private BarChart barChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ShoppingViewModel shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stats, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        //setup top 10 items in bargraph
        barChart = (BarChart) root.findViewById(R.id.bargraph);
        barChart.setTouchEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDescription("");

//        //setup top 10 items recyclerview
//        RecyclerView topItemsRecycler = root.findViewById(R.id.topItems);
//        topItemsRecycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
//                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//        topItemsRecycler.setLayoutManager(layoutManager);
        topItemsAdapter = new StatsItemAdapter(getActivity().getApplicationContext(), topItems);
//        topItemsRecycler.setAdapter(topItemsAdapter);

        // set up recycler for shopping list histories
        RecyclerView historyRecycler = root.findViewById(R.id.history);
        historyRecycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager historyLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        historyRecycler.setLayoutManager(historyLayoutManager);
        historyAdapter = new ShoppingListAdapter(getActivity(), shoppingViewModel.getHistory().getValue());
        historyRecycler.setAdapter(historyAdapter);

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
                            topItemsAdapter.refreshList(topItems);
                            topItemsAdapter.notifyDataSetChanged();

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
