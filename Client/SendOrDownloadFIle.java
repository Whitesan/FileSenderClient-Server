import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that store 2 methods: for sending or downloading files to server
 *
 */
public class SendOrDownloadFIle {
    /**
     * Maximal size of reading file
     */
    public final static int FILE_SIZE = 6022386;
    /**
     *  Send file to Server
     * @param FILE_TO_SEND path to source file
     * @param os output stream to server
     */
    static void sendFile(String FILE_TO_SEND,DataOutputStream os)throws IOException
    {
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {

            while (true) {
                System.out.println("Waiting...");
                try {
                    File myFile = new File (FILE_TO_SEND);
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);

                    System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                    os.write(mybytearray,0,mybytearray.length);

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
    /**
     *  Send file to Server
     * @param FILE_TO_RECEIVED name of the incoming file
     * @param dis input stream to read form server
     */
    static void readFile (String FILE_TO_RECEIVED,DataInputStream dis) throws IOException {

        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {

            System.out.println("Connecting...");
            File file = new File(Main.path+FILE_TO_RECEIVED);


            byte [] mybytearray  = new byte [FILE_SIZE];

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);


            bytesRead = dis.read(mybytearray);

            current = bytesRead;
            System.out.println(bytesRead);


            System.out.println("Check1");
            bos.write(mybytearray, 0 , current);
            System.out.println("Check2");
            bos.flush();

            System.out.println("File " + FILE_TO_RECEIVED
                    + " downloaded (" + current + " bytes read)");



            System.out.println("Free");
        }
        finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
            Client.clientDownloadingFIle = false;

        }
    }
}
