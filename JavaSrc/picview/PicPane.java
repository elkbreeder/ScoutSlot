package picview;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicPane extends JComponent {

    private File file;

    public PicPane(File file) throws IOException {
        this.file = file;
        BufferedImage img = loadImage();
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        // Just for debugging
        setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    /**
     * Fucking method not working @TODO Work out how to resize img
     */
    private static BufferedImage resize(BufferedImage img, int height) {
        int width = (int) ((((double) img.getHeight()) / ((double) img.getWidth())) * height);
        System.out.println(height + " : " + width);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();

        return resized;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        try {
            BufferedImage img = loadImage();
            int x = getWidth() / 2 - img.getWidth() / 2;
            int y = getHeight() / 2 - img.getHeight() / 2;
            graphics.drawImage(ImageIO.read(file), x, y, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load and process the image so it can be used in the gui
     *
     * @return processed image
     */
    private BufferedImage loadImage() throws IOException {
        // return resize(ImageIO.read(file), 300);
        return ImageIO.read(file);
    }


}
