package com.example.shortsgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private File lastSrtFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.inputText);
        TextView resultText = findViewById(R.id.resultText);
        Button generateButton = findViewById(R.id.btnGenerate);
        Button srtButton = findViewById(R.id.btnSrt);
        Button shareButton = findViewById(R.id.btnShare);
        Spinner speedSpinner = findViewById(R.id.speedSpinner);
        Spinner styleSpinner = findViewById(R.id.styleSpinner);

        // Speed
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Медленно", "Нормально", "Быстро"}
        );
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(speedAdapter);

        // Style
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Классика", "TikTok PRO"}
        );
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(styleAdapter);

        // Preview
        generateButton.setOnClickListener(v ->
                animateClick(v, () -> {
                    String text = inputText.getText().toString().trim();
                    if (text.isEmpty()) {
                        resultText.setText("❗ Вставь текст");
                        return;
                    }
                    resultText.setText(
                            "🎬 Стиль: " + styleSpinner.getSelectedItem() + "\n" +
                            "⏱ Скорость: " + speedSpinner.getSelectedItem() + "\n\n" +
                            text
                    );
                })
        );

        // Create SRT
        srtButton.setOnClickListener(v ->
                animateClick(v, () -> {
                    String text = inputText.getText().toString().trim();
                    if (text.isEmpty()) {
                        Toast.makeText(this, "Нет текста", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String style = styleSpinner.getSelectedItem().toString();
                    String speed = speedSpinner.getSelectedItem().toString();

                    if (style.equals("TikTok PRO")) {
                        lastSrtFile = generateTikTokSrt(text);
                    } else {
                        lastSrtFile = generateSmartSrt(text, speed);
                    }

                    if (lastSrtFile != null) {
                        Toast.makeText(this, "SRT сохранён в Загрузки", Toast.LENGTH_SHORT).show();
                    }
                })
        );

        // Share
        shareButton.setOnClickListener(v ->
                animateClick(v, () -> {
                    if (lastSrtFile == null || !lastSrtFile.exists()) {
                        Toast.makeText(this, "Сначала создай SRT", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/x-subrip");
                    intent.putExtra(
                            Intent.EXTRA_STREAM,
                            FileProvider.getUriForFile(
                                    this,
                                    getPackageName() + ".provider",
                                    lastSrtFile
                            )
                    );
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Поделиться SRT"));
                })
        );
    }

    // ===== SMART SRT =====
    private File generateSmartSrt(String text, String speed) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            );

            String name = new SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss",
                    Locale.getDefault()
            ).format(new Date());

            File file = new File(dir, "shorts_" + name + ".srt");

            float duration;
            if (speed.equals("Медленно")) duration = 3f;
            else if (speed.equals("Быстро")) duration = 1.2f;
            else duration = 2f;

            StringBuilder srt = new StringBuilder();
            String[] parts = text.split("[.!?]");
            float time = 0f;
            int i = 1;

            for (String p : parts) {
                p = p.trim();
                if (p.isEmpty()) continue;

                srt.append(i++).append("\n");
                srt.append(formatTime(time))
                        .append(" --> ")
                        .append(formatTime(time + duration))
                        .append("\n");
                srt.append(p).append("\n\n");

                time += duration;
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(srt.toString().getBytes());
            fos.close();

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    // ===== TIKTOK PRO SRT =====
    private File generateTikTokSrt(String text) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            );

            String name = new SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss",
                    Locale.getDefault()
            ).format(new Date());

            File file = new File(dir, "tiktok_" + name + ".srt");

            StringBuilder srt = new StringBuilder();
            String[] words = text.split("\\s+");
            int time = 0;
            int i = 1;

            for (String w : words) {
                if (w.trim().isEmpty()) continue;

                srt.append(i++).append("\n");
                srt.append(formatTimeMs(time))
                        .append(" --> ")
                        .append(formatTimeMs(time + 500))
                        .append("\n");
                srt.append(w.toUpperCase()).append("\n\n");

                time += 500;
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(srt.toString().getBytes());
            fos.close();

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private String formatTime(float sec) {
        int s = (int) sec;
        int ms = (int) ((sec - s) * 1000);
        return String.format("00:00:%02d,%03d", s, ms);
    }

    private String formatTimeMs(int ms) {
        int s = ms / 1000;
        int m = ms % 1000;
        return String.format("00:00:%02d,%03d", s, m);
    }

    private void animateClick(View v, Runnable action) {
        v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(80)
                .withEndAction(() -> v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .withEndAction(action)
                        .start())
                .start();
    }
}
