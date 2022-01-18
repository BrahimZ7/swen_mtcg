package handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.HTTPModel;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final UserHandler userHandler;
    private final PackageStore packageStore;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private Boolean activeConnection = true;


    public ClientHandler(Socket socket, UserHandler userHandler, PackageStore packageStore) throws IOException {
        this.userHandler = userHandler;
        this.packageStore = packageStore;
        this.socket = socket;
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
                break;
            case "packages":
                break;
            case "transactions":
                break;
            case "cards":
                break;
            case "deck":
                break;
            case "stats":
                break;
            case "score":
                break;
            case "battles":
                break;
            case "tradings":
                break;
            default:
                System.out.println("We are here");
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
                        sendResponse("400", mapper.writeValueAsString(Map.of(
                                "value", "Malformed Request"
                        )));

                    if (userHandler.checkIfUsernameExists(userData.get("Username"))) {
                        sendResponse("409", mapper.writeValueAsString(Map.of(
                                "value", "A User with this username already exists."
                        )));
                    } else {
                        userHandler.addUser(mapper.readValue(httpRequest.getBody(), Map.class));
                        sendResponse("200", mapper.writeValueAsString(Map.of(
                                "value", "User added successfully"
                        )));
                    }
                    break;
                case "GET":
                    if (!userHandler.checkIfUserExists(httpRequest.getAuthorization())) {
                        sendResponse("404", mapper.writeValueAsString(Map.of(
                                "value", "User could not be found"
                        )));
                    } else {
                        User user = userHandler.getUser(httpRequest.getAuthorization());

                        sendResponse("200", user.toJsonString());
                    }

                    break;
                case "PUT":
                    if (!userHandler.checkIfUserExists(httpRequest.getAuthorization())) {
                        sendResponse("404", mapper.writeValueAsString(Map.of(
                                "value", "User could not be found"
                        )));
                    }

                    System.out.println(httpRequest.getBody());

                    userHandler.updateUser(userData, httpRequest.getAuthorization());
                    sendResponse("200", mapper.writeValueAsString(Map.of(
                            "value", "User updated successfully"
                    )));
                    break;
                default:
                    sendResponse("404", mapper.writeValueAsString(Map.of(
                            "value", "Route not found"
                    )));
                    break;
            }
        } catch (Exception e) {
            sendResponse("500", mapper.writeValueAsString(Map.of(
                    "value", "Internal Server error"
            )));
            e.printStackTrace();
        }
    }

    private void handleSessionRoute(HTTPModel httpRequest) {
    }

    private void handlePackagesRoute(HTTPModel httpRequest) {
    }

    private void handleCardsRoute(HTTPModel httpRequest) {
    }

    private void handleDeckRoute(HTTPModel httpRequest) {
    }

    private void handleStatsRoute(HTTPModel httpRequest) {
    }

    private void handleScoreRoute(HTTPModel httpRequest) {
    }

    private void handleBattlesRoute(HTTPModel httpRequest) {
    }

    private void handleTradingsRoute(HTTPModel httpRequest) {
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

}