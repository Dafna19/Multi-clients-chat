import java.util.ArrayList;

/**
 * управляет пересылкой сообщений
 */
public class ServerManager extends Thread {
    private ArrayList<ClientInfo> clients = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();

    public void run(){
        while (true){
            try {
                sendToAll(getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void addClient(ClientInfo c){
        clients.add(c);
    }

    private synchronized String getMessage() throws InterruptedException {
        if(messages.size() == 0)
            wait();//как только там появится сообщение, проснётся
        String mes = messages.get(0);
        messages.remove(0);
        return mes;
    }

    private synchronized void sendToAll(String mes){
        for(int i = 0; i<clients.size(); i++){
            //рассылает сообщения через sender'a каждого клиента
            clients.get(i).sender.send(mes);

        }
    }
}
