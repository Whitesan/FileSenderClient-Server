import javax.swing.*;
import java.awt.*;
import java.beans.DefaultPersistenceDelegate;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Oserve selected Client folder
 */
public class FileListener implements Runnable {

    //private  DefaultListModel<String> list;
    /**
     * Path to file
     */
    private  String path;
    /**
     * Frame object used to refresh window
     */
    private  JFrame frame;
    /**
     * Name of selected Client
   */
    static String selectedClient;

    static String user;
    FileListener( JFrame frame)
    {
        this.frame = frame;

    }

    /**
     * Set client
     * @param client name of client
     */
    static void setSelectedClient(String client)
    {
        selectedClient = client;

    }
    @Override
    public void run() {
        DefaultListModel<String> deletedItems = new DefaultListModel<>();
        DefaultListModel<String> newItems = new DefaultListModel<>();
        for(;;) {
            if(Main.isMainWindowInitialize)
            {
               
                if(Main.listModelFile !=null && user == null)
                {

                    if(!Main.listModelFile.isEmpty())
                        Main.listModelFile.clear();

                    newItems.clear();
                    deletedItems.clear();
                }

                if(selectedClient != null)
                {
                    if(user == null)
                        user = selectedClient;

                    if(!user.equals(selectedClient))
                    {

                        user = selectedClient;

                        if(!Main.listModelFile.isEmpty())
                            Main.listModelFile.clear();

                        newItems.clear();
                        deletedItems.clear();
                    }


                    
                    path = System.getProperty("user.dir")+"/"+ user;
                  
                    deletedItems.clear();
                    //Copy all items from list to deleted list
                    for(int i=0;i<Main.listModelFile.getSize();i++)
                    {

                        deletedItems.addElement(Main.listModelFile.elementAt(i));

                       
                    }
                    newItems.clear();

                    
                    Path dir = Paths.get(path);
                   

                    // Read all files and insert to newItems list
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                        for (Path file : stream) {
                            String temp = String.valueOf(file.getFileName());

                            if(!deletedItems.isEmpty())
                                deletedItems.removeElement(temp);

                            newItems.addElement(temp);



                        }

                        //add new items
                        for(int i=0;i<newItems.getSize();i++)
                        {

                            int flag = 0;
                            for(int j=0;j<Main.listModelFile.getSize();j++)
                            {

                                if(Main.listModelFile.getElementAt(j).equals(newItems.elementAt(i)))
                                {
                                    flag = 1;
                                    break;
                                }


                            }

                            if(flag == 0 || Main.listModelFile.isEmpty())
                                Main.listModelFile.addElement(newItems.elementAt(i));

                            
                        }
                        //Remove deleted item
                        for (int j=0;j<deletedItems.getSize();j++) {


                            Main.listModelFile.removeElement(deletedItems.getElementAt(j));

                        }


                        if(Main.frame != null)
                            Main.frame.revalidate();



                    } catch (IOException | DirectoryIteratorException x) {

                    }

                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}