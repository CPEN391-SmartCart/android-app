package com.cpen391.smartcart.ui.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.smartcart.R;

import java.util.ArrayList;

/**
 * Allows us to display the contents of a shopping list
 */
public class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemAdapter.ShoppingListItemViewHolder>{
    private LayoutInflater inflater;
    private Context context;
    ArrayList<ShoppingListItem> shoppingList;
    View.OnClickListener remove_item;

    public ShoppingListItemAdapter(Context context, ArrayList<ShoppingListItem> shoppingList, View.OnClickListener remove_item) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.shoppingList = shoppingList;
        this.remove_item = remove_item;
    }

    public void refreshList(ArrayList<ShoppingListItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

    @Override
    public ShoppingListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingListItemViewHolder holder, int position) {
        holder.quantity.setText(String.format("%s", shoppingList.get(position).getQuantity().toString()));
        holder.itemName.setText(shoppingList.get(position).getItemName());
        if(shoppingList.get(position).getWeightString().equals("")) {
            holder.weight.setText("");
        } else {
            holder.weight.setText(String.format("%skg", shoppingList.get(position).getWeightString()));
        }
        holder.price.setText(String.format("$%s", shoppingList.get(position).getTotalPrice().toString()));
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

        private TextView quantity;
        private TextView itemName;
        private TextView weight;
        private TextView price;
        private ImageButton remove;

        /**
         * Represents the layout associated with an entry in the recyclerView
         */
        public ShoppingListItemViewHolder(View itemView) {
            super(itemView);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            weight = (TextView) itemView.findViewById(R.id.weight);
            price = (TextView) itemView.findViewById(R.id.price);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            if (remove_item != null) {
                remove.setOnClickListener(remove_item);
            } else {
                LinearLayout layout = itemView.findViewById(R.id.linearLayout2);
                layout.setWeightSum(9);
                remove.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f));
            }
        }
    }
}
