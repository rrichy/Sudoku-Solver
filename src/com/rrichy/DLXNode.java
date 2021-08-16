package com.rrichy;

public class DLXNode {
    public int row, col, value, nChild;
    public int matRow, matCol;
    boolean header = false;

    public DLXNode colHeader = null;
    public DLXNode left = null;
    public DLXNode right = null;
    public DLXNode top = null;
    public DLXNode bottom = null;

    public DLXNode (int r, int c, int v, DLXNode ch, int mr, int mc, int child) {
        this.row = r;
        this.col = c;
        this.value = v;
        this.colHeader = ch;
        this.matRow = mr;
        this.matCol = mc;
        if(ch == null) {
            this.nChild = child;
            this.header = true;
        }
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

    public void removeChild() {
        if(header) this.nChild--;
        else System.out.println("The selected node is not a header!");
    }

    public void clearChild() {
        if(header) this.nChild = 0;
        else System.out.println("The selected node is not a header!");
    }

    public int[] puzzleValues() {
        return new int[] {row, col, value};
    }

    public void printInfo() {
        System.out.println("(" + row + ", " + col + ", " + value + ")\tmatCol: " + matCol + "\tmatRow: " + matRow + "\tisHeader: " + header);
    }

    public String test() {
        return "[" + row + ", " + col + ", " + value + ", " + matCol + "]";
    }
}
