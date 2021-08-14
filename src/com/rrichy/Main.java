package com.rrichy;

import com.rrichy.SudokuSolver;
import com.rrichy.DLXNode;
import java.util.Date;
// This Sudoku solver only solves puzzles that are NxN size,
// where N is a perfect square.

public class Main {

    public static void main(String[] args) {
        long t0 = new Date().getTime();
        SudokuSolver sudoku = new SudokuSolver(new int[][] {{1,0,4,0}, {0,0,0,0}, {3,0,0,0}, {0,2,0,0}});
//        SudokuSolver sudoku = new SudokuSolver(new int[][] {{0,0,4,0}, {0,0,0,0}, {3,0,0,0}, {0,2,0,0}});

//	    SudokuSolver sudoku = new SudokuSolver(
//	            new int[][] {
//	                    {0, 0, 6, 1, 0, 0, 0, 0, 8},
//                        {0, 8, 0, 0, 9, 0, 0, 3, 0},
//                        {2, 0, 0, 0, 0, 5, 4, 0, 0},
//                        {4, 0, 0, 0, 0, 1, 8, 0, 0},
//                        {0, 3, 0, 0, 7, 0, 0, 4, 0},
//                        {0, 0, 7, 9, 0, 0, 0, 0, 3},
//                        {0, 0, 8, 4, 0, 0, 0, 0, 6},
//                        {0, 2, 0, 0, 5, 0, 0, 8, 0},
//                        {1, 0, 0, 0, 0, 2, 5, 0, 0}
//	            }
//            );

//        sudoku.createMatrix();
//        sudoku.printSparse+Matrix();
        sudoku.createDLXMatrix();
//        sudoku.printDLXMatrix();
//        sudoku.test();
        sudoku.solve();
        System.out.println("");
        System.out.print("Finished in " + (new Date().getTime() - t0) + "ms.");
//        System.out.println(sudoku.size);
    }
}
