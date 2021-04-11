package com.example.smartcart;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartcart.ui.camera.CameraActivity;
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.shopping.ShoppingFragment;
import com.example.smartcart.ui.shopping.ShoppingItemSearchFragment;
import com.example.smartcart.ui.shopping.ShoppingList;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class HomeActivity extends AppCompatActivity {
    private static final String BT_TAG = "MY_APP_DEBUG_TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final ArrayList<SearchItem> searchableItems = new ArrayList<>();
    ShoppingViewModel shoppingViewModel;
    String googleId;
    RequestQueue queue;

    private NavController navController;
    private Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleId = getIntent().getStringExtra("googleId");
        shoppingViewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        queue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shopping, R.id.navigation_not_shopping, R.id.navigation_stats, R.id.navigation_camera)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Disable shopping and camera fragments while a session is not active
        navView.getMenu().findItem(R.id.navigation_shopping).setOnMenuItemClickListener(item -> {
            if (!shoppingViewModel.isSessionActive().getValue()) {
                showErrorDialog();
            } else {
                navController.navigate(R.id.navigation_shopping);
            }

            return true;
        });

        navView.getMenu().findItem(R.id.navigation_camera).setOnMenuItemClickListener(item -> {
            if (!shoppingViewModel.isSessionActive().getValue()) {
                showErrorDialog();
            } else {
                Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
                startActivityForResult(intent, 2);
            }

            return true;
        });

        initBluetooth();
        initializeSearchableItems();
        initializeReceipts();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2)
        {
            if (data != null) {
                String barcode = data.getStringExtra("BARCODE");
                Log.d("BARCODE", barcode);
                if (barcode != null) {
                    Log.d("BARCODE", String.valueOf(barcode.length()));
                    int length = 3 + barcode.length();
                    Log.d("BARCODE", bluetooth.toString());
                    bluetooth.send(String.format("%02d", length) + "sc:" + barcode);
                    navController.navigate(R.id.navigation_shopping);
                }
            }
        }
    }

    public String getGoogleId() {
        return googleId;
    }

    private void initializeReceipts() {
        String url ="https://cpen391-smartcart.herokuapp.com/receipts/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray resp = new JSONArray(response);
                            for (int i = 0; i < resp.length(); i++) {
                                JSONObject receipt = resp.getJSONObject(i);
                                initializeReceiptItems(receipt.getString("id"), receipt.getDouble("subtotal"), receipt.getString("created_at"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void initializeReceiptItems(String receiptId, Double subtotal, String purchaseDate) {
        String url ="https://cpen391-smartcart.herokuapp.com/receipt-items/" + receiptId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray items = new JSONArray(response);
                            ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                shoppingListItems.add(new ShoppingListItem(item.getInt("quantity"), item.getString("name"), item.getDouble("cost"), item.getDouble("weight")));
                            }
                            shoppingViewModel.addHistory(new ShoppingList(shoppingListItems, subtotal, purchaseDate));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void initializeSearchableItems() {
        //call database to populate searchable items
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://cpen391-smartcart.herokuapp.com/items/search?keyword=";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray items = new JSONArray(response);
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                searchableItems.add(new SearchItem(item.getString("name"), item.getDouble("cost"), item.getString("barcode")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public ArrayList<SearchItem> getSearchableItems() {
        return new ArrayList<>(this.searchableItems);
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

    private void initBluetooth()
    {
        bluetooth = new Bluetooth(this);
        bluetooth.setDeviceCallback(deviceCallback);
        shoppingViewModel.setBluetooth(bluetooth);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();

        if(!bluetooth.isEnabled()){
            Toast.makeText(HomeActivity.this, "Enabling bluetooth", Toast.LENGTH_LONG).show();
            bluetooth.enable();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }

    private DeviceCallback deviceCallback = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            //Toast.makeText(HomeActivity.this, "Connected to: " + device + "!", Toast.LENGTH_LONG).show();
            shoppingViewModel.getBluetoothButton().setText("Connected");
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            Log.d("BT", "DISCONNECTED " + device.getName() + " | " + message);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onMessage(byte[] message) {
            String receivedMsg = new String(message);
            Log.d("NICE", receivedMsg);
            String[] item = receivedMsg.split("\\|");
            String name = item[0];
            double price = Double.parseDouble(item[1]);
            shoppingViewModel.addShoppingListItem(new ShoppingListItem(1, name, price, 0.0));

            String ack = "ic:" + price;
            int length = ack.length();
            bluetooth.send(String.format("%02d", length) + ack);
        }

        @Override
        public void onError(int errorCode) {
            Log.d("BT", "OK!!O?");
        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {
            Log.d("BT", message);
            shoppingViewModel.getBluetoothButton().setText("Retry");
        }
    };
}