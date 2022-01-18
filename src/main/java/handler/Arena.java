package handler;

import lombok.Getter;
import model.Card;
import model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arena {
    private final UserHandler userHandler;

    public Arena(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Getter
    private List<User> userQueue = new ArrayList<>();

    public List<Integer> startBattle() {
        User player1 = userQueue.get(0);
        User player2 = userQueue.get(1);

        System.out.println("We started some Battle here right now");

        int player1Score = 0, player2Score = 0;
        int roundCounter = 0;

        while ((!player1.getDeck().isEmpty() && !player2.getDeck().isEmpty()) && roundCounter < 100) {
            Card card1 = player1.getFirstCard();
            Card card2 = player2.getFirstCard();

            int result = card1.compareTo(card2);

            switch (result) {
                case 1:
                    player1Score++;
                    System.out.println(player1.getUsername() + " won this Battle");
                    player1.addCardToDeck(card1);
                    player1.addCardToDeck(card2);
                    break;
                case -1:
                    player2Score++;
                    System.out.println(player2.getUsername() + " won this Battle");
                    player2.addCardToDeck(card1);
                    player2.addCardToDeck(card2);
                    break;
            }

            roundCounter++;
        }
        if (player1Score > player2Score) {
            player1.setWon(player1.getWon() + 1);
            player2.setLost(player2.getLost() + 1);
        } else if (player1Score < player2Score) {
            player1.setLost(player1.getLost() + 1);
            player2.setWon(player2.getWon() + 1);
        } else {
            player1.setDraw(player1.getDraw() + 1);
            player2.setDraw(player2.getDraw() + 1);
        }

        player1.setBattleCount(player1.getBattleCount() + 1);
        player2.setBattleCount(player2.getBattleCount() + 1);

        player1.resetDeck();
        player2.resetDeck();

        userHandler.updateUser(player1);
        userHandler.updateUser(player2);

        return Arrays.asList(player1Score, player2Score);
    }

    public void addPlayer(User player) {
        userQueue.add(player);
    }
}