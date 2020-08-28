import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Client handler : send messages to client , read messages from clients
 */
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    /**
     * Name of the client
     */
    private String name;
    /**
     * Name of Clients folder
     */
    private String folderPathName;
    /**
     * Client's input Stream
     */
    final DataInputStream dis;
    /**
     * Client's ouput Stream
     */
    final DataOutputStream dos;
    /**
     * File object of Clietnt's folder created here
     */
    private File file;
    /**
     * Socket object
     */
    Socket s;
    /**
     * Indicates if Client is available
     */
    boolean isloggedin;
    private String programPath = System.getProperty("user.dir");

    // constructor 
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    /**
     * Get path to folder
     * @return folder path
     */
    public String getFolderPath()
    {
        return folderPathName;
    }
    /**
     * Return name of the client
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return Client's Ouput stream
     * @return Client's Ouput stream
     */
    public DataOutputStream getDos()
    {
        return dos;
    }
    /**
     * Return Client's Input stream
     * @return Client's Input stream
     */
    public DataInputStream getDis()
    {
        return dis;
    }

    /**
     * Recursively delete direcory
     * @param directoryToBeDeleted File object to delete*
     */
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * Send same message to all active clients
     * @param mes message to send
     */
    void sendMessageToAllActive(String mes)
    {
        for (ClientHandler mc : Server.ar)
        {
            // if the recipient is found, write on its
            // output stream
            if (mc.isloggedin==true && !mc.name.equals(name))
            {
                try {
                    mc.dos.writeUTF(mes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Send Clients list to Clients
     */
    void sendClientList()
    {
        for (ClientHandler mc : Server.ar)
        {
            // if the recipient is found, write on its
            // output stream
            if (mc.isloggedin==true && !mc.name.equals(name))
            {
                try {
                    dos.writeUTF(mc.name+"#Joined");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    @Override
    public void run() {

        String received ;
        boolean userNameFilled = false;
        while (true)
        {
            try
            {
                // receive the string 
                received = dis.readUTF();

                System.out.println(received);


                if(received.equals("Closing#xd")){
                    this.isloggedin=false;
                    dos.writeUTF("Server#Closing "+name);
                    sendMessageToAllActive(name + "#Exited");
                    if(FileListener.selectedClient != null)
                    {
                        if(FileListener.selectedClient.equals(name))
                        {
                            FileListener.selectedClient = "Default";
                            FileListener.user = null;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Main.selectedClient = "Default";
                          
                        }
                    }



                    if(deleteDirectory(file))
                    {
                        System.out.println("File deleted successfully");
                    }
                    else
                    {
                        System.out.println("Failed to delete the file");
                    }

                    Main.listModel.removeElement(name);


                    this.s.close();
                    break;
                }


                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();


                String recipient = st.nextToken();

                // search for the recipient in the connected devices list. 
                // ar is the vector storing client of active users
                if(userNameFilled == false)
                {

                    if(MsgToSend != null)
                    {

                        name = MsgToSend;

                        sendMessageToAllActive(name + "#Joined");
                        Main.listModel.addElement(name);

                        userNameFilled = true;
                        file = new File(programPath+"/"+ name);
                        if (!file.exists()) {
                            if (file.mkdir()) {
                                System.out.println("Directory is created!");
                                folderPathName = programPath+"/"+ name;
                            } else {
                                System.out.println("Failed to create directory!");
                            }
                        }
                       
                        sendClientList();
                    }

                }
                if(MsgToSend.contains("AddedEmpty"))
                {
                    StringTokenizer fil = new StringTokenizer(MsgToSend, "$");
                    String filnam = fil.nextToken();
                    Path path = Paths.get(programPath+"/"+name+"/"+filnam);
                    Files.createFile(path);
                    dos.writeUTF("xd#Free");
                }
                else if(MsgToSend.contains("AddedDir"))
                {
                    System.out.println("adadad");
                    StringTokenizer fil = new StringTokenizer(MsgToSend, "$");
                    String filnam = fil.nextToken();
                    //SendOrDownloadFile.readFile("/home/piotrek/Desktop/Projekt_1.0Server/"+name+"/"+filnam,dis,dos);
                    Path path = Paths.get(programPath+"/"+name+"/"+filnam);
                    Files.createDirectories(path);
                    dos.writeUTF("xd#Free");

                }
                else if(MsgToSend.contains("Added"))
                {
                    System.out.println("adadad");
                    StringTokenizer fil = new StringTokenizer(MsgToSend, "$");
                    String filnam = fil.nextToken();
                    SendOrDownloadFile.readFile(programPath+"/"+name+"/"+filnam,dis,dos);


                }
                else if(MsgToSend.contains("Removed"))
                {

                    
                   
                    StringTokenizer fil = new StringTokenizer(MsgToSend, "$");
                    String filnam = fil.nextToken();
                    Path path = Paths.get(programPath+"/"+name+"/"+filnam);
                    Files.delete(path);
                   
                }
                else if(!recipient.equals("xd") && !MsgToSend.equals("Free"))
                {
                    DataOutputStream tempDos = null;
                    for(ClientHandler c : Server.ar)
                    {
                        if(c.isloggedin == true)
                        {
                            if(c.getName().equals(recipient))
                            {
                                tempDos = c.getDos();
                                break;
                            }
                        }
                    }
                    File myFile = new File (programPath+"/"+name+"/"+MsgToSend);
                    if(myFile.isDirectory())
                    {
                        tempDos.writeUTF(MsgToSend+"#INCOMEEMPTYDIR");
                    }
                    else if(myFile.length() == 0)
                    {
                        tempDos.writeUTF(MsgToSend+"#INCOMEEMPTYFILE");
                    }
                    else
                    {
                        tempDos.writeUTF(MsgToSend+"#INCOME");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SendOrDownloadFile.sendFile(MsgToSend,name,tempDos);
                    }

                }



            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources 
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
} 
