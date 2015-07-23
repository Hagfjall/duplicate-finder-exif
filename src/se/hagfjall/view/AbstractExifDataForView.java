package se.hagfjall.view;

import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import se.hagfjall.model.ExifData;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Wycheproof on 15-07-13.
 */
public abstract class AbstractExifDataForView {

    protected ExifData exifData;

    public ExifData getExifData() {
        return exifData;
    }

    public String getFileInfo() {
        StringBuilder sb = new StringBuilder(512);
        File file = new File(exifData.getCanonicalPath());
        sb.append("Size: " + readableFileSize(file.length()) + "\n");
        ExifSubIFDDirectory directory = exifData.getMetadata().getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        int width, height;
        if (directory != null) {
            try {
                width = directory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                height = directory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
                sb.append("Width: " + width + "\n");
                sb.append("Height: " + height + "\n");
            } catch (MetadataException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
