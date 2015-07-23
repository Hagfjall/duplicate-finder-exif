package se.hagfjall.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Wycheproof on 15-07-17.
 */
public class DeleteTest {

    public static void main(String[] args) {
        String path = "/Users/Wycheproof/Desktop/pic2.txt";
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
