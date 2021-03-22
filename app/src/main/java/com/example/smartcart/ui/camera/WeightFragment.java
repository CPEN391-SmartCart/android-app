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

import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingViewModel;

public class WeightFragment extends Fragment {
    private ShoppingViewModel shoppingViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weight, container, false);
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        // Get the Intent that started this activity and extract the string
        //ViewModel
        String message = "apple";

        // Capture the layout's TextView and set the string as its text
        TextView itemNameView = root.findViewById(R.id.weight_item_name_text);
        itemNameView.setText(message);
        TextView itemWeightView = root.findViewById(R.id.weight_text);
        double weight = getCurrentWeight();
        itemWeightView.setText(String.format( "%.2f", weight) + " lb");
        TextView itemWeightPrice = root.findViewById(R.id.weight_item_price_text);
        itemWeightPrice.setText(getCurrentPrice());
        TextView itemCost = root.findViewById(R.id.weight_cost_text);
        double cost = weight*1.29;
        String costString = "$"+String.valueOf(String.format( "%.2f",cost));
        itemCost.setText(costString);

        View weightAddCartButton = root.findViewById(R.id.weight_add_cart_button);
        weightAddCartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Added " + message + " costing " + costString, Toast.LENGTH_SHORT).show();
                // TODO: perform look up to get price
                shoppingViewModel.addShoppingListItem(new ShoppingListItem(1, shoppingViewModel.getNextItemName(), 6.9));
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_weight_to_navigation_camera);
            }
        });

        return root;
    }


    double getCurrentWeight()
    {
        return Math.random() + 5.5;
    }

    String getCurrentPrice()
    {
        return "$1.29/lb";
    }
}
