package service;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private Connection connection;

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mctg", "newuser", "password");

        createUserTable();
        createCardTable();
        createTradeTable();
        createDeckTable();
        createCardListTable();
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER");

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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TABLE");

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

    private void createUserTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS USER"
                    + "  (id                 VARCHAR(10) PRIMARY KEY,"
                    + "   username           VARCHAR(100),"
                    + "   passwordHash       VARCHAR(10000),"
                    + "   bio                VARCHAR(10000),"
                    + "   image              VARCHAR(300),"
                    + "   battleCount        INTEGER,"
                    + "   won                INTEGER,"
                    + "   lost               INTEGER,"
                    + "   draw               INTEGER,"
                    + "   coins              INTEGER,"
                    + "   authorizationToken VARCHAR(300))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTradeTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS TRADE"
                    + "  (id                 VARCHAR(10) PRIMARY KEY,"
                    + "   cardToTrade        VARCHAR(10),"
                    + "   type               VARCHAR(10),"
                    + "   minimumDamage      FLOAT(24),"
                    + "   userID             VARCHAR(10))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCardTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS CARD"
                    + "  (id                 VARCHAR(10) PRIMARY KEY,"
                    + "   damage             FLOAT(24),"
                    + "   name               FLOAT(24))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDeckTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS DECK"
                    + "  (cardID             VARCHAR(10),"
                    + "   userID             VARCHAR(10))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCardListTable() {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS CARDLIST"
                    + "  (cardID             VARCHAR(10),"
                    + "   userID             VARCHAR(10))";

            connection.createStatement().execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO USER
                    (id, username, passwordHash, bio, image, battleCount, won, lost, draw, coins, authorizationToken)
                    VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?)    """);

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
                        VALUES(?,?) """);

                statement.setString(1, card.getId());
                statement.setString(2, userID);

                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE USER SET
                    usnerame=?, bio=?, image=?, battleCount=?, won=?, lost=?, draw=? WHERE id=?
                    """);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getBio());
            statement.setString(3, user.getImage());
            statement.setInt(4, user.getBattleCount());
            statement.setInt(6, user.getWon());
            statement.setInt(7, user.getLost());
            statement.setInt(8, user.getDraw());

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
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TABLE WHERE id=?");

            statement.setString(1, tradeID);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
