package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.inputText);
        Button generateButton = findViewById(R.id.generateButton);
        TextView resultText = findViewById(R.id.resultText);

        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();

            if (text.isEmpty()) {
                resultText.setText("Введите текст");
            } else {
                resultText.setText("Сценарий:\n" + text);
            }
        });
    }
}
