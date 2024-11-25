/*
 *@author: Felipe Bernardes Cisilo
*/
package br.edu.fateczl.maisidiomas.model;

public class User extends UserProfile {

    private String email;
    private String encryptedPassword;

    public User(int id, String name, String email, String encryptedPassword) {
        super(id, name);
        this.email = email;
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + email + '\'' +
                ", totalScore=" + getTotalScore() +
                ", wordsLearned=" + getWordsLearned() +
                '}';
    }
}