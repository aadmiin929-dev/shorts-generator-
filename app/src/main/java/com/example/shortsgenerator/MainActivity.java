package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.*;
import android.content.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private File lastSrtFile; // последний SRT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.inputText);
        TextView resultText = findViewById(R.id.resultText);
        Button generateButton = findViewById(R.id.generateButton);
        Button copyButton = findViewById(R.id.copyButton);
        Button srtButton = findViewById(R.id.srtButton);
        Button shareButton = findViewById(R.id.shareButton);
        Spinner speedSpinner = findViewById(R.id.speedSpinner);

        // Spinner скоростей
        String[] speeds = {"Медленно", "Нормально", "Быстро"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                speeds
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(adapter);

        //Spinner styleSpinner = findViewById(R.id.styleSpinner);

String[] styles = {
        "Классика",
        "Агрессивный",
        "Минимал",
        "TikTok PRO"
};

ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
        styles
);
styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
styleSpinner.setAdapter(styleAdapter); Генерация текста
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

        // Генерация SRT
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
                case "Быстро":
                    duration = 1;
                    wordsPerLine = 2;
                    break;
                case "Медленно":
                    duration = 3;
                    wordsPerLine = 3;
                    break;
                default:
                    duration = 2;
                    wordsPerLine = 3;
            }

            String[] words = text.split("\\s+");
            StringBuilder srt = new StringBuilder();

            int index = 1;
            int startSec = 0;
            
// HOOK — первая строка
String hook = buildHook(text);
srt.append(index++).append("\n");
srt.append("00:00:00,000 --> 00:00:03,000\n");
srt.append(hook).append("\n\n");

startSec = 3;
           for (int i = 0; i < words.length; i += wordsPerLine) {
    StringBuilder line = new StringBuilder();
    for (int j = i; j < i + wordsPerLine && j < words.length; j++) {
        line.append(styleWord(words[j])).append(" ");
    }

    String emoji = detectEmoji(line.toString());

    int extra = 0;
    if (line.length() > 12) extra++;
    if (line.toString().equals(line.toString().toUpperCase())) extra++;
String emoji = enableEmoji ? detectEmoji(line.toString()) : "";
    extra += detectPauseBonus(line.toString());

    int endSec = startSec + Math.max(1, duration + extra);

    srt.append(index++).append("\n");
    srt.append(formatTime(startSec))
            .append(" --> ")
            .append(formatTime(endSec))
            .append("\n");
    srt.append(line.toString().trim()).append(emoji).append("\n\n");

    startSec = endSec;
} 

            try {
                String fileName;
                switch (speed) {
                    case "Быстро": fileName = "tiktok.srt"; break;
                    case "Медленно": fileName = "reels.srt"; break;
                    default: fileName = "shorts.srt";
                }

                File file = new File(getExternalFilesDir(null), fileName);
                lastSrtFile = file;

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(srt.toString().getBytes());
                fos.close();

                resultText.setText(srt.toString());

                Toast.makeText(
                        this,
                        "SRT готов:\n" + file.getAbsolutePath(),
                        Toast.LENGTH_LONG
                ).show();

            } catch (IOException e) {
                Toast.makeText(this, "Ошибка сохранения SRT", Toast.LENGTH_SHORT).show();
            }int effectiveWords = maxWordsOverride > 0 ? maxWordsOverride : wordsPerLine;

for (int i = 0; i < words.length; i += effectiveWords)
        });

        // Поделиться SRT
        shareButton.setOnClickListener(v -> {
            if (lastSrtFile == null || !lastSrtFile.exists()) {
                Toast.makeText(this, "Сначала создай SRT", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/x-subrip");
            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".provider",
                            lastSrtFile
                    )
            );
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Поделиться SRT"));
        });
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("00:%02d:%02d,000", min, sec);
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

    private String buildHook(String text) {
    String[] words = text.split("\\s+");
    StringBuilder hook = new StringBuilder();

    for (int i = 0; i < Math.min(6, words.length); i++) {
        hook.append(words[i]).append(" ");
    }

    return "⚡ " + hook.toString().trim().toUpperCase() + "!";
}

private int detectPauseBonus(String line) {
    String l = line.toLowerCase();

    if (l.contains("?")) return 2;
    if (l.contains("!")) return 1;
    if (l.contains("...")) return 2;

    if (l.contains("почему") ||
        l.contains("как") ||
        l.contains("но") ||
        l.contains("если")) {
        return 1;
    }

    return 0;
}

  private String detectEmoji(String line) {
    String l = line.toLowerCase();

    if (l.contains("?") || l.contains("почему") || l.contains("как")) {
        return " 🤔";
    }

    if (l.contains("внимание") || l.contains("опасно") || l.contains("ошибка")) {
        return " ⚠️";
    }

    if (l.contains("секрет") || l.contains("узнай") || l.contains("идея")) {
        return " 💡";
    }

    if (l.contains("никогда") || l.contains("шок") || l.contains("страшно")) {
        return " 😱";
    }

    if (l.contains("успех") || l.contains("получилось") || l.contains("работает")) {
        return " 🔥";
    }

    return "";
}  
                 boolean forceCaps = false;
boolean enableEmoji = true;
int maxWordsOverride = -1;

switch (style) {
    case "Агрессивный":
        forceCaps = true;
        enableEmoji = true;
        maxWordsOverride = 2;
        break;

    case "Минимал":
        enableEmoji = false;
        maxWordsOverride = 2;
        break;

    case "TikTok PRO":
        forceCaps = true;
        enableEmoji = true;
        maxWordsOverride = 1;
        break;

    default: // Классика
        enableEmoji = true;
}           
