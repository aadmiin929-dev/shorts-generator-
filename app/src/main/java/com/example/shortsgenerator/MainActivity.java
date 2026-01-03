package com.example.shortsgenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Button copyButton = findViewById(R.id.copyButton);
        Button srtButton = findViewById(R.id.srtButton);
        
        generateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();

            if (text.isEmpty()) {
                resultText.setText("Введите текст");
            } else {
                resultText.setText("Сценарий:\n" + text);
            }
        });
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
    }
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
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(srt.toString().getBytes());
        fos.close();

        Toast.makeText(this,
                "SRT сохранён: " + file.getAbsolutePath(),
                Toast.LENGTH_LONG).show();

    } catch (IOException e) {
        Toast.makeText(this, "Ошибка сохранения SRT", Toast.LENGTH_SHORT).show();
    }
}); 
    
}
