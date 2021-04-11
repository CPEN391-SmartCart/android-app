package com.example.smartcart.ui.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.smartcart.R;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ShoppingCheckoutDialogFragment extends DialogFragment {

    private ShoppingViewModel shoppingViewModel;
    private String receiptId;
    private String googleId;
    private RequestQueue queue;

    private String paymentIntentClientSecret;
    private Stripe stripe;

    /**
     * USE CARD 4242 4242 4242 4242 WITH ANY DATE IN THE FUTURE ANY CVC AND A 5? DIGIT NUMERICAL POSTAL CODE (US)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        googleId = ((HomeActivity) requireActivity()).getGoogleId();
        queue = Volley.newRequestQueue(requireActivity());
        stripe = new Stripe(
                requireContext().getApplicationContext(),
                "pk_test_51IOeizF8GwWH2Z4EuiD4cCfl7DtLAaK7SA0lVYnSVs4O84LGyr92CwAOhCwIrW2BUt3Xgw3re7Q6Z7WXPCnu5o1A004jK1w6CV"
        );
        startCheckout(shoppingViewModel.subtotal.getValue().multiply(new BigDecimal(105)).intValue());

        View checkoutView = inflater.inflate(R.layout.checkout_dialog, container, false);
        Button payButton = checkoutView.findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = checkoutView.findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null && paymentIntentClientSecret != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            } else {
                dismiss();
                displayAlert(
                        "Payment failed", "Invalid payment information"
                );
            }
        });
        return checkoutView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback());
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                shoppingViewModel.clearShoppingListAndAddToHistory();
                shoppingViewModel.stopSession();
                PostReceipt();
                String message = "sp: Payment Successful";
                if (shoppingViewModel.getBluetooth().getBluetoothAdapter() != null) {
                    shoppingViewModel.getBluetooth().send(String.format("%02d", message.length()) + message);
                }
                dismiss();
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                dismiss();
                displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }
        @Override
        public void onError(@NonNull Exception e) {
            // Payment request failed – allow retrying using the same payment method
            dismiss();
            displayAlert("Error", e.toString());
        }
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
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
            body.put("quantity", item.getQuantity());
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

    private void startCheckout(int amount) {
        String url ="https://cpen391-smartcart.herokuapp.com/payment";
        JSONObject body = new JSONObject();
        try {
            body.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = body.toString();
        System.out.println(requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Payment Success
                        try {
                            JSONObject resp = new JSONObject(response);
                            System.out.println(resp);
                            paymentIntentClientSecret = resp.getString("clientSecret");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Payment Failure
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
