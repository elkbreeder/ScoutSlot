package picview;

import com.jcraft.jsch.SftpException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame implements MouseListener, ActionListener, ComponentListener, WindowListener {

    public static final Color BACKGROUND_COLOR = Color.decode("#222222");
    private static final String TITEL = "Einarmiger Bandit - Fotos";
    private static final String LABEL_MARK = "Markierung";
    private static final String CONFIRM_MARK = "Foto markieren?";
    private static final String CONFIRM_UNMARK = "Markierung entfernen?";
    private static final String MENU_RELOAD = "Neu laden";
    private static final String MENU_DIASHOW = "Diashow";
    private static final String MENU_LOGIN = "SSH login";
    private static final String FILE_MARKED = "markedList.j";
    private static final int WAIT_SLEEP = 200;
    private boolean markedListLock;
    private boolean loading;
    private Synchronizer syncThread;
    private UpdateThread updateThread;
    private List<PicPane> picBuffer;
    private List<String> markedList;
    private File defaultDir;
    private JComponent bigView;
    private JScrollPane scrollView;
    private Box scrollBox;
    private JPopupMenu popupMenu;
    private JMenuItem reloadMenuItem;
    private JMenuItem diashowMenuItem;
    private JMenuItem loginMenuItem;

    public MainFrame() {
        super(TITEL);
        init();
    }

    /**
     * Main function to start the program
     *
     * @param args array with args - first arg is input path
     */
    public static void main(String[] args) {
        if (args.length == 0)
            new MainFrame();
    }

    /**
     * Initialize attributes, UI objects and the frame and configures the layout
     */
    private void init() {
        // Set/Init default attributes
        defaultDir = new File(Synchronizer.LOCAL_DIR);
        picBuffer = new ArrayList<>();
        markedList = loadMarked();
        syncThread = new Synchronizer();
        syncThread.init();
        updateThread = new UpdateThread(this, syncThread);

        // Init UI objects
        bigView = new JPanel();
        scrollBox = Box.createHorizontalBox();
        scrollView = new JScrollPane(scrollBox,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollView.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        scrollView.setPreferredSize(new Dimension(1000, 300));
        scrollView.getHorizontalScrollBar().setUnitIncrement(20);
        createMenu();

        // Configure Layout
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(bigView, BorderLayout.CENTER);
        contentPane.add(scrollView, BorderLayout.PAGE_END);

        // Configure Frame
        setSize(600, 400);
        setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(this);
        addWindowListener(this);
    }

    /**
     * Create the popup menu
     */
    private void createMenu() {
        popupMenu = new JPopupMenu();
        reloadMenuItem = new JMenuItem(MENU_RELOAD);
        diashowMenuItem = new JMenuItem(MENU_DIASHOW);
        loginMenuItem = new JMenuItem(MENU_LOGIN);
        reloadMenuItem.addActionListener(this);
        diashowMenuItem.addActionListener(this);
        loginMenuItem.addActionListener(this);
        popupMenu.add(reloadMenuItem);
        popupMenu.add(diashowMenuItem);
        popupMenu.add(loginMenuItem);
        addMouseListener(new PopupListener(popupMenu));
        bigView.addMouseListener(new PopupListener(popupMenu));
    }

    /**
     * Loads a serialized array of marked filenames
     *
     * @return a list with marked filenames
     */
    private List<String> loadMarked() {
        File markedFile = new File(FILE_MARKED);
        if (markedFile.isFile()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(markedFile));
                String[] sarr = (String[]) ois.readObject();
                return new ArrayList<>(Arrays.asList(sarr));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Writes the marked filenames to a file as a serialized array
     */
    public void writeMarked() {
        File markedFile = new File(FILE_MARKED);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(markedFile));
            oos.writeObject(markedList.toArray(new String[0]));
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initially loads the images from the directory and sets a image for the big panel
     */
    private void loadImages() {
        loading = true;
        // Check if dir exists
        if (!defaultDir.isDirectory()) {
            System.err.println("Default dir (" + defaultDir.getAbsolutePath() + ") is not a directory!");
            loading = false;
            return;
        }
        // Load all files and according to image filename filter
        File[] imgFiles = defaultDir.listFiles(
                MyFilenameFilter.imageFilter());
        // Exception handling (wtf can listFiles() return null?)
        if (imgFiles == null || imgFiles.length == 0) {
            System.err.println("No image files found");
            loading = false;
            return;
        }
        // Sort by filename to get chronologic order
        Arrays.sort(imgFiles);
        for (int i = imgFiles.length-1; i >= 0; i--) {
            try {
                PicPane panel = createPicPanel(imgFiles[i], scrollBox.getHeight());
                addPicPane(panel);
                ((JPanel) getContentPane()).updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            addBigView(imgFiles[imgFiles.length - 1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update ui
        ((JPanel) getContentPane()).updateUI();
        loading = false;
    }

    /**
     * Reloads the images from the directory and sets a image for the big panel
     * Should only be called after {@link #loadImages()}
     * The method creates a buffer and therefore does not affect the GUI for a long time
     */
    private void reloadImages() {
        loading = true;
        // Check if dir exists
        if (!defaultDir.isDirectory()) {
            System.err.println("Default dir (" + defaultDir.getAbsolutePath() + ") is not a directory!");
            loading = false;
            return;
        }
        // Load all files and according to image filename filter
        File[] imgFiles = defaultDir.listFiles(
                MyFilenameFilter.imageFilter());
        // Exception handling (wtf can listFiles() return null?)
        if (imgFiles == null) {
            System.err.println("No image files found");
            loading = false;
            return;
        }
        // Sort by filename to get chronologic order
        Arrays.sort(imgFiles);
        for (int i = imgFiles.length-1; i >= 0; i--) {
            try {
                PicPane panel = createPicPanel(imgFiles[i], scrollBox.getHeight());
                bufferPicPane(panel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadBuffer();

        // Update ui
        ((JPanel) getContentPane()).updateUI();
        loading = false;
    }

    /**
     * Creates a {@link PicPane}
     *
     * @param file   a image file as the content for the panel
     * @param height the height the panel has to fit in
     * @return the new {@link PicPane}
     * @throws IOException thrown if {@link PicPane#PicPane(File, int)} fails
     */
    private PicPane createPicPanel(File file, int height) throws IOException {
        PicPane panel = new PicPane(file, height);
        panel.addMouseListener(this);
        panel.addMouseListener(new PopupListener(popupMenu));
        if (markedList.contains(file.getName())) {
            panel.setMarked(true);
        }
        return panel;
    }

    /**
     * Adds a {@link PicPane} to the scroll box at the bottom of the layout
     *
     * @param panel the panel to be added
     */
    private void addPicPane(PicPane panel) {
        scrollBox.add(panel);
    }

    /**
     * Adds a {@link PicPane} to the buffer
     *
     * @param panel the panel to be added
     */
    private void bufferPicPane(PicPane panel) {
        if (!picBuffer.contains(panel)) {
            picBuffer.add(panel);
        }
    }

    /**
     * Adds all {@link PicPane}s in the buffer to the scroll box at the bottom of the layout and clears the buffer
     */
    private void loadBuffer() {
        scrollBox.removeAll();
        for (PicPane panel :
                picBuffer) {
            addPicPane(panel);
        }

        while (markedListLock) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (PicPane panel :
                picBuffer) {
            if (markedList.contains(panel.getFile().getName()))
                panel.setMarked(true);
            else
                panel.setMarked(false);
        }
        picBuffer = new ArrayList<>();
    }

    /**
     * Loads a picture in to the big view box in the center
     *
     * @param file the picture to be loaded
     * @throws IOException thrown if {@link PicPane#PicPane(File, int)} fails
     */
    private void addBigView(File file) throws IOException {
        int height = getContentPane().getHeight() - scrollView.getHeight();
        getContentPane().remove(bigView);
        bigView = new PicPane(file, height);
        ((PicPane) bigView).setMarked(markedList.contains(file.getName()));
        getContentPane().add(bigView, BorderLayout.CENTER);
        getContentPane().revalidate();
    }

    public void updatePics() {
        while (loading) {
            try {
                Thread.sleep(WAIT_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (scrollBox.getComponents().length == 0) {
            System.out.println("Loading images into UI... ");
            loadImages();
            System.out.println("\tDone");
        } else {
            System.out.println("Reloading images into UI... ");
            reloadImages();
            System.out.println("\tDone");
        }
    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        Object src = mouseEvent.getSource();

        if (src instanceof PicPane) {
            PicPane panel = (PicPane) src;
            if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                markedListLock = true;
                // Marks or unmarks a picture if desired
                if (!panel.isMarked()) {
                    /*
                    int response =
                            JOptionPane.showConfirmDialog(this,
                                    CONFIRM_MARK,
                                    LABEL_MARK,
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        panel.setMarked(true);
                        if (bigView instanceof PicPane) {
                            PicPane bigPane = (PicPane) bigView;
                            if (bigPane.getFile().getName().equals(panel.getFile().getName()))
                                bigPane.setMarked(true);
                        }
                        markedList.add(panel.getFile().getName());
                    }
                     */

                    panel.setMarked(true);
                    if (bigView instanceof PicPane) {
                        PicPane bigPane = (PicPane) bigView;
                        if (bigPane.getFile().getName().equals(panel.getFile().getName()))
                            bigPane.setMarked(true);
                    }
                    markedList.add(panel.getFile().getName());
                    System.out.println("Marking " + panel.getFile().getName());
                } else {
                    int response =
                            JOptionPane.showConfirmDialog(this,
                                    CONFIRM_UNMARK,
                                    LABEL_MARK,
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        panel.setMarked(false);
                        if (bigView instanceof PicPane) {
                            PicPane bigPane = (PicPane) bigView;
                            if (bigPane.getFile().getName().equals(panel.getFile().getName()))
                                bigPane.setMarked(false);
                        }
                        markedList.remove(panel.getFile().getName());
                        System.out.println("Unmarking " + panel.getFile().getName());
                    }
                }
                markedListLock = false;
            } else if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                // Loads a picture into the big center box
                try {
                    addBigView(panel.getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ((JPanel) getContentPane()).updateUI();
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object src = actionEvent.getSource();


        if (src == reloadMenuItem) {
            new LoadingThread();
        } else if (src == diashowMenuItem) {
            new DiaFrame(defaultDir);
        } else if (src == loginMenuItem) {
            syncThread.init();
        }
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        if (bigView instanceof PicPane) {
            try {
                ((PicPane) bigView).resize(bigView.getHeight());
                ((JPanel) getContentPane()).updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        System.out.println("Bye \uD83D\uDE4B \uD83D\uDE09");
        writeMarked();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }

    private class LoadingThread extends Thread {

        private LoadingThread() {
            start();
        }

        @Override
        public void run() {

            try {
                if (syncThread.isInitialized()) {
                    syncThread.sync();
                }
            } catch (SftpException e) {
                e.printStackTrace();
            }
            updatePics();
        }
    }
}
