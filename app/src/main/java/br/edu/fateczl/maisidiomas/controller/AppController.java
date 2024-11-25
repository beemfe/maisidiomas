/*
 *@author: Felipe Bernardes Cisilo
*/
package br.edu.fateczl.maisidiomas.controller;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import br.edu.fateczl.maisidiomas.database.DatabaseHelper;
import br.edu.fateczl.maisidiomas.model.UserProfile;
import br.edu.fateczl.maisidiomas.model.VocabularyLesson;

public class AppController {
    private Context context;
    private DatabaseHelper dbHelper;
    private UserProfile currentUser;
    private List<VocabularyLesson> lessons;

    public AppController(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.lessons = new ArrayList<>();
        initializeLessons();
        loadCurrentUser();
    }

    private void initializeLessons() {
        List<String> coresWords = Arrays.asList("Red", "Blue", "Green", "Yellow", "Black", "White", "Purple", "Orange", "Pink", "Brown");
        List<String> coresTranslations = Arrays.asList("Vermelho", "Azul", "Verde", "Amarelo", "Preto", "Branco", "Roxo", "Laranja", "Rosa", "Marrom");
        lessons.add(new VocabularyLesson(1, "Cores", coresWords, coresTranslations));

        List<String> animaisWords = Arrays.asList("Dog", "Cat", "Bird", "Fish", "Horse", "Rabbit", "Lion", "Tiger", "Elephant", "Monkey");
        List<String> animaisTranslations = Arrays.asList("Cachorro", "Gato", "Pássaro", "Peixe", "Cavalo", "Coelho", "Leão", "Tigre", "Elefante", "Macaco");
        lessons.add(new VocabularyLesson(2, "Animais", animaisWords, animaisTranslations));
    }

    private void loadCurrentUser() {
        currentUser = dbHelper.getUser();
        if (currentUser == null) {
            currentUser = new UserProfile(1, "Usuário");
            saveUser(currentUser);
        }
    }

    public UserProfile getCurrentUser() {
        return currentUser;
    }

    public void saveUser(UserProfile user) {
        dbHelper.saveUser(user);
        this.currentUser = user;
    }

    public VocabularyLesson getNextLesson() {
        for (VocabularyLesson lesson : lessons) {
            if (!lesson.isCompleted()) {
                return lesson;
            }
        }
        return null;
    }

    public List<String> getRandomOptions(VocabularyLesson lesson, String word) {
        List<String> options = new ArrayList<>();
        List<String> words = lesson.getWords();
        List<String> translations = lesson.getTranslations();

        int wordIndex = words.indexOf(word);
        if (wordIndex >= 0) {
            options.add(translations.get(wordIndex));
        }

        List<String> availableTranslations = new ArrayList<>(translations);
        if (wordIndex >= 0) {
            availableTranslations.remove(wordIndex);
        }

        Random random = new Random();
        while (options.size() < 3 && !availableTranslations.isEmpty()) {
            int index = random.nextInt(availableTranslations.size());
            options.add(availableTranslations.remove(index));
        }

        for (int i = options.size() - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = options.get(index);
            options.set(index, options.get(i));
            options.set(i, temp);
        }

        return options;
    }

    public void updateLessonProgress(VocabularyLesson lesson) {
        lesson.setCompleted(true);
        dbHelper.updateLessonProgress(lesson);
        currentUser.addCompletedLesson(lesson);
        saveUser(currentUser);
    }

    public List<VocabularyLesson> getAllLessons() {
        return new ArrayList<>(lessons);
    }

    public void resetProgress() {
        currentUser.resetProgress();
        for (VocabularyLesson lesson : lessons) {
            lesson.setCompleted(false);
        }
        dbHelper.resetProgress();
        saveUser(currentUser);
    }
}