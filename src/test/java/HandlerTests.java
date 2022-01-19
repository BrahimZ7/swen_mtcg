import com.fasterxml.jackson.core.JsonProcessingException;
import handler.Arena;
import handler.Marketplace;
import handler.UserHandler;
import model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HandlerTests {
    @Mock
    UserHandler userHandler = new UserHandler(null);
    @Mock
    Marketplace marketplace = new Marketplace(null);

    @Test
    public void testUserCreation() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "testPassword"
        ));

        assertTrue(userHandler.checkIfUsernameExists("TestUser"), "Returns true because the user with this Username exists");
        assertFalse(userHandler.checkIfUsernameExists("OtherUser"), "Return false because the user with this Username does not exist");
    }

    @Test
    public void testUserAuthorizationToken() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "testPassword"
        ));

        assertFalse(userHandler.checkIfUserExists("OtherUser-mtcgToken"), "The Authorization Token doesnt exist");
        assertTrue(userHandler.checkIfUserExists("TestUser-mtcgToken"), "The Authorization Token does exist");
    }

    @Test
    public void testUserLogin() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "testPassword"
        ));

        assertEquals(userHandler.loginUser("TestUser", "password"), null, "Login not successfully and null returned");
        assertEquals(userHandler.loginUser("TestUser", "testPassword"), "TestUser-mtcgToken", "Login successfully and authorization token will be returned");
    }

    @Test
    public void testUserUpdateMetaData() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "TestPassword"
        ));

        userHandler.updateUser(Map.of(
                "Name", "NewUsername",
                "Bio", "This is some Bio of the User",
                "Image", ":D"
        ), "TestUser-mtcgToken");

        User user = userHandler.getUser("TestUser-mtcgToken");

        assertNotEquals(user.getUsername(), "TestUser", "Username not successfully updated");
        assertEquals(user.getUsername(), "NewUsername", "Username successfully updated");
    }

    @Test
    public void testUserUpdateElo() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "TestPassword"
        ));

        User user = userHandler.getUser("TestUser-mtcgToken");

        assertEquals(0, user.getBattleCount());
        assertEquals(0, user.getWon());

        user.setBattleCount(10);
        user.setWon(10);

        userHandler.updateUser(user);

        User newUser = userHandler.getUser("TestUser-mtcgToken");

        assertNotEquals(0, newUser.getBattleCount());
        assertNotEquals(0, newUser.getWon());
    }

    @Test
    public void testUserJsonParse() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "password"
        ));
        User user = userHandler.getUser("TestUser-mtcgToken");
        String json = "";
        try {
            json = user.toJsonString();
            assertTrue(!json.isEmpty());
        } catch (JsonProcessingException e) {
            assertFalse(json.isEmpty());
        }
    }

    @Test
    public void testDuplicateUsername() {
        assertTrue(userHandler.addUser(Map.of(
                "Username", "TestUser",
                "password", "password"
        )), "Creating the first User returns true");

        assertFalse(userHandler.addUser(Map.of(
                "Username", "TestUser",
                "password", "newPassword"
        )), "Creating the seconds User returns false");
    }

    @Test
    public void testPackageAcquiring() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "password", "password"
        ));

        List<Card> cardList = List.of(
                new SpellCard("1", 20, "FireSpell"),
                new SpellCard("2", 30, "WaterSpell"),
                new MonsterCard("3", 10, "Goblin"),
                new MonsterCard("4", 15, "WaterDragon"),
                new SpellCard("5", 20, "NormalSpell")
        );

        marketplace.createPackage(cardList);
        marketplace.createPackage(cardList);
        marketplace.createPackage(cardList);
        marketplace.createPackage(cardList);
        marketplace.createPackage(cardList);
        marketplace.createPackage(cardList);

        User user = userHandler.getUser("TestUser-mtcgToken");

        user.addCardPackage(marketplace.buyPackage());
        user.addCardPackage(marketplace.buyPackage());
        user.addCardPackage(marketplace.buyPackage());

        assertTrue(user.addCardPackage(marketplace.buyPackage()), "User is possible to buy the Package");
        user.addCardPackage(marketplace.buyPackage());
        assertFalse(user.addCardPackage(marketplace.buyPackage()), "User will not be able to buy the Package");
        assertNotEquals(null, user.getCardList(), "User should have Cards");
        assertEquals(null, marketplace.buyPackage(), "No Packages left to buy");
    }

    @Test
    public void testDeckConfiguration() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "password", "password"
        ));

        List<Card> cardList = List.of(
                new SpellCard("1", 20, "FireSpell"),
                new SpellCard("2", 30, "WaterSpell"),
                new MonsterCard("3", 10, "Goblin"),
                new MonsterCard("4", 15, "WaterDragon"),
                new SpellCard("5", 20, "NormalSpell"),
                new SpellCard("6", 20, "FireSpell"),
                new SpellCard("7", 30, "WaterSpell"),
                new MonsterCard("8", 10, "Goblin"),
                new MonsterCard("9", 15, "WaterDragon"),
                new SpellCard("10", 20, "NormalSpell")
        );

        List<Card> deck = List.of(
                new SpellCard("2", 30, "WaterSpell"),
                new SpellCard("5", 20, "NormalSpell"),
                new MonsterCard("8", 10, "Goblin"),
                new MonsterCard("9", 15, "WaterDragon"),
                new SpellCard("10", 20, "NormalSpell")
        );

        User user = userHandler.getUser("TestUser-mtcgToken");

        user.setCardList(cardList);

        assertTrue(user.getDeck().isEmpty());

        user.setCardDeck(deck);
        assertFalse(user.getDeck().isEmpty());

    }

    @Test
    public void testTradeCreation() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "password"
        ));

        User user = userHandler.getUser("TestUser-mtcgToken");

        Trade trade = new Trade("100", "1", CardType.Spell, 10, user.getId());

        marketplace.addTrade(trade);

        assertEquals(null, marketplace.getTrade("101"), "There is no Trade with the ID 101");
        assertEquals(trade, marketplace.getTrade("100"), "There is a Trade with the ID 100");
    }

    @Test
    public void testTradeDeletion() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "password"
        ));

        User user = userHandler.getUser("TestUser-mtcgToken");

        Trade trade = new Trade("100", "1", CardType.Spell, 10, user.getId());

        marketplace.addTrade(trade);

        assertEquals(trade, marketplace.getTrade("100"));
        assertTrue(marketplace.deleteTrade(trade.getId()));
        assertFalse(marketplace.deleteTrade(trade.getId()));
    }

    @Test
    public void testBattle() {
        userHandler.addUser(Map.of(
                "Username", "TestUser",
                "Password", "password"
        ));

        userHandler.addUser(Map.of(
                "Username", "OtherTest",
                "Password", "password"
        ));

        List<Card> cardList = new ArrayList();

        cardList.add(new SpellCard("1", 20, "FireSpell"));
        cardList.add(new SpellCard("2", 30, "WaterSpell"));
        cardList.add(new MonsterCard("3", 10, "FireGoblin"));
        cardList.add(new MonsterCard("4", 15, "WaterDragon"));
        cardList.add(new SpellCard("5", 20, "NormalSpell"));
        cardList.add(new SpellCard("6", 20, "FireSpell"));
        cardList.add(new SpellCard("7", 30, "WaterSpell"));
        cardList.add(new MonsterCard("8", 10, "FireGoblin"));
        cardList.add(new MonsterCard("9", 15, "WaterDragon"));
        cardList.add(new SpellCard("10", 20, "WaterSpell"));

        List<Card> deck1 = new ArrayList();

        deck1.add(new SpellCard("2", 30, "WaterSpell"));
        deck1.add(new MonsterCard("4", 15, "WaterDragon"));
        deck1.add(new SpellCard("7", 30, "WaterSpell"));
        deck1.add(new MonsterCard("9", 15, "WaterDragon"));
        deck1.add(new SpellCard("10", 20, "WaterSpell"));

        List<Card> deck2 = new ArrayList();

        deck2.add(new SpellCard("1", 20, "FireSpell"));
        deck2.add(new MonsterCard("3", 10, "FireGoblin"));
        deck2.add(new SpellCard("6", 20, "FireSpell"));
        deck2.add(new SpellCard("5", 20, "NormalSpell"));
        deck2.add(new MonsterCard("8", 10, "FireGoblin"));

        User user1 = userHandler.getUser("TestUser-mtcgToken");

        user1.setCardList(cardList);
        user1.setCardDeck(deck1);

        User user2 = userHandler.getUser("OtherTest-mtcgToken");

        user2.setCardList(cardList);
        user2.setCardDeck(deck2);

        Arena arena = new Arena(this.userHandler);

        arena.addPlayer(user1);
        arena.addPlayer(user2);

        arena.startBattle();

        user1 = userHandler.getUser("TestUser-mtcgToken");
        user2 = userHandler.getUser("OtherTest-mtcgToken");

        assertEquals(1, user1.getBattleCount());
        assertEquals(1, user1.getWon());
        assertEquals(1, user2.getBattleCount());
        assertEquals(1, user2.getLost());
    }

    @Test
    public void testScoreBoard() {
        testBattle();

        assertFalse(userHandler.getScoreboard().isEmpty());
    }
}
