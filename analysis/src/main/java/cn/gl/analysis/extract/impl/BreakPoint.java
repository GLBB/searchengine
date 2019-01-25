package cn.gl.analysis.extract.impl;

public class BreakPoint{
    AjacentZeroLine pre;
    AjacentZeroLine end;
    int fontN;
    int score;

    public BreakPoint(AjacentZeroLine pre, AjacentZeroLine end, int fontN, int score) {
        this.pre = pre;
        this.end = end;
        this.fontN = fontN;
        this.score = score;
    }
}