package data_process;

import dataowner.SpatialData;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Process_Gowalla_data {

/*    public static String movePoint(String num, int moveCount) {
        StringBuilder ans = new StringBuilder();
        int tag = -1;
        for (int i = 0; i < num.length(); ++i) {
            if (tag != -1 && i - tag > moveCount || !(num.charAt(i) >= '0' && num.charAt(i) <= '9' || num.charAt(i) == '-' || num.charAt(i) == '.')) {
                return ans.toString();
            }
            if (num.charAt(i) == '.') {
                tag = i;
            } else {
                ans.append(num.charAt(i));
            }
        }
        return ans.toString();
    }

    public static boolean check(String s) {
        for (char c : s.toCharArray()) {
            if (!(c >= '0' && c <= '9' || c == '-')) return false;
        }
        return true;
    }
    public static HashMap<Integer, List<String>> readData(String path) throws IOException {
        int MN = Integer.MAX_VALUE;
        HashMap<Integer, List<String>> dataSet = new HashMap<>();
        int id = 0;
        File file = new File(path);
        if(file.isFile()&&file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null){
                String[] split = str.split("\t");
                if (split.length != 5) continue;
                String x = movePoint(split[2], 5);
                String y = movePoint(split[3], 5);
                dataSet.put(id++, Arrays.asList(x, y));
                MN = Math.min(MN, Math.min(Integer.parseInt(x), Integer.parseInt(y)));
            }
            scr.close();
            fla.close();
        }

        for (Map.Entry<Integer, List<String>> entry : dataSet.entrySet()) {
            List<String> value = entry.getValue();
            value.set(0, Integer.toString(Integer.parseInt(value.get(0)) - MN + 1));
            value.set(1, Integer.toString(Integer.parseInt(value.get(1)) - MN + 1));
        }

        System.out.println(MN);
        return dataSet;
    }*/

/*    public static void writeToLocal(String path, HashMap<Integer, List<String>> mp) throws IOException {
        try {
            File writeName = new File(path);
            writeName.createNewFile();
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                for (Map.Entry<Integer, List<String>> entry : mp.entrySet()) {
                    Integer id = entry.getKey();
                    List<String> value = entry.getValue();
                    out.write(Integer.toString(id));
                    out.write("\t");
                    out.write(value.get(0));
                    out.write("\t");
                    out.write(value.get(1));
                    out.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static List<String> readData(String path) throws IOException {
        List<String> dataSet = new ArrayList<>();
        File file = new File(path);
        if(file.isFile() && file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null) {
                dataSet.add(str);
            }
            scr.close();
            fla.close();
        }
        return dataSet;
    }

    public static void writeToLocal(List<String> dataSet, String path, int dataSize) throws IOException {
        try {
            File writeName = new File(path);
            writeName.createNewFile();
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                for (int i = 0; i < dataSize; ++i) {
                    System.out.println(i);
                    out.write(dataSet.get(i));
                    out.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        String path = "D:\\mycode\\mywork\\dataset\\CA_100w";
        List<String> readData = readData(path);

        String path_processed = "D:\\mycode\\mywork\\dataset\\CA_50w";

        writeToLocal(readData, path_processed, 500000);
    }
}
