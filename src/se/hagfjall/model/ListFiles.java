package se.hagfjall.model;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Wycheproof on 15-05-20.
 */
public class ListFiles {

    private List<String> imageFiles = new LinkedList<String>();

    private static String imageFilesExtension[] = {"bmp", "dds", "gif", "jpg", "png", "psd", "pspimage", "tga", "thm", "tif", "tiff", "yuv"};

    public List<String> getFilesInDir(String directory) {
        listRecursive(new File(directory));
        return imageFiles;
    }

    private void listRecursive(File dir) {
        Arrays.stream(
                dir.listFiles((f, n) -> (f.isDirectory())))
                .forEach(unchecked((file) -> {
                    if (isImageFile(file.getName())) {
                        imageFiles.add(file.getCanonicalPath());
                    }
                    if (file.isDirectory()) {
                        listRecursive(file);
                    }
                }));
    }

    private boolean isImageFile(String file) {
        int extensionIndex = file.lastIndexOf(".");
        if(extensionIndex < 1 && file.length() < extensionIndex+4){
            return false;
        }
        for (String supportedExtension : imageFilesExtension) {
            String extension = file.substring(extensionIndex+1);
            if (extension.equalsIgnoreCase(supportedExtension)) {
                return true;
            }
        }
        return false;
    }

    private <T> Consumer<T>
    unchecked(CheckedConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    private interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }
}
