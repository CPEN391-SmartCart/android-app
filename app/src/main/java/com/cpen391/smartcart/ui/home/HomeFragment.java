package com.cpen391.smartcart.ui.home;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cpen391.smartcart.R;
import com.cpen391.smartcart.ui.not_shopping.NotShoppingViewModel;
import com.cpen391.smartcart.ui.shopping.ShoppingListItem;
import com.cpen391.smartcart.ui.shopping.ShoppingViewModel;

import java.util.List;

import me.aflak.bluetooth.Bluetooth;

/**
 * This fragment represents the home screen of the home activity
 */
public class HomeFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
    private NotShoppingViewModel notShoppingViewModel;
    private Bluetooth bluetooth;
    private Button startSession;
    private Button bluetoothButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        notShoppingViewModel =
                new ViewModelProvider(requireActivity()).get(NotShoppingViewModel.class);

        this.bluetooth = shoppingViewModel.getBluetooth();
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Setup buttons for starting session
        startSession = root.findViewById(R.id.start_session_button);
        bluetoothButton = root.findViewById(R.id.smartcart_connect);
        shoppingViewModel.setBluetoothButton(bluetoothButton);

        startSession.setOnClickListener(v -> {
            if(bluetooth.isConnected()) {
                shoppingViewModel.startSession();
                if (shoppingViewModel.getBluetooth().getBluetoothAdapter() != null) {
                    String message = "rs: Resetting VGA display";
                    shoppingViewModel.getBluetooth().send(String.format("%02d", message.length()) + message); // reset display

                    Toast.makeText(getActivity(), "Session started", Toast.LENGTH_SHORT).show();
                }
            } else {
                new AlertDialog.Builder(this.getActivity())
                        .setTitle("Error")
                        .setMessage("You must first connect to a SmartCart")
                        .setNegativeButton("Understood", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupBluetooth();
            }
        });

        if(bluetooth.isConnected()){
            bluetoothButton.setText("Connected");
        } else {
            bluetoothButton.setText("Connect to SmartCart");
        }

        return root;
    }

    /**
     * Handles pairing with the De1
     */
    private void setupBluetooth(){
        List<BluetoothDevice> bluetoothDevices = bluetooth.getPairedDevices();
        AlertDialog.Builder bluetoothDialog = new AlertDialog.Builder(getActivity());

        bluetoothDialog.setTitle("Please select your SmartCart:");
        final ArrayAdapter<String> bluetoothAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);

        for(BluetoothDevice bluetoothDevice : bluetoothDevices){
            bluetoothAdapter.add(bluetoothDevice.getName());
        }

        bluetoothDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        bluetoothDialog.setAdapter(bluetoothAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedDevice = bluetoothAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setTitle("Connecting to");
                builderInner.setMessage(selectedDevice);
                bluetooth.connectToName(selectedDevice);
                bluetoothButton.setText("Connecting");
                dialog.dismiss();
            }
        });

        bluetoothDialog.show();
    }
}