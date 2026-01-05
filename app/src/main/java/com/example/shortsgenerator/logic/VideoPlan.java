package com.example.shortsgenerator.logic;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class VideoPlan {

    private List<Scene> scenes;
    private File lastFile;

    // 🔹 ПУСТОЙ конструктор (оставляем)
    public VideoPlan() {
    }

    // 🔹 КОНСТРУКТОР С Scene
    public VideoPlan(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public String generateSrt(String text, String speed, String style, Context context) {
        // твоя логика генерации SRT
        return "";
    }

    public File getLastFile() {
        return lastFile;
    }
}
public class VideoPlan {

    private File lastFile;

    public File getLastFile() {
        return lastFile;
    }

    public String generateSrt(
            String text,
            String speed,
            String style,
            Context context
    ) {

        // --------------------
        // SPEED SETTINGS
        // --------------------
        int duration;
        int wordsPerLine;

        switch (speed) {
            case "Быстро":
                duration = 1;
                wordsPerLine = 2;
                break;
            case "Медленно":
                duration = 3;
                wordsPerLine = 3;
                break;
            default:
                duration = 2;
                wordsPerLine = 3;
        }

        // --------------------
        // STYLE SETTINGS
        // --------------------
        boolean forceCaps = false;
        boolean enableEmoji = true;
        int maxWordsOverride = -1;

        switch (style) {
            case "Агрессивный":
                forceCaps = true;
                maxWordsOverride = 2;
                break;

            case "Минимал":
                enableEmoji = false;
                maxWordsOverride = 2;
                break;

            case "TikTok PRO":
                forceCaps = true;
                maxWordsOverride = 1;
                break;
        }

        int effectiveWords =
                maxWordsOverride > 0 ? maxWordsOverride : wordsPerLine;

        // --------------------
        // BUILD SRT
        // --------------------
        String[] words = text.split("\\s+");
        StringBuilder srt = new StringBuilder();

        int index = 1;
        int startSec = 0;

        // HOOK
        String hook = buildHook(text);
        srt.append(index++).append("\n");
        srt.append("00:00:00,000 --> 00:00:03,000\n");
        srt.append(hook).append("\n\n");
        startSec = 3;

        for (int i = 0; i < words.length; i += effectiveWords) {

            StringBuilder line = new StringBuilder();
            for (int j = i; j < i + effectiveWords && j < words.length; j++) {
                line.append(words[j]).append(" ");
            }

            String resultLine = line.toString().trim();
            if (forceCaps) resultLine = resultLine.toUpperCase();

            String emoji = enableEmoji ? detectEmoji(resultLine) : "";

            int extra = 0;
            if (resultLine.length() > 12) extra++;
            if (resultLine.equals(resultLine.toUpperCase())) extra++;
            extra += detectPauseBonus(resultLine);

            int endSec = startSec + Math.max(1, duration + extra);

            srt.append(index++).append("\n");
            srt.append(formatTime(startSec))
                    .append(" --> ")
                    .append(formatTime(endSec))
                    .append("\n");
            srt.append(resultLine).append(emoji).append("\n\n");

            startSec = endSec;
        }

        // --------------------
        // SAVE FILE
        // --------------------
        try {
            String fileName;
            switch (speed) {
                case "Быстро": fileName = "tiktok.srt"; break;
                case "Медленно": fileName = "reels.srt"; break;
                default: fileName = "shorts.srt";
            }

            File file = new File(context.getExternalFilesDir(null), fileName);
            lastFile = file;

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(srt.toString().getBytes());
            fos.close();

        } catch (IOException e) {
            return "Ошибка сохранения SRT";
        }

        return srt.toString();
    }

    // --------------------
    // HELPERS
    // --------------------

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("00:%02d:%02d,000", min, sec);
    }

    private String buildHook(String text) {
        String[] words = text.split("\\s+");
        StringBuilder hook = new StringBuilder();

        for (int i = 0; i < Math.min(6, words.length); i++) {
            hook.append(words[i]).append(" ");
        }

        return "⚡ " + hook.toString().trim().toUpperCase() + "!";
    }

    private int detectPauseBonus(String line) {
        String l = line.toLowerCase();

        if (l.contains("?")) return 2;
        if (l.contains("!")) return 1;
        if (l.contains("...")) return 2;

        if (l.contains("почему") ||
            l.contains("как") ||
            l.contains("но") ||
            l.contains("если")) {
            return 1;
        }

        return 0;
    }

    private String detectEmoji(String line) {
        String l = line.toLowerCase();

        if (l.contains("?") || l.contains("почему") || l.contains("как")) {
            return " 🤔";
        }

        if (l.contains("внимание") || l.contains("опасно") || l.contains("ошибка")) {
            return " ⚠️";
        }

        if (l.contains("секрет") || l.contains("узнай") || l.contains("идея")) {
            return " 💡";
        }

        if (l.contains("никогда") || l.contains("шок") || l.contains("страшно")) {
            return " 😱";
        }

        if (l.contains("успех") || l.contains("получилось") || l.contains("работает")) {
            return " 🔥";
        }

        return "";
    }
}

