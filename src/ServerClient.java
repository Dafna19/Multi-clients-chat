import java.io.*;
import java.net.*;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * пт 3пара бм
 * вт/чт
 * здесь происходит отправка и прием сообщений
 * <p>
 * map! +
 * логин пароль на одной строчке +
 * повторный ввод логина +
 * если клиента нет - как-то оформить! +
 * отправляю сама себе +
 * задержать цикл! +
 * 
 * под одним логином несколько пользователей
 */
public class ServerClient extends Thread {
    private ConcurrentHashMap<String, Socket> allClients;
    private ConcurrentHashMap<String, String> logins;
    private Socket socket;
    private String myName = "";
    public DataInputStream in;
    public DataOutputStream out;

    public ServerClient(Socket s, ConcurrentHashMap<String, Socket> list, ConcurrentHashMap<String, String> map) {
        logins = map;
        socket = s;
        allClients = list;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {//сначала проверить логин
            while (true) {
                String login, password;
                login = in.readUTF();
                password = in.readUTF();
                if (logins.containsKey(login))
                    if (logins.get(login).equals(password)) {//совпало
                        out.writeUTF("Welcome");
                        myName = login;
                        break;
                    }
                new DataOutputStream(socket.getOutputStream()).writeUTF("Incorrect login or password");
            }
            while (true) {
                allClients.put(myName, socket);//добавили себя в список
                String line;
                line = in.readUTF();
                if (line.contains("@quit")) {
                    sendAll(myName + " is quited");
                    allClients.remove(myName);
                    //break; ?
                } else if (line.contains("@senduser")) {//отправляем кому-то
                    int end = line.indexOf(" ", "@senduser".length() + 1);//находим конец имени
                    String name = line.substring("@senduser".length() + 1, end);//имя получателя
                    line = line.substring(end + 1);
                    //поиск клиента в списке
                    if (allClients.containsKey(name))
                        new DataOutputStream(allClients.get(name).getOutputStream()).writeUTF(myName + ": " + line);
                    else
                        out.writeUTF(name + " is not online.");
                } else sendAll(myName + ": " + line);
            }
        } catch (IOException e) {//socket closed
        }
    }

    private void sendAll(String line) {//отправляет всем

        for (Socket s : allClients.values())
            if (!s.equals(socket))
                try {
                    new DataOutputStream(s.getOutputStream()).writeUTF(line);
                } catch (IOException e) {
                   // e.printStackTrace();
                }
    }
}
