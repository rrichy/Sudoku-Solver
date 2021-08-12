package com.rrichy;

public class DLXNode {
    public int row, col, value;
//    boolean header = false;

    public DLXNode colHeader = null;
    public DLXNode rowHeader = null;
    public DLXNode left = null;
    public DLXNode right = null;
    public DLXNode top = null;
    public DLXNode bottom = null;

    public DLXNode (int r, int c, int v, DLXNode ch, DLXNode rh) {
        this.row = r;
        this.col = c;
        this.value = v;
        this.colHeader = ch;
        this.rowHeader = rh;
    }

    public DLXNode setRight(DLXNode node) {
        this.right = node;
        return this;
    }

    public DLXNode setLeft(DLXNode node) {
        this.left = node;
        return this;
    }

    public DLXNode setTop(DLXNode node) {
        this.top = node;
        return this;
    }

    public DLXNode setBottom(DLXNode node) {
        this.bottom = node;
        return this;
    }
}
