package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.*;
import android.content.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

import com.example.shortsgenerator.logic.VideoPlan;

public class MainActivity extends AppCompatActivity {

    private File lastSrtFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.inputText);
        TextView resultText = findViewById(R.id.resultText);
        Button generateButton = findViewById(R.id.generateButton);
        Button srtButton = findViewById(R.id.srtButton);
        Button shareButton = findViewById(R.id.shareButton);
        Spinner speedSpinner = findViewById(R.id.speedSpinner);
        Spinner styleSpinner = findViewById(R.id.styleSpinner);

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
            resultText.setText(inputText.getText().toString());
        });

        srtButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Нет текста", Toast.LENGTH_SHORT).show();
                return;
            }

            String speed = speedSpinner.getSelectedItem().toString();
            String style = styleSpinner.getSelectedItem().toString();

            VideoPlan plan = new VideoPlan();
            String srt = plan.generateSrt(text, speed, style, this);

            resultText.setText(srt);
            lastSrtFile = plan.getLastFile();
        });

        shareButton.setOnClickListener(v -> {
            if (lastSrtFile == null) {
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
        });
    }
}
