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
import java.util.ArrayList;
import java.util.List;

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

        // Генерация
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
            clipboard.setPrimaryClip(
                    ClipData.newPlainText("Shorts Script", result)
            );

            Toast.makeText(this, "Скопировано", Toast.LENGTH_SHORT).show();
        });

        // УМНЫЙ SRT
        srtButton.setOnClickListener(v -> {
            String text = resultText.getText().toString().trim();

            if (text.isEmpty()) {
                Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> captions = splitSmart(text);
            StringBuilder srt = new StringBuilder();

            int currentTime = 0;

            for (int i = 0; i < captions.size(); i++) {
                String line = captions.get(i);

                int duration = line.length() < 40 ? 2 :
                               line.length() < 80 ? 3 : 4;

                int endTime = currentTime + duration;

                srt.append(i + 1).append("\n");
                srt.append(formatTime(currentTime))
                        .append(" --> ")
                        .append(formatTime(endTime))
                        .append("\n");
                srt.append(line).append("\n\n");

                currentTime = endTime;
            }

            try {
                File file = new File(getExternalFilesDir(null), "shorts.srt");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(srt.toString().getBytes());
                fos.close();

                Toast.makeText(
                        this,
                        "SRT сохранён:\n" + file.getAbsolutePath(),
                        Toast.LENGTH_LONG
                ).show();

            } catch (IOException e) {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Умное разбиение по смыслу
    private List<String> splitSmart(String text) {
        List<String> result = new ArrayList<>();

        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder buffer = new StringBuilder();

        for (String s : sentences) {
            if (buffer.length() + s.length() < 80) {
                buffer.append(s).append(" ");
            } else {
                result.add(buffer.toString().trim());
                buffer.setLength(0);
                buffer.append(s).append(" ");
            }
        }

        if (buffer.length() > 0) {
            result.add(buffer.toString().trim());
        }

        return result;
    }

    // Формат времени SRT
    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("00:%02d:%02d,000", min, sec);
    }
}
