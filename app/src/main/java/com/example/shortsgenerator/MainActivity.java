package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shortsgenerator.logic.TextProcessor;
import com.example.shortsgenerator.logic.VideoPlan;
import com.example.shortsgenerator.logic.Scene;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);

        String text =
                "Это первое предложение. " +
                "Это второе предложение! " +
                "Это третье предложение.";

        VideoPlan plan = TextProcessor.generateFromText(text);

        StringBuilder out = new StringBuilder();
        for (Scene scene : plan.scenes) {
            out.append(scene.text)
               .append(" [")
               .append(scene.mood)
               .append("]\n");
        }

        tv.setText(out.toString());
        setContentView(tv);
    }
}
