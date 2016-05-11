import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MultiThreadChatClient implements Runnable 
{
    private static Socket client = null;
    private static PrintStream ps = null;
    private static DataInputStream dis = null;

    private static BufferedReader br = null;
    private static boolean closed = false;

    public static void main(String[] args) 
    {
        int port = 2222;
        String ip = "localhost";

        if (args.length < 2)
            System.out.println("App is using the following specifications, ip=" + ip + ", port=" + port);
        else 
        {
            ip = args[0];
            port = Integer.valueOf(args[1]).intValue();
        }

        try 
        {
            client = new Socket(ip, port);
            br = new BufferedReader(new InputStreamReader(System.in));
            ps = new PrintStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());
        } 
        catch (UnknownHostException e) 
        {
            System.err.println("Unknown host " + ip);
        } 
        catch (IOException e) 
        {
            System.err.println("Problems in I/O from host " + ip);
        }

        if (client != null && ps != null && dis != null) 
        {
            try 
            {
                new Thread(new MultiThreadChatClient()).start();
                while (!closed)
                    ps.println(br.readLine().trim());
                
                ps.close();
                dis.close();
                client.close();
            } 
            catch (IOException e) 
            {
                System.err.println("IOException:  " + e);
            }
        }
    }

    public void run() 
    {
        String response;
        try 
        {
            while ((response = dis.readLine()) != null) 
            {
                System.out.println(response);
                if (response.contains("*** Bye"))
                    break;
            }
            closed = true;
        } 
        catch (IOException e) 
        {
            System.err.println("IOException:  " + e);
        }
    }
}
