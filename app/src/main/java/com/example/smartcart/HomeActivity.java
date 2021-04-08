package com.example.smartcart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.example.smartcart.ui.search.SearchItem;
import com.example.smartcart.ui.shopping.ShoppingFragment;
import com.example.smartcart.ui.shopping.ShoppingItemSearchFragment;
import com.example.smartcart.ui.shopping.ShoppingList;
import com.example.smartcart.ui.shopping.ShoppingViewModel;
import com.example.smartcart.util.LocalDateConverter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class HomeActivity extends AppCompatActivity {
    private static final String BT_TAG = "MY_APP_DEBUG_TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    public Handler mHandler;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    public final static String MODULE_MAC = "20:18:11:21:24:11";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final ArrayList<SearchItem> searchableItems = new ArrayList<>();
    ShoppingViewModel shoppingViewModel;

    public static Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingViewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
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
                navController.navigate(R.id.navigation_camera);
            }

            return true;
        });
        //initializeBluetooth();
        initBluetoothTest();
        initializeSearchableItems();
        initializeHistory();
    }

    private void initializeHistory() {
        try (FileInputStream file = this.openFileInput("history.txt")) {
            InputStreamReader input = new InputStreamReader(file, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(input);
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
            String content = stringBuilder.toString();
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(new TypeToken<LocalDate>(){}.getType(), new LocalDateConverter());
            Gson gson = builder.create();
            Type type = new TypeToken<ArrayList<ShoppingList>>() {}.getType();
            ArrayList<ShoppingList> history = gson.fromJson(content, type);
            shoppingViewModel.setHistory(history);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void initBluetoothTest()
    {
        bluetooth = new Bluetooth(this);
        bluetooth.setDeviceCallback(deviceCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        if (bluetooth.isEnabled())
        {
            Log.d("BT:", "ENABLED");
            Log.d("BT:", bluetooth.getPairedDevices().toString());
            bluetooth.connectToDevice(bluetooth.getPairedDevices().get(3));
            Log.d("BT:", String.valueOf(bluetooth.isConnected()));
        }
        else
        {
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
            Log.d("BT:", "CONNECTED " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            Log.d("BT:", "DISCONNECTED " + device.getName() + " | " + message);
        }

        @Override
        public void onMessage(byte[] message) {
            String test = new String(message);

        }

        @Override
        public void onError(int errorCode) {
            Log.d("BT:", "OK!!O?");
        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {
            Log.d("BT:", message);
        }
    };

//    public static void handleReadMessage(String msg) {
//        if (msg.startsWith("in:")) {
//            btt.setLastLookupName(msg.substring(3));
//        } else {
//            int integerMessage = 0;
//            try {
//                integerMessage = Integer.parseInt(msg.substring(3).trim());
//            } catch (NumberFormatException e) {
//                Log.e(BT_TAG, "Error parsing int", e);
//            }
//            if (msg.startsWith("pw:")) {
//                btt.setLastLookupPrice(((double) integerMessage) / 100.0);
//                btt.setLastLookupByWeight(true);
//            } else if (msg.startsWith("pq:")) {
//                btt.setLastLookupPrice(((double) integerMessage) / 100.0);
//                btt.setLastLookupByWeight(false);
//            } else if (msg.startsWith("sw:")) {
//                btt.setScaleWeightInGrams(((double) integerMessage) / 1000.0);
//            }
//        }
//    }
}