import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadChatServerSynchronized 
{
    private static ServerSocket server = null;
    private static Socket client = null;

    private static final int maximumClients = 10;
    private static final clientThread[] clients = new clientThread[maximumClients];

    public static void main(String args[]) 
    {
        int port = 2222;
        if (args.length < 1)
            System.out.println("App is using the following port=" + port);
        else
            port = Integer.valueOf(args[0]).intValue();

        try 
        {
            server = new ServerSocket(port);
        } 
        catch (IOException e) 
        {
            System.out.println(e);
        }

        while (true) 
        {
            try 
            {
                client = server.accept();
                int i = 0;
                for (i = 0; i < maximumClients; i++) {
                    if (clients[i] == null) {
                        (clients[i] = new clientThread(client, clients)).start();
                        break;
                    }
                }
                if (i == maximumClients) {
                    PrintStream os = new PrintStream(client.getOutputStream());
                    os.println("Chat room is full. Try again later.");
                    os.close();
                    client.close();
                }
            } 
            catch (IOException e) 
            {
                System.out.println(e);
            }
        }
    }
}

class clientThread extends Thread {

    private String Name = null;
    private DataInputStream dis = null;
    private PrintStream ps = null;
    private Socket client = null;
    private final clientThread[] clients;
    private int maximumClients;

    public clientThread(Socket socket, clientThread[] clients) 
    {
        this.client = socket;
        this.clients = clients;
        maximumClients = clients.length;
    }

    public void run() 
    {
        int maximumClients = this.maximumClients;
        clientThread[] clients = this.clients;

        try 
        {
            dis = new DataInputStream(client.getInputStream());
            ps = new PrintStream(client.getOutputStream());
            String name;
            while (true) 
            {
                ps.println("Register your name.");
                name = dis.readLine().trim();
                if (!name.contains("@"))
                    break;
                else
                    ps.println("'@' character should not be in the name.");
            }

            ps.println("Welcome aboard " + name + "\nTo exit this chat room type \"/quit\"");
            synchronized (this) 
            {
                for (int i = 0; i < maximumClients  ; i++)
                    if (clients[i] != null && clients[i] == this) 
                    {
                        Name = "@" + name;
                        break;
                    }
                for (int i = 0; i < maximumClients  ; i++)
                    if (clients[i] != null && clients[i] != this)
                        clients[i].ps.println("*** " + name + " has joined the chat room ***");
            }
            while (true) 
            {
                String message = dis.readLine();
                if (message.startsWith("/quit"))
                    break;
                if (message.startsWith("@")) 
                {
                    String[] splittedMessage = message.split("\\s", 2);
                    if (splittedMessage.length > 1 && splittedMessage[1] != null) 
                    {
                        splittedMessage[1] = splittedMessage[1].trim();
                        if (!splittedMessage[1].isEmpty()) 
                        {
                            synchronized (this) 
                            {
                                for (int i = 0; i < maximumClients; i++) 
                                {
                                    if (clients[i] != null && clients[i] != this && clients[i].Name != null && clients[i].Name.equals(splittedMessage[0])) 
                                    {
                                        clients[i].ps.println("<" + name + "> " + splittedMessage[1]);
                                        this.ps.println(">" + name + "> " + splittedMessage[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } 
                else 
                {
                    synchronized (this) 
                    {
                        for (int i = 0; i < maximumClients  ; i++)
                            if (clients[i] != null && clients[i].Name != null)
                                clients[i].ps.println("<" + name + "> " + message);
                    }
                }
            }
            synchronized (this) 
            {
                for (int i = 0; i < maximumClients  ; i++) 
                    if (clients[i] != null && clients[i] != this && clients[i].Name != null)
                        clients[i].ps.println("*** " + name + " left the chat room ***");
            }
            ps.println("*** See u later " + name + " ***");

            synchronized (this) 
            {
                for (int i = 0; i < maximumClients; i++) 
                    if (clients[i] == this)
                        clients[i] = null;
            }
            dis.close();
            ps.close();
            client.close();
        } 
        catch (IOException e) {}
    }
}
