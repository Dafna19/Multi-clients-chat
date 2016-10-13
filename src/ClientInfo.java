import java.net.*;

/**
 * Created by Наташа on 13.10.2016.
 */
public class ClientInfo {
    private Socket socket;
    private String name;
    public ServerSender sender;
    //listener/reader

    public ClientInfo(Socket s){
        socket = s;
        sender = new ServerSender(socket);
        sender.start();
    }


}
