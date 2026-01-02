package com.example.shortsgenerator.logic;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor {

    public static VideoPlan generateFromText(String inputText) {

        String[] sentences = inputText.split("\\.");
        List<Scene> scenes = new ArrayList<>();

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;

            scenes.add(new Scene(sentence, Mood.NEUTRAL));
        }

        return new VideoPlan(scenes);
    }
}
