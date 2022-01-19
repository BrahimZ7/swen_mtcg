import handler.Arena;
import handler.ClientHandler;
import handler.Marketplace;
import handler.UserHandler;
import service.DatabaseService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Marketplace marketplace;
    private UserHandler userHandler;
    private Arena arena;
    private DatabaseService databaseService;

    final ExecutorService executorService = Executors.newCachedThreadPool();

    public Server(DatabaseService databaseService) {
        this.databaseService = databaseService;
        marketplace = new Marketplace(databaseService);
        userHandler = new UserHandler(databaseService);
        arena = new Arena(userHandler);

        userHandler.init();
        marketplace.init();
    }

    public void startListening() throws IOException {
        ServerSocket serverSocket = new ServerSocket(10001);

        while (true) {
            try {
                System.out.println("We are waiting for new Requests");
                Socket socket = serverSocket.accept();
                System.out.println("We received a Response");

                ClientHandler clientHandler = new ClientHandler(socket, userHandler, marketplace, arena);
                executorService.submit(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
