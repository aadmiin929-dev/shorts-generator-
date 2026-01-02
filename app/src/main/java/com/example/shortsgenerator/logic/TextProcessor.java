package com.example.shortsgenerator.logic;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor {

    public static VideoPlan generateFromText(String inputText) {

        String[] sentences = inputText.split("\\.");
        List<Scene> scenes = new ArrayList<>();

        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (sentence.isEmpty()) continue;

            Mood mood;
            int duration;

            if (sentence.length() < 30) {
                mood = Mood.FAST;
                duration = 2;
            } else if (sentence.length() < 80) {
                mood = Mood.CALM;
                duration = 3;
            } else {
                mood = Mood.DRAMATIC;
                duration = 4;
            }

            scenes.add(new Scene(sentence, duration, mood));
        }

        return new VideoPlan(scenes);
    }
}
