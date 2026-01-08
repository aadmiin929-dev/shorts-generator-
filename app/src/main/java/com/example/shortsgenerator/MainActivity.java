package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.*;
import android.content.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

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
                new String[]{"Классика", "Агрессивный", "Минимал", "TikTok PRO"}
        );
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(styleAdapter);

        // Generate preview
        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();

            if (text.isEmpty()) {
                resultText.setText("❗ Вставь текст");
                return;
            }

            String result =
                    "🎬 Стиль: " + styleSpinner.getSelectedItem() + "\n" +
                    "⏱ Скорость: " + speedSpinner.getSelectedItem() + "\n\n" +
                    text;

            resultText.setText(result);
        });

        // Create SRT
        srtButton.setOnClickListener(v -> {
    String text = inputText.getText().toString().trim();
    String style = styleSpinner.getSelectedItem().toString();

    if (text.isEmpty()) {
        Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
        return;
    }

    if (style.equals("TikTok PRO")) {
        lastSrtFile = generateTikTokSrt(text);
    } else {
        lastSrtFile = generateSrt(text); // обычный SRT
    }

    if (lastSrtFile != null) {
        Toast.makeText(this, "SRT создан", Toast.LENGTH_SHORT).show();
    }
});

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
        });
    }

    // ===== SRT LOGIC =====

    private File generateSrt(String text) {
        try {
            File file = new File(getFilesDir(), "subtitles.srt");
            StringBuilder srt = new StringBuilder();

            String[] lines = text.split("[.!?]\\s*");
            int time = 0;

            for (int i = 0; i < lines.length; i++) {
                srt.append(i + 1).append("\n");
                srt.append(formatTime(time))
                        .append(" --> ")
                        .append(formatTime(time + 2))
                        .append("\n");
                srt.append(lines[i].trim()).append("\n\n");
                time += 2;
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
private File generateTikTokSrt(String text) {
    try {
        File file = new File(getFilesDir(), "subtitles_tiktok.srt");
        StringBuilder srt = new StringBuilder();

        String[] words = text.split("\\s+");
        int timeMs = 0;
        int index = 1;

        for (String word : words) {
            if (word.trim().isEmpty()) continue;

            srt.append(index).append("\n");
            srt.append(formatTimeMs(timeMs))
               .append(" --> ")
               .append(formatTimeMs(timeMs + 500))
               .append("\n");
            srt.append(word.toUpperCase()).append("\n\n");

            timeMs += 500;
            index++;
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
private String formatTimeMs(int ms) {
    int seconds = ms / 1000;
    int millis = ms % 1000;
    return String.format("00:00:%02d,%03d", seconds, millis);
}


    private String formatTime(int seconds) {
        return String.format("00:00:%02d,000", seconds);
    }
}
