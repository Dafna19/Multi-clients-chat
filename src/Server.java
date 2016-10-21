import java.net.*;
import java.io.*;
import java.util.LinkedList;

/**
 * Адреса и порты задаются через командную строку:
 * клиенту --- куда соединяться, серверу --- на каком порту слушать.
 *
 * * * файл с логинами и паролями пользователей
 *
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
    private LinkedList<ClientInfo> clients = new LinkedList<>();

    public void main(String[] args) throws IOException {
        System.out.println("Welcome!");
        int port = Integer.parseInt(args[0]);
        ServerSocket ss = new ServerSocket(port);

        while(true){
            try {
                Socket socket = ss.accept();
                ServerClient client = new ServerClient(socket, clients);
                client.start();
                ClientInfo info = new ClientInfo(socket);
                synchronized (clients){
                    clients.add(info);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
