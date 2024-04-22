package util;

import dataowner.SpatialData;
import server.VONode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static util.BloomFilter.charset;
import static util.BloomFilter.createHashes;
import static vsQuery.Main.B;
import static vsQuery.Main.K;

public class Utils {

    public static long serialize(String path, Object c) {
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

    public static List<Integer> getHashPos(String input, int bitSetSize) {
        byte[] bytes = input.getBytes(charset);
        List<Integer> hashPos = new ArrayList<>();
        int[] hashes = createHashes(bytes, K);
        for (int hash : hashes) hashPos.add(Math.abs(hash % bitSetSize));
        hashPos.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        return hashPos;
    }

    public static long getFileSize(String path) {
        FileInputStream fis  = null;
        try {
            File file = new File(path);
            String fileName = file.getName();
            fis = new FileInputStream(file);
            return fis.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String IntegerToBinStr(int x, int len) {
        String binStr = Integer.toBinaryString(x);
        while (binStr.length() < len) {
            binStr = "0" + binStr;
        }
        return binStr;
    }

    public static List<String> getZeroPrefix(int num) {
        List<String> ans = new LinkedList<>();
        String binStr = IntegerToBinStr(num, B);
        for (int i = binStr.length() - 1; i >= 0; --i) {
            if (binStr.charAt(i) == '0') {
                ans.add(binStr.substring(0, i) + "1");
            }
        }
        return ans;
    }
    public static List<String> getOnePrefix(int num) {
        List<String> ans = new LinkedList<>();
        String binStr = IntegerToBinStr(num, B);
        for (int i = binStr.length() - 1; i >= 0; --i) {
            if (binStr.charAt(i) == '1') {
                ans.add(binStr.substring(0, i + 1));
            }
        }
        return ans;
    }
    public static void addTagForPrefix(List<String> prefixes, String tag) {
        prefixes.replaceAll(s -> tag + s);
    }

    public static List<Double> computeVariance(List<SpatialData> arr) {

        List<Double> ans = new ArrayList<>(2);
        int sumX = 0, sumY = 0;
        int n = arr.size();
        for (SpatialData data : arr) {
            sumX += data.x;
            sumY += data.y;
        }
        double avgX = sumX * 1.0 / n, avgY = sumY * 1.0 / n;
        double varianceX = 0, varianceY = 0;
        for (SpatialData data : arr) {
            varianceX += Math.pow(data.x - avgX, 2) / n;
            varianceY += Math.pow(data.y - avgY, 2) / n;
        }
        ans.add(varianceX);
        ans.add(varianceY);
        return ans;
    }


    public static void main(String[] args) {
    }
}
