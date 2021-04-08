package com.example.smartcart.ui.shopping;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartcart.HomeActivity;
import com.example.smartcart.util.LocalDateConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;

public class ShoppingCheckoutDialogFragment extends DialogFragment {

    private ShoppingViewModel shoppingViewModel;
    private String receiptId;
    private String googleId;
    private RequestQueue queue;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        googleId = ((HomeActivity) requireActivity()).getGoogleId();
        queue = Volley.newRequestQueue(requireActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(String.format("Checkout and pay for total of $%s", shoppingViewModel.subtotal.getValue()))
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: call payment api, show check mark
                        if (true) { //replace with whether payment api returns true
                            shoppingViewModel.clearShoppingListAndAddToHistory();
                            shoppingViewModel.stopSession();
                            PostReceipt();
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

    private void PostReceipt() {
        String url ="https://cpen391-smartcart.herokuapp.com/receipts";
        JSONObject body = new JSONObject();
        try {
            body.put("googleId", googleId);
            body.put("subTotal", shoppingViewModel.getRecentShoppingList().getSubTotal());
            body.put("gst", shoppingViewModel.getRecentShoppingList().getGST());
            body.put("total", shoppingViewModel.getRecentShoppingList().getTotalPrice());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = body.toString();
        System.out.println(requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        readReceipt();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void readReceipt() {
        String url ="https://cpen391-smartcart.herokuapp.com/receipts/id/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray resp = new JSONArray(response);
                            JSONObject res = resp.getJSONObject(0);
                            System.out.println(res);
                            receiptId = res.getString("max");
                            for (ShoppingListItem item : shoppingViewModel.getRecentShoppingList().getItems()) {
                                postItem(item);
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

    private void postItem(ShoppingListItem item) {
        String url ="https://cpen391-smartcart.herokuapp.com/receipt-items";
        JSONObject body = new JSONObject();
        try {
            body.put("receiptId", receiptId);
            body.put("name", item.getItemName());
            body.put("cost", item.getPrice());
            body.put("weight", item.getWeight());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = body.toString();
        System.out.println(requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
