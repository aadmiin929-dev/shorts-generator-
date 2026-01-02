package com.example.shortsgenerator;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setTextSize(16f);
        textView.setText(
                "Это первое предложение.\n" +
                "Это второе предложение.\n" +
                "Это третье предложение."
        );

        setContentView(textView);
    }
}
