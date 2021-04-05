package com.example.smartcart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.smartcart.ui.camera.WeightFragment;
import com.example.smartcart.ui.shopping.ShoppingItemSearchFragment;
import com.example.smartcart.ui.shopping.ShoppingFragment;
import com.example.smartcart.ui.shopping.ShoppingViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String BT_TAG = "MY_APP_DEBUG_TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    public static ConnectedThread btt = null;
    public Handler mHandler;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    public final static String MODULE_MAC = "20:18:11:20:33:43";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingViewModel shoppingViewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shopping, R.id.navigation_not_shopping, R.id.navigation_camera)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.getMenu().findItem(R.id.navigation_shopping).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!shoppingViewModel.isSessionActive().getValue()) {
                    showErrorDialog();
                } else {
                    navController.navigate(R.id.navigation_shopping);
                }

                return true;
            }
        });
        initializeBluetooth();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (f instanceof ShoppingItemSearchFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, ShoppingFragment.class, null).addToBackStack("").commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchView = menu.findItem(R.id.action_search);
        searchView.setVisible(false);
        this.invalidateOptionsMenu();

        return true;
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("You must first start a shopping session by connecting with the cart over bluetooth")
                .setNegativeButton("Understood", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void initializeBluetooth()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Toast.makeText(getApplicationContext(), deviceName + "  " + deviceHardwareAddress, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No devices found!", Toast.LENGTH_SHORT).show();
        }

        if(bluetoothAdapter.isEnabled()){

            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            mmDevice = bluetoothAdapter.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[BLUETOOTH]","Connected to: "+mmDevice.getName());
            }catch(IOException e){
                try{mmSocket.close();}catch(IOException c){return;}
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if(msg.what == MessageConstants.MESSAGE_TOAST){
                        String txt = (String)msg.obj;
                        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
                    }
                    if(msg.what == MessageConstants.MESSAGE_READ){
                        String txt = new String((byte[])msg.obj, StandardCharsets.UTF_8);
                        Toast.makeText(getApplicationContext(), "BTReceived: "+ txt, Toast.LENGTH_SHORT).show();
                        if(txt.startsWith("in:")){
                            btt.setLastLookupName(txt.substring(3));
                        }
                        else if(txt.startsWith("pw:"))
                        {
                            btt.setLastLookupPrice(((double)Integer.parseInt(txt.substring(3)))/100.0);
                            btt.setLastLookupByWeight(true);
                        }
                        else if(txt.startsWith("pq:")){
                            btt.setLastLookupPrice(((double)Integer.parseInt(txt.substring(3)))/100.0);
                            btt.setLastLookupByWeight(false);
                        }
                        else if(txt.startsWith("sw:"))
                        {
                            btt.setScaleWeightInGrams(((double)Integer.parseInt(txt.substring(3)))/1000.0);
                        }
                    }
                }
            };

            Log.i("[BLUETOOTH]", "Creating and running Thread");
            btt = new ConnectedThread(mmSocket);
            btt.start();


        }
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public interface DoubleUpdateHandler {
        public void handleDoubleUpdate(double val);
    }


    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private Optional<String> lastLookupName;
        private Optional<Double> lastLookupPrice;
        private Optional<Boolean> lastLookupByWeight;
        private double scaleWeightInGrams;
        private ArrayList<DoubleUpdateHandler> weightUpdateHandlers;

        public class Item
        {
            public Item(String name, double price, boolean byWeight){
                name_ = name;
                price_ = price;
                byWeight_ = byWeight;
            }

            public final String name_;
            public final double price_;
            public final boolean byWeight_;
        }

        public void addWeightChangedCallback(DoubleUpdateHandler handler){
            weightUpdateHandlers.add(handler);
        }

        public void setScaleWeightInGrams(double grams)
        {
            scaleWeightInGrams = grams;
            for (DoubleUpdateHandler handler : weightUpdateHandlers){
                handler.handleDoubleUpdate(grams);
            }
        }

        public double getScaleWeightInGrams(){
            return scaleWeightInGrams;
        }

        public void setLastLookupName(String str)
        {
            lastLookupName = Optional.of(str);
        }

        public void setLastLookupPrice(double val)
        {
            lastLookupPrice = Optional.of(new Double(val));
        }

        public void setLastLookupByWeight(boolean val)
        {
            lastLookupByWeight = Optional.of(new Boolean(val));
        }

        public void clearLastLookupItem()
        {
            lastLookupName = Optional.empty();
            lastLookupPrice = Optional.empty();
            lastLookupByWeight = Optional.empty();
        }

        public Optional<Item> getLastLookupItem()
        {
            if(lastLookupByWeight.isPresent()&&lastLookupPrice.isPresent()&&lastLookupName.isPresent()) {
                return Optional.of(new Item(lastLookupName.get(), lastLookupPrice.get(), lastLookupByWeight.get()));
            }
            else {
                return Optional.empty();
            }
        }

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(BT_TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(BT_TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(BT_TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String str) {
            try {
                byte[]bytes = str.getBytes();
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, bytes);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(BT_TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");

                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(BT_TAG, "Could not close the connect socket", e);
            }
        }
    }
}