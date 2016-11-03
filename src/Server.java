import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Адреса и порты задаются через командную строку:
 * клиенту --- куда соединяться, серверу --- на каком порту слушать.
 * Написать текстовый многопользовательский чат.
 * Пользователь управляет клиентом. На сервере пользователя нет.
 * Сервер занимается пересылкой сообщений между клиентами
 * По умолчанию сообщение посылается всем участникам чата
 * Есть команда послать сообщение конкретное пользователи (@senduser Vasya)
 * Программа работает по протоколу TCP.
 * <p>
 * <p>
 * * * файл с логинами и паролями пользователей
 * <p>
 * Сервер подключает клиентов и только
 */
//java Server port(0) logins.txt(1)
public class Server {
    private ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();
    private ServerSocket ss;
    private ConcurrentHashMap<String, String> logins = new ConcurrentHashMap<>();

    public Server(int port, String file) throws IOException {
        ss = new ServerSocket(port);
        Scanner input = new Scanner(new FileReader(file));
        while (input.hasNextLine()) {
            logins.put(input.next(), input.next());//login, password
        }
        new Thread(new Service()).start();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("I'm working...");
        int port = Integer.parseInt(args[0]);
        new Server(port, args[1]).run();
    }

    public void run() {
        while (true) {
            try {
                Socket socket = ss.accept();    //подсоединение
                System.out.println("New client");
                ServerClient client = new ServerClient(socket, clients, logins);
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
