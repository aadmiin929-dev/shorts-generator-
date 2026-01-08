      package com.example.shortsgenerator;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.*;
import android.content.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

import com.example.shortsgenerator.logic.VideoPlan;

public class MainActivity extends AppCompatActivity {

    private VideoPlan videoPlan;   // ✅ ОДИН экземпляр
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

        videoPlan = new VideoPlan(); // ✅ создаём ОДИН РАЗ

        // speed spinner
        String[] speeds = {"Медленно", "Нормально", "Быстро"};
        speedSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                speeds
        ));

        // style spinner
        String[] styles = {"Классика", "Агрессивный", "Минимал", "TikTok PRO"};
        styleSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                styles
        ));

        generateButton.setOnClickListener(v -> {

    String text = inputText.getText().toString().trim();
    String speed = speedSpinner.getSelectedItem().toString();
    String style = styleSpinner.getSelectedItem().toString();

    if (text.isEmpty()) {
        resultText.setText("❗ Вставь текст");
        return;
    }

    String result =
            "🎬 Стиль: " + style + "\n" +
            "⏱ Скорость: " + speed + "\n\n" +
            text;

    resultText.setText(result);
});
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
}  
