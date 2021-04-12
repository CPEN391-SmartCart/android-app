package com.example.smartcart.ui.home;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.not_shopping.NotShoppingViewModel;
import com.example.smartcart.ui.shopping.ShoppingList;
import com.example.smartcart.ui.shopping.ShoppingListAdapter;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingListItemAdapter;
import com.example.smartcart.ui.shopping.ShoppingViewModel;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;

public class HomeFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
    private NotShoppingViewModel notShoppingViewModel;
    private ShoppingListAdapter adapter;
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

        // set up button for starting session
        startSession = root.findViewById(R.id.start_session_button);
        bluetoothButton = root.findViewById(R.id.smartcart_connect);
        shoppingViewModel.setBluetoothButton(bluetoothButton);

        startSession.setOnClickListener(v -> {
            shoppingViewModel.startSession();
            if (shoppingViewModel.getBluetooth().getBluetoothAdapter() != null && shoppingViewModel.getBluetooth().isConnected()) { //TODO: check for bluetooth connection
                String message = "rs: Resetting VGA display";
                shoppingViewModel.getBluetooth().send(String.format("%02d", message.length()) + message); // reset display


                ShoppingListItem nextShoppingItem = notShoppingViewModel.getNextPathedItem();
                if (nextShoppingItem != null) {
                    int pathedBarcodeLength = 3 + nextShoppingItem.getBarcode().length();
                    notShoppingViewModel.getBluetooth().send(String.format("%02d", pathedBarcodeLength) + "ps:" + nextShoppingItem.getBarcode());
                }

                Toast.makeText(getActivity(), "Session started", Toast.LENGTH_SHORT).show();
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