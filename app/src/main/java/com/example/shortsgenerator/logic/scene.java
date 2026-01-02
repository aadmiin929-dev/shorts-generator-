package com.example.shortsgenerator.logic;

public class Scene {

    public String text;
    public int durationSec;
    public Mood mood;

    public Scene(String text, int durationSec, Mood mood) {
        this.text = text;
        this.durationSec = durationSec;
        this.mood = mood;
    }
}
