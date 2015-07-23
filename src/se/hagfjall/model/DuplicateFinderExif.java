package se.hagfjall.model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import org.apache.commons.collections4.map.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Created by Wycheproof on 15-05-22.
 */
public class DuplicateFinderExif {

    public static String inputFolder, outputFolder;
    List<String> files = new LinkedList<>();

    private MultiValueMap<ExifData, ExifData> tagMapExifData;
    private MultiValueMap<String, ExifData> fileMapExifData;
    int tagLengths[] = new int[400];
    private List originals;


    public DuplicateFinderExif(String inputFolder, String outputFolder) {
        try {
            this.inputFolder = new File(inputFolder).getCanonicalPath();
            this.outputFolder = new File(outputFolder).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void findDuplicates() {
        fileMapExifData = new MultiValueMap();
        tagMapExifData = new MultiValueMap();
        files = new ListFiles().getFilesInDir(inputFolder);
        for (String sfile : files) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(new File(sfile));
//                Main.print(metadata);
                ExifData ExifData = new ExifData(metadata, sfile);
//                System.out.println("\nkey: " + ExifData.getKey() + "__FILE__" + ExifData.getFilename());
                tagMapExifData.put(ExifData, ExifData);
            } catch (ImageProcessingException | IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println("DuplicateFinderExif->Number of images: " + tagMapExifData.size());
        for (Map.Entry<ExifData, Object> next : tagMapExifData.entrySet()) {
            List<ExifData> list = (List<ExifData>) next.getValue();
            for (ExifData ExifData : list) {
                System.out.println(ExifData);
            }
        }

    }

    public int getNumberOfDuplicates(ExifData original) {
        Iterator<ExifData> iterator = tagMapExifData.getCollection(original).iterator();
        int i = -1;
        while (iterator.hasNext()) {
            iterator.next();
            i++;
        }
        return i;
    }

    public List<ExifData> getDuplicates(ExifData exifData) {
        List<ExifData> ret = new ArrayList<>();
        for (ExifData e : tagMapExifData.getCollection(exifData)) {
            if (!e.equalsOnFile(exifData)) {
                ret.add(e);
            }
        }
        return ret;
    }

    public void moveDuplicates() {
        List<ExifData> originals = getOriginals();
        for (ExifData original : originals) {
            moveDuplicates(original);
        }
    }

    public void moveDuplicates(ExifData original) {
        List<ExifData> duplicates = getDuplicates(original);
        for (ExifData dup : duplicates) {
            String folderToOriginal = original.getFilename();
            String destination = DuplicateFinderExif.outputFolder + "/" + folderToOriginal + "/" + dup.getFilename();
            File destinationFile = new File(destination.substring(0, destination.lastIndexOf("/")));
            boolean suc = destinationFile.mkdirs();
            try {
                Files.move(Paths.get(dup.getCanonicalPath()), Paths.get(destination), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public List<ExifData> getOriginals() {
        ArrayList<ExifData> ret = new ArrayList<>();
        for (ExifData key : tagMapExifData.keySet()) {
            ret.add(key);
        }
        return ret;
    }

}
