import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Piotr Misztal
 */
public class Main implements ActionListener{

    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    /**
     * Main window intialized or no
     */
    static boolean isMainWindowInitialize = false;
    /**
     * Main window frame
     */
     static JFrame frame; // Main window
    /**
     * Name of selected Client in GUI
     */
    static String selectedClient;
    /**
     * Status label displayed in GUI
     */
    static JLabel status; // Status label
    /**
     * List of clients files
     */
    private static JList<String> files; // Files in local folder
    /**
     * List of available clients
     */
     static JList<String> clients; // list of available clients
    /**
     * List that store clients, allow add and remove item
     */
    static DefaultListModel<String> listModel; // list for clients
    /**
     * List that store files, allow add and remove item
     */
    static DefaultListModel<String> listModelFile; //list for files

    /**
     * File listener Object
     */
    private static FileListener Flistener;

    /**
     * set up application's main window
     */
    public static void mainWindow(Container pane) {

        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        if (shouldFill) {
            //natural height, maximum width
            c.fill = GridBagConstraints.HORIZONTAL;
        }

        //Set Column name Pliki
        JLabel label1;
        label1 = new JLabel("Pliki");

        if (shouldWeightX) {
            c.weightx = 0.5;
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        pane.add(label1, c);

        //Set column name status
        JLabel label2;
        label2 = new JLabel("Status");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        label2.setBackground(Color.BLUE);
        pane.add(label2, c);

        //Set column name klienci
        JLabel label3;
        label3 = new JLabel("Klienci");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        pane.add(label3, c);


        files = new JList<String>();

        listModelFile = new DefaultListModel<String>();
        files.setModel(listModelFile);





        JScrollPane filess = new JScrollPane(files);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(filess, c);


           clients = new JList<String>();
        listModel = new DefaultListModel<String>();

        clients.setModel(listModel);

        clients.addMouseListener( new MouseAdapter()
        {
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isLeftMouseButton(e) ) {

                    clients.setSelectedIndex(clients.locationToIndex(e.getPoint()));
                    selectedClient  = clients.getModel().getElementAt(clients.getSelectedIndex());
                    FileListener.setSelectedClient(selectedClient);

            }
            }
        });


        JScrollPane clientss = new JScrollPane(clients);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 1;
        pane.add(clientss, c);

        //Set status window
        status = new JLabel("Server");
        status.setHorizontalAlignment(JLabel.CENTER);
       
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        pane.add(status, c);



    }


    //start main Window

    /**
     * Create Application's main window
     */
    private static void createAndShowGUI() {
        //Create and set up the window.

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800,600));

        //Set up the content pane.
        mainWindow(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        isMainWindowInitialize = true;

    }

    /**
     * Start Main window of the application
     * Run Server and File listener threads
     */
    public static void main(String[] args) {
        

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();

                }
            });
            Thread server = new Thread(new Server());
            server.start();


        Flistener = new FileListener(frame);
            Thread FileListen = new Thread(Flistener);
            FileListen.start();

    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }



}