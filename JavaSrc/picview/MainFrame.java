package picview;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame implements MouseListener, ActionListener {

    private static final String TITEL = "Einarmiger Bandit - Fotos";
    private static final String LABEL_MARK = "Markierung";
    private static final String CONFIRM_MARK = "Foto markieren?";
    private static final String CONFIRM_UNMARK = "Markierung entfernen?";
    private static final String MENU_RELOAD = "Neu laden";
    private boolean loading;
    private File defaultDir;
    private JComponent bigView;
    private JScrollPane scrollView;
    private Box scrollBox;
    private JMenuItem reloadMenuItem;
    private JPopupMenu popupMenu;
    private List<PicPane> picBuffer;

    public MainFrame() {
        super(TITEL);
        init();

        new LoadingThread();
    }

    public MainFrame(String path) {
        super(TITEL);
        init();

        this.defaultDir = new File(path);

        new LoadingThread();
    }

    public static void main(String[] args) {
        if (args.length == 0)
            new MainFrame();
        else
            new MainFrame(args[0]);
    }

    private void init() {
        // Set/Init default attributes
        defaultDir = new File(System.getProperty("user.dir"));

        // Init UI objects
        bigView = new JPanel();
        bigView.setBorder(BorderFactory.createLineBorder(Color.RED));
        scrollBox = Box.createHorizontalBox();
        scrollView = new JScrollPane(scrollBox,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollView.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        scrollView.setPreferredSize(new Dimension(1000, 300));
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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createMenu() {
        popupMenu = new JPopupMenu();
        reloadMenuItem = new JMenuItem(MENU_RELOAD);
        reloadMenuItem.addActionListener(this);
        popupMenu.add(reloadMenuItem);
        addMouseListener(new PopupListener(popupMenu));
    }

    /**
     * Load all images from directory (attribute)
     */
    private void loadImages() {
        loading = true;
        // Check if dir exists
        if (!defaultDir.isDirectory()) {
            System.err.println("Default dir (" + defaultDir.getAbsolutePath() + ") is not a directory!");
            return;
        }
        // Load all files and according to image filename filter
        File[] imgFiles = defaultDir.listFiles(
                MyFilenameFilter.imageFilter());
        // Exception handling (wtf can listFiles() return null?)
        if (imgFiles == null) {
            System.err.println("No image files found");
            return;
        }
        // Sort by filename to get chronologic order
        Arrays.sort(imgFiles);
        for (File imgFile : imgFiles) {
            try {
                addPreviewImage(imgFile);
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

    private void addPreviewImage(File file) throws IOException {
        // Load image into PicPane and add to gui
        System.out.println("Loading " + file.getAbsolutePath());
        PicPane panel = new PicPane(file, scrollBox.getHeight());
        panel.addMouseListener(this);
        panel.addMouseListener(new PopupListener(popupMenu));
        scrollBox.add(panel);
        System.out.println("\tLoaded " + file.getAbsolutePath());
    }

    private void addBigView(File file) throws IOException {
        int height = getHeight() - scrollView.getHeight();
        System.out.println("BigViewHeight: " + height);
        getContentPane().remove(bigView);
        bigView = new PicPane(file, height);
        bigView.setBorder(BorderFactory.createLineBorder(Color.RED));
        getContentPane().add(bigView, BorderLayout.CENTER);
        getContentPane().revalidate();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        Object src = mouseEvent.getSource();

        if (src instanceof PicPane && SwingUtilities.isLeftMouseButton(mouseEvent)) {
            PicPane panel = (PicPane) src;
            if (!panel.isMarked()) {
                int response =
                        JOptionPane.showConfirmDialog(this,
                                CONFIRM_MARK,
                                LABEL_MARK,
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    panel.setMarked(true);
                }
            } else {
                int response =
                        JOptionPane.showConfirmDialog(this,
                                CONFIRM_UNMARK,
                                LABEL_MARK,
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    panel.setMarked(false);
                }
            }
        }
        ((JPanel) getContentPane()).updateUI();
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
        }
    }

    private class LoadingThread extends Thread {
        private LoadingThread() {
            start();
        }

        @Override
        public void run() {
            super.run();
            while (loading) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            scrollBox.removeAll();
            loadImages();
        }
    }
}
