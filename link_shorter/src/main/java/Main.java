import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try {
            while (true) {
                ServerSocket serverSocket = new ServerSocket(8080);
                Socket socket = serverSocket.accept();
                ExecutorService executorService
                        = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
                executorService.submit(new LinksShortener(socket));
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
