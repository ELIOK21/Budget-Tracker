package com.example.app2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class DataActivity extends AppCompatActivity {

    TextView textViewData;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        textViewData = findViewById(R.id.textViewData);

        sharedPreferences = getSharedPreferences("DataActivity", MODE_PRIVATE);
        showSavedData();

        FloatingActionButton fabClearData = findViewById(R.id.fabClearData);
        fabClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllData();
            }
        });
    }

    private void showSavedData() {
        String savedData = sharedPreferences.getString("savedData", "");
        textViewData.setText(savedData);
    }

    private void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();
        textViewData.setText("");
    }
}
