package server;

import dataowner.SATreeNode;
import dataowner.SearchToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.out;
import static util.Utils.*;

public class SP {
    SATreeNode root;
    int state;
    public SP(SATreeNode root, int state) {
        this.root = root;
        this.state = state;
    }

    public void getIndexSize() {
        String path = "./src/indexSize" + state + ".txt";
        long ans = getIndexSize(path);
        System.out.println("scheme" + state + "index size：" + ans / 1024 / 1024 + "mb");
    }

    public long getIndexSize(String path) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));

            objectOutputStream.writeObject(root);
            objectOutputStream.close();
            return getFileSize(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public long travelGetIndexSize(SATreeNode node, String path) {
        if (node == null) return 0;
        long lft = travelGetIndexSize(node.left, path);
        long rgt = travelGetIndexSize(node.right, path);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));

            switch (state) {
                case 0: objectOutputStream.writeObject(root.bf);
                    break;
                case 1: objectOutputStream.writeObject(root.compressedBf.basicCompressedBf);
                    break;
                case 2: objectOutputStream.writeObject(root.compressedBf.promoteCompressedBf);
                    break;
            }
            objectOutputStream.close();
            long fileSize = getFileSize(path);
            return fileSize / 1024 + lft + rgt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

/*
    private void travelTreeToSer(SATreeNode node, String path) {
        if (node == null) return null;
        StringBuilder builder = new StringBuilder();
        builder.append(travelTreeGetIndexSize(node.left));
        builder.append(travelTreeGetIndexSize(node.right));
        builder.append(node.getBfHash(state) + "," + node.getLeftHash(state) + "," + node.getRightHash(state) + ",");
        switch (state) {
            case 0: builder.append(node.bf.getBitSet().toString());
                break;
            case 1: builder.append(node.compressedBf.basicCompressedBf.toString());
                break;
            case 2: builder.append(node.compressedBf.promoteCompressedBf.toString());
                break;
        }
        builder.append("\r\n");
        return builder.toString();
    }*/
    public SearchRes search(SearchToken token) {
        long s = System.nanoTime();

        List<Integer> res = new ArrayList<>();
        VONode voNode = traverTree(root, token, res);

        long e = System.nanoTime();
        out.println("scheme" + state + " query time：" + (e - s) * 1.0 / 1e9 + "ms");
        return new SearchRes(res, voNode);
    }
    private VONode traverTree(SATreeNode node, SearchToken token, List<Integer> res) {
        if (node == null) return null;
        VONode voNode = new VONode();
        int findTag = node.findInBf(token, state);
        if (findTag == 0) {
            voNode.bfHash = node.getBfHash(state);
            if (node.id != -1) {
                res.add(node.id);
            } else {
                voNode.left = traverTree(node.left, token, res);
                voNode.right = traverTree(node.right, token, res);
            }
        } else {
            voNode.bf = node.bf;
            voNode.randNumber = node.randNumber;
            voNode.compressedBf = node.compressedBf;
            if (node.left != null) voNode.leftNodeHash = node.getLeftHash(state);
            if (node.right != null) voNode.rightNodeHash = node.getRightHash(state);
            voNode.findTag = findTag;
        }
        return voNode;
    }
}
