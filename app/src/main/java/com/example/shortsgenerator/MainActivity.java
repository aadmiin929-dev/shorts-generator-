package com.example.shortsgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private File lastSrtFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI
        EditText inputText = findViewById(R.id.inputText);
        TextView resultText = findViewById(R.id.resultText);
        Button generateButton = findViewById(R.id.btnGenerate);
        Button srtButton = findViewById(R.id.btnSrt);
        Button shareButton = findViewById(R.id.btnShare);
        Spinner speedSpinner = findViewById(R.id.speedSpinner);
        Spinner styleSpinner = findViewById(R.id.styleSpinner);

        // Speed spinner
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Медленно", "Нормально", "Быстро"}
        );
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(speedAdapter);

        // Style spinner
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
                        Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "SRT создан", Toast.LENGTH_SHORT).show();
                    }
                })
        );

        // Share SRT
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
            File file = new File(getFilesDir(), "subtitles.srt");
            StringBuilder srt = new StringBuilder();

            float duration;
            switch (speed) {
                case "Медленно": duration = 3.0f; break;
                case "Быстро": duration = 1.2f; break;
                default: duration = 2.0f;
            }

            List<String> lines = splitSmart(text);
            float time = 0f;
            int index = 1;

            for (String line : lines) {
                srt.append(index++).append("\n");
                srt.append(formatTime(time))
                        .append(" --> ")
                        .append(formatTime(time + duration))
                        .append("\n");
                srt.append(line).append("\n\n");
                time += duration;
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(srt.toString().getBytes());
            fos.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== TIKTOK PRO SRT =====

    private File generateTikTokSrt(String text) {
        try {
            File file = new File(getFilesDir(), "subtitles_tiktok.srt");
            StringBuilder srt = new StringBuilder();

            String[] words = text.split("\\s+");
            int timeMs = 0;
            int index = 1;

            for (String word : words) {
                if (word.trim().isEmpty()) continue;

                srt.append(index++).append("\n");
                srt.append(formatTimeMs(timeMs))
                        .append(" --> ")
                        .append(formatTimeMs(timeMs + 500))
                        .append("\n");
                srt.append(word.toUpperCase()).append("\n\n");

                timeMs += 500;
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(srt.toString().getBytes());
            fos.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== HELPERS =====

    private List<String> splitSmart(String text) {
        String[] raw = text.split("[.!?]");
        List<String> result = new ArrayList<>();

        for (String part : raw) {
            part = part.trim();
            if (part.length() > 40) {
                int mid = part.length() / 2;
                result.add(part.substring(0, mid).trim());
                result.add(part.substring(mid).trim());
            } else {
                result.add(part);
            }
        }
        return result;
    }

    private String formatTime(float seconds) {
        int sec = (int) seconds;
        int ms = (int) ((seconds - sec) * 1000);
        return String.format("00:00:%02d,%03d", sec, ms);
    }

    private String formatTimeMs(int ms) {
        int sec = ms / 1000;
        int milli = ms % 1000;
        return String.format("00:00:%02d,%03d", sec, milli);
    }

    private void animateClick(View view, Runnable action) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(80)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .withEndAction(action)
                        .start())
                .start();
    }
}
