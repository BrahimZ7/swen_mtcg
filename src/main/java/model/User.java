package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class User {
    private String id;
    private String username;
    private String passwordHash;
    private String bio;
    private String image;
    private List<Card> deck;
    private List<Card> cardList;
    private int battleCount;
    private int won;
    private int lost;
    private int draw;
    private String authorizationToken;
    private int coins;

    public User(String username) {
        this.coins = 20;
        this.username = username;
        this.cardList = new ArrayList<Card>();
        this.deck = new ArrayList<Card>();
        this.passwordHash = "";
        this.battleCount = 0;
        this.won = 0;
        this.lost = 0;
        this.draw = 0;
        this.authorizationToken = "";
        this.bio = "";
        this.image = "";
    }

    public void addCardPackage(List<Card> cardPackage) {
        coins -= 5;
        cardList.addAll(cardPackage);
    }

    public void resetDeck() {
        for (Card card : deck) {
            cardList.add(card);
        }

        deck.clear();
    }

    public void setDeck(String[] cards) {
        for (String cardID : cards) {
            int index = getIndexOfCardList(cardID);
            deck.add(cardList.remove(index));
        }
    }

    public void removeCardFromList(String cardID) {
        int index = getIndexOfCardList(cardID);
        cardList.remove(index);
    }

    public void addCardToList(Card card) {
        cardList.add(card);
    }

    private int getIndexOfCardList(String cardID) {
        for (int i = 0; i < cardList.size(); i++) {
            if (cardList.get(i).getId().equals(cardID)) return i;
        }
        return -1;
    }

    public Card getFirstCard() {
        return deck.remove(0);
    }

    public void addCardToDeck(Card card) {
        deck.add(card);
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(Map.of(
                "username", username,
                "bio", bio,
                "image", image,
                "battleCount", Integer.toString(battleCount),
                "won", Integer.toString(won),
                "lost", Integer.toString(lost),
                "draw", Integer.toString(draw),
                "authorizationToken", authorizationToken
        ));
    }
}