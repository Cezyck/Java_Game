package edu.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    public static final class Entry {
        public String name;
        public int score;
        public String date;

        public Entry(String name, int score, String date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type listType = new TypeToken<List<Entry>>() {}.getType();

    // Хранение в папке resources/scores проекта
    private static final Path file = Paths.get(System.getProperty("user.home"), "scores.json");

    public static List<Entry> load() {
        try {
            if (Files.exists(file)) {
                try {
                    String json = Files.readString(file, StandardCharsets.UTF_8);
                    List<Entry> list = gson.fromJson(json, listType);
                    if (!list.isEmpty()) {
                        list.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
                        if (list.size() > 10) return new ArrayList<>(list.subList(0, 10));
                    }
                    return list;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
    }

    public static void save(List<Entry> entries) {
        String json = gson.toJson(entries, listType);
        try {
            Files.writeString(file, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(String name, int score){
        List<Entry> list = load();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        list.add(new Entry(name, score, ts));
        list.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
        if(list.size() > 10){
            list = new ArrayList<>(list.subList(0,10));
        }
        try {
            save(list);
        } catch (Exception e) {
            System.out.println("Not Found: " + e.getMessage());;
        }
    }

    public static List<Entry> top(){return new ArrayList<>(load());}

    // Новый метод: проверяет, является ли результат достаточно высоким для таблицы рекордов
    public static boolean isHighScore(int score) {
        List<Entry> currentScores = load();
        if (currentScores.size() < 10) {
            return true; // Если меньше 10 записей, любой результат подходит
        }
        return score > currentScores.get(currentScores.size() - 1).score;
    }
}