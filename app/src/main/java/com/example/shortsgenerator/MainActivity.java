package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.*;
import android.content.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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

        // Скорости
        String[] speeds = {"Медленно", "Нормально", "Быстро"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                speeds
        );
        speedSpinner.setAdapter(adapter);

        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            resultText.setText(text.isEmpty() ? "Введите текст" : text);
        });

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

        srtButton.setOnClickListener(v -> {
            String text = resultText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Нет текста для SRT", Toast.LENGTH_SHORT).show();
                return;
            }

            int speedMultiplier = getSpeed(speedSpinner.getSelectedItemPosition());
            List<String> captions = splitSmart(text);
            StringBuilder srt = new StringBuilder();

            int time = 0;

            for (int i = 0; i < captions.size(); i++) {
                String line = captions.get(i);
                int base = line.length() < 40 ? 2 :
                           line.length() < 80 ? 3 : 4;

                int duration = Math.max(1, base * speedMultiplier);
                int end = time + duration;

                srt.append(i + 1).append("\n");
                srt.append(formatTime(time))
                   .append(" --> ")
                   .append(formatTime(end))
                   .append("\n");
                srt.append(line).append("\n\n");

                time = end;
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

    private int getSpeed(int pos) {
        if (pos == 0) return 2; // медленно
        if (pos == 2) return 1; // быстро
        return 1;              // нормально
    }

    private List<String> splitSmart(String text) {
        List<String> list = new ArrayList<>();
        String[] parts = text.split("(?<=[.!?])\\s+");

        StringBuilder buf = new StringBuilder();
        for (String p : parts) {
            if (buf.length() + p.length() < 80) {
                buf.append(p).append(" ");
            } else {
                list.add(buf.toString().trim());
                buf.setLength(0);
                buf.append(p).append(" ");
            }
        }
        if (buf.length() > 0) list.add(buf.toString().trim());
        return list;
    }

    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("00:%02d:%02d,000", m, s);
    }
}
            
