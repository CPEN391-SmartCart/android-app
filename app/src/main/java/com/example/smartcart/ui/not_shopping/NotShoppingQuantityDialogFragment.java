package com.example.smartcart.ui.not_shopping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingFragment;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingViewModel;

import org.jetbrains.annotations.NotNull;

public class NotShoppingQuantityDialogFragment extends DialogFragment {


    private NotShoppingViewModel notShoppingViewModel;
    private NumberPicker aNumberPicker;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        notShoppingViewModel = new ViewModelProvider(requireActivity()).get(NotShoppingViewModel.class);

        RelativeLayout linearLayout = new RelativeLayout(requireActivity());
        aNumberPicker = new NumberPicker(requireActivity());
        aNumberPicker.setMaxValue(99);
        aNumberPicker.setMinValue(1);
        aNumberPicker.setValue(1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Select quantity");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                notShoppingViewModel.addShoppingListItem(new ShoppingListItem(aNumberPicker.getValue(), notShoppingViewModel.getNextItem()));
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_not_shopping_search_to_navigation_not_shopping);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

        return alertDialogBuilder.create();
    }
}
