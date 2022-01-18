import handler.ClientHandler;
import handler.PackageStore;
import handler.UserHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    final PackageStore packageStore = new PackageStore();
    final UserHandler userHandler = new UserHandler();


    private List<Thread> clientThreads = new ArrayList<Thread>();
    final ExecutorService executorService = Executors.newCachedThreadPool();

    public void startListening() throws IOException {
        ServerSocket serverSocket = new ServerSocket(10001);

        while (true) {
            try {
                System.out.println("We are waiting for new Requests");
                Socket socket = serverSocket.accept();
                System.out.println("We received a Response");

                ClientHandler clientHandler = new ClientHandler(socket, userHandler, packageStore);
                executorService.submit(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
