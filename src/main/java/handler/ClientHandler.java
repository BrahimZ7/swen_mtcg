package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Arena arena;
    private final UserHandler userHandler;
    private final Marketplace marketplace;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private Boolean activeConnection = true;


    public ClientHandler(Socket socket, UserHandler userHandler, Marketplace marketplace, Arena arena) throws IOException {
        this.userHandler = userHandler;
        this.marketplace = marketplace;
        this.socket = socket;
        this.arena = arena;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            String line;
            line = bufferedReader.readLine();

            StringBuilder header = new StringBuilder();
            header.append("" + line);

            int contentLength = 0;

            boolean isPost = line.startsWith("POST") || line.startsWith("PUT");
            while (!(line = bufferedReader.readLine()).equals("")) {
                header.append("\n" + line);
                if (isPost) {
                    final String contentHeader = "Content-Length: ";
                    if (line.startsWith(contentHeader)) {
                        contentLength = Integer.parseInt(line.substring(contentHeader.length()));
                    }
                }
            }

            StringBuilder body = new StringBuilder();
            if (isPost) {
                int c = 0;
                for (int i = 0; i < contentLength; i++) {
                    c = bufferedReader.read();
                    body.append((char) c);
                }
            }

            HTTPModel httpModel = new HTTPModel(header.toString(), body.toString());

            handleRequest(httpModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(HTTPModel httpRequest) throws IOException {

        switch (httpRequest.getPath().split("/")[1]) {
            case "users":
                handleUserRoute(httpRequest);
                break;
            case "sessions":
                handleSessionRoute(httpRequest);
                break;
            case "packages":
                handlePackagesRoute(httpRequest);
                break;
            case "transactions":
                handleTransactionsRoute(httpRequest);
                break;
            case "cards":
                handleCardsRoute(httpRequest);
                break;
            case "deck":
                handleDeckRoute(httpRequest);
                break;
            case "stats":
                handleStatsRoute(httpRequest);
                break;
            case "score":
                handleScoreRoute(httpRequest);
                break;
            case "battles":
                handleBattlesRoute(httpRequest);
                break;
            case "tradings":
                handleTradingsRoute(httpRequest);
                break;
            default:
                sendResponse("404", mapper.writeValueAsString(Map.of(
                        "value", "Route not found"
                )));
                break;
        }
    }

    private void handleUserRoute(HTTPModel httpRequest) throws IOException {
        try {
            Map<String, String> userData = new HashMap<>();
            if (httpRequest.getBody() != null && !httpRequest.getBody().isEmpty())
                userData = mapper.readValue(httpRequest.getBody(), Map.class);
            switch (httpRequest.getRequestMethod()) {
                case "POST":
                    if (userData.isEmpty())
                        sendResponse("400", mapper.writeValueAsString(Map.of("value", "Malformed Request")));

                    if (userHandler.checkIfUsernameExists(userData.get("Username"))) {
                        sendResponse("409", mapper.writeValueAsString(Map.of("value", "A User with this username already exists.")));
                    } else {
                        userHandler.addUser(mapper.readValue(httpRequest.getBody(), Map.class));
                        sendResponse("200", mapper.writeValueAsString(Map.of("value", "User added successfully")));
                    }
                    break;
                case "GET":
                    if (httpRequest.getAuthorization().isEmpty()) {
                        sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
                        return;
                    }
                    if (!userHandler.checkIfUserExists(httpRequest.getAuthorization())) {
                        sendResponse("404", mapper.writeValueAsString(Map.of("value", "User could not be found")));
                        return;
                    }

                    User user = userHandler.getUser(httpRequest.getAuthorization());

                    if (!user.getAuthorizationToken().split("-")[0].equals(httpRequest.getPath().split("/")[2])) {
                        sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
                        return;
                    }
                    sendResponse("200", user.toJsonString());

                    break;
                case "PUT":
                    if (httpRequest.getAuthorization().isEmpty()) {
                        sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
                        return;
                    }
                    if (!userHandler.checkIfUserExists(httpRequest.getAuthorization())) {
                        sendResponse("404", mapper.writeValueAsString(Map.of("value", "User could not be found")));
                        return;
                    }

                    user = userHandler.getUser(httpRequest.getAuthorization());
                    System.out.println(user.getAuthorizationToken().split("-")[0]);
                    if (!user.getAuthorizationToken().split("-")[0].equals(httpRequest.getPath().split("/")[2])) {
                        sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
                        return;
                    }

                    userHandler.updateUser(userData, httpRequest.getAuthorization());
                    sendResponse("200", mapper.writeValueAsString(Map.of("value", "User updated successfully")));
                    break;
                default:
                    sendResponse("404", mapper.writeValueAsString(Map.of("value", "Route not found")));
                    break;
            }
        } catch (Exception e) {
            sendResponse("500", mapper.writeValueAsString(Map.of("value", "Internal Server error")));
            e.printStackTrace();
        }
    }

    private void handleSessionRoute(HTTPModel httpRequest) throws IOException {

        if (httpRequest.getRequestMethod().equals("POST")) {
            Map<String, String> userData = mapper.readValue(httpRequest.getBody(), Map.class);

            if (userHandler.checkIfUsernameExists(userData.get("Username"))) {
                String result = userHandler.loginUser(userData.get("Username"), userData.get("Password"));

                if (result == null)
                    sendResponse("401", mapper.writeValueAsString(Map.of("value", "Password is incorrect")));
                else sendResponse("200", mapper.writeValueAsString(Map.of("authorizationToken", "Basic " + result)));
            } else {
                sendResponse("404", mapper.writeValueAsString(Map.of("value", "There is no User with this username")));
            }
        } else {
            sendResponse("404", mapper.writeValueAsString(Map.of("value", "Route not found")));
        }
    }

    private void handleTransactionsRoute(HTTPModel httpRequest) throws IOException {
        if (!httpRequest.getPath().equals("/transactions/packages")) {
            sendResponse("404", mapper.writeValueAsString(Map.of("value", "Route not found")));
            return;
        }

        if (httpRequest.getAuthorization().isEmpty()) {
            sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
            return;
        }

        if (!userHandler.checkIfUserExists(httpRequest.getAuthorization())) {
            sendResponse("401", mapper.writeValueAsString(Map.of("value", "Authorization-Token is not valid")));
            return;
        }

        User user = userHandler.getUser(httpRequest.getAuthorization());

        if (user.getCoins() < 5) {
            sendResponse("400", mapper.writeValueAsString(Map.of("value", "User does not have enough coins")));
            return;
        }

        List<Card> cardPackage = marketplace.buyPackage();

        if (cardPackage == null) {
            sendResponse("400", mapper.writeValueAsString(Map.of("value", "There are no packages to be bought")));
            return;
        }

        user.addCardPackage(cardPackage);

        sendResponse("200", mapper.writeValueAsString(Map.of("value", "User successfully bought package")));

    }

    private void handlePackagesRoute(HTTPModel httpRequest) throws IOException {

        if (httpRequest.getRequestMethod().equals("POST")) {
            if (httpRequest.getAuthorization().isEmpty()) {
                sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
                return;
            }

            if (!httpRequest.getAuthorization().equals("admin-mtcgToken")) {
                sendResponse("400", mapper.writeValueAsString(Map.of("value", "You are not authorized")));
                return;
            }

            Map[] cardData = mapper.readValue(httpRequest.getBody(), Map[].class);


            if (cardData.length != 5) {
                sendResponse("400", mapper.writeValueAsString(Map.of("value", "A package consists of 5 Cards")));
            }

            List<Card> cardList = new ArrayList<>();

            for (Map data : cardData) {
                if (data.get("Name").toString().toLowerCase().contains("spell")) {
                    cardList.add(new SpellCard(data.get("Id").toString(), Float.parseFloat(data.get("Damage").toString()), data.get("Name").toString()));
                } else {
                    cardList.add(new MonsterCard(data.get("Id").toString(), Float.parseFloat(data.get("Damage").toString()), data.get("Name").toString()));
                }
            }

            marketplace.createPackage(cardList);

            sendResponse("200", mapper.writeValueAsString(Map.of("value", "Package created successfully")));
        } else {
            sendResponse("404", mapper.writeValueAsString(Map.of("value", "Route not found")));
        }


    }

    private void handleCardsRoute(HTTPModel httpRequest) throws IOException {
        if (!httpRequest.getRequestMethod().equals("GET")) {
            sendResponse("404", mapper.writeValueAsString(Map.of("value", "Route not found")));
            return;
        }

        if (httpRequest.getAuthorization() == null || httpRequest.getAuthorization().isEmpty()) {
            sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
            return;
        }

        User user = userHandler.getUser(httpRequest.getAuthorization());

        sendResponse("200", mapper.writeValueAsString(user.getCardList()));
    }

    private void handleDeckRoute(HTTPModel httpRequest) throws IOException {
        if (httpRequest.getAuthorization() == null || httpRequest.getAuthorization().isEmpty()) {
            sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
            return;
        }
        User userModel = userHandler.getUser(httpRequest.getAuthorization());

        if (userModel == null) {
            sendResponse("400", mapper.writeValueAsString(Map.of("value", "Not authorized")));
            return;
        }

        switch (httpRequest.getRequestMethod()) {
            case "GET":
                if (httpRequest.getParameter() != null) {
                    if (httpRequest.getParameter().containsKey("format")) {
                        switch (httpRequest.getParameter().get("format").toString()) {
                            case "plain":
                                StringBuilder deckString = new StringBuilder();
                                for (int i = 0; i < userModel.getDeck().size(); i++) {
                                    deckString.append(userModel.getDeck().get(i) + "\n");
                                }
                                sendResponse("200", deckString.toString());
                                return;
                        }
                    }
                    sendResponse("200", mapper.writeValueAsString(userModel.getDeck()));
                    return;
                }
                sendResponse("200", mapper.writeValueAsString(userModel.getDeck()));

                break;
            case "PUT":
                String[] deck = mapper.readValue(httpRequest.getBody(), String[].class);

                if (deck.length != 4) {
                    sendResponse("400", mapper.writeValueAsString(Map.of(
                            "value", "To configure a deck 5 cards are needed"
                    )));
                    return;
                }

                if (userModel.getDeck().size() == 4) {
                    sendResponse("400", mapper.writeValueAsString(Map.of(
                            "value", "Deck already configured"
                    )));
                    return;
                }

                userModel.setDeck(deck);

                sendResponse("200", mapper.writeValueAsString(Map.of(
                        "value", "Deck successfully set"
                )));
                break;
            default:
                sendResponse("404", mapper.writeValueAsString(Map.of("value", "Route not found")));
                break;
        }
    }

    private void handleStatsRoute(HTTPModel httpRequest) throws IOException {
        if (!httpRequest.getRequestMethod().equals("GET")) {
            sendResponse("404", mapper.writeValueAsString(Map.of(
                    "value", "Route not found"
            )));
            return;
        }
        if (checkAuthToken(httpRequest)) {
            User userModel = userHandler.getUser(httpRequest.getAuthorization());

            sendResponse("200", "Username: " + userModel.getUsername() + "; WLD:" + userModel.getWon() + "," + userModel.getLost() + "," + userModel.getDraw());
        }
    }

    private void handleScoreRoute(HTTPModel httpRequest) throws IOException {
        if (!httpRequest.getRequestMethod().equals("GET")) {
            sendResponse("404", mapper.writeValueAsString(Map.of(
                    "value", "Route not found"
            )));
            return;
        }

        if (checkAuthToken(httpRequest)) {
            sendResponse("200", userHandler.getScoreboard());
        }
    }

    private void handleBattlesRoute(HTTPModel httpRequest) throws IOException {
        if (!httpRequest.getRequestMethod().equals("POST")) {
            sendResponse("404", mapper.writeValueAsString(Map.of(
                    "value", "Route not found"
            )));
            return;
        }

        if (checkAuthToken(httpRequest)) {
            User userModel = userHandler.getUser(httpRequest.getAuthorization());

            arena.addPlayer(userModel);

            if (arena.getUserQueue().size() >= 2) {
                arena.startBattle();
            }

            sendResponse("200", mapper.writeValueAsString(Map.of(
                    "value", "Added User successfully to the Arena and waiting for Battle..."
            )));
        }
    }

    private void handleTradingsRoute(HTTPModel httpRequest) throws IOException {
        if (checkAuthToken(httpRequest)) {
            switch (httpRequest.getRequestMethod()) {
                case "GET":
                    sendResponse("200", mapper.writeValueAsString(marketplace.getActiveTrades()));
                    break;
                case "POST":
                    if (httpRequest.getPath().split("/").length < 3) {
                        Map tradeData = mapper.readValue(httpRequest.getBody(), Map.class);
                        User user = userHandler.getUser(httpRequest.getAuthorization());

                        CardType type;
                        if (tradeData.get("Type").toString().toLowerCase().equals("monster"))
                            type = CardType.Monster;
                        else
                            type = CardType.Spell;

                        Trade trade = new Trade(tradeData.get("Id").toString(), tradeData.get("CardToTrade").toString(), type, Float.parseFloat(tradeData.get("MinimumDamage").toString()), user.getId());


                        if (user.getCardList().stream().anyMatch(e -> e.getId().equals(trade.getCardToTrade()))) {
                            marketplace.addTrade(trade);

                            sendResponse("200", mapper.writeValueAsString(Map.of(
                                    "value", "Successfully added Trade"
                            )));
                        } else {
                            sendResponse("400", mapper.writeValueAsString(Map.of(
                                    "value", "User does not own the to be traded card"
                            )));
                        }

                    } else {
                        String tradeID = httpRequest.getPath().split("/")[2];

                        Trade trade = marketplace.getTrade(tradeID);

                        User user = userHandler.getUserByID(trade.getUserID());

                        if (user.getAuthorizationToken().equals(httpRequest.getAuthorization())) {
                            sendResponse("400", mapper.writeValueAsString(Map.of(
                                    "value", "A User cant trade with himself"
                            )));
                            return;
                        }

                        User trader = userHandler.getUser(httpRequest.getAuthorization());

                        String cardID = httpRequest.getBody().replace("\"", "");

                        Card traderCard = trader.getCardList().stream().filter(card -> card.getId().equals(cardID)).findFirst().orElse(null);

                        System.out.println(trader.getCardList());
                        System.out.println(cardID);

                        if (traderCard == null) {
                            sendResponse("404", mapper.writeValueAsString(Map.of(
                                    "value", "User does not have the Card to be traded"
                            )));
                            return;
                        }

                        Card cardToBeTraded = user.getCardList().stream().filter(card -> card.getId().equals(trade.getCardToTrade())).findFirst().orElse(null);

                        System.out.println(trade.getCardToTrade());
                        System.out.println(user.getCardList());

                        if (cardToBeTraded == null) {
                            marketplace.deleteTrade(tradeID);
                            sendResponse("404", mapper.writeValueAsString(Map.of(
                                    "value", "Trade is not available anymore"
                            )));
                            return;
                        }

                        trader.removeCardFromList(traderCard.getId());
                        user.removeCardFromList(cardToBeTraded.getId());

                        trader.addCardToList(cardToBeTraded);
                        user.addCardToList(traderCard);

                        marketplace.deleteTrade(tradeID);

                        sendResponse("200", mapper.writeValueAsString(Map.of(
                                "value", "Successfully traded"
                        )));
                    }
                    break;
                case "DELETE":
                    String tradeID = httpRequest.getPath().split("/")[2];

                    Trade trade = marketplace.getTrade(tradeID);

                    User user = userHandler.getUserByID(trade.getUserID());

                    if (user.getAuthorizationToken().equals(httpRequest.getAuthorization())) {
                        marketplace.deleteTrade(trade.getId());

                        sendResponse("200", mapper.writeValueAsString(Map.of(
                                "value", "Trade successfully deleted"
                        )));
                        return;
                    } else {
                        sendResponse("400", mapper.writeValueAsString(Map.of(
                                "value", "Not authorized"
                        )));
                    }

                    break;
                default:
                    sendResponse("404", mapper.writeValueAsString(Map.of(
                            "value", "Route not found"
                    )));
                    break;
            }
        }
    }

    private void sendResponse(String code, String body) throws IOException {
        bufferedWriter.write(String.format("HTTP/1.1 %s OK\r\n", code));
        bufferedWriter.write("SERVER.Server: Java Server example\r\n");
        bufferedWriter.write("Content-Type: application/json\r\n");
        bufferedWriter.write("Connection: close\r\n");
        if (!body.isEmpty()) {
            bufferedWriter.write("Content-Length: " + body.length() + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(body + "\r\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private boolean checkAuthToken(HTTPModel httpRequest) throws IOException {
        if (httpRequest.getAuthorization() == null || httpRequest.getAuthorization().isEmpty()) {
            sendResponse("400", mapper.writeValueAsString(Map.of(
                    "value", "Not authorized")));
            return false;
        }

        if (!userHandler.checkIfUserExists(httpRequest.getAuthorization())) {
            sendResponse("400", mapper.writeValueAsString(Map.of(
                    "value", "Not authorized"
            )));
            return false;
        }

        return true;
    }

}