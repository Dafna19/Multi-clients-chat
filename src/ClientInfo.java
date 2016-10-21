import java.net.*;

/**
 * только информация для списка
 */
public class ClientInfo {
    private Socket socket;
    private String name = "";

    public ClientInfo(Socket s){
        socket = s;
    }

    public String getName(){
        return name;
    }

    public void setName(String s){
        name = s;
    }

    public Socket getSocket(){
        return socket;
    }
}
