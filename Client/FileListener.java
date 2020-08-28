import javax.swing.*;
import java.awt.*;
import java.beans.DefaultPersistenceDelegate;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Checking directory for files once a second and removing/adding files to list
 */
public class FileListener implements Runnable {

    /**
     * List of Files in local folder
     */
    private  DefaultListModel<String> list;
    /**
     * Path to local folder
     */
    private  String path;
    /**
     * Main window frame, used to refresh it
     */
    private  JFrame frame;
    /**
     * Mutex used for synchronize adding and romoving operations in list
     */
    private  ReentrantLock mutex ;
    /**
     * Client object
     */
    private  Client client;
    /**
     * Inform if Server is busy
     */
    static boolean serverBusy;

    /**
     *
     * @param client class of the client it's needed to send messages to server
     * @param frame main window frame
     * @param files list of files in the local folder
     * @param path Path to local folder
     * @param mutex synchrionizes operation on file list
     */
    FileListener( Client client,JFrame frame,DefaultListModel<String> files,String path,ReentrantLock mutex)
    {
        this.client = client;
        this.frame = frame;
        this.list= files;
        this.path = path;
        this.mutex = mutex;
    }


    @Override
    public void run() {
        DefaultListModel<String> deletedItems = new DefaultListModel<>();
        DefaultListModel<Path> newItems = new DefaultListModel<>();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(;;) {
            if(!Client.clientDownloadingFIle) {
                Main.status.setText("Checking...");
                deletedItems.clear();
                //Copy all items from list to deleted list
                for(int i=0;i<list.getSize();i++)
                {
                    mutex.lock();
                    deletedItems.addElement(list.elementAt(i));
                    mutex.unlock();
                }
                newItems.clear();

                mutex.lock();
                Path dir = Paths.get(path);
                mutex.unlock();

                // Read all files and insert to newItems list
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path file : stream) {

                        String temp = String.valueOf(file.getFileName());

                        if(!deletedItems.isEmpty())
                            deletedItems.removeElement(temp);

                        newItems.addElement(file);

                    }

                    //add new items
                    for(int i=0;i<newItems.getSize();i++)
                    {
                        mutex.lock();
                        int flag = 0;
                        for(int j=0;j<list.getSize();j++)
                        {
                            if(list.getElementAt(j).equals(String.valueOf(newItems.elementAt(i).getFileName())))
                            {
                                flag = 1;
                                break;
                            }

                        }
                        if(flag == 0 || list.isEmpty())
                        {
                            list.addElement(String.valueOf(newItems.elementAt(i).getFileName()));
                            //client.setMessage(String.valueOf(newItems.elementAt(i).getFileName())+"$Added");
                            serverBusy = true;
                            if(newItems.elementAt(i).toFile().length() == 0)
                            {
                                Main.status.setText("Sending...");
                                client.setMessage(String.valueOf(newItems.elementAt(i).getFileName())+"$AddedEmpty");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Main.status.setText("Checking...");
                            }
                            else if(newItems.elementAt(i).toFile().isDirectory())
                            {
                                Main.status.setText("Sending...");
                                client.setMessage(String.valueOf(newItems.elementAt(i).getFileName())+"$AddedDir");

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Main.status.setText("Checking...");
                                //System.out.println(String.valueOf(newItems.elementAt(i).getFileName()));
                                //Path path = Paths.get(Main.path+String.valueOf(newItems.elementAt(i).getFileName()));
                                //Files.createDirectories(path);
                            }
                            else
                            {
                                Main.status.setText("Sending...");
                                 mutex.lock();
                    		 frame.revalidate();
                    		 mutex.unlock();
                                client.setMessage(String.valueOf(newItems.elementAt(i).getFileName())+"$Added");

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                SendOrDownloadFIle.sendFile(Main.path+String.valueOf(newItems.elementAt(i).getFileName()),Client.dos);
                                Main.status.setText("Checking...");
                            }

                        }
                        while(serverBusy)
                        {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        mutex.unlock();
                    }
                    //Remove deleted item
                    for (int j=0;j<deletedItems.getSize();j++) {
                        mutex.lock();
                        list.removeElement(deletedItems.getElementAt(j));
                        client.setMessage(deletedItems.getElementAt(j)+"$Removed");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mutex.unlock();
                    }

                    mutex.lock();
                    frame.revalidate();
                    mutex.unlock();

                } catch (IOException | DirectoryIteratorException x) {

                }
                Main.status.setText("Nothing");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
