/*
 *@author: Felipe Bernardes Cisilo
 */
package br.edu.fateczl.maisidiomas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.edu.fateczl.maisidiomas.model.User;
import br.edu.fateczl.maisidiomas.model.UserProfile;
import br.edu.fateczl.maisidiomas.model.VocabularyLesson;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "maisidiomas.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_SCORE = "total_score";
    private static final String COLUMN_USER_WORDS_LEARNED = "words_learned";

    private static final String TABLE_LESSON = "lesson";
    private static final String COLUMN_LESSON_ID = "id";
    private static final String COLUMN_LESSON_TITLE = "title";
    private static final String COLUMN_LESSON_COMPLETED = "completed";
    private static final String COLUMN_LESSON_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_SCORE + " INTEGER,"
                + COLUMN_USER_WORDS_LEARNED + " INTEGER" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_LESSON_TABLE = "CREATE TABLE " + TABLE_LESSON + "("
                + COLUMN_LESSON_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LESSON_TITLE + " TEXT,"
                + COLUMN_LESSON_COMPLETED + " INTEGER,"
                + COLUMN_LESSON_SCORE + " INTEGER" + ")";
        db.execSQL(CREATE_LESSON_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSON);
        onCreate(db);
    }

    public long saveNewUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getEncryptedPassword()); // Use encrypted password
        values.put(COLUMN_USER_SCORE, user.getTotalScore());
        values.put(COLUMN_USER_WORDS_LEARNED, user.getWordsLearned());
        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    public void saveUser(UserProfile user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_SCORE, user.getTotalScore());
        values.put(COLUMN_USER_WORDS_LEARNED, user.getWordsLearned());
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_NAME);
                int emailIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL);
                int passwordIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD);
                int scoreIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_SCORE);
                int wordsLearnedIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_WORDS_LEARNED);

                user = new User(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(emailIndex),
                        cursor.getString(passwordIndex)
                );

                user.addScore(cursor.getInt(scoreIndex));
                user.addWordsLearned(cursor.getInt(wordsLearnedIndex));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        db.close();
        return user;
    }

    public UserProfile getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, null, null, null, null, null);
        UserProfile user = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_NAME);
                int scoreIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_SCORE);
                int wordsLearnedIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_WORDS_LEARNED);

                user = new UserProfile(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex)
                );

                user.addScore(cursor.getInt(scoreIndex));
                user.addWordsLearned(cursor.getInt(wordsLearnedIndex));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        db.close();
        return user;
    }

    public void updateLessonProgress(VocabularyLesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_LESSON_TITLE, lesson.getTitle());
        values.put(COLUMN_LESSON_COMPLETED, lesson.isCompleted() ? 1 : 0);
        values.put(COLUMN_LESSON_SCORE, lesson.getScore());

        db.insertWithOnConflict(TABLE_LESSON, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public void resetProgress() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_LESSON, null, null);

        db.close();
    }

    public int generateNewUserId() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_USER_ID + ") FROM " + TABLE_USER, null);

        int id = 0;

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0) + 1;
            cursor.close();
        }

        db.close();

        return id;
    }
}