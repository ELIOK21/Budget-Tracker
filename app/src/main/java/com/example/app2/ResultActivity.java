package com.example.app2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    TextView textViewResult, textViewTotal;
    Button buttonCalculateTotal, buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewResult = findViewById(R.id.textView4);
        buttonCalculateTotal = findViewById(R.id.buttonCalculate);
        textViewTotal = findViewById(R.id.textView6);
        buttonReset = findViewById(R.id.buttonReset);

        int totalFoodCost = getIntent().getIntExtra("totalFoodCost", 0);
        String mealsSummary = getIntent().getStringExtra("mealsSummary");

        String displayText = "Total Food Cost: $" + totalFoodCost + "\n\nMeals Summary:\n" + mealsSummary;
        textViewResult.setText(displayText);

        buttonCalculateTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotal(totalFoodCost);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActivity();
            }
        });

        // FloatingActionButton onClick listener
        FloatingActionButton fabSaveData = findViewById(R.id.fabSave);
        fabSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToSharedPreferences(totalFoodCost, mealsSummary);
            }
        });
    }

    private void calculateTotal(int totalFoodCost) {
        int salary = getIntent().getIntExtra("salary", 0);
        int remaining = salary - totalFoodCost;
        textViewTotal.setText("Remaining Budget: $" + remaining);
    }

    private void resetActivity() {
        setResult(RESULT_OK);
        finish();
    }

    private void saveDataToSharedPreferences(int totalFoodCost, String mealsSummary) {

        String dateTime = DateFormat.getDateTimeInstance().format(new Date());

        int salary = getIntent().getIntExtra("salary", 0);
        int remaining = salary - totalFoodCost;

        String newData = "Date and Time: " + dateTime + "\n" +
                "Meals Summary:\n" + mealsSummary + "\n" +
                "Total Food Cost: $" + totalFoodCost + "\n" +
                "Remaining Budget: $" + remaining + "\n";

        SharedPreferences sharedPreferences = getSharedPreferences("DataActivity", MODE_PRIVATE);

        String existingData = sharedPreferences.getString("savedData", "");

        String delimiter = "=========================\n";

        String updatedData = existingData + delimiter + newData;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("savedData", updatedData);
        editor.apply();

        Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
    }
}

