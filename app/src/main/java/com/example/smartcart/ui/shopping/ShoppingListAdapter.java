package com.example.smartcart.ui.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{
    private LayoutInflater inflater;
    private Context context;
    ArrayList<ShoppingList> history;

    public ShoppingListAdapter(Context context, ArrayList<ShoppingList> history) {
        inflater = LayoutInflater.from(context);
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView price;
        private TextView date;

        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
