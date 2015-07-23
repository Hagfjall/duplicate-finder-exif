package se.hagfjall.view;

import se.hagfjall.model.ExifData;

/**
 * Created by Wycheproof on 15-07-13.
 */
public abstract class AbstractExifDataForView {

    protected ExifData exifData;

    public ExifData getExifData(){
        return exifData;
    }
}
