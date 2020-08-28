import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Piotr Misztal
 */
public class Main implements ActionListener{

    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    private static boolean flag;
    /**
     * Main window frame
     */
    private static JFrame frame; // Main window
    /**
     * Name of the application user
     */
    private static String userName; // username
    /**
     * Path to local folder
     */
    static String path; // path for local folder
    /**
     * Path to a file selected in GUI
     */
    private static String selectedPath; // selected folder path to send
    /**
     * Name of the file selected in GUI
     */
    private static String selectedFile;
    /**
     * Status label - informs client what application is doing
     */
    static JLabel status; // Status label
    /**
     * List of all files in local folder
     */
    private static JList<String> files; // Files in local folder
    /**
     * List of all clients active in Server
     */
    private static JList<String> clients; // list of available clients
    /**
     * Holds all filles from local folder it allows to add and remove them
     */
    static DefaultListModel<String> listModel;
    /**
     * Holds all Clients available in server, it allows to add and remove them
     */
    static DefaultListModel<String> listModelClients;
    /**
     * Synchronize operation in lists (adding removing elements)
     */

    private static ReentrantLock mutex = new ReentrantLock();
    /**
     * Client object
     */
    private static Client client;
    /**
     * This method setup Main window of the application: labels,lists,buttons
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
        mutex.lock();
        listModel = new DefaultListModel<String>();
        files.setModel(listModel);
        mutex.unlock();

        files.addMouseListener( new MouseAdapter()
        {
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isRightMouseButton(e) ) {
                    files.setSelectedIndex(files.locationToIndex(e.getPoint()));
                    String selectedItem  = files.getModel().getElementAt(files.getSelectedIndex());

                    selectedPath = path + selectedItem + "/";
                    selectedFile = selectedItem;
                    //client.setMessage(selectedPath);

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem itemSend = new JMenuItem("Send");
                    itemSend.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {

                            //status.setText("Wybieram klienta");
                            System.out.println("Remove the element in position " + files.getSelectedValue());
                            createFrame();


                        }
                    });
                    menu.add(itemSend);
                    menu.show(files, e.getPoint().x, e.getPoint().y);
                }
            }
        });


        JScrollPane filess = new JScrollPane(files);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(filess, c);


        //Set list of clients

       // String klienty[] = {"Jarek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek", "Whitesan", "arek"};
        clients = new JList<String>();
        listModelClients = new DefaultListModel<String>();
       clients.setModel(listModelClients);
        JScrollPane clientss = new JScrollPane(clients);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 1;
        pane.add(clientss, c);

        //Set status window
        status = new JLabel("aa");
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setText("Nic nie robie");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        pane.add(status, c);



    }

    /**
     * Creates login window: User need to fill username and path to local folder
     */
    public static void loginWindow() {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Login Window");
        frame.setMinimumSize(new Dimension(400,250));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(10, 10, 80, 40);
        frame.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 300, 40);
        frame.add(userText);

        JLabel pathLabel = new JLabel("Path");
        pathLabel.setBounds(10, 70, 80, 40);
        frame.add(pathLabel);

        JTextField pathText = new JTextField(20);
        pathText.setBounds(100, 70, 300, 40);
        frame.add(pathText);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(10, 110, 80, 40);
        frame.add(loginButton);


        ActionListener loginButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                userName = userText.getText();

                System.out.println(userName);
                mutex.lock();
                path = pathText.getText();
                mutex.unlock();
                System.out.println(path);


                flag = true;
                frame.dispose();
            }
        };
        loginButton.addActionListener(loginButtonListener);


    }
    //New frame, popup after click file to send, we can choose to which client send file

    /**
     * Create window: Show active clients, choose one and send him file
     */
    public static void createFrame()
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame frame = new JFrame("Clients");
                frame.setMinimumSize(new Dimension(300,400));
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setOpaque(true);


                JList<String> copyofClients = new JList<String>();
                copyofClients.setModel(listModelClients);
                JScrollPane clientpane = new JScrollPane( copyofClients);
                panel.add(clientpane);

                JButton b = new JButton("Wyślij");
                b.setHorizontalAlignment(JLabel.RIGHT);
                b.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        client.setMessage(selectedFile);
                        client.setReceiver(copyofClients.getSelectedValue());
                        System.out.println( copyofClients.getSelectedValue());


                    }
                });

                panel.add(b);

                frame.getContentPane().add(BorderLayout.CENTER, panel);
                frame.pack();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                frame.setResizable(false);

            }
        });
    }
    //start main Window

    /**
     * Running main window application
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new JFrame(userName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800,600));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.setMessage("Closing");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }


            }
        });
        //Set up the content pane.
        mainWindow(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Running application widnows, Client and File listener threads
     */
    public static void main(String[] args) throws InterruptedException {

        flag = false;
        int flag2 = 0;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loginWindow();
            }
        });

        for(;;)
        {
            if(flag == true)
            {

                if(flag == true)
                {
                    client = new Client(userName);
                    Thread clientThread= new Thread(client);
                    clientThread.start();
                }


                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            createAndShowGUI();

                            Thread th = new Thread(new FileListener(client,frame,listModel,path,mutex));
                                    th.start();
                        }
                    });


                flag = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }



}