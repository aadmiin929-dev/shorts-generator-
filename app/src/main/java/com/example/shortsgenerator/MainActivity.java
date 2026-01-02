package com.example.shortsgenerator;

import com.example.shortsgenerator.logic.TextProcessor;
import com.example.shortsgenerator.logic.VideoPlan;
import com.example.shortsgenerator.logic.Scene;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String text =
            "Это первое предложение. " +
            "Это второе предложение. " +
            "Это третье предложение.";

    VideoPlan plan = TextProcessor.generateFromText(text);

    StringBuilder debug = new StringBuilder();

for (Scene scene : plan.scenes) {
    debug.append(scene.text)
         .append("\nMood: ")
         .append(scene.mood)
         .append(", ")
         .append(scene.durationSec)
         .append("s\n\n");
}

    TextView tv = new TextView(this);
    tv.setText(debug.toString());
    tv.setTextSize(18);
    setContentView(tv);
}
}
