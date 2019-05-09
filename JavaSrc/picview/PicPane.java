package picview;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicPane extends JComponent {

    private File file;
    private BufferedImage image;
    private int height;
    private boolean marked;

    public PicPane(File file) throws IOException {
        this.file = file;
        image = ImageIO.read(file);
        this.height = image.getHeight();
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    public PicPane(File file, int height) throws IOException {
        this.file = file;
        this.height = height;
        loadResizedImage();
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

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
    }

    private void loadResizedImage() throws IOException {
        BufferedImage origImg = ImageIO.read(file);
        int width = (int) (((double) origImg.getWidth() / origImg.getHeight()) * height);
        image = resizeImage(
                origImg,
                width,
                height
        );
    }

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
