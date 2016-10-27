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
    private String myName = "";
    public DataInputStream in;

    public ServerClient(Socket s, LinkedList list) {
        socket = s;
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
                //клмент посылает без имени!
                if (line.contains("@quit")) {
                    int space = line.indexOf(" ");//находим конец имени
                    String name = line.substring(0, space-1);//без ":"
                    System.out.println(name + " is quited");

                    //как-то удалить его из списка!
                }
                else if(line.contains("@")){//отправляем кому-то
                    int at = line.indexOf("@");
                    int space = line.indexOf(" ", at);//находим конец имени
                    String name = line.substring(at+1, space);//имя получателя
                    line = line.substring(space+1);
                    //поиск клиента в списке
                    synchronized (allClients){
                        ListIterator<ClientInfo> it = allClients.listIterator();
                        while (it.hasNext()) {
                            ClientInfo receiver = it.next();
                            if(name.equals(receiver.getName())){
                                new DataOutputStream(receiver.getSocket().getOutputStream()).writeUTF(myName + ": " + line);//отправляем
                            }
                        }
                    }
                }
                else
                    synchronized (allClients) { //отправляет всем
                        ListIterator<ClientInfo> it = allClients.listIterator();
                        while (it.hasNext()) {
                            Socket toSocket =  it.next().getSocket();
                            if (!toSocket.equals(socket))//себе не отправлять
                                try {
                                    new DataOutputStream(toSocket.getOutputStream()).writeUTF(myName + ": " + line);//отправляем всем
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }
            }
        } catch (IOException e) {//socket closed
        }
    }
}
