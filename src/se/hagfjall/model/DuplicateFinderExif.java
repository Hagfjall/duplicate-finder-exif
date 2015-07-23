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
        Iterator<Map.Entry<ExifData, Object>> iterator = tagMapExifData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ExifData, Object> next = iterator.next();
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
        Iterator<ExifData> iterator = tagMapExifData.getCollection(exifData).iterator();
        while (iterator.hasNext()) {
            ExifData e = iterator.next();
            if (!e.equalsOnFile(exifData)) {
                ret.add(e);
            }
        }
        return ret;
    }

    public void moveDuplicates(){
        List<ExifData> originals = getOriginals();
        for (ExifData original : originals) {
            moveDuplicates(original);
        }
    }
    /*
    /Bilder/2012/01/original.jpg
    /Bilder/2012/01/kopia.jpg -> /kopior/2012/01/original.jpg/2012/01/kopia.jpg
     */

    //TODO implementera flytten av filer
    public void moveDuplicates(ExifData original) {
        List<ExifData> duplicates = getDuplicates(original);
        for (ExifData dup : duplicates) {
            String folderToOriginal = original.getFilename();
            String destination = DuplicateFinderExif.outputFolder + "/" + folderToOriginal + "/" + dup.getFilename();
            File destinationFile = new File(destination.substring(0,destination.lastIndexOf("/")));
            boolean suc = destinationFile.mkdirs();
            try {
                Files.move(Paths.get(dup.getCanonicalPath()), Paths.get(destination), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public void moveFiles() {
//        Path parentDir = FileSystems.getDefault().getPath(outputFolder);
//        new File(outputFolder).mkdir();
//        for (ExifData d : duplicatedFiles) {
//            Path file = FileSystems.getDefault().getPath(d.getFilename());
//            File file_target = new File(parentDir.toFile(), file.getFileName().toString());
//            String file_target_old = null;
//            try {
//                file_target_old = file_target.getCanonicalPath();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int filenameIncrementer = 1;
//            while (file_target.exists()) {
//                String file_target_new = file_target_old.substring(0, file_target_old.lastIndexOf("."));
//                file_target_new += "-" + filenameIncrementer;
//                file_target_new += file_target_old.substring(file_target_old.lastIndexOf("."));
//                file_target = new File(file_target_new);
//                filenameIncrementer++;
//            }
//            try {
//                Path target = FileSystems.getDefault().getPath(file_target.getCanonicalPath());
//                Files.move(file, target, StandardCopyOption.ATOMIC_MOVE);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    public List<ExifData> getOriginals() {
        ArrayList<ExifData> ret = new ArrayList<>();
        for (ExifData key : tagMapExifData.keySet()) {
            ret.add(key);
//            Iterator<ExifData> iterator = tagMapExifData.getCollection(key).iterator();
//            int i = 0;
//            while (iterator.hasNext()) {
//                if (i > 0) {
//                    break;
//                }
//                i++;
//            }
//            while (iterator.hasNext()) {
//                ExifData exifData = iterator.next();
//                if (i > 0) {
//                    temp.add("*" + stripoutInputFolderFromFilePath(exifData.getFilename()));
//                    break;
//                }else {
//                    temp.add(stripoutInputFolderFromFilePath(exifData.getFilename()));
//                }
//                i++;
//            }
        }
//        System.out.print(temp  +  "= returning: ");
//        for (String s : ret) {
//            System.out.print(s + " , ");
//        }
        return ret;
    }

}
