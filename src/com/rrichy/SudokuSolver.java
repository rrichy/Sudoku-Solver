package com.rrichy;

import com.rrichy.DLXNode;

public class SudokuSolver {
    private  DLXNode rootNode = new DLXNode(0,0, 0, null, null);
    private int[][] board = new int[9][9];
    private int size, sqrt;

    public SudokuSolver(int[][] p) {
        if(p.length != p[0].length) System.out.println("The puzzle is not a square");
        this.board = p;
        this.size = p.length;
        this.sqrt = (int) Math.sqrt(size);
    }

    // Sudoku will always have 4 constraints. All rows/cols/grid should have a digit
    // from 1-9 with no repeat. All cell should be filled.
    public void createMatrix() {
        DLXNode colHeader = rootNode;

        // Cell filled constraint - converting the puzzle into a single row
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                // r, c for headers refers to the row and column in the matrix not the puzzle
                DLXNode head = new DLXNode(0, (row*size) + col+1, 0, null, null);
                colHeader.setRight(head);
                colHeader = colHeader.right;

                DLXNode currentNode = colHeader;
                for(int val = 0; val < size; val++) {
                    DLXNode node = new DLXNode(row, col, val, colHeader, null);
                    currentNode.setBottom(node);
                    currentNode = currentNode.bottom;
                }
            }
        }
    }

    // Visualizing the matrix
    public void printSparseMatrix() {
        for(int curRow = 0; curRow < size; curRow++) {
            for(int curCol = 0; curCol < size; curCol++) {
                for(int val = 0; val < size; val++) {
                    //cell - row - col - grid
                    int[] cell = new int[size * size];
                    int[] row = new int[size * size];
                    int[] col = new int[size * size];

                    int[] grid = new int[size * size];
                    int gridN = Math.floorDiv(curRow, sqrt) * sqrt + Math.floorDiv(curCol, sqrt);

                    cell[size * curRow + curCol] = 1;
                    row[size * curRow + val] = 1;
                    col[size * curCol + val] = 1;
                    grid[size * gridN + val] = 1;
                    int[] allConstraints = new int[size * size * 4];
                    for(int i = 0; i < cell.length; i++) allConstraints[i] = cell[i];
                    for(int i = 0; i < row.length; i++) allConstraints[i+size*size] = row[i];
                    for(int i = 0; i < col.length; i++) allConstraints[i+size*size*2] = col[i];
                    for(int i = 0; i < grid.length; i++) allConstraints[i+size*size*3] = grid[i];

                    for(int i = 0; i < allConstraints.length; i++) System.out.print(allConstraints[i]);
                    System.out.println("");
                }
            }
        }
    }
//    public void printMatrix() {
//        DLXNode currentNode = rootNode.right;
//
//        // print headers
//        while(currentNode != null) {
//            System.out.print(currentNode.col);
//            System.out.print(" ");
//            currentNode = currentNode.right;
//        }
//
//        DLXNode colHeader = rootNode.right;
//        DLXNode row = colHeader.bottom;
//        currentNode = row;
//
//        while(row != null) {
//            System.out.println("");
//            while(currentNode != null) {
//                System.out.print(currentNode.value);
//                System.out.print(" ");
//                currentNode = currentNode.right;
//            }
//
//            colHeader = colHeader.right;
//            row = colHeader.bottom;
//            currentNode = row;
//        }
//
//    }

}
