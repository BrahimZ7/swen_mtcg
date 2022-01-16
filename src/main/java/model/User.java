package model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private String id;
    private String username;
    private String bio;
    private String image;
    private List<Card> deck;
    private List<Card> cardList;
    private String score;
    private int coins;

    public User(String id, String username) {
        this.coins = 20;
        this.username = username;
        cardList = new ArrayList<Card>();
        deck = new ArrayList<Card>();
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
}