import model.Card;
import model.MonsterCard;
import model.SpellCard;
import model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PackageStore packageStore = new PackageStore();
        Arena arena = new Arena();

        List<Card> package1 = Arrays.asList(
                new MonsterCard("845f0dc7", 10.0f, "WaterGoblin"),
                new MonsterCard("99f8f8dc", 50.0f, "Dragon"),
                new SpellCard("e85e3976", 20.0f, "WaterSpell"),
                new SpellCard("1cb6ab86", 45.0f, "Ork"),
                new SpellCard("dfdd758f", 25.0f, "FireSpell")
        );

        List<Card> package2 = Arrays.asList(
                new MonsterCard("644808c2", 9.0f, "WaterGoblin"),
                new MonsterCard("4a2757d6", 55.0f, "Dragon"),
                new SpellCard("91a6471b", 21.0f, "WaterSpell"),
                new SpellCard("4ec8b269", 55.0f, "Ork"),
                new SpellCard("f8043c23", 23.0f, "WaterSpell")
        );

        packageStore.createPackage(package1);
        packageStore.createPackage(package2);

        User user1 = new User("123", "User1");
        User user2 = new User("124", "User2");

        try {
            user1.addCardPackage(packageStore.buyPackage());
            user2.addCardPackage(packageStore.buyPackage());
        } catch (Exception e) {
            //we do not need to catch it really, because this is just to test out the Methods and Fields of the Classes
        }

        List<String> deckUser1 = Arrays.asList("99f8f8dc", "1cb6ab86", "dfdd758f", "845f0dc7", "e85e3976");
        List<String> deckUser2 = Arrays.asList("4ec8b269", "644808c2", "f8043c23", "4a2757d6", "91a6471b");

        user1.setDeck(deckUser1);
        user2.setDeck(deckUser2);

        System.out.println("We are starting the first Battle right now");

        List<Integer> result = arena.startBattle(user1, user2);

        if (result.get(0) < result.get(1)) {
            System.out.println("User2 won this");
        } else if (result.get(0) > result.get(1)) {
            System.out.println("User 1 won this");
        } else {
            System.out.println("Its a draw");
        }
    }
}