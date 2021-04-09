package com.example.smartcart.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingViewModel;

import java.util.Locale;

public class WeightFragment extends Fragment {
    private ShoppingViewModel shoppingViewModel;
    double cost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weight, container, false);
        TextView itemWeightView = root.findViewById(R.id.weight_text);
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        // Get the Intent that started this activity and extract the string
        String itemName = getArguments().getString("itemName");
        double itemWeightPrice = getArguments().getDouble("itemPricePerGrams");
        // Capture the layout's TextView and set the string as its text
        TextView itemNameView = root.findViewById(R.id.weight_item_name_text);
        TextView itemWeightPriceView = root.findViewById(R.id.weight_item_price_text);
        TextView itemCostView = root.findViewById(R.id.weight_cost_text);


        // set name and price text views
        itemNameView.setText(itemName);
        itemWeightPriceView.setText(String.format("%s/g", String.format(Locale.CANADA, "%.2f", itemWeightPrice)));

        // set up call back for when weight changes
        HomeActivity.addWeightChangedCallback(val -> {
            itemWeightView.setText(String.format(Locale.CANADA, "%.2f", val));
            cost = val*HomeActivity.item.price;
            String costString = "$"+String.format(Locale.CANADA, "%.2f",val*itemWeightPrice);
            itemCostView.setText(costString);
        });

        View weightAddCartButton = root.findViewById(R.id.weight_add_cart_button);
        weightAddCartButton.setOnClickListener(v -> {
            String costString = "$"+String.format(Locale.CANADA, "%.2f",cost);
            Toast.makeText(requireActivity(), "Added " + itemName + " costing " + costString, Toast.LENGTH_SHORT).show();
            double cost = HomeActivity.item.weight*HomeActivity.item.price;
            shoppingViewModel.addShoppingListItem(new ShoppingListItem(HomeActivity.item.weight, itemName, cost));
            HomeActivity.bluetooth.send("ic:" + cost);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_weight_to_navigation_camera);
        });

        return root;
    }
}
