package com.example.smartcart.ui.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;

import java.util.ArrayList;

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
        holder.price.setText(String.format("$%s", shoppingList.get(position).getTotalPrice().toString()));
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

        private TextView quantity;
        private TextView itemName;
        private TextView price;
        private ImageButton remove;

        public ShoppingListItemViewHolder(View itemView) {
            super(itemView);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            price = (TextView) itemView.findViewById(R.id.price);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            remove.setOnClickListener(remove_item);
        }
    }
}
