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

    private File lastSrtFile;

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
        Spinner styleSpinner = findViewById(R.id.styleSpinner);

        // SPEED
        String[] speeds = {"Медленно", "Нормально", "Быстро"};
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                speeds
        );
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(speedAdapter);

        // STYLE
        String[] styles = {"Классика", "Агрессивный", "Минимал", "TikTok PRO"};
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                styles
        );
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(styleAdapter);

        // Generate text
        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            resultText.setText(text.isEmpty() ? "Введите текст" : text);
        });

        // Copy
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

        // Generate SRT
        srtButton.setOnClickListener(v -> {

            String text = resultText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
                return;
            }

            String speed = speedSpinner.getSelectedItem().toString();
            String style = styleSpinner.getSelectedItem().toString();

            int duration = 2;
            int wordsPerLine = 3;

            if (speed.equals("Быстро")) {
                duration = 1;
                wordsPerLine = 2;
            } else if (speed.equals("Медленно")) {
                duration = 3;
            }

            boolean forceCaps = false;
            boolean enableEmoji = true;
            int maxWordsOverride = -1;

            switch (style) {
                case "Агрессивный":
                    forceCaps = true;
                    maxWordsOverride = 2;
                    break;
                case "Минимал":
                    enableEmoji = false;
                    maxWordsOverride = 2;
                    break;
                case "TikTok PRO":
                    forceCaps = true;
                    maxWordsOverride = 1;
                    break;
            }

            if (maxWordsOverride > 0) {
                wordsPerLine = maxWordsOverride;
            }

            String[] words = text.split("\\s+");
            StringBuilder srt = new StringBuilder();

            int index = 1;
            int startSec = 0;

            // HOOK
            srt.append(index++).append("\n");
            srt.append("00:00:00,000 --> 00:00:03,000\n");
            srt.append(buildHook(text)).append("\n\n");
            startSec = 3;

            for (int i = 0; i < words.length; i += wordsPerLine) {

                StringBuilder line = new StringBuilder();
                for (int j = i; j < i + wordsPerLine && j < words.length; j++) {
                    String w = words[j];
                    line.append(forceCaps ? w.toUpperCase() : styleWord(w)).append(" ");
                }

                String emoji = enableEmoji ? detectEmoji(line.toString()) : "";

                int extra = detectPauseBonus(line.toString());
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
                File file = new File(getExternalFilesDir(null), "shorts.srt");
                lastSrtFile = file;

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(srt.toString().getBytes());
                fos.close();

                resultText.setText(srt.toString());
                Toast.makeText(this, "SRT создан", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        });

        // Share
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
        return String.format("00:%02d:%02d,000", seconds / 60, seconds % 60);
    }

    private String styleWord(String word) {
        return word.length() >= 6 ? word.toUpperCase() : word;
    }

    private String buildHook(String text) {
        String[] w = text.split("\\s+");
        StringBuilder h = new StringBuilder("⚡ ");
        for (int i = 0; i < Math.min(6, w.length); i++) h.append(w[i]).append(" ");
        return h.toString().trim().toUpperCase() + "!";
    }

    private int detectPauseBonus(String l) {
        l = l.toLowerCase();
        if (l.contains("?")) return 2;
        if (l.contains("!")) return 1;
        return 0;
    }

    private String detectEmoji(String l) {
        l = l.toLowerCase();
        if (l.contains("?")) return " 🤔";
        if (l.contains("секрет")) return " 💡";
        if (l.contains("ошибка")) return " ⚠️";
        if (l.contains("успех")) return " 🔥";
        return "";
    }
}
