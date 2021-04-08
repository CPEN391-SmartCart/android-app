package com.example.smartcart.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StatsFragment extends Fragment {

    StatsItemAdapter adapter;
    ArrayList<StatsItem> topItems = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        //setup top 10 items recyclerview
        RecyclerView topItemsRecycler = root.findViewById(R.id.topItems);
        topItemsRecycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        topItemsRecycler.setLayoutManager(layoutManager);
        adapter = new StatsItemAdapter(getActivity().getApplicationContext(), topItems);
        topItemsRecycler.setAdapter(adapter);

        getStats();
        adapter.notifyDataSetChanged();
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
                                topItems.add(new StatsItem(item.getString("name"), item.getInt("count")));
                            }
                            adapter.refreshList(topItems);
                            adapter.notifyDataSetChanged();
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
