import java.net.*;
import java.io.*;
/**
 * Адреса и порты задаются через командную строку:
 * клиенту --- куда соединяться, серверу --- на каком порту слушать.
 * * * файл с логинами и паролями пользователей
 * Написать текстовый многопользовательский чат.
 * Пользователь управляет клиентом. На сервере пользователя нет.
 * Сервер занимается пересылкой сообщений между клиентами
 * По умолчанию сообщение посылается всем участникам чата
 * Есть команда послать сообщение конкретное пользователи (@senduser Vasya)
 * Программа работает по протоколу TCP.
 *
 * Сервер подключает клиентов и только
 *
 */
//java Client port(0) ipAddr(1)
public class Server {
    private ServerSocket ss;
    private Socket socket;

    public Server(int port) throws IOException {
        ss = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome!");
        int port = Integer.parseInt(args[0]);
        new Server(port).run(); //порт задаёт пользователь
    }
    public void run(){
        ServerManager manager = new ServerManager();
        manager.start();

        while(true){
            try {
                socket = ss.accept();
                ClientInfo client = new ClientInfo(socket);
                manager.addClient(client);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
