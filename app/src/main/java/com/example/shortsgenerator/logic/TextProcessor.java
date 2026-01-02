package com.example.shortsgenerator.logic;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor {

    public static VideoPlan generateFromText(String inputText) {

        String[] sentences = inputText.split("\\.");

        List<Scene> scenes = new ArrayList<>();

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                scenes.add(new Scene(sentence, 3)); // 3 секунды на сцену
            }
        }

        return new VideoPlan(scenes);
    }
}
