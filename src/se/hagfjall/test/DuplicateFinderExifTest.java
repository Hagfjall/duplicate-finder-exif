package se.hagfjall.test;

import se.hagfjall.model.DuplicateFinderExif;
import se.hagfjall.model.ExifData;

/**
 * Created by Wycheproof on 15-07-13.
 */
public class DuplicateFinderExifTest {

    public static void main(String[] args) {
        DuplicateFinderExif duplicateFinderExif = new DuplicateFinderExif(args[0], args[1]);
        duplicateFinderExif.findDuplicates();

        System.out.println("Originals: " + duplicateFinderExif.getOriginals().size());
        for (ExifData s : duplicateFinderExif.getOriginals()) {
            System.out.println(s.getFilename());
        }


    }

}
