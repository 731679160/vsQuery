package vsQuery;

import dataowner.DO;
import dataowner.SearchToken;
import dataowner.SpatialData;
import server.SP;
import server.SearchRes;
import server.VONode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.Utils.getFileSize;

//CA: maxX:1244070maxY:420009minX:1144660minY:326230
public class Main {
    public static final int B = 12;

    public static final int K = 8;
    public static final double C = 10 * K;
    public static final int N = 50;


//    state = 0 is basic scheme，state = 1 is promoted basic scheme，state = 2 is the last scheme

    public static void main(String[] args) throws Exception {

        String path = "./src/Uniform.txt";
        oneTest(path, 2);

        System.out.println("---------------------------------------------");
        path = "D:\\mycode\\mywork\\dataset\\Uniform60w.txt";
        oneTest(path, 2);
    }

    public static void oneTest(String path, int state) throws Exception {
        DO dataOwner = new DO(path, state);

        SP server = new SP(dataOwner.getRoot(), state);

//        SpatialData p1 = new SpatialData(540, 230);
//        SpatialData p2 = new SpatialData(540, 230);
        SearchToken searchToken = dataOwner.getSearchToken(0.02);
//        SearchToken searchToken = dataOwner.getSearchToken(p1, p2);

        SearchRes searchRes = server.search(searchToken);

        System.out.println("scheme" + state + "：verification result：" + dataOwner.verify(searchRes, searchToken, state));
        System.out.println("scheme" + state + ":query result：" + searchRes.res.toString());
        System.out.println("scheme" + state + ":verification information size：" + getVOSize("./src/vo0.txt", searchRes.VONodeRoot) / 1024 + "kb");

        server.getIndexSize();
    }

    public static long getVOSize(String path, VONode vo) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));
            objectOutputStream.writeObject(vo);
            objectOutputStream.close();
            return getFileSize(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
