package picview;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainFrame extends JFrame {

    static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg"
    };
    private static final String TITEL = "Einarmiger Bandit - Fotos";
    private File defaultDir;
    private JComponent bigView;
    private JScrollPane scrollView;
    private Box scrollBox;

    public MainFrame() {
        super(TITEL);
        init();

        loadImages();
    }

    public MainFrame(String path) {
        super(TITEL);
        init();

        this.defaultDir = new File(path);

        loadImages();
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
        scrollBox = Box.createHorizontalBox();
        scrollView = new JScrollPane(scrollBox,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollView.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        scrollView.setPreferredSize(new Dimension(1000, 350));

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

    /**
     * Load all images from directory (attribute)
     */
    private void loadImages() {
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
                // Load image into PicPane and add to gui
                System.out.println("Loading " + imgFile.getAbsolutePath());
                PicPane panel = new PicPane(imgFile);
                scrollBox.add(panel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Add big image
        if (imgFiles.length > 0) {
            try {
                getContentPane().remove(bigView);
                bigView = new PicPane(imgFiles[imgFiles.length - 1]);
                getContentPane().add(bigView, BorderLayout.CENTER);
                getContentPane().revalidate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Update ui
        ((JPanel) getContentPane()).updateUI();
    }
}
