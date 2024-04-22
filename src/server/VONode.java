package server;

import dataowner.CompressedBf;
import dataowner.SearchToken;
import util.BloomFilter;

import java.io.Serializable;
import java.util.List;

public class VONode implements Serializable {
    public CompressedBf compressedBf;
    public String leftNodeHash;
    public String rightNodeHash;
    public BloomFilter<String> bf;
    public String bfHash;
    public int findTag = 0;
    public String randNumber;
    public VONode left;
    public VONode right;

    public int find(SearchToken token) {
        boolean tag = false;
        for (String s : token.zeroPrefix_x) {
            if (bf.contains(randNumber + s)) {
                tag = true;
                break;
            }
        }
        if (!tag) return 1;
        tag = false;
        for (String s : token.zeroPrefix_y) {
            if (bf.contains(randNumber + s)) {
                tag = true;
                break;
            }
        }
        if (!tag) return 2;
        tag = false;
        for (String s : token.onePrefix_x) {
            if (bf.contains(randNumber + s)) {
                tag = true;
                break;
            }
        }
        if (!tag) return 3;
        tag = false;
        for (String s : token.onePrefix_y) {
            if (bf.contains(randNumber + s)) {
                tag = true;
                break;
            }
        }
        if (!tag) return 4;
        return 0;
    }
}
