package com.example.smartcart.ui.stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.R;
import java.util.ArrayList;

public class StatsItemAdapter extends RecyclerView.Adapter<StatsItemAdapter.StatsItemViewHolder> {
    private LayoutInflater inflater;
    ArrayList<StatsItem> item_list;

    public StatsItemAdapter(Context context, ArrayList<StatsItem> item_list) {
        inflater = LayoutInflater.from(context);
        this.item_list = item_list;
    }

    public void refreshList(ArrayList<StatsItem> item_list) {
        this.item_list = item_list;
    }

    @Override
    public StatsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.stats_item, parent, false);
        return new StatsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatsItemViewHolder holder, int position) {
        holder.itemName.setText(item_list.get(position).getName());
        holder.count.setText(item_list.get(position).getCount().toString());
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public class StatsItemViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private TextView count;

        public StatsItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            count = itemView.findViewById(R.id.count);
        }
    }
}
