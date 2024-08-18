package com.example.app2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    EditText editTextSalary, editTextFoodCost;
    Spinner spinnerMeal;
    Button buttonCalculate, buttonTotal;
    ImageView profilePhotoImageView;
    static final int REQUEST_CODE_MEAL_ACTIVITY = 1;
    static final int RESET_REQUEST_CODE = 2;
    static final int REQUEST_IMAGE_PICK = 3;
    int totalFoodCost = 0;
    String mealsSummary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editTextSalary = findViewById(R.id.editTextText);
        editTextFoodCost = findViewById(R.id.editTextText2);
        spinnerMeal = findViewById(R.id.spinnerMeals);
        buttonCalculate = findViewById(R.id.button);
        buttonTotal = findViewById(R.id.buttonTotale);
        profilePhotoImageView = findViewById(R.id.profilePhotoImageView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fabDataActivity = findViewById(R.id.floatingActionButton);


        fabDataActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dataActivityIntent = new Intent(MainActivity2.this, DataActivity.class);
                startActivity(dataActivityIntent);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Meals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeal.setAdapter(adapter);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateRemainingBudget();
            }
        });

        buttonTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String salaryText = editTextSalary.getText().toString().trim();
                String foodCostText = editTextFoodCost.getText().toString().trim();

                if (salaryText.isEmpty() || foodCostText.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "Please enter both salary and food cost", Toast.LENGTH_SHORT).show();
                } else {

                    int salary = Integer.parseInt(salaryText);
                    int foodCost = Integer.parseInt(foodCostText);

                    Intent resultIntent = new Intent(MainActivity2.this, ResultActivity.class);
                    resultIntent.putExtra("totalFoodCost", totalFoodCost);
                    resultIntent.putExtra("mealsSummary", mealsSummary);
                    resultIntent.putExtra("salary", salary);
                    resultIntent.putExtra("foodCost", foodCost);
                    startActivityForResult(resultIntent, RESET_REQUEST_CODE);
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile_photo) {
            openGallery();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEAL_ACTIVITY) {
            if (resultCode == RESULT_OK && data != null) {
                int foodCostFromActivity = data.getIntExtra("foodCost", 0);
                String mealFromActivity = data.getStringExtra("meal");

                if (!mealsSummary.contains(mealFromActivity)) {
                    // If the meal is not added, accumulate data
                    totalFoodCost += foodCostFromActivity;
                    mealsSummary += mealFromActivity + ": $" + foodCostFromActivity + "\n";
                } else {
                    String[] mealData = mealsSummary.split("\n");
                    for (int i = 0; i < mealData.length; i++) {
                        if (mealData[i].startsWith(mealFromActivity)) {
                            mealData[i] = mealFromActivity + ": $" + foodCostFromActivity;
                            break;
                        }
                    }

                    StringBuilder builder = new StringBuilder();
                    for (String meal : mealData) {
                        builder.append(meal).append("\n");
                    }
                    mealsSummary = builder.toString();
                    totalFoodCost = recalculateTotalCost(mealData);
                }
            }
        } else if (requestCode == RESET_REQUEST_CODE && resultCode == RESULT_OK) {
            mealsSummary = "";
            totalFoodCost = 0;
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                profilePhotoImageView.setImageBitmap(circularBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int radius = Math.min(width, height) / 2;

        Bitmap circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circularBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return circularBitmap;
    }

    private int recalculateTotalCost(String[] mealData) {
        int totalCost = 0;
        for (String meal : mealData) {
            String[] parts = meal.split(": \\$");
            totalCost += Integer.parseInt(parts[1]);
        }
        return totalCost;
    }

    private void calculateRemainingBudget() {
        try {
            int salary = Integer.parseInt(editTextSalary.getText().toString());
            int foodCost = Integer.parseInt(editTextFoodCost.getText().toString());
            int remainingBudget = salary - foodCost;

            String selectedMeal = spinnerMeal.getSelectedItem().toString();
            Intent intent = null;

            switch (selectedMeal) {
                case "Breakfast":
                    intent = new Intent(MainActivity2.this, BreakfastActivity.class);
                    break;
                case "Lunch":
                    intent = new Intent(MainActivity2.this, LunchActivity.class);
                    break;
                case "Dinner":
                    intent = new Intent(MainActivity2.this, DinnerActivity.class);
                    break;
                case "Snack":
                    intent = new Intent(MainActivity2.this, SnackActivity.class);
                    break;
            }

            if (intent != null) {
                intent.putExtra("remainingBudget", remainingBudget);
                intent.putExtra("foodCost", foodCost);
                intent.putExtra("selectedMeal", selectedMeal); // Pass selected meal
                startActivityForResult(intent, REQUEST_CODE_MEAL_ACTIVITY);
            } else {
                Toast.makeText(this, "Please select a meal", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}