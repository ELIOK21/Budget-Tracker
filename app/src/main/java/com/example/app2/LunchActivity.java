package com.example.app2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LunchActivity extends AppCompatActivity {

    TextView textViewBudgetAndCost;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        textViewBudgetAndCost = findViewById(R.id.textView3);
        backButton = findViewById(R.id.button2);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("remainingBudget") && intent.hasExtra("foodCost")) {
            int remainingBudget = intent.getIntExtra("remainingBudget", 0);
            int foodCost = intent.getIntExtra("foodCost", 0);
            String selectedMeal = intent.getStringExtra("selectedMeal"); // Retrieve selected meal

            String displayText = "Meal: " + selectedMeal + "\nFood Cost: $" + foodCost + "\nRemaining Budget: $" + remainingBudget;
            textViewBudgetAndCost.setText(displayText);
        }

        backButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("foodCost", intent.getIntExtra("foodCost", 0));
            resultIntent.putExtra("meal", intent.getStringExtra("selectedMeal"));
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
