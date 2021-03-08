package com.example.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WeightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView itemNameView = findViewById(R.id.weight_item_name_text);
        itemNameView.setText(message);
        TextView itemWeightView = findViewById(R.id.weight_text);
        double weight = getCurrentWeight();
        itemWeightView.setText(String.format( "%.2f", weight) + " lb");
        TextView itemWeightPrice = findViewById(R.id.weight_item_price_text);
        itemWeightPrice.setText(getCurrentPrice());
        TextView itemCost = findViewById(R.id.weight_cost_text);
        double cost = weight*1.29;
        String costString = "$"+String.valueOf(String.format( "%.2f",cost));
        itemCost.setText(costString);

        View weightAddCartButton = findViewById(R.id.weight_add_cart_button);
        weightAddCartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Added " + message + " costing " + costString, Toast.LENGTH_SHORT).show();
            }
        });
    }
    

    double getCurrentWeight()
    {
        return Math.random() + 5.5;
    }
    
    String getCurrentPrice()
    {
        return "$1.29/lb";
    }
}
