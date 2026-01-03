package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.*;
import android.content.*;

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
        TextView resultText = findViewById(R.id.resultText);
        Button generateButton = findViewById(R.id.generateButton);
        Button copyButton = findViewById(R.id.copyButton);
        Button srtButton = findViewById(R.id.srtButton);
        Spinner speedSpinner = findViewById(R.id.speedSpinner);

        // Spinner скоростей
        String[] speeds = {"Медленно", "Нормально", "Быстро"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                speeds
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        speedSpinner.setAdapter(adapter);

        // Генерация текста
        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            resultText.setText(text.isEmpty() ? "Введите текст" : text);
        });

        // Копирование
        copyButton.setOnClickListener(v -> {
            String text = resultText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(this, "Нечего копировать", Toast.LENGTH_SHORT).show();
                return;
            }

            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(
                    ClipData.newPlainText("Shorts Script", text)
            );
            Toast.makeText(this, "Скопировано", Toast.LENGTH_SHORT).show();
        });

        // SRT
        srtButton.setOnClickListener(v -> {
            String text = resultText.getText().toString().trim();

            if (text.isEmpty()) {
                Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
                return;
            }

            String speed = speedSpinner.getSelectedItem().toString();

int duration;
int wordsPerLine;

switch (speed) {
    case "Быстро": // TikTok
        duration = 1;
        wordsPerLine = 2;
        break;
    case "Медленно": // Reels
        duration = 3;
        wordsPerLine = 3;
        break;
    default: // Shorts
        duration = 2;
        wordsPerLine = 3;
}
            String[] words = text.split("\\s+");
            StringBuilder srt = new StringBuilder();

            int index = 1;
            int startSec = 0;

            for (int i = 0; i < words.length; i += wordsPerLine) {
    ;
    StringBuilder line = new StringBuilder();
    for (int j = i; j < i + wordsPerLine && j < words.length; j++) {
        line.append(styleWord(words[j])).append(" ");
    }
                
    int extra = 0;

// если строка длинная или с CAPS — даём больше времени
if (line.length() > 12) extra = 1;
if (line.toString().equals(line.toString().toUpperCase())) extra = 1;

int endSec = startSec + Math.max(1, duration + extra)
    srt.append(index++).append("\n");
    srt.append(formatTime(startSec))
            .append(" --> ")
            .append(formatTime(endSec))
            .append("\n");
    srt.append(line.toString().trim()).append("\n\n");

    startSec = endSec;
}
            try {
               String fileName;

switch (speed) {
    case "Быстро":
        fileName = "tiktok.srt";
        break;
    case "Медленно":
        fileName = "reels.srt";
        break;
    default:
        fileName = "shorts.srt";
}

File file = new File(getExternalFilesDir(null), fileName); 
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(srt.toString().getBytes());
                fos.close();

                Toast.makeText(
                        this,
                        "SRT готов:\n" + file.getAbsolutePath(),
                        Toast.LENGTH_LONG
                ).show();

            } catch (IOException e) {
                Toast.makeText(this, "Ошибка сохранения SRT", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("00:%02d:%02d,000", min, sec);
    }
} 
private boolean isImportantWord(String word) {
    String w = word.toLowerCase();
    return w.length() >= 6 ||
            w.contains("не") ||
            w.contains("никогда") ||
            w.contains("всегда") ||
            w.contains("очень");
}

private String styleWord(String word) {
    return isImportantWord(word) ? word.toUpperCase() : word;
}
