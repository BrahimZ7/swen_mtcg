package service;

import model.*;

import java.sql.*;
import java.util.*;

public class DatabaseService {

    private Connection connection;

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mctg", "newuser", "password");

        createTables();
    }

    public void createTables() {
        createCardTable();
        createTradeTable();
        createDeckTable();
        createCardListTable();
        createUserTable();
        createPackageTable();
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                User user = new User(rs.getString("username"));
                user.setId(rs.getString("id"));
                user.setPasswordHash(rs.getString("passwordHash"));
                user.setAuthorizationToken(rs.getString("authorizationToken"));
                user.setDraw(rs.getInt("draw"));
                user.setLost(rs.getInt("lost"));
                user.setWon(rs.getInt("won"));
                user.setBattleCount(rs.getInt("battleCount"));
                user.setCoins(rs.getInt("coins"));
                List<Card> cardList = getCardListOfUser(user.getId());
                System.out.println("This is the length of the cards of the User " + cardList.size());
                user.setCardDeck(getDeckOfUser(user.getId()));
                user.setCardList(getCardListOfUser(user.getId()));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return users;
        }
    }

    public List<Trade> getTrades() {
        List<Trade> trades = new ArrayList<Trade>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TRADE");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                CardType cardType;

                if (rs.getString("type").equals(CardType.Monster.toString()))
                    cardType = CardType.Monster;
                else
                    cardType = CardType.Spell;

                Trade trade = new Trade(rs.getString("id"), rs.getString("cardID"), cardType, rs.getFloat("minimumDamage"), rs.getString("userID"));
                trades.add(trade);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return trades;
        }
    }

    private List<Card> getDeckOfUser(String userID) {
        List<Card> deck = new ArrayList();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM DECK WHERE userID=?");
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                if (rs.getString("name").contains("Spell")) {
                    deck.add(new SpellCard(rs.getString("id"), rs.getFloat("damage"), rs.getString("name")));
                } else {
                    deck.add(new MonsterCard(rs.getString("id"), rs.getFloat("damage"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return deck;
        }
    }

    private List<Card> getCardListOfUser(String userID) {
        List<Card> cardList = new ArrayList();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM CARDLIST WHERE userID=?");
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();


            while (rs.next()) {
                if (rs.getString("name").contains("Spell")) {
                    cardList.add(new SpellCard(rs.getString("id"), rs.getFloat("damage"), rs.getString("name")));
                } else {
                    cardList.add(new MonsterCard(rs.getString("id"), rs.getFloat("damage"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return cardList;
        }
    }

    public List<List<Card>> getCardPackages() {
        Map<Integer, List<Card>> cardPackage = new HashMap<>();
        List<List<Card>> cardPackages = new ArrayList();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PACKAGES JOIN CARD ON PACKAGES.cardID = CARD.id");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int index = rs.getInt("index");

                if (cardPackage.get(index) == null) {
                    cardPackage.put(index, new ArrayList());
                }

                if (rs.getString("name").contains("Spell")) {
                    cardPackage.get(index).add(new SpellCard(rs.getString("id"), rs.getFloat("damage"), rs.getString("name")));
                } else {
                    cardPackage.get(index).add(new MonsterCard(rs.getString("id"), rs.getFloat("damage"), rs.getString("name")));
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int count = cardPackage.size();
        for (int i = 0; i < count; i++) {
            cardPackages.add(cardPackage.get(i));
        }

        return cardPackages;
    }

    private void createUserTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS USERS ("
                    + " id VARCHAR(1000) PRIMARY KEY,"
                    + " username VARCHAR(1000),"
                    + " passwordHash VARCHAR(10000),"
                    + " bio VARCHAR(10000),"
                    + " image VARCHAR(300),"
                    + " battleCount INTEGER,"
                    + " won INTEGER,"
                    + " lost INTEGER,"
                    + " draw INTEGER,"
                    + " coins INTEGER,"
                    + " authorizationToken VARCHAR(300))";
            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTradeTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS TRADE"
                    + "  (id                 VARCHAR(1000) PRIMARY KEY,"
                    + "   cardToTrade        VARCHAR(1000),"
                    + "   type               VARCHAR(1000),"
                    + "   minimumDamage      FLOAT(24),"
                    + "   userID             VARCHAR(1000))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCardTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS CARD"
                    + "  (id                 VARCHAR(1000) PRIMARY KEY,"
                    + "   damage             FLOAT(24),"
                    + "   name               VARCHAR(1000))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDeckTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS DECK"
                    + "  (cardID             VARCHAR(1000),"
                    + "   userID             VARCHAR(1000))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCardListTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS CARDLIST"
                    + "  (cardID             VARCHAR(1000),"
                    + "   userID             VARCHAR(1000))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPackageTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS PACKAGES"
                    + "  (index             INTEGER,"
                    + "   cardID             VARCHAR(1000))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO USERS
                    (id, username, passwordHash, bio, image, battleCount, won, lost, draw, coins, authorizationToken)
                    VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO NOTHING""");

            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getBio());
            statement.setString(5, user.getImage());
            statement.setInt(6, user.getBattleCount());
            statement.setInt(7, user.getWon());
            statement.setInt(8, user.getLost());
            statement.setInt(9, user.getDraw());
            statement.setInt(10, user.getCoins());
            statement.setString(11, user.getAuthorizationToken());

            statement.execute();

            saveCardList(user.getCardList(), user.getId());
            saveDeck(user.getDeck(), user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveDeck(List<Card> deck, String userID) {
        for (Card card : deck) {
            try {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO DECK
                        (cardID, userID)
                        VALUES(?,?) """);

                statement.setString(1, card.getId());
                statement.setString(2, userID);

                statement.execute();

                saveCard(card);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCardList(List<Card> cardList, String userID) {
        for (Card card : cardList) {
            try {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO CARDLIST
                        (cardID, userID)
                        VALUES(?,?) 
                        ON CONFLICT DO NOTHING""");

                statement.setString(1, card.getId());
                statement.setString(2, userID);

                statement.execute();

                saveCard(card);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCard(Card card) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO CARD
                    (id, damage, name)
                    VALUES(?,?,?)
                    ON CONFLICT DO NOTHING""");

            statement.setString(1, card.getId());
            statement.setFloat(2, card.getDamage());
            statement.setString(3, card.getName().toString());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePackage(List<Card> cardPackage, int index) {
        for (Card card : cardPackage) {
            try {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO PACKAGES
                        (index, cardID)
                        VALUES(?,?) """);


                statement.setInt(1, index);
                statement.setString(2, card.getId());

                statement.execute();

                saveCardList(cardPackage, "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE USERS SET
                    username=?, bio=?, image=?, battleCount=?, won=?, lost=?, draw=? WHERE id=?
                    """);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getBio());
            statement.setString(3, user.getImage());
            statement.setInt(4, user.getBattleCount());
            statement.setInt(5, user.getWon());
            statement.setInt(6, user.getLost());
            statement.setInt(7, user.getDraw());
            statement.setString(8, user.getId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTrade(Trade trade) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO TRADE
                    (id, cardToTrade, type, minimumDamage, userID)
                    VALUES(?,?,?,?,?) """);

            statement.setString(1, trade.getId());
            statement.setString(2, trade.getCardToTrade());
            statement.setString(3, trade.getType().toString());
            statement.setFloat(4, trade.getMinimumDamage());
            statement.setString(5, trade.getUserID());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrade(String tradeID) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TRADE WHERE id=?");

            statement.setString(1, tradeID);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePackage(List<Card> cardPackage) {
        try {
            System.out.println(cardPackage.size());
            System.out.println("We want to delete some cards");
            for (Card card : cardPackage) {

                PreparedStatement statement = connection.prepareStatement("DELETE FROM PACKAGES WHERE cardID=?");
                statement.setString(1, card.getId());
                statement.execute();


                PreparedStatement deleteCard = connection.prepareStatement("DELETE FROM CARD WHERE id=?");
                deleteCard.setString(1, card.getId());
                deleteCard.execute();


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
