package com.example.smartcart.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingList;
import com.example.smartcart.ui.shopping.ShoppingListAdapter;
import com.example.smartcart.ui.shopping.ShoppingListItemAdapter;
import com.example.smartcart.ui.shopping.ShoppingViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
    private ShoppingListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // set up button for starting session
        Button startSession = root.findViewById(R.id.start_session_button);
        startSession.setOnClickListener(v -> {
            if (true ) { //TODO: check for bluetooth connection
                shoppingViewModel.startSession();
                Toast.makeText(getActivity(), "Session started", Toast.LENGTH_SHORT).show();
            }
        });

        // set up recycler for shopping list histories
        RecyclerView recycler = root.findViewById(R.id.history);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        adapter = new ShoppingListAdapter(getActivity().getApplicationContext(), shoppingViewModel.getHistory().getValue());
        recycler.setAdapter(adapter);

        shoppingViewModel.getHistory().observe(getActivity(), new Observer<ArrayList<ShoppingList>>() {
            @Override
            public void onChanged(ArrayList<ShoppingList> shoppingLists) {
                adapter.refreshList(shoppingViewModel.getHistory().getValue());
                adapter.notifyDataSetChanged();
            }
        });

        return root;
    }
}