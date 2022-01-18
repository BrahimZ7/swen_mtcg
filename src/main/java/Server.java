import handler.Arena;
import handler.ClientHandler;
import handler.Marketplace;
import handler.UserHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Marketplace packageStore;
    private UserHandler userHandler;
    private Arena arena;

    final ExecutorService executorService = Executors.newCachedThreadPool();

    public Server() {
        packageStore = new Marketplace();
        userHandler = new UserHandler();
        arena = new Arena(userHandler);
    }

    public void startListening() throws IOException {
        ServerSocket serverSocket = new ServerSocket(10001);

        while (true) {
            try {
                System.out.println("We are waiting for new Requests");
                Socket socket = serverSocket.accept();
                System.out.println("We received a Response");

                ClientHandler clientHandler = new ClientHandler(socket, userHandler, packageStore, arena);
                executorService.submit(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
