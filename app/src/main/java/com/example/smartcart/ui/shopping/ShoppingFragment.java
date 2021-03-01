package com.example.smartcart.ui.shopping;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;

import java.util.ArrayList;

public class ShoppingFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;

    private RecyclerView recycler;

    PopupWindow popUp;
    boolean click = true;

    private ArrayList<ShoppingListItem> shopping_list = new ArrayList<>();
    private ShoppingListItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(this).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopping, container, false);
        final TextView textView = root.findViewById(R.id.text_shopping);
        shoppingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // recycler
        recycler = (RecyclerView) root.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        adapter = new ShoppingListItemAdapter(getActivity().getApplicationContext(), shopping_list);
        recycler.setAdapter(adapter);

        // Add Items to cart
        Button but = root.findViewById(R.id.button);
        LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
        but.setOnClickListener(v -> {
            if (click) {
                popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
                popUp.update(50, 50, 300, 80);
                click = false;
            } else {
                popUp.dismiss();
                click = true;
            }
        });

        // filler text
        shopping_list.add(new ShoppingListItem("1", "Pickles", "$9.90"));
        shopping_list.add(new ShoppingListItem("1", "Pickles", "$9.90"));
        adapter.notifyDataSetChanged();
        return root;
    }

    public void addItems(View v) {
        //shopping_list.add();
        adapter.notifyDataSetChanged();
    }

    class ShoppingListItem {
        public String quantity;
        public String item_name;
        public String price;

        public ShoppingListItem(String quantity, String item_name, String price) {
            this.quantity = quantity;
            this.item_name = item_name;
            this.price = price;
        }
    }

    public class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemAdapter.ShoppingListItemViewHolder>{
        private LayoutInflater inflater;
        private Context context;
        ArrayList<ShoppingListItem> shopping_list;

        public ShoppingListItemAdapter(Context context, ArrayList<ShoppingListItem> shopping_list) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.shopping_list = shopping_list;
        }

        @Override
        public ShoppingListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.shopping_list_item, parent, false);
            return new ShoppingListItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ShoppingListItemViewHolder holder, int position) {
            holder.quantity.setText(shopping_list.get(position).quantity);
            holder.item_name.setText(shopping_list.get(position).item_name);
            holder.price.setText(shopping_list.get(position).price);
            holder.remove.setOnClickListener(v -> {
                shopping_list.remove(position);
                adapter.notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return shopping_list.size();
        }

        public class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

            private TextView quantity;
            private TextView item_name;
            private TextView price;
            private ImageButton remove;

            public ShoppingListItemViewHolder(View itemView) {
                super(itemView);
                quantity = (TextView) itemView.findViewById(R.id.quantity);
                item_name = (TextView) itemView.findViewById(R.id.item_name);
                price = (TextView) itemView.findViewById(R.id.price);
                remove = (ImageButton) itemView.findViewById(R.id.remove);
            }
        }
    }


}