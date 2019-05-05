package picview;

import java.io.File;
import java.io.FilenameFilter;

public class MyFilenameFilter implements FilenameFilter {

    private String[] extensions;

    private MyFilenameFilter(String[] extensions) {
        this.extensions = extensions;
    }

    public static MyFilenameFilter imageFilter() {
        return new MyFilenameFilter(new String[]{
                "gif", "png", "bmp", "jpg" // @TODO adjust file extension list for image filter
        });
    }

    @Override
    public boolean accept(final File dir, final String name) {
        for (final String ext : extensions) {
            if (name.endsWith("." + ext)) {
                return (true);
            }
        }
        return (false);
    }
}
