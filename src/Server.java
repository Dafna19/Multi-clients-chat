import java.net.*;
import java.io.*;
import java.util.LinkedList;

/**
 * Адреса и порты задаются через командную строку:
 * клиенту --- куда соединяться, серверу --- на каком порту слушать.
 * Написать текстовый многопользовательский чат.
 * Пользователь управляет клиентом. На сервере пользователя нет.
 * Сервер занимается пересылкой сообщений между клиентами
 * По умолчанию сообщение посылается всем участникам чата
 * Есть команда послать сообщение конкретное пользователи (@senduser Vasya)
 * Программа работает по протоколу TCP.
 *
 *
 * * * файл с логинами и паролями пользователей
 *
 * Сервер подключает клиентов и только
 *
 */
//java Server port(0)
public class Server {
    private LinkedList<ClientInfo> clients = new LinkedList<>();

    public void main(String[] args) throws IOException {
        System.out.println("I'm working...");
        int port = Integer.parseInt(args[0]);
        ServerSocket ss = new ServerSocket(port);

        while(true){
            try {
                Socket socket = ss.accept();    //подсоединение
                System.out.println("New client.");
                ClientInfo info = new ClientInfo(socket);
                synchronized (clients){
                    clients.add(info);  //добавляем в список
                }
                ServerClient client = new ServerClient(info, clients);
                client.start(); //запускаем
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
