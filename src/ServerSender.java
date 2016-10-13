import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerSender extends Thread{
    private DataOutputStream out;
    private ArrayList<String> localMessages = new ArrayList<>();


    public ServerSender(Socket s){
        try {
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String mes){
        localMessages.add(mes);
        notify();//оповещает, что нужно отправить сообщение
    }

    private void sendMessage(String mes){
        /*while (localMessages.size() == 0)
            wait();
        String*/
        try {
            out.writeUTF(mes);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){}
}
