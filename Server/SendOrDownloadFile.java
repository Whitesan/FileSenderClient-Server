import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;

/**
 * Sending and Reading files Server-Client  Client-Server
 */
public class SendOrDownloadFile {



    /**
     * Maximal filesieze to be read
     */
    public final static int FILE_SIZE = 6022386; // file size temporary hard coded

    /**
     * Reads file from Client
     * @param FILE_TO_RECEIVED Path to file
     * @param is Input stream
     * @param os Output Stream
     * @throws IOException
     */
     static void readFile (String FILE_TO_RECEIVED, DataInputStream is,DataOutputStream os) throws IOException {
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {

            System.out.println("Connecting...");
            File file = new File(FILE_TO_RECEIVED);

            // receive file
            byte [] mybytearray  = new byte [FILE_SIZE];
          
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            System.out.println("control");


            bytesRead = is.read(mybytearray);

            current = bytesRead;
            System.out.println(bytesRead);


            System.out.println("Check1");
            bos.write(mybytearray, 0 , current);
            System.out.println("Check2");
            bos.flush();

            System.out.println("File " + FILE_TO_RECEIVED
                    + " downloaded (" + current + " bytes read)");
            os.writeUTF("xd#Free");
            System.out.println("Free");
        }
        finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
          
        }
    }

    /**
     * Send filet to Client
     * @param FILE_TO_SEND filename to be send
     * @param clientName Name of destined client
     * @param dos Data output stream
     * @throws IOException
     */
    static void sendFile(String FILE_TO_SEND,String clientName,DataOutputStream dos)throws IOException
    {

        FileInputStream fis = null;
        BufferedInputStream bis = null;
       
        try {
         
            while (true) {
                System.out.println("Waiting...");
                try {
                    
                    File myFile = new File (System.getProperty("user.dir")+"/"+clientName+"/"+FILE_TO_SEND);
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                 
                    System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes) + to: ");
                    dos.write(mybytearray,0,mybytearray.length);
                    
                    System.out.println("Done.");
                    break;
                }
                finally {
                    if (bis != null) bis.close();

                }
            }
        }
        finally {
            
        }
    }
}
