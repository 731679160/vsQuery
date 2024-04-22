package dataowner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompressedBf implements Serializable {
    int bfSize;
    public byte[] basicCompressedBf;
//    public byte[][] promoteCompressedBf;
    public byte[] promoteCompressedBf;

    public byte[] posTag;
/*    public String getBasicBfString() {
        List<Integer> temp = new ArrayList<>(basicCompressedBf.size());
        for (int i = 0; i < basicCompressedBf.size(); ++i) {
            temp.add(basicCompressedBf.get(i) + 128);
        }
        return temp.toString();
    }
    public String getPromoteBfString() {
        List<List<Integer>> temp = new ArrayList<>(promoteCompressedBf.size());
        for (List<Byte> round : promoteCompressedBf) {
            List<Integer> rondTemp = new ArrayList<>(round.size());
            for (Byte b : round) {
                rondTemp.add(b + 128);
            }
            temp.add(rondTemp);
        }
        return temp.toString();
    }*/
    public CompressedBf(List<Byte> basicCompressedBf, List<Byte> promoteCompressedBf, int bfSize, List<Byte> posTag) {
        if (basicCompressedBf != null) {
            this.basicCompressedBf = new byte[basicCompressedBf.size()];
            for (int i = 0; i < basicCompressedBf.size(); ++i) {
                this.basicCompressedBf[i] = basicCompressedBf.get(i);
            }
        }

        if (promoteCompressedBf != null) {
/*            this.promoteCompressedBf = new byte[promoteCompressedBf.size()][];
            for (int i = 0; i < promoteCompressedBf.size(); ++i) {
                this.promoteCompressedBf[i] = new byte[promoteCompressedBf.get(i).size()];
                for (int j = 0; j < this.promoteCompressedBf[i].length; ++j) {
                    this.promoteCompressedBf[i][j] = promoteCompressedBf.get(i).get(j);
                }
            }*/

            this.promoteCompressedBf = new byte[promoteCompressedBf.size()];
            for (int i = 0; i < this.promoteCompressedBf.length; ++i) {
                this.promoteCompressedBf[i] = promoteCompressedBf.get(i);
            }
            this.posTag = new byte[posTag.size()];
            for (int i = 0; i < this.posTag.length; ++i ){
                this.posTag[i] = posTag.get(i);
            }
        }

        this.bfSize = bfSize;
    }
    public CompressedBf() {}
    public boolean findInBasic(List<Integer> pos) {
        int p = 0;
        int sum = 0;
        for (int i = 0; i < basicCompressedBf.length && p < pos.size(); ++i) {
            sum += basicCompressedBf[i] + 128;
            while (p < pos.size() && sum == pos.get(p)) p++;
            if (p == pos.size()) {
                return true;
            }
            if (sum > pos.get(p)) return false;
        }

        return p == pos.size();
    }

    public boolean findInPromote(List<Integer> pos) {

        int dis = (int) Math.sqrt(bfSize);

        int t = 0;
        int preTag = 0;
        int nexTag = 0;
        int sum = 0;
        for (int num : pos) {
            while (t < posTag.length && sum <= num) {
                preTag = nexTag;
                nexTag += posTag[t++] + 128;
                sum += dis;
            }
            if (!findInSubPromote(num, preTag, nexTag - 1, sum - dis)) return false;
        }

        return true;
    }
    private boolean findInSubPromote(int num, int start, int end, int sum) {
        for (int i = start; i <= end; ++i) {
            sum += promoteCompressedBf[i] + 128;
            if (sum == num) return true;
            else if (sum > num) return false;
        }
        return false;
    }


/*    public boolean findInPromote(List<Integer> pos) {
        int j = 1;
        for (int num : pos) {
            while (j < promoteCompressedBf.length && getStartRange(j) <= num) {
                j++;
            }
            if (!findInSubPromote(num, j)) return false;
        }
        return true;
    }
    private boolean findInSubPromote(int num, int j) {
        byte[] subCompressedBf = promoteCompressedBf[j - 1];
        int sum = getStartRange(j - 1);
        for (int i = 0; i < subCompressedBf.length; ++i) {
            sum += subCompressedBf[i] + 128;
            if (sum == num) return true;
            else if (sum > num) return false;
        }
        return false;
    }*/
    private int getStartRange(int pos) {
        return pos * (int) Math.sqrt(bfSize);
    }
}
