import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * здесь происходит отправка и прием сообщений
 */
public class ServerClient extends Thread {
    private LinkedList<ClientInfo> allClients;
    private ClientInfo myInfo;
    private Socket socket;
    private String myName = "";
    public DataInputStream in;

    public ServerClient(ClientInfo c, LinkedList list) {
        myInfo = c;
        socket = myInfo.getSocket();
        allClients = list;
        try {
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
                if (line.contains("@quit")) {
                    sendAll(myName + " is quited");
                    synchronized (allClients){
                        allClients.remove(myInfo);
                    } // удалить его из списка! И отправить всем, что он вышел
                } else if (line.contains("@senduser")) {//отправляем кому-то
                    int end = line.indexOf(" ", "@senduser".length() + 1);//находим конец имени
                    String name = line.substring("@senduser".length() + 1, end);//имя получателя
                    line = line.substring(end + 1);
                    //поиск клиента в списке
                    synchronized (allClients) {
                        ListIterator<ClientInfo> it = allClients.listIterator();
                        while (it.hasNext()) {
                            ClientInfo receiver = it.next();
                            if (name.equals(receiver.getName())) {
                                new DataOutputStream(receiver.getSocket().getOutputStream()).writeUTF(myName + ": " + line);//отправляем
                                break;
                            }
                        }
                    }
                } else if (line.contains("@name")){
                    myName = line.substring("@name".length() + 1);
                    synchronized (allClients){//добавляем имя в список
                        myInfo.setName(myName);//добавиться?
                        /*ListIterator<ClientInfo> it = allClients.listIterator();
                        while (it.hasNext()){
                            if(it.next().equals(myInfo)){
                                it.previous().setName(myName);
                                break;
                            }
                        }*/
                    }
                }
                else sendAll(myName + ": " + line);
            }
        } catch (IOException e) {//socket closed
        }
    }

    private void sendAll(String line){
        synchronized (allClients) { //отправляет всем
            ListIterator<ClientInfo> it = allClients.listIterator();
            while (it.hasNext()) {
                ClientInfo toClient = it.next();
                if (!toClient.equals(myInfo))//себе не отправлять
                    try {
                        new DataOutputStream(toClient.getSocket().getOutputStream()).writeUTF(line);//отправляем всем
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
