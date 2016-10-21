import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * здесь происходит отправка и прием сообщений
 */
public class ServerClient extends Thread {
    private LinkedList<ClientInfo> allClients;
    private Socket socket;
    private String name = "";
    public DataOutputStream out;
    public DataInputStream in;

    public ServerClient(Socket s, LinkedList list) {
        socket = s;
        allClients = list;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String line;
                line = in.readUTF();
                synchronized (allClients) {
                    ListIterator<ClientInfo> it = allClients.listIterator();
                    while (it.hasNext()) {
                        Socket toSocket = it.next().getSocket();
                        if (!toSocket.equals(socket))//себе не отправлять
                            try {
                                new DataOutputStream(toSocket.getOutputStream()).writeUTF(line);//отправляем всем
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }

           /* if (line.equals("@quit")) {
                System.out.println("client is quited");
                break;
            }*/
            }
        }catch(IOException e) {//socket closed
        }
    }
}
