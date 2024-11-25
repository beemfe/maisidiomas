/*
 *@author: Felipe Bernardes Cisilo
 */
package br.edu.fateczl.maisidiomas.model;

import br.edu.fateczl.maisidiomas.utils.Scorable;
import java.util.ArrayList;
import java.util.List;

public class VocabularyLesson extends Lesson implements Scorable {
    private List<String> words;
    private List<String> translations;
    private int score;
    private boolean isCompleted;

    public VocabularyLesson(int id, String title, List<String> words, List<String> translations) {
        super(id, title);
        this.words = new ArrayList<>(words);
        this.translations = new ArrayList<>(translations);
        this.score = 0;
        this.isCompleted = false;
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public List<String> getTranslations() {
        return new ArrayList<>(translations);
    }

    public int getScore() {
        return score;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public void start() {
        System.out.println("Starting vocabulary lesson: " + getTitle());
    }

    @Override
    public void finish() {
        setCompleted(true);
        System.out.println("Finishing vocabulary lesson: " + getTitle());
    }

    @Override
    public int calculateScore() {
        return score;
    }

    @Override
    public void addScore(int points) {
        if (points > 0) {
            this.score += points;
        } else {
            throw new IllegalArgumentException("Points must be positive");
        }
    }

    @Override
    public void resetScore() {
        this.score = 0;
    }

    public boolean checkAnswer(String word, String translation) {
        int index = words.indexOf(word);
        if (index != -1 && index < translations.size()) {
            return translations.get(index).equalsIgnoreCase(translation);
        }
        return false;
    }

    @Override
    public String toString() {
        return "VocabularyLesson{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", words=" + words +
                ", translations=" + translations +
                ", score=" + score +
                ", isCompleted=" + isCompleted +
                '}';
    }
}