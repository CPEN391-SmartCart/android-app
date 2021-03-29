package com.example.smartcart.ui.shopping;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class ShoppingCheckoutDialogFragment extends DialogFragment {

    private ShoppingViewModel shoppingViewModel;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format("Checkout and pay for total of $%s", shoppingViewModel.total.getValue()))
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: call payment api, show check mark
                        if (true) { //replace with whether payment api returns true
                            shoppingViewModel.clearShoppingListAndAddToHistory();
                            shoppingViewModel.stopSession();
                        } else {
                            // display payment failed
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
