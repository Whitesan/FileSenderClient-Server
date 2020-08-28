import java.io.*;
import java.util.*;
import java.net.*;

// Server class

/**
 * Server class , accpetiing clients connections and adding them to active clients , running their threads
 */
public class Server implements Runnable
{

    // Vector to store active clients
    /**
     * Vector of Clients
     */
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients 
    static int i = 0;

    static DataOutputStream dos;
    public static void main(String[] args) throws IOException
    {
        // server is listening on port 1234 

    }

    @Override
    public void run() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(1234);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket s;

        // running infinite loop for getting
        // client request
        while (true)
        {
            // Accept the incoming request
            try {
                s = ss.accept();
                System.out.println("New client request received : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);
                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.


                // Create a new Thread with this object.
                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                ar.add(mtch);

                // start the thread.
                t.start();

                // increment i for new client.
                // i is used for naming only, and can be replaced
                // by any naming scheme
                i++;


            } catch (IOException e) {
                e.printStackTrace();
            }



            // obtain input and output streams



        }
    }
}