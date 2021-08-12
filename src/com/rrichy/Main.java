package com.rrichy;

import com.rrichy.SudokuSolver;
import com.rrichy.DLXNode;
// This Sudoku solver only solves puzzles that are NxN size,
// where N is a perfect square.

public class Main {

    public static void main(String[] args) {
	    SudokuSolver sudoku = new SudokuSolver(new int[][] {{1,0,4,0}, {0,0,0,0}, {3,0,0,0}, {0,2,0,0}});
//        sudoku.createMatrix();
        sudoku.printSparseMatrix();
//        System.out.println(sudoku.size);
    }
}
