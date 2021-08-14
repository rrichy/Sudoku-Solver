package com.rrichy;

import com.rrichy.DLXNode;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;

public class SudokuSolver {
    private  DLXNode rootNode = new DLXNode(-1,-1, 0, null, -1, - 1, -1);
    private int[][] board;
    private final int size, matrixRow, matrixCol;
    private boolean matrixFilled = false;
    private ArrayList<DLXNode[]> dlx;
    private ArrayList<DLXNode> dlxHeaders;
    private HashSet<Integer> rowFilter = new HashSet<Integer>();
    private ArrayList<int[]> clues = new ArrayList<int[]>();
    private ArrayList<int[]> solution = new ArrayList<int[]>();

    public SudokuSolver(int[][] p) {
        if(p.length != p[0].length) System.out.println("The puzzle is not a square");
        this.board = p;
        this.size = p.length;
        this.matrixRow = size*size*size;
        this.matrixCol = size*size*4;
        this.dlx = new ArrayList<DLXNode[]>();
        this.dlxHeaders = new ArrayList<DLXNode>();


        // transform the puzzle clues into an array of [row, col, val]

        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                if(board[row][col] != 0) {
                    int[] clue = new int[] {row, col, board[row][col]};
                    this.clues.add(clue);
                }
            }
        }
    }

    // Sudoku will always have 4 constraints. All rows/cols/grid should have a digit
    // from 1-9 with no repeat. All cell should be filled.
    public void createDLXMatrix() {
        // creating all the header nodes at the same time, linking then to each other
        DLXNode current = rootNode;
        for(int matColIndex = 0; matColIndex < matrixCol; matColIndex++) {
            DLXNode header = new DLXNode(-1, -1, -1, null, -1, matColIndex, size);
            header.setLeft(current);
            current.setRight(header);
            current = current.right;
            dlxHeaders.add(header);
        }
        current.setRight(rootNode);

        // creating all the dlxnodes while linking them by rows and connecting each node to its column header
        for(int curRow = 0; curRow < size; curRow++) {
            for(int curCol = 0; curCol < size; curCol++) {
                for(int val = 0; val < size; val++) {
                    //cell - row - col - grid
                    DLXNode[] cell = new DLXNode[size * size];
                    DLXNode[] row = new DLXNode[size * size];
                    DLXNode[] col = new DLXNode[size * size];
                    DLXNode[] grid = new DLXNode[size * size];

                    int sqrt = (int) Math.sqrt(size);
                    int gridN = Math.floorDiv(curRow, sqrt) * sqrt + Math.floorDiv(curCol, sqrt);
                    int matRow = size*size*curRow + size*curCol + val;

                    cell[size * curRow + curCol] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(size * curRow + curCol), matRow, size * curRow + curCol, -1);
                    row[size * curRow + val] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(size * curRow + val + size*size), matRow, size * curRow + val + size*size, -1);
                    col[size * curCol + val] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(size * curCol + val + size*size*2), matRow, size * curCol + val + size*size*2, -1);
                    grid[size * gridN + val] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(size * gridN + val + size*size*3), matRow, size * gridN + val + size*size*3, -1);

                    linkLeftRight(cell[size * curRow + curCol], row[size * curRow + val]);
                    linkLeftRight(row[size * curRow + val], col[size * curCol + val]);
                    linkLeftRight(col[size * curCol + val], grid[size * gridN + val]);
                    linkLeftRight(grid[size * gridN + val], cell[size * curRow + curCol]);

                    DLXNode[] constraints = new DLXNode[matrixCol];
                    for(int i = 0; i < cell.length; i++) constraints[i] = cell[i];
                    for(int i = 0; i < row.length; i++) constraints[i+size*size] = row[i];
                    for(int i = 0; i < col.length; i++) constraints[i+size*size*2] = col[i];
                    for(int i = 0; i < grid.length; i++) constraints[i+size*size*3] = grid[i];

                    dlx.add(constraints);
//                    dlx[curRow*size*size + curCol*size + val] = constraints;
                }
            }
        }
        this.matrixFilled = true;
        // at this point, all the nodes has been created, only the linking of up and down is left

        // linking nodes up and down
        for(int matColIndex = 0; matColIndex < matrixCol; matColIndex++) {
            current = dlxHeaders.get(matColIndex);
            int instances = 0;
            int matRowIndex = 0;
            while(instances < size) {
                if(dlx.get(matRowIndex)[matColIndex] != null) {
                    linkUpDown(current, dlx.get(matRowIndex)[matColIndex]);
                    current = dlx.get(matRowIndex)[matColIndex];
                    instances++;
                }
                matRowIndex++;
            }
            linkUpDown(current, dlxHeaders.get(matColIndex));
        }

        for(int[] clue : clues) {
            this.solution.add(clue);
            int remCol = clue[0] * size + clue[1];
            DLXNode header = rootNode.right;
            while(header.matCol != remCol) header = header.right;
            coverColumn(header);

            DLXNode clueCell = header.bottom;
            while(clueCell.value != clue[2]) clueCell = clueCell.bottom;

            for(DLXNode nodeRight = clueCell.right; nodeRight != clueCell; nodeRight = nodeRight.right) coverColumn(nodeRight);
        }
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
            int colCount = 0, rowCount = 0;
            DLXNode header = rootNode.right;
            while(header != rootNode){
                colCount++;
                System.out.print(header.matCol + "\t");
                header = header.right;
            }

            for(int i = 0; i < matrixRow; i++) {
                if(!rowFilter.contains(i)) {
                    rowCount++;
                    System.out.println("");
                    header = rootNode.right;
                    while(header != rootNode) {
                        DLXNode current = header.bottom;
                        while(current.matRow < i && current != header) current = current.bottom;

                        if(current.matRow == i) System.out.print(current.value + "\t");
                        else System.out.print("0" + "\t");
                        header = header.right;
                    }
                }
            }

            System.out.println("Matrix of: " + rowCount + "x" + colCount);
        }
    }

    public void solve() {
        if(rootNode.right == rootNode) System.out.println("Puzzle Solved!");
        else {
            DLXNode header = findLeastColumn();
            coverColumn(header);

//            DLXNode node = header.bottom;
            for(DLXNode node = header.bottom; node != header; node = node.bottom) {
                solution.add(node.puzzleValues());
                for(DLXNode nodeRight = node.right; nodeRight != node; nodeRight = nodeRight.right) coverColumn(nodeRight);
                solve();
            }

//            for(DLXNode nodeLeft = node.left; nodeLeft != node; nodeLeft = nodeLeft.left) uncoverColumn(nodeLeft);
//            uncoverColumn(header);

            System.out.println("Looped out!");
        }

        printDLXMatrix();
    }

    private DLXNode findLeastColumn() {
        DLXNode minNode = rootNode.right;
        int min = size;

        for(DLXNode header = rootNode.right; header != rootNode; header = header.right) {
            int count = 0;
            for(DLXNode child = header.bottom; child != header; child = child.bottom) count++;
            if(count == 0) return null;
            if(count < min) {
                min = count;
                minNode = header;
                if(min == 1) return minNode;
            }
        }
        return minNode;
    }

    private void coverColumn(DLXNode c) {
        DLXNode header = c;
        if(!header.header) header = header.colHeader;
        header.left.setRight(header.right);
        header.right.setLeft(header.left);

        for(DLXNode node = header.bottom; node != header; node = node.bottom) {
            rowFilter.add(node.matRow);
            for(DLXNode nodeRight = node.right; node != nodeRight; nodeRight = nodeRight.right) {
                nodeRight.top.setBottom(nodeRight.bottom);
                nodeRight.bottom.setTop(nodeRight.top);
            }
        }
    }

    private void uncoverColumn(DLXNode c) {
        DLXNode header = c;
        if(!header.header) header = header.colHeader;

        for(DLXNode node = header.top; node != header; node = node.top) {
            rowFilter.remove(node.matRow);
            for(DLXNode nodeLeft = node.left; node != nodeLeft; nodeLeft = nodeLeft.left) {
                nodeLeft.top.setBottom(nodeLeft.bottom);
                nodeLeft.bottom.setTop(nodeLeft.top);

            }
        }
        header.left.setRight(header);
        header.right.setLeft(header);
    }

//    private void trimMatrix(int[] clue) {
//        DLXNode current = rootNode.right;
//        int remCol = clue[0] * size + clue[1];
//
//        while(current.matCol != remCol) current = current.right;
//        coverNode(current); // cover header
//        current.clearChild();
//
//        while(current.value != clue[2]) current = current.bottom; // find the correct cell
//
//        DLXNode row = current.bottom;
//
//        while(row != current) { // cover column nodes except for the hint cell
//            coverNode(row);
//            row = row.bottom;
//        }
//        coverNode(current); // cover hint cell then move to the next
//
//        current = current.right;
//        while(current.right != current) {
//            row = current.bottom;
//
//            while (row != current) {
//                if (!row.header) coverRow(row);
//                else {
//                    coverNode(row);
//                    row.clearChild();
//                }
//                row = row.bottom;
//            }
//            coverNode(current);
//            current = current.right;
//        }
//
//        row = current.bottom;
//        while (row != current) {
//            if (!row.header) coverRow(row);
//            else coverNode(row);
//            row = row.bottom;
//        }
//        coverNode(current);
//    }

//    public ArrayList<int[]> algorithmX(ArrayList<int[]> moves) {
//        if(rootNode.right == rootNode) return null; // matrix is empty
//        // check for empty header
//        int minChild = size;
//        DLXNode header = rootNode.right;
//        DLXNode currentHeader = header;
//        while(header != rootNode) {
//            if(header.nChild == 0) return null; // invalid move
//            if(header.nChild < minChild) {
//                minChild = header.nChild;
//                currentHeader = header;
//
//                if(header.nChild == 1) break;
//            }
//            header = header.right;
//        }
//
//        DLXNode currentNode = currentHeader.bottom;
//        while(currentNode != currentHeader) {
//            int[] move = new int[] {currentNode.row, currentNode.col, currentNode.value};
//            moves.add(move);
//            trimMatrix(move);
//
//            ArrayList<int[]> recur = algorithmX(moves);
//
////            if(recur == null)
//
//        }
//        return null;
//    }

}
