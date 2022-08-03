import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LinksShortener implements Runnable{
    private static Map<String, String> map = new HashMap<>();

    static {
        try (Scanner scanner = new Scanner(new File("src/shortenerData"))) {
            while (scanner.hasNext()) {
                String string = scanner.nextLine();
                map.put(string.split(" ")[0], string.split(" ")[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Socket connection;

    public LinksShortener(Socket socket) {
        this.connection = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            String string = bufferedReader.readLine();

            if (string.split(" ")[0].equals("POST")) {
                String url = string.split(" ")[1];
                byte[] bytes = messageDigest.digest(url.getBytes());
                BigInteger number = new BigInteger(1, bytes);
                String hash = number.toString(16);

                if (!map.containsValue(url)) {
                    map.put(hash, url);
                    PrintWriter printWriter = new PrintWriter(new FileWriter("src/shortenerData", true));
                    printWriter.println(hash + " " + url);
                    printWriter.flush();
                    printWriter.close();
                }
                // вернуть ответ с кодом 201 и хэшем в теле
                // HttpResponse response =
                System.out.println(hash);

            } else if (string.split(" ")[0].equals("GET")) {
                String hash = string.split(" ")[1].substring(1);

                if (map.containsKey(hash)){
                    // вернуть ответ с кодом 307 и данными в http-заголовке Location
                    System.out.println(map.get(hash));
                }else {
                    // вернуть ответ с кодом 400
                    System.out.println("Wrong key argument");
                }
            } else {
                // вернуть ответ с кодом 400
                System.out.println("Wrong key argument");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
