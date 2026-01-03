package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.inputText);
        Button generateButton = findViewById(R.id.generateButton);
        TextView resultText = findViewById(R.id.resultText);
        Button copyButton = findViewById(R.id.copyButton);
        Button srtButton = findViewById(R.id.srtButton);

        // Генерация текста
        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();

            if (text.isEmpty()) {
                resultText.setText("Введите текст");
            } else {
                resultText.setText(text);
            }
        });

        // Копирование
        copyButton.setOnClickListener(v -> {
            String result = resultText.getText().toString();

            if (result.isEmpty()) {
                Toast.makeText(this, "Нечего копировать", Toast.LENGTH_SHORT).show();
                return;
            }

            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("Shorts Script", result);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Сценарий скопирован", Toast.LENGTH_SHORT).show();
        });

        // SRT
        srtButton.setOnClickListener(v -> {
            String text = resultText.getText().toString();

            if (text.isEmpty()) {
                Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] lines = text.split("\\n+");
            StringBuilder srt = new StringBuilder();

            int startSec = 0;

            for (int i = 0; i < lines.length; i++) {
                int endSec = startSec + 2;

                srt.append(i + 1).append("\n");
                srt.append(formatTime(startSec))
                        .append(" --> ")
                        .append(formatTime(endSec))
                        .append("\n");
                srt.append(lines[i].trim()).append("\n\n");

                startSec = endSec;
            }

            try {
                File file = new File(getExternalFilesDir(null), "shorts.srt");
                FileOutputStream fos =
