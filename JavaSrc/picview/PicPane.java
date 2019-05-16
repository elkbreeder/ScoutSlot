package picview;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicPane extends JComponent {

    private static final int STRING_OFFSET = 5;
    private boolean marked;
    private int height;
    private File file;
    private BufferedImage image;

    public PicPane(File file, int height) throws IOException {
        this.file = file;
        this.height = height;
        loadResizedImage();
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    /**
     * Resizes an image
     *
     * @param originalImage image to be resized
     * @param dWidth        the width to resize the image to
     * @param dHeight       the height to resize the image to
     * @return the new resized image
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int dWidth, int dHeight) {
        int type = originalImage.getType();
        if (dWidth == 0 || dHeight == 0) {
            dWidth = 1;
            dHeight = 1;
        }
        BufferedImage resizedImage = new BufferedImage(dWidth, dHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, dWidth, dHeight, null);
        g.dispose();

        return resizedImage;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        int x = getWidth() / 2 - image.getWidth() / 2;
        int y = getHeight() / 2 - image.getHeight() / 2;
        graphics.drawImage(image, x, y, this);
        graphics.setColor(Color.green);
        Font font = new Font("Consolas",Font.BOLD,15);
        graphics.setFont(font);
        String string = file.getName().substring(0, file.getName().lastIndexOf("."));
        graphics.drawString(string, x + STRING_OFFSET, y + font.getSize() + STRING_OFFSET);
    }

    /**
     * Loads the image and resizes it to match the given height via {@link #resizeImage(BufferedImage, int, int)}
     *
     * @throws IOException thrown if {@link ImageIO#read(File)} fails
     */
    private void loadResizedImage() throws IOException {
        BufferedImage origImg = ImageIO.read(file);
        int width = (int) (((double) origImg.getWidth() / origImg.getHeight()) * height);
        image = resizeImage(
                origImg,
                width,
                height
        );
    }

    /**
     * Sets a new height and resizes the image to match it
     *
     * @param height height to resize the panel
     * @throws IOException thrown if {@link ImageIO#read(File)} fails
     */
    public void resize(int height) throws IOException {
        this.height = height;
        loadResizedImage();
    }

    public File getFile() {
        return file;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
        if (marked) {
            setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
    }
}
