import model.Card;
import model.User;

import java.util.Arrays;
import java.util.List;

public class Arena {

    public List<Integer> startBattle(User player1, User player2) {
        int player1Score = 0, player2Score = 0;
        int roundCounter = 0;

        while ((!player1.getDeck().isEmpty() && !player2.getDeck().isEmpty()) && roundCounter < 100) {
            Card card1 = player1.getFirstCard();
            Card card2 = player2.getFirstCard();

            int result = card1.compareTo(card2);

            System.out.println(String.format("%s uses %s", player1.getUsername(), card1.toString()));
            System.out.println(String.format("%s uses %s", player2.getUsername(), card2.toString()));


            switch (result) {
                case -1:
                    player1Score++;
                    System.out.println(player1.getUsername() + " won this Battle");
                    player1.addCardToDeck(card1);
                    player1.addCardToDeck(card2);
                    break;
                case 1:
                    player2Score++;
                    System.out.println(player2.getUsername() + " won this Battle");
                    player2.addCardToDeck(card1);
                    player2.addCardToDeck(card2);
                    break;
            }

            roundCounter++;
        }

        return Arrays.asList(player1Score, player2Score);
    }
}