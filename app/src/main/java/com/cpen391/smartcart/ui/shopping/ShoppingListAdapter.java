package com.cpen391.smartcart.ui.shopping;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.smartcart.R;

import java.util.ArrayList;

/**
 * Allows us to display a list of shopping lists
 */
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
        holder.date.setText(String.format("%s", history.get(position).getPurchaseDate().substring(0, 10)));
        holder.time.setText(String.format("%s", history.get(position).getPurchaseDate().substring(11, 16)));
        holder.price.setText(String.format("$%s", history.get(position).getTotalPrice()));
        // Creates a dialog to display the items in a selected shopping list
        holder.layout.setOnClickListener(v -> {
            AlertDialog.Builder receiptItemDialogBuilder = new AlertDialog.Builder(activity)
                    .setTitle("Items in Shopping List")
                    .setPositiveButton("Ok", null);
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

    /**
     * Represents the layout associated with an entry in the recyclerView
     */
    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView time;
        private TextView price;
        private LinearLayout layout;

        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            price = (TextView) itemView.findViewById(R.id.price);
            layout = itemView.findViewById(R.id.shopping_list_item_layout);
        }
    }
}
