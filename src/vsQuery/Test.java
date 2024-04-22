package vsQuery;
import util.BloomFilter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.Utils.getFileSize;
import static vsQuery.Main.*;

public class Test {

    static long t(String path, Object c) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));
            objectOutputStream.writeObject(c);
            objectOutputStream.close();
            long fileSize = getFileSize(path);
            return fileSize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) throws FileNotFoundException {

        BloomFilter<String> bf = new BloomFilter<>(C, N, K);

        String path = "./src/test.txt";
        byte a = 0x01;
        int b = 0x01;
        System.out.println("bloom filter size after serialization" + t(path, bf));
        System.out.println("int size after serialization" + t(path, b));



    }
}
