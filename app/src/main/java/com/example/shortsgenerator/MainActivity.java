package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.shortsgenerator.logic.Scene;
import com.example.shortsgenerator.logic.TextProcessor;
import com.example.shortsgenerator.logic.VideoPlan;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setTextSize(16f);

        String text =
                "Это первое предложение. " +
                "Это второе предложение. " +
                "Это третье предложение.";

        VideoPlan plan = TextProcessor.generateFromText(text);

        StringBuilder debug = new StringBuilder();
        debug.append("Сцен:\n");

        for (int i = 0; i < plan.scenes.size(); i++) {
            debug.append(i + 1)
                 .append(". ")
                 .append(plan.scenes.get(i).text)
                 .append("\n");
        }

        textView.setText(debug.toString());
        setContentView(textView);
    }
}
