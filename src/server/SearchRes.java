package server;

import java.util.List;

public class SearchRes {
    public List<Integer> res;
    public VONode VONodeRoot;

    public SearchRes(List<Integer> res, VONode VONodeRoot) {
        this.res = res;
        this.VONodeRoot = VONodeRoot;
    }
}
