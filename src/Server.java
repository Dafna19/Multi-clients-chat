import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Адреса и порты задаются через командную строку:
 * клиенту --- куда соединяться, серверу --- на каком порту слушать.
 * <p>
 * без авторизации
 * <p>
 * сервер должен писать в лог (текстовый файл) о освоих действиях
 */
//java Server port(0) logins.txt(1)
public class Server {
    private ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();
    private ServerSocket ss;
    private ConcurrentHashMap<String, String> logins = new ConcurrentHashMap<>();
    private FileWriter logFile;
    private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss z dd.MM.yyyy");


    public Server(int port) throws IOException {
        ss = new ServerSocket(port);
        logFile = new FileWriter("serverLog.txt", true);//дозаписывает в конец
        logFile.write("\n\nServer started at ip: " + ss.getInetAddress() + " port: " + port + " at " + date.format(new Date()));
        logFile.flush();
      //  new Thread(new Service()).start();////

    }

    public static void main(String[] args) throws IOException {
        System.out.println("I'm working...");
        int port = Integer.parseInt(args[0]);
        new Server(port).run();
    }

    public void run() {
        while (true) {
            try {
                Socket socket = ss.accept();    //подсоединение
                System.out.println("New client");
                logFile.flush();
                ServerClient client = new ServerClient(socket, clients, logFile);
                client.start(); //запускаем
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private class Service implements Runnable {
        BufferedReader keyboard;
        public void run() {
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String command = keyboard.readLine();
                    if (command.equals("show clients")) {//показывает список подключенных пользователей
                        if (clients.isEmpty())
                            System.out.println("No clients yet");
                        else
                            for (Map.Entry<String, Socket> entry : clients.entrySet())
                                System.out.println(entry.getKey() + " " + entry.getValue());
                        System.out.println();
                    } else if (command.equals("show logins")) {//показывает список зарегистрированных пользователей
                        for (Map.Entry<String, String> entry : logins.entrySet())
                            System.out.println("login: " + entry.getKey() + "\tpassword: " + entry.getValue());
                        System.out.println();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
