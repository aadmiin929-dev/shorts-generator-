package com.example.shortsgenerator.logic;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor {

    public static VideoPlan generateFromText(String inputText) {
        String[] sentences = inputText.split("\\.");
        List<Scene> scenes = new ArrayList<>();

        for (String s : sentences) {
            s = s.trim();
            if (s.isEmpty()) continue;

            scenes.add(new Scene(s, Mood.NEUTRAL));
        }

        return new VideoPlan(scenes);
    }
}
