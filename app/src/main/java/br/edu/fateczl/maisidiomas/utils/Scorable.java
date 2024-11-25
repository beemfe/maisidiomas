/*
 *@author: Felipe Bernardes Cisilo
 */
package br.edu.fateczl.maisidiomas.utils;

public interface Scorable {
    int calculateScore();
    void addScore(int points);
    int getScore();
    void resetScore();
}