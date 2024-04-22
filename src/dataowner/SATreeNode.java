package dataowner;

import com.sun.istack.internal.localization.NullLocalizable;
import util.BloomFilter;
import util.SHA;
import util.Utils;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static util.Utils.*;
import static vsQuery.Main.*;

public class SATreeNode implements Serializable {
    public String leftNodeHash;
    public String rightNodeHash;
    public int id = -1;
    public BloomFilter<String> bf;
    public CompressedBf compressedBf;
    public String bfHash;
    public String randNumber;
    public SATreeNode left;
    public SATreeNode right;

    public String getLeftHash(int state) {
        return leftNodeHash;
    }
    public String getRightHash(int state) {
        return rightNodeHash;
    }
    public String getBfHash(int state) {
        return bfHash;
    }
    public void buildBf(SpatialData p1, SpatialData p2, int id, int state) {
        this.id = id;
        buildBf(p1, p2, state);
    }
    public void buildBf(SpatialData p1, SpatialData p2, int state) {
        bf = new BloomFilter<>(C, N, K);

        List<String> zeroPrefix_x = Utils.getZeroPrefix(p1.x - 1);
        List<String> zeroPrefix_y = Utils.getZeroPrefix(p1.y - 1);
        List<String> onePrefix_x = Utils.getOnePrefix(p2.x);
        List<String> onePrefix_y = Utils.getOnePrefix(p2.y);


        randNumber = Double.toString(Math.random());

        addTagForPrefix(zeroPrefix_x, randNumber + "d1:0");
        addTagForPrefix(zeroPrefix_y, randNumber + "d2:0");
        addTagForPrefix(onePrefix_x, randNumber + "d1:1");
        addTagForPrefix(onePrefix_y, randNumber + "d2:1");


        bf.addAll(zeroPrefix_x);
        bf.addAll(zeroPrefix_y);
        bf.addAll(onePrefix_x);
        bf.addAll(onePrefix_y);

        if (state != 0) this.compressedBf = compressBf(bf, state);



        switch (state) {
            case 0: this.bfHash = SHA.HASHDataToString(bf.toString()); break;
            case 1: this.bfHash = SHA.HASHDataToString(Arrays.toString(compressedBf.basicCompressedBf)); break;
            case 2: this.bfHash = SHA.HASHDataToString(Arrays.toString(compressedBf.promoteCompressedBf)); break;
        }
    }

    public int findInBf(SearchToken token, int state) {
        if (!checkSubTokenIsInBf(token.zeroPrefix_x, state)) return 1;
        if (!checkSubTokenIsInBf(token.zeroPrefix_y, state)) return 2;
        if (!checkSubTokenIsInBf(token.onePrefix_x, state)) return 3;
        if (!checkSubTokenIsInBf(token.onePrefix_y, state)) return 4;
        return 0;
    }
    public boolean checkSubTokenIsInBf(List<String> subToken, int state) {

        switch (state) {
            case 0: {
                for (String s : subToken) {
                    if (bf.contains(randNumber + s)) {
                        return true;
                    }
                }
                return false;
            }
            case 1: {
                for (String s : subToken) {
                    List<Integer> hashPos = getHashPos(randNumber + s, compressedBf.bfSize);
//                    List<Integer> hashPos = bf.getHashPos(randNumber + s);
                    if (compressedBf.findInBasic(hashPos)) {
                        return true;
                    }
                }
                return false;
            }
            case 2: {
                for (String s : subToken) {
                    List<Integer> hashPos = getHashPos(randNumber + s, compressedBf.bfSize);
//                    List<Integer> hashPos = bf.getHashPos(randNumber + s);
                    if (compressedBf.findInPromote(hashPos)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    private CompressedBf compressBf(BloomFilter<String> bf, int state) {

        BitSet bitSet = bf.getBitSet();
        List<Byte> basicCompressedBf = null;
//        List<List<Byte>> promoteCompressedBf = null;
        List<Byte> promoteCompressedBf = null;
        List<Byte> posTag = null;

        if (state == 1) {
            basicCompressedBf = new ArrayList<>();
        } else if (state == 2) {
            promoteCompressedBf = new ArrayList<>();
            posTag = new ArrayList<>();
        }
//        int n = bitSet.size();
        int n = bf.size();

/*        for (int i = 0; i < n; ++i) {
            if (bitSet.get(i)) System.out.print("1 ");
            else System.out.print("0 ");
        }
        System.out.println();*/

        int blockSize = (int) Math.sqrt(n);
        int basicPre = 0;
        int promotePre = 0;

        int posPre = 0;
//        List<Byte> tempBf = null;
        for (int i = 0; i < n; ++i) {
            if (promoteCompressedBf != null && i != 0 && i % blockSize == 0) {
//                tempBf = new ArrayList<>();

                posTag.add((byte) (promoteCompressedBf.size() - posPre - 128));

                posPre = promoteCompressedBf.size();

//                promoteCompressedBf.add(tempBf);
                promotePre = i;
            }

            if (bitSet.get(i)) {
                if (state == 1) {

                    basicCompressedBf.add((byte) (i - basicPre - 128));
                    basicPre = i;
                } else {

                    promoteCompressedBf.add((byte) (i - promotePre - 128));
                    promotePre = i;
                }
            }
        }
        if (state == 2 && posTag.size() != posPre) {
            posTag.add((byte)(posTag.size() - posPre - 128));
        }

        CompressedBf compressedBf = new CompressedBf(basicCompressedBf, promoteCompressedBf, n, posTag);
        this.bf = null;
        return compressedBf;
    }
}
