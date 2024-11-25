/*
 *@author: Felipe Bernardes Cisilo
 */
package br.edu.fateczl.maisidiomas.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import br.edu.fateczl.maisidiomas.R;
import br.edu.fateczl.maisidiomas.controller.AppController;
import br.edu.fateczl.maisidiomas.model.VocabularyLesson;
import java.util.List;
import java.util.Random;

public class LessonFragment extends Fragment {
    private VocabularyLesson currentLesson;
    private TextView wordTextView;
    private ImageView wordImageView;
    private Button option1Button, option2Button, option3Button;
    private int currentWordIndex = 0;
    private int score = 0;
    private AppController appController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);

        wordTextView = view.findViewById(R.id.tvWord);
        option1Button = view.findViewById(R.id.btnOption1);
        option2Button = view.findViewById(R.id.btnOption2);
        option3Button = view.findViewById(R.id.btnOption3);

        appController = new AppController(getContext());
        currentLesson = appController.getNextLesson();

        setupButtonListeners();
        loadNextWord();

        return view;
    }

    private void setupButtonListeners() {
        option1Button.setOnClickListener(v -> checkAnswer(option1Button.getText().toString()));
        option2Button.setOnClickListener(v -> checkAnswer(option2Button.getText().toString()));
        option3Button.setOnClickListener(v -> checkAnswer(option3Button.getText().toString()));
    }

    private void loadNextWord() {
        if (currentWordIndex < currentLesson.getWords().size()) {
            String word = currentLesson.getWords().get(currentWordIndex);
            wordTextView.setText(word);
            setRandomOptions(word);
        } else {
            showCompletionDialog();
        }
    }

    private void setRandomOptions(String correctWord) {
        List<String> options = appController.getRandomOptions(currentLesson, correctWord);
        shuffleOptions(options);
        option1Button.setText(options.get(0));
        option2Button.setText(options.get(1));
        option3Button.setText(options.get(2));
    }

    private void shuffleOptions(List<String> options) {
        Random random = new Random();
        for (int i = options.size() - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = options.get(index);
            options.set(index, options.get(i));
            options.set(i, temp);
        }
    }

    private void checkAnswer(String selectedAnswer) {
        String correctAnswer = currentLesson.getTranslations().get(currentWordIndex);
        if (selectedAnswer.equals(correctAnswer)) {
            score += 10;
            Toast.makeText(getContext(), R.string.correct_answer, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.incorrect_answer, correctAnswer), Toast.LENGTH_SHORT).show();
        }
        currentWordIndex++;
        loadNextWord();
    }

    private void showCompletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lição Concluída!")
                .setMessage("Parabéns! Você completou a lição com " + score + " pontos!\nDeseja continuar?")
                .setCancelable(false)
                .setPositiveButton("Continuar", (dialog, which) -> {
                    resetLesson();
                })
                .setNegativeButton("Sair", (dialog, which) -> {
                    saveProgress();
                    getActivity().finish();
                });
        builder.show();
    }

    private void resetLesson() {
        saveProgress();
        currentWordIndex = 0;
        score = 0;
        loadNextWord();
    }

    private void saveProgress() {
        currentLesson.setCompleted(true);
        currentLesson.addScore(score);
        ((MainActivity) getActivity()).updateUserProgress(currentLesson.getWords().size(), score);
    }
}