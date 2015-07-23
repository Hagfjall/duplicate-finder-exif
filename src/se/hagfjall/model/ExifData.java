package se.hagfjall.model;

import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

/**
 * Created by Wycheproof on 15-07-13.
 */
public class ExifData {
    private static final int EXIF_TAGS_EXIFSUBIFD[] = {ExifSubIFDDirectory.TAG_EXPOSURE_TIME,
            ExifSubIFDDirectory.TAG_FNUMBER, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL,
            ExifSubIFDDirectory.TAG_SHUTTER_SPEED, ExifSubIFDDirectory.TAG_APERTURE,
            ExifSubIFDDirectory.TAG_BRIGHTNESS_VALUE, ExifSubIFDDirectory.TAG_EXPOSURE_BIAS,
            ExifSubIFDDirectory.TAG_MAX_APERTURE, ExifSubIFDDirectory.TAG_METERING_MODE,
            ExifSubIFDDirectory.TAG_FOCAL_LENGTH};
    private static final int EXIF_TAGS_EXIFIFD0[] = {ExifIFD0Directory.TAG_MAKE, ExifIFD0Directory.TAG_MODEL,
            ExifIFD0Directory.TAG_SOFTWARE, ExifIFD0Directory.TAG_DATETIME};

    private String file;
    private String tags;
    private Metadata metadata;

    public ExifData(Metadata metadata, String file) {
        this.file = file;
        this.metadata = metadata;
        StringBuilder stringBuilder = new StringBuilder(400);
        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        if (gpsDirectory != null) {
            GeoLocation geoLocation = gpsDirectory.getGeoLocation();
            if (geoLocation != null) {
                stringBuilder.append(gpsDirectory.getGeoLocation().toString());
            }
        }
        Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (directory != null) {
            for (int tag : EXIF_TAGS_EXIFSUBIFD) {
                stringBuilder.append(directory.getString(tag));
            }
        }
        directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (directory != null) {
            for (int tag : EXIF_TAGS_EXIFIFD0) {
                stringBuilder.append(directory.getString(tag));
            }
        }
        tags = stringBuilder.toString();

    }

    @Override
    public boolean equals(Object o) {
        // not enough entropy to decide whether the images are equal.
        if (tags.length() < 82) {
            return false;
        }
        if (o instanceof ExifData == false) {
            return false;
        }
        ExifData comp = (ExifData) o;
        return tags.equals(comp.tags);
    }

    public boolean equalsOnFile(ExifData exifData) {
        return tags.equals(exifData.tags) && file.equals(exifData.file);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }


    public String getKey() {
        return tags;
    }

    public String getFilename() {
        return stripInputFolderFromFilePath(file);
    }

    public String getCanonicalPath() {
        return file;
    }

    @Override
    public String toString() {
        return "ExifData{" +
                "file='" + file + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    public Metadata getMetadata() {
        return metadata;
    }

    protected String stripInputFolderFromFilePath(String file) {
        return file.substring(file.indexOf(DuplicateFinderExif.inputFolder) + DuplicateFinderExif.inputFolder.length() + 1);
    }
}
