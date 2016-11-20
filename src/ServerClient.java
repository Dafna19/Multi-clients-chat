import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * здесь происходит отправка и прием сообщений
 * <p>
 * map! +
 * логин пароль на одной строчке +
 * повторный ввод логина +
 * если клиента нет - как-то оформить! +
 * отправляю сама себе +
 * задержать цикл! +
 * <p>
 * под одним логином несколько пользователей +
 * не удаляет клиента после выхода +
 */
public class ServerClient extends Thread {
    private ConcurrentHashMap<String, Socket> allClients;
    private Socket socket;
    private String myName = "";
    public DataInputStream in;
    public DataOutputStream out;
    private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss z dd.MM.yyyy");
    private FileWriter logFile;

    public ServerClient(Socket s, ConcurrentHashMap<String, Socket> list, FileWriter log) {
        logFile = log;
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
        try {
            myName = in.readUTF();
            allClients.put(myName, socket);//добавили себя в список
            logFile.write("\nNew client \"" + myName + "\" ip: " + socket.getLocalAddress() + " port: " + socket.getPort() + " at " + date.format(new Date()));
            logFile.flush();
            System.out.println(myName + " in " + Thread.currentThread().getName());

            while (true) {
                String line;
                line = in.readUTF();
                if (line.contains("@quit")) {
                    sendAll(myName + " came out");
                    allClients.remove(myName);
                    logFile.write("\nClient \"" + myName + "\" came out at " + date.format(new Date()));
                    logFile.flush();
                    break;
                } else if (line.contains("@sendfile")) {//отправляем файл
                    sendAll(line);//переправляем всем
                    sendAll(myName);
                    long size = in.readLong();
                    System.out.println(" size = " + size);
                    //рассылаем всем размер
                    for (Socket s : allClients.values())
                        if (!s.equals(socket))
                            new DataOutputStream(s.getOutputStream()).writeLong(size);

                    byte[] buf = new byte[1024];
                    int count, all = 0;
                    double limit = Math.ceil((double) size / 1024);
                    System.out.println("limit = " + (int)limit);
                    for (int i = 0; i < (int)limit; i++) {
                        count = in.read(buf);
                        all += count;
                        System.out.println(" count = " + count + "  all = " + all + "  i = " + i);
                        for (Socket s : allClients.values())
                            if (!s.equals(socket))
                                try {
                                    //посылаем всем файл по частям
                                    new DataOutputStream(s.getOutputStream()).write(buf, 0, count);
                                } catch (SocketException z) {
                                    System.out.println(" can't send file ");
                                    z.printStackTrace();
                                    break;
                                }
                    }
                    System.out.println(" send " + all + " bytes");
                    //отсылаем всем имя отправителя
                   // sendAll(myName); System.out.println("send name");
                } else sendAll(myName + ": " + line);
            }
        } catch (IOException e) {//socket closed
            e.printStackTrace();
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
