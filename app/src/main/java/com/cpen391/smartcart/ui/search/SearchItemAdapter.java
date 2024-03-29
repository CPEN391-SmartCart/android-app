package com.cpen391.smartcart.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.smartcart.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Allows us to display a filtered searchable item list
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder> {
    private LayoutInflater inflater;
    ArrayList<SearchItem> item_list;
    ArrayList<SearchItem> filtered_item_list = new ArrayList<>();
    View.OnClickListener add_item;

    /**
     * Sets up the search item adapter
     * @param context the context
     * @param item_list the list of items to add
     * @param add_item the listener for adding items
     */
    public SearchItemAdapter(Context context, ArrayList<SearchItem> item_list, View.OnClickListener add_item) {
        inflater = LayoutInflater.from(context);
        this.item_list = item_list;
        this.filtered_item_list.addAll(item_list);
        this.add_item = add_item;
    }

    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_item, parent, false);
        return new SearchItemViewHolder(view);
    }

    /**
     * This is continuously called on characterText change to filter the item list
     * @param characterText The character text to filter with
     */
    public void filter(String characterText) {
        characterText = characterText.toLowerCase(Locale.getDefault());
        filtered_item_list.clear();
        if (characterText.length() == 0) {
            filtered_item_list.addAll(item_list);
        } else {
            for (SearchItem item: item_list) {
                if (item.getName().toLowerCase(Locale.getDefault()).contains(characterText)) {
                    filtered_item_list.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        holder.itemName.setText(filtered_item_list.get(position).getName());
        if (filtered_item_list.get(position).getPrice().equals(new BigDecimal("0.0"))) {
            holder.price.setText("");
        } else {
            if (filtered_item_list.get(position).requiresWeighing) {
                holder.price.setText(String.format("$%s/kg", filtered_item_list.get(position).getPrice()));
            } else {
                holder.price.setText(String.format("$%s", filtered_item_list.get(position).getPrice()));
            }
        }
        holder.barcode.setText(filtered_item_list.get(position).getBarcode());
    }

    @Override
    public int getItemCount() {
        return filtered_item_list.size();
    }

    /**
     * Represents the layout associated with an entry in the recyclerView
     */
    public class SearchItemViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private TextView price;
        private TextView barcode;
        private LinearLayout layout;

        /**
         * Makes a new search item view holder
         * @param itemView the item view
         */
        public SearchItemViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            price = (TextView) itemView.findViewById(R.id.price);
            barcode = (TextView) itemView.findViewById(R.id.barcode);
            layout = (LinearLayout) itemView.findViewById(R.id.search_item_layout);
            layout.setOnClickListener(add_item);
        }
    }
}
