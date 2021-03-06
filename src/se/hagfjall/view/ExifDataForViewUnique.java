package se.hagfjall.view;

import se.hagfjall.model.ExifData;

/**
 * Created by Wycheproof on 15-07-13.
 */
public class ExifDataForViewUnique extends AbstractExifDataForView {

    public ExifDataForViewUnique(ExifData exifData) {
        this.exifData = exifData;
    }

    @Override
    public String toString() {
        return "*" + exifData.getFilename();
    }
}
