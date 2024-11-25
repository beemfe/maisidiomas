/*
 *@author: Felipe Bernardes Cisilo
 */
package br.edu.fateczl.maisidiomas.model;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int id;
    private String name;
    private int totalScore;
    private int wordsLearned;
    private List<VocabularyLesson> completedLessons;

    public UserProfile(int id, String name) {
        this.id = id;
        this.name = name;
        this.totalScore = 0;
        this.wordsLearned = 0;
        this.completedLessons = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addScore(int points) {
        if (points >= 0) {
            this.totalScore += points;
        } else {
            throw new IllegalArgumentException("Points must be non-negative");
        }
    }

    public int getWordsLearned() {
        return wordsLearned;
    }

    public void addWordsLearned(int words) {
        if (words >= 0) {
            this.wordsLearned += words;
        } else {
            throw new IllegalArgumentException("Words learned must be non-negative");
        }
    }

    public List<VocabularyLesson> getCompletedLessons() {
        return new ArrayList<>(completedLessons);
    }

    public void addCompletedLesson(VocabularyLesson lesson) {
        if (!completedLessons.contains(lesson)) {
            completedLessons.add(lesson);
            updateProgress(lesson);
        }
    }

    private void updateProgress(VocabularyLesson lesson) {
        totalScore += lesson.getScore();
        wordsLearned += lesson.getWords().size();
    }

    public void resetProgress() {
        totalScore = 0;
        wordsLearned = 0;
        completedLessons.clear();
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalScore=" + totalScore +
                ", wordsLearned=" + wordsLearned +
                ", completedLessons=" + completedLessons.size() +
                '}';
    }
}