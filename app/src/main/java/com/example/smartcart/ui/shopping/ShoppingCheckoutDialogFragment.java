package com.example.smartcart.ui.shopping;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartcart.util.LocalDateConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

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

                            try (FileOutputStream historyFile = getActivity().openFileOutput("history.txt", Context.MODE_PRIVATE)) {
                                GsonBuilder builder = new GsonBuilder();
                                builder.registerTypeAdapter(new TypeToken<LocalDate>(){}.getType(), new LocalDateConverter());
                                Gson gson = builder.create();
                                String jsonHistory = gson.toJson(shoppingViewModel.getHistory().getValue());
                                historyFile.write(jsonHistory.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
