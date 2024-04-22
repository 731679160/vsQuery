package dataowner;


import com.carrotsearch.sizeof.RamUsageEstimator;
import server.SP;
import server.SearchRes;
import server.VONode;
import util.BloomFilter;
import util.SHA;
import util.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static util.Utils.*;


public class DO {

    int maxX;
    int minX;
    int maxY;
    int minY;
    int state;
    SATreeNode root;
    String rootHash;
    public SATreeNode getRoot() {
        return root;
    }

    public DO(String path, int state) throws Exception {
        long s = System.currentTimeMillis();

        this.state = state;

        List<SpatialData> data = readSpatialData(path);

        getSpatialRange(data);

        root = buildTree(data);

        assert root != null;

        rootHash = SHA.HASHDataToString(root.getBfHash(0) + root.getLeftHash(0) + root.getRightHash(state));

        long e = System.currentTimeMillis();
        System.out.println("scheme" + state + " index construction time：" + (e - s) + "ms");
    }

    private String getRootHash() {
        return rootHash;
    }
    public boolean verify(SearchRes searchRes, SearchToken searchToken, int state) {
        long s = System.nanoTime();
        String verifyRes = verifyVO(searchRes.VONodeRoot, searchToken, state);
        long e = System.nanoTime();
        System.out.println("scheme" + state + "verification time：" + (e - s) * 1.0 / 1e9 + "ms");
        return Objects.equals(verifyRes, this.getRootHash());
    }
    private String verifyVO(VONode node, SearchToken searchToken, int state) {
        if (node.left != null) {
            node.leftNodeHash = verifyVO(node.left, searchToken, state);
        }
        if (node.right != null) {
            node.rightNodeHash = verifyVO(node.right, searchToken, state);
        }
        if (node.findTag != 0) {
            List<String> subToken = searchToken.getByIndex(node.findTag);

            for (String s : subToken) {
                s = node.randNumber + s;
                List<Integer> hashPos = null;
                if (node.bf != null) {
                    hashPos = node.bf.getHashPos(s);
                } else {
                    hashPos = getHashPos(s, node.compressedBf.bfSize);
                }

                switch (state) {
                    case 0: if (node.bf.contains(s)) {
                        return null;
                    }
                    break;
                    case 1: if (node.compressedBf.findInBasic(hashPos)) {
                        return null;
                    }
                    break;
                    case 2: if (node.compressedBf.findInPromote(hashPos)) return null; break;
                }

            }

            switch (state) {
                case 0: node.bfHash = SHA.HASHDataToString(node.bf.toString()); break;
                case 1: node.bfHash = SHA.HASHDataToString(Arrays.toString(node.compressedBf.basicCompressedBf)); break;
                case 2: node.bfHash = SHA.HASHDataToString(Arrays.toString(node.compressedBf.promoteCompressedBf)); break;
            }
        }
        return SHA.HASHDataToString(node.bfHash + node.leftNodeHash + node.rightNodeHash);
    }

    public SearchToken getSearchToken(double per) {
        long area = (long) (maxX - minX) * (maxY - minY);
        int dis = (int) (Math.sqrt(area * per) + 0.5);
        return getSearchToken(new SpatialData(minX + 1, minY + 1), new SpatialData(minX + dis + 1, minY + dis + 1));
    }

    public SearchToken getSearchToken(SpatialData p1, SpatialData p2) {

        long s = System.nanoTime();

        List<String> zeroPrefix_x = Utils.getZeroPrefix(p1.x - 1);
        List<String> zeroPrefix_y = Utils.getZeroPrefix(p1.y - 1);
        List<String> onePrefix_x = Utils.getOnePrefix(p2.x);
        List<String> onePrefix_y = Utils.getOnePrefix(p2.y);

        addTagForPrefix(zeroPrefix_x, "d1:1");
        addTagForPrefix(zeroPrefix_y, "d2:1");
        addTagForPrefix(onePrefix_x, "d1:0");
        addTagForPrefix(onePrefix_y, "d2:0");

        long e = System.nanoTime();

        System.out.println("query token time:" + (e - s) + "ns");

        SearchToken searchToken = new SearchToken(zeroPrefix_x, zeroPrefix_y, onePrefix_x, onePrefix_y);

        getSearchTokenSize(searchToken);

        return searchToken;
    }

    private void getSearchTokenSize(SearchToken token) {

        try {
            String path =  "./src/token.txt";
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));
            objectOutputStream.writeObject(token);
            objectOutputStream.close();
            long fileSize = getFileSize(path);
            System.out.println("query token size：" + fileSize + "b");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private SATreeNode buildTree(List<SpatialData> data) {
        SATreeNode tree = new SATreeNode();
        if (data.size() == 1) {
            tree.buildBf(data.get(0), data.get(0), data.get(0).id, state);
            return tree;
        }

        List<List<SpatialData>> subData = splitSpatialData(data);
        if (subData == null) return null;

        tree.left = buildTree(subData.get(0));
        tree.right = buildTree(subData.get(1));

        tree.buildBf(subData.get(2).get(0), subData.get(2).get(1), state);

        if (tree.left != null) {
            tree.leftNodeHash = SHA.HASHDataToString(tree.left.bfHash + tree.left.leftNodeHash + tree.left.rightNodeHash);
        }
        if (tree.right != null) {
            tree.rightNodeHash = SHA.HASHDataToString(tree.right.bfHash + tree.right.leftNodeHash + tree.right.rightNodeHash);
        }

        return tree;
    }

    private List<SpatialData> readSpatialData(String path) throws Exception{

        List<SpatialData> data = new ArrayList<>();
        File file = new File(path);
        if(file.isFile()&&file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null){
//                String[] s = str.split("\t");
                String[] s = str.split(" ");
                int id = Integer.parseInt(s[0]);
                int x = Integer.parseInt(s[1]);
                int y = Integer.parseInt(s[2]);

                data.add(new SpatialData(x + 1, y + 1, id));
            }
            scr.close();
            fla.close();
        }
        return data;
    }

    private List<List<SpatialData>> splitSpatialData(List<SpatialData> data) {
        if (data.size() < 2) {
            System.out.println("need not split");
            return null;
        }

        List<List<SpatialData>> ans = new ArrayList<>(3);
        List<Double> variance = computeVariance(data);
        int n = data.size();
        if (variance.get(0) > variance.get(1)) {
            data.sort(new Comparator<SpatialData>() {
                @Override
                public int compare(SpatialData o1, SpatialData o2) {
                    return o1.x - o2.x;
                }
            });
        } else {
            data.sort(new Comparator<SpatialData>() {
                @Override
                public int compare(SpatialData o1, SpatialData o2) {
                    return o1.y - o2.y;
                }
            });
        }
        ans.add(data.subList(0, n / 2));
        ans.add(data.subList(n / 2, n));
        int x0 = Integer.MAX_VALUE;
        int x1 = Integer.MIN_VALUE;
        int y0 = Integer.MAX_VALUE;
        int y1 = Integer.MIN_VALUE;
        for (SpatialData d : data) {
            x0 = Math.min(d.x, x0);
            x1 = Math.max(d.x, x1);
            y0 = Math.min(d.y, y0);
            y1 = Math.max(d.y, y1);
        }
        ans.add(Arrays.asList(new SpatialData(x0, y0), new SpatialData(x1, y1)));
        return ans;
    }

    private void getSpatialRange(List<SpatialData> data) {
        int maxX = 0, maxY = 0;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        for (SpatialData d : data) {
            maxX = Math.max(d.x, maxX);
            maxY = Math.max(d.y, maxY);
            minX = Math.min(d.x, minX);
            minY = Math.min(d.y, minY);
        }
        this.maxX = maxX;
        this.maxY = maxY;
        this.minX = minX;
        this.minY = minY;
    }
}
