package com.rrichy;

import com.rrichy.DLXNode;
import java.util.Arrays;
import java.util.ArrayList;

public class SudokuSolver {
    private  DLXNode rootNode = new DLXNode(-1,-1, 0, null, -1, -1);
    private int[][] board;
    private int size;
    private boolean matrixFilled = false;
    private DLXNode[][] dlx;
    private DLXNode[] dlxHeaders;

    public SudokuSolver(int[][] p) {
        if(p.length != p[0].length) System.out.println("The puzzle is not a square");
        this.board = p;
        this.size = p.length;
        this.dlx = new DLXNode[size*size*size][size*size*4];
        this.dlxHeaders = new DLXNode[size*size*4];
    }

    // Sudoku will always have 4 constraints. All rows/cols/grid should have a digit
    // from 1-9 with no repeat. All cell should be filled.
    public void createDLXMatrix() {
        // creating all the header nodes at the same time, linking then to each other
        DLXNode current = rootNode;
        for(int matColIndex = 0; matColIndex < dlxHeaders.length; matColIndex++) {
            dlxHeaders[matColIndex] = new DLXNode(-1, -1, -1, null, matColIndex, size);
            dlxHeaders[matColIndex].setLeft(current);
            current.setRight(dlxHeaders[matColIndex]);
            current = current.right;
        }
        current.setRight(rootNode);

        // creating all the dlxnodes while linking them by rows and connecting each node to its column header
        for(int curRow = 0; curRow < size; curRow++) {
            for(int curCol = 0; curCol < size; curCol++) {
                for(int val = 0; val < size; val++) {
                    //cell - row - col - grid
                    DLXNode[] constraints = new DLXNode[size * size * 4];
                    DLXNode[] cell = new DLXNode[size * size];
                    DLXNode[] row = new DLXNode[size * size];
                    DLXNode[] col = new DLXNode[size * size];
                    DLXNode[] grid = new DLXNode[size * size];

                    int sqrt = (int) Math.sqrt(size);
                    int gridN = Math.floorDiv(curRow, sqrt) * sqrt + Math.floorDiv(curCol, sqrt);

                    cell[size * curRow + curCol] = new DLXNode(curRow, curCol, val+1, dlxHeaders[size * curRow + curCol],  size * curRow + curCol, -1);
                    row[size * curRow + val] = new DLXNode(curRow, curCol, val+1, dlxHeaders[size * curRow + val + size*size], size * curRow + val + size*size, -1);
                    col[size * curCol + val] = new DLXNode(curRow, curCol, val+1, dlxHeaders[size * curCol + val + size*size*2], size * curCol + val + size*size*2, -1);
                    grid[size * gridN + val] = new DLXNode(curRow, curCol, val+1, dlxHeaders[size * gridN + val + size*size*3], size * gridN + val + size*size*3, -1);

                    linkLeftRight(cell[size * curRow + curCol], row[size * curRow + val]);
                    linkLeftRight(row[size * curRow + val], col[size * curCol + val]);
                    linkLeftRight(col[size * curCol + val], grid[size * gridN + val]);
                    linkLeftRight(grid[size * gridN + val], cell[size * curRow + curCol]);

                    for(int i = 0; i < cell.length; i++) constraints[i] = cell[i];
                    for(int i = 0; i < row.length; i++) constraints[i+size*size] = row[i];
                    for(int i = 0; i < col.length; i++) constraints[i+size*size*2] = col[i];
                    for(int i = 0; i < grid.length; i++) constraints[i+size*size*3] = grid[i];

                    dlx[curRow*size*size + curCol*size + val] = constraints;
                }
            }
        }
        this.matrixFilled = true;
        // at this point, all the nodes has been created, only the linking of up and down is left

        // linking nodes up and down
        for(int matColIndex = 0; matColIndex < dlxHeaders.length; matColIndex++) {
            current = dlxHeaders[matColIndex];
            int instances = 0;
            int matRowIndex = 0;
            while(instances < size) {
                if(dlx[matRowIndex][matColIndex] != null) {
                    linkUpDown(current, dlx[matRowIndex][matColIndex]);
                    current = dlx[matRowIndex][matColIndex];
                    instances++;
                }
                matRowIndex++;
            }
            linkUpDown(current, dlxHeaders[matColIndex]);
        }

        rootNode.setRight(dlxHeaders[0]);
    }

    private void linkLeftRight(DLXNode posA, DLXNode posB) { // posA is on left of posB, posB is on right of posA
        posA.setRight(posB);
        posB.setLeft(posA);
    }

    private void linkUpDown(DLXNode posA, DLXNode posB) { // posA is on top of posB, posB is below posA
        posA.setBottom(posB);
        posB.setTop(posA);
    }

    // Visualizing the matrix
    public void printDLXMatrix() {
        if(!matrixFilled) System.out.println("Sparse Matrix has not been filled yet. Run the fillMatrix method first.");
        else {
//            for(DLXNode node : dlxHeaders) System.out.print(node.matCol + "\t");
//            for(DLXNode[] row : dlx) {
//                System.out.println("");
//                for(DLXNode node : row) {
//                    if(node == null) System.out.print(0 + "\t");
//                    else System.out.print(node.value + "\t");
//                }
//            }



            // count remaining headers
            int headCount = 0;
            DLXNode current = rootNode.right;

            while(current != rootNode) {
                System.out.println(current.matCol);
                headCount++;
                current = current.right;
            }
            System.out.println(headCount);
//            for(int row = 0; row < headCount; row++) {
//                for(int col = 0; col < size*size*4; col++) {
//
//                }
//            }

        }
    }

    public void solve() {
        // transform the puzzle clues into an array of [row, col, val]
        ArrayList<ArrayList<Integer>> clues = new ArrayList<ArrayList<Integer>>();
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                if(board[row][col] != 0) {
                    ArrayList<Integer> clue = new ArrayList<Integer>();
                    clue.add(row);
                    clue.add(col);
                    clue.add(board[row][col]);
                    clues.add(clue);
                }
            }
        }

        // narrowing the matrix using the clues
        for(int i = 0; i < clues.size(); i++) {
            DLXNode current = rootNode.right;
            int remCol = clues.get(i).get(0) * 4 + clues.get(i).get(1);
            while(current.matCol != remCol) current = current.right;
            while(current.value != clues.get(i).get(2)) current = current.bottom;

            DLXNode head = current.colHeader;
            current = current.right;

            while(current.matCol != head.matCol) {
                DLXNode header = current.colHeader;
                DLXNode row = current.bottom;

                while(row != current) {
                    if(row != header) {
                        row = row.right;

                        while (row.matCol != header.matCol) {
                            row.top.setBottom(row.bottom);
                            row.bottom.setTop(row.top);
                            row = row.right;
                        }
                    }
                    row = row.bottom;
                }
                header.left.setRight(header.right);
                header.right.setLeft(header.left);

                current = current.right;

                System.out.println(head.matCol);
            }
            head.left.setRight(head.right);
            head.right.setLeft(head.left);
        }
    }
}
