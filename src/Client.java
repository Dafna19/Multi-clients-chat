import java.io.*;
import java.net.*;
import java.util.Scanner;

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
 * * * файл с логинами и паролями пользователей
 */
//java Client port(0) ipAddr(1)
public class Client {
    private Socket socket;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private BufferedReader keyboard;
    private Thread listener;

    public Client(String adr, int port) throws IOException {
        InetAddress ipAddress = InetAddress.getByName(adr); // создаем объект который отображает вышеописанный IP-адрес
        socket = new Socket(ipAddress, port); // создаем сокет используя IP-адрес и порт сервера
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        keyboard = new BufferedReader(new InputStreamReader(System.in));
        listener = new Thread(new FromServer());
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);// порт, к которому привязывается сервер
        //String address = "localhost";//"127.0.0.1"// это IP-адрес компьютера, где исполняется наша серверная программа.
        new Client(args[1], port).run();
    }

    private void socketClose() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {//отправляет на сервер
        try {
            while (true) {
                System.out.println("write your login:");
                String line = keyboard.readLine();
                out.writeUTF(line);
                out.flush();
                System.out.println("write your password:");
                /*
                * придумать, как сделать пароль звёздочками
                * */
                line = keyboard.readLine();
                out.writeUTF(line);
                out.flush();
                if(confirm()){//если пароль верный
                    listener.start();
                    break;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        try {
            while (true) {
                String line;
                line = keyboard.readLine();
                if (socket.isClosed())
                    break;
                out.writeUTF(line); // отсылаем серверу
                out.flush();
                if (line.equals("@quit")) {
                    socketClose();
                    break;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private boolean confirm() throws IOException {
        String line = in.readUTF();
        System.out.println(line);
        if (line.equals("Welcome"))
            return true;
        return false;
    }

    private class FromServer implements Runnable {//принимает сообщения

        public void run() {
            try {
                while (true) {
                    String line;
                    line = in.readUTF(); // ждем пока сервер отошлет строку текста
                    System.out.println(line);
                }
            } catch (IOException e) {
                socketClose();
            } finally {
                socketClose();
            }
        }
    }

}
