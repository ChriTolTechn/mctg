package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.concurrent.ConcurrentHashMap;

import static bif3.tolan.swe1.mcg.utils.PasswordHashUtils.hashPassword;

/**
 * Base class for user
 *
 * @author Christopher Tolan
 */
public class User implements Cloneable {
    @JsonProperty("Username")
    private String username;
    @JsonIgnore
    private int id;
    @JsonIgnore
    private String passwordHash;
    @JsonIgnore
    private int elo;
    @JsonIgnore
    private ConcurrentHashMap<String, Card> deck;
    @JsonIgnore
    private int coins;
    @JsonIgnore
    private int gamesPlayed;
    @JsonIgnore
    private int wins;
    @JsonProperty("Bio")
    private String bio;
    @JsonProperty("Image")
    private String image;
    @JsonProperty("Name")
    private String name;

    public User(String username, String passwordHash, int elo, int coins, int gamesPlayed, int id, int wins, String name, String bio, String image) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.elo = elo;
        this.coins = coins;
        this.gamesPlayed = gamesPlayed;
        this.id = id;
        this.wins = wins;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    // Default Constructor for Jackson
    public User() {
        this.coins = DefaultValues.NEW_USER_COINS;
        this.elo = DefaultValues.ELO;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.name = "";
        this.bio = "";
        this.image = "";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // Getter and setter
    public ConcurrentHashMap<String, Card> getDeck() {
        return deck;
    }

    public void setDeck(ConcurrentHashMap<String, Card> deck) {
        this.deck = deck;
    }

    public String getUsername() {
        return username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getId() {
        return id;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public String getToken() {
        return "Basic " + this.username + "-mtcgToken";
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @JsonSetter("Password")
    private void setPasswordHash(String password) {
        this.passwordHash = hashPassword(password);
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id || username.equals(user.username);
    }
}
