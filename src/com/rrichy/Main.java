package com.rrichy;

import java.util.Date;
// This Sudoku solver only solves puzzles that are NxN size,
// where N is a perfect square.

public class Main {
    public static void main(String[] args) {
        long t0 = new Date().getTime();

        System.out.println("");
        System.out.print("Finished in " + (new Date().getTime() - t0) + "ms.");
    }
}
