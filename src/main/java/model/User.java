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
    private String score;
    private String authorizationToken;
    private int coins;

    public User(String username) {
        this.id = "12";
        this.coins = 20;
        this.username = username;
        this.cardList = new ArrayList<Card>();
        this.deck = new ArrayList<Card>();
        this.score = "";
        this.authorizationToken = "";
        this.bio = "";
        this.image = "";
    }

    public void addCardPackage(List<Card> cardPackage) throws Exception {
        if (coins < 5) {
            throw new Exception();
        }

        coins -= 5;
        cardList.addAll(cardPackage);
    }

    public void setDeck(List<String> cards) {
        for (String cardID : cards) {
            deck.add(cardList.stream().filter(card -> card.getId().equals(cardID)).findFirst().get());
        }
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
                "authorizationToken", authorizationToken
        ));
    }
}