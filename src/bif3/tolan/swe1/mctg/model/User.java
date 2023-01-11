package bif3.tolan.swe1.mctg.model;

import bif3.tolan.swe1.mctg.constants.DefaultValues;
import bif3.tolan.swe1.mctg.model.jsonViews.CardViews;
import bif3.tolan.swe1.mctg.model.jsonViews.UserViews;
import bif3.tolan.swe1.mctg.utils.UserUtils;
import com.fasterxml.jackson.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

import static bif3.tolan.swe1.mctg.utils.PasswordHashUtils.hashPassword;

/**
 * User model class
 *
 * @author Christopher Tolan
 */
@JsonClassDescription("User")
public class User implements Cloneable {
    @JsonProperty("Username")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.CreateUser.class, UserViews.ReadStatsUser.class, UserViews.ReadBattleUser.class})
    private String username;
    @JsonProperty("Id")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.ReadStatsUser.class, UserViews.ReadBattleUser.class})
    private int id;
    @JsonIgnore
    private String passwordHash;
    @JsonProperty("Elo")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.ReadStatsUser.class})
    private int elo;
    @JsonProperty("Deck")
    @JsonView(CardViews.ReadCard.class)
    private ConcurrentHashMap<String, Card> deck;
    @JsonProperty("Coins")
    @JsonView(UserViews.ReadProfileUser.class)
    private int coins;
    @JsonProperty("GamesPlayed")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.ReadStatsUser.class})
    private int gamesPlayed;
    @JsonProperty("Wins")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.ReadStatsUser.class})
    private int wins;
    @JsonProperty("Bio")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.EditUser.class})
    private String bio;
    @JsonProperty("Image")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.EditUser.class})
    private String image;
    @JsonProperty("Name")
    @JsonView({UserViews.ReadProfileUser.class, UserViews.EditUser.class})
    private String name;
    @JsonProperty("Token")
    @JsonView(UserViews.ReadLoginUser.class)
    private String token;
    @JsonProperty("WinrateInPercent")
    @JsonView(UserViews.ReadStatsUser.class)
    private double winrateInPercent;

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
        this.winrateInPercent = UserUtils.calculateWinPercentage(gamesPlayed, wins);
        this.token = "Basic " + username + "-mtcgToken";
    }

    /**
     * Default Constructor for Jackson
     */
    public User() {
        this.coins = DefaultValues.NEW_USER_COINS;
        this.elo = DefaultValues.ELO;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.name = "";
        this.bio = "";
        this.image = "";
        this.winrateInPercent = 0;
        this.token = "";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

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
        return token;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    @JsonIgnore
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

    public double getWinrateInPercent() {
        return winrateInPercent;
    }

    public void setWinrateInPercent(double winrateInPercent) {
        this.winrateInPercent = winrateInPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id || username.equals(user.username);
    }
}
