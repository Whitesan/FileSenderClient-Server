import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * Client class, that communicate with Server: sending and receiving messages
 */
public class Client implements Runnable
{
    /**
     * Client name
     */
    private String userName;
    /**
     * Message to write to server
     */
    private static String message;
    /**
     * Part 2 of Message to write to server
     */
    private  String receiver;
    /**
     * Server port number
     */
    final static int ServerPort = 1234;
    /**
     * If user closed window marked as true else false
     */
    volatile boolean shutdown = false;
    /**
     * Oustput stream to server
     */
    static DataOutputStream dos;
    /**
     * Input  stream from server
     */
    static DataInputStream dis;
    /**
     * Marked as true means that Client is busy downloading a file from server
     */
    static boolean clientDownloadingFIle;

    /**
     * Set user name and create class
     * @param userName
     */
    Client(String userName)
    {
        this.userName = userName;
        this.message = userName;
    }

    /**
     * Set message to send to server
     * @param message
     */
    public void setMessage(String message)
    {
        this.message = message;

    }
    /**
     * Set reciver name to inform server
     * @param receiver
     */
    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    @Override
    public void run() {
        clientDownloadingFIle = false;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // establish the connection
        Socket s = null;
        try {
            s = new Socket(ip, ServerPort);

            dis = new DataInputStream(s.getInputStream());
             dos = new DataOutputStream(s.getOutputStream());


            // sendMessage thread
            Thread sendMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {
                    receiver = "default";
                    while (!shutdown) {

                        // read the message to deliver.
                        //String msg = scn.nextLine();
                        if(message!= null && message.equals("Closing"))
                            System.out.println("Closing client!");
                        //else
                           // System.out.println(message);

                        String msg = null;
                        if(!receiver.equals("default"))
                        {
                            msg = message +"#"+ receiver;
                            receiver = "default";
                        }
                        else if(message!=null)
                            msg = message + "#" + "xd";

                        try {
                            // write on the output stream
                            if(message != null)
                            {
                                dos.writeUTF(msg);
                                if(message.equals("Closing"))
                                {
                                    shutdown = true;
                                }
                                message = null;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            // readMessage thread
            Thread readMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {

                    while (!shutdown) {
                        if(!clientDownloadingFIle)
                        {
                            try {
                                // read the message sent to this client

                                String received = dis.readUTF();
                                StringTokenizer st = new StringTokenizer(received, "#");
                                String MsgToSend = st.nextToken();
                                String recipient = st.nextToken();

                                //Message form server: "Im sending file, be ready"
                                if(recipient.equals("INCOME"))
                                {
                                    clientDownloadingFIle = true;
                                    System.out.println(MsgToSend +" " +recipient);
                                    System.out.println("im trying to read");

                                    Main.status.setText("Downloading...");
                                    SendOrDownloadFIle.readFile(MsgToSend,dis);
                                    Main.status.setText("Nothing");


                                }
                                else if(recipient.equals("INCOMEEMPTYDIR")) //Message form server: "Im sending empty directory"
                                {
                                    Main.status.setText("Downloading...");
                                    Path path = Paths.get(Main.path+MsgToSend);
                                    Files.createDirectory(path);
                                    Main.status.setText("Nothing");
                                }
                                else if(recipient.equals("INCOMEEMPTYFILE")) //Message form server: "Im sending empty file"
                                {
                                    Main.status.setText("Downloading...");
                                    Path path = Paths.get(Main.path+MsgToSend);
                                    Files.createFile(path);
                                    Main.status.setText("Nothing");
                                }

                                //Client exited from server, remove him from active
                                if(recipient.equals("Exited"))
                                    Main.listModelClients.removeElement(MsgToSend);
                                //Server got your file, you can talk with server agian
                                if(recipient.equals("Free"))
                                    FileListener.serverBusy = false;
                                //Client joined to server, add him to active clients
                                if(recipient.equals("Joined"))
                                    Main.listModelClients.addElement(MsgToSend);
                                System.out.println(received);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

            sendMessage.start();
            readMessage.start();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}