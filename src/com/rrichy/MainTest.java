package com.rrichy;

import org.junit.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    public void shouldSolve4x4(){
        long t0 = new Date().getTime();
        SudokuSolver sudoku = new SudokuSolver(new int[][] {
                {1,0,4,0},
                {0,0,0,0},
                {3,0,0,0},
                {0,2,0,0}
        });

//        sudoku.createDLXMatrix();
//        sudoku.solve();
        System.out.println("Solved 4x4");
        System.out.print("Finished in " + (new Date().getTime() - t0) + "ms.");
    }
}