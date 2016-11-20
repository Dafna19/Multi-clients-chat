import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Адреса и порты задаются через командную строку:
 * клиенту --- куда соединяться, серверу --- на каком порту слушать.
 *
 * директория задаётся в командной строке
 */
//java Client port(0) ipAddr(1) dir(2)
public class Client {
    private Socket socket;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private BufferedReader keyboard;
    private Thread listener;
    private String directory;

    public Client(String adr, int port, String dir) throws IOException {
        InetAddress ipAddress = InetAddress.getByName(adr); // создаем объект который отображает вышеописанный IP-адрес
        socket = new Socket(ipAddress, port); // создаем сокет используя IP-адрес и порт сервера
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        keyboard = new BufferedReader(new InputStreamReader(System.in));
        listener = new Thread(new FromServer());
        directory = dir;
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);// порт, к которому привязывается сервер
        //String address = "localhost","127.0.0.1" это IP-адрес сервера
        new Client(args[1], port, args[2]).run();
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
            System.out.println("my directory is " + directory);
            System.out.println("write your login:");
            String line = keyboard.readLine();
            out.writeUTF(line);
            listener.start();
            System.out.println("Welcome!");

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
                if (line.contains("@sendfile")) {
                    String fileName = line.substring("@sendfile".length() + 1);
                    File file = new File(directory + fileName);
                    out.writeLong(file.length());//отправляем размер
                    FileInputStream inputFile = new FileInputStream(file);
                    byte[] buf = new byte[1024];
                    int count;
                    while ((count = inputFile.read(buf)) != -1) {
                        out.write(buf, 0, count);//отсылаем файл
                        out.flush();
                    }
                    System.out.println("File was successfully sent");
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private class FromServer implements Runnable {//принимает сообщения

        public void run() {
            try {
                while (true) {
                    String line;
                    line = in.readUTF(); // ждем пока сервер отошлет строку текста

                    if (line.contains("@sendfile")) {//принимаем файл
                        String fileName = line.substring("@sendfile".length() + 1);
                        String name = in.readUTF();
                        long size = in.readLong();
                       // System.out.println("receiving file " + fileName + " size = " + size);
                        byte[] buf = new byte[1024];
                        FileOutputStream outputFile = new FileOutputStream(directory + fileName);
                        int count, all = 0;
                        double limit = Math.ceil((double) size / 1024);
                        for (int i = 0; i < (int) limit; i++) {
                            count = in.read(buf);
                            all += count;
                            outputFile.write(buf, 0, count);//записываем файл
                            outputFile.flush();
                            if (all == size) {
                                System.out.println("broke");
                                break;
                            }
                        }
                        System.out.println("received \"" + fileName + "\" (" + all + " bytes) from " + name);
                    } else
                        System.out.println(line);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                socketClose();
            } finally {
                socketClose();
            }
        }
    }

}
