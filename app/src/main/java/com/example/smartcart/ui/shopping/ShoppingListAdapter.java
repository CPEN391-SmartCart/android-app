package com.example.smartcart.ui.shopping;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;
import com.example.smartcart.ui.stats.StatsFragment;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{
    private LayoutInflater inflater;
    private FragmentActivity activity;
    ArrayList<ShoppingList> history;

    public ShoppingListAdapter(FragmentActivity activity, ArrayList<ShoppingList> history) {
        inflater = LayoutInflater.from(activity.getApplicationContext());
        this.activity = activity;
        this.history = history;
    }

    public void refreshList(ArrayList<ShoppingList> history) {
        this.history = history;
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.shopping_list_entry, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, int position) {
        holder.name.setText(String.format("%s", history.get(position).getName()));
        holder.price.setText(String.format("$%s", history.get(position).getTotalPrice()));
        holder.date.setText(String.format("%s", history.get(position).getPurchaseDate()));
        holder.layout.setOnClickListener(v -> {
            AlertDialog.Builder receiptItemDialogBuilder = new AlertDialog.Builder(activity);
            View receiptItemView = inflater.inflate(R.layout.receipt_items_dialog, null);

            RecyclerView receiptItems = receiptItemView.findViewById(R.id.receipt_items_recycler);
            receiptItems.setLayoutManager(new LinearLayoutManager(activity));
            receiptItems.setHasFixedSize(true);

            ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(activity, history.get(position).getItems(), null);
            receiptItems.setAdapter(shoppingListItemAdapter);
            receiptItemDialogBuilder.setView(receiptItemView);

            AlertDialog receiptItemDialog = receiptItemDialogBuilder.create();
            receiptItemDialog.getWindow().setLayout(600, 400);

            receiptItemDialog.show();
            shoppingListItemAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView price;
        private TextView date;
        private LinearLayout layout;

        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            date = (TextView) itemView.findViewById(R.id.date);
            layout = itemView.findViewById(R.id.shopping_list_item_layout);
        }
    }
}
