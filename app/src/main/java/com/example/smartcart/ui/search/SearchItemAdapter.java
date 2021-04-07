package com.example.smartcart.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder> {
    private LayoutInflater inflater;
    ArrayList<SearchItem> item_list;
    ArrayList<SearchItem> filtered_item_list = new ArrayList<>();
    View.OnClickListener add_item;

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
        holder.price.setText(String.format("$%s", filtered_item_list.get(position).getPrice()));
        holder.barcode.setText(filtered_item_list.get(position).getBarcode());
    }

    @Override
    public int getItemCount() {
        return filtered_item_list.size();
    }

    public class SearchItemViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private TextView price;
        private TextView barcode;
        private LinearLayout layout;

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
