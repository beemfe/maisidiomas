/*
 * Nome: Felipe Bernardes Cisilo
 * RA: 1110482413017
 */
package br.edu.fateczl.maisidiomas.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import br.edu.fateczl.maisidiomas.R;
import br.edu.fateczl.maisidiomas.controller.AppController;
import br.edu.fateczl.maisidiomas.controller.AuthController;
import br.edu.fateczl.maisidiomas.model.UserProfile;

public class MainActivity extends AppCompatActivity {
    private AppController appController;
    private AuthController authController;
    private UserProfile userProfile;
    private TextView progressTextView;
    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authController = new AuthController(this);
        if (!authController.isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        initializeViews();
        loadUserData();
        updateUI();
        loadLessonFragment();
    }

    private void initializeViews() {
        progressTextView = findViewById(R.id.tvProgress);
        scoreTextView = findViewById(R.id.tvScore);
    }

    private void loadUserData() {
        appController = new AppController(this);
        userProfile = appController.getCurrentUser();

        if (userProfile == null) {
            userProfile = new UserProfile(1, "Usuário");
            appController.saveUser(userProfile);
        }
    }

    private void updateUI() {
        progressTextView.setText(getString(R.string.progress_label, userProfile.getWordsLearned()));
        scoreTextView.setText(getString(R.string.score_label, userProfile.getTotalScore()));
    }

    private void loadLessonFragment() {
        LessonFragment lessonFragment = new LessonFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, lessonFragment);
        transaction.commit();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void updateUserProgress(int wordsLearned, int score) {
        userProfile.addWordsLearned(wordsLearned);
        userProfile.addScore(score);
        appController.saveUser(userProfile);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sair da Lição")
                .setMessage("Deseja voltar para a tela de login?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    authController.logout();
                    redirectToLogin();
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!authController.isUserLoggedIn()) {
            redirectToLogin();
            return;
        }
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        appController.saveUser(userProfile);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authController != null) {
            authController.logout();
        }
    }
}