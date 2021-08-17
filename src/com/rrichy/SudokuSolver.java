package com.rrichy;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SudokuSolver {
    private final DLXNode rootNode = new DLXNode(-1,-1, 0, null, -1, - 1, -1);
    private int numSolutions = 0;
    private final int size, matrixRow, matrixCol;
    private boolean matrixFilled = false, continueSolving = true;
    private final ArrayList<DLXNode[]> dlx;
    private final ArrayList<DLXNode> dlxHeaders;
    private final HashSet<Integer> rowFilter = new HashSet<>();
    private final ArrayList<int[]> solution = new ArrayList<>();
    private final ArrayList<int[][]> solutions = new ArrayList<>();

    public SudokuSolver(int[][] p) {
        this.size = p.length;

        // Check first if board is valid

        if(p.length != p[0].length) System.out.println("The puzzle is not a square");
        this.matrixRow = size*size*size;
        this.matrixCol = size*size*4;
        this.dlx = new ArrayList<>();
        this.dlxHeaders = new ArrayList<>();

        // transform the puzzle clues into an array of [row, col, val] and adding then to the solution
//        for(int row = 0; row < size; row++) {
//            for(int col = 0; col < size; col++) {
//                if(p[row][col] != 0) {
//                    int[] clue = new int[] {row, col, p[row][col]};
//                    this.clues.add(clue);
//                }
//            }
//        }


        createDLXMatrix();

        // For every clue in the given board, add it to the solution
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                if(p[row][col] != 0) {
                    int digit = p[row][col];
                    this.solution.add(new int[] {row, col, digit});

                    int colCoverIndex = row * size + col;
                    DLXNode column = rootNode.right;

                    while(column.matCol != colCoverIndex) column = column.right;
                    coverColumn(column);

                    DLXNode node = column.bottom;
                    while(node.value != digit) node = node.bottom;

                    for(DLXNode nodeR = node.right; nodeR != node; nodeR = nodeR.right) coverColumn(nodeR);
                }
            }
        }

        getSolved();

        System.out.println("No more solutions found.");
        System.out.println("Number of solution/s: " + numSolutions);
    }

//    private boolean boardIsValid(int[][] board) {
//        for(int i = 0; i < size; i++) if(board[i].length != size) return false;
//
//
//    }

    // Sudoku will always have 4 constraints. All rows/cols/grid should have a digit
    // from 1 to its size with no repeat. All cell should be filled.
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
                    int square = size * size;
                    //cell - row - col - grid
                    DLXNode[] cell = new DLXNode[square];
                    DLXNode[] row = new DLXNode[square];
                    DLXNode[] col = new DLXNode[square];
                    DLXNode[] grid = new DLXNode[square];

                    int sqrt = (int) Math.sqrt(size);
                    int gridN = Math.floorDiv(curRow, sqrt) * sqrt + Math.floorDiv(curCol, sqrt);
                    int matRow = square * curRow + size * curCol + val;

                    int cellIndex = size * curRow + curCol;
                    int rowIndex = size * curRow + val;
                    int colIndex = size * curCol + val;
                    int gridIndex = size * gridN + val;

                    cell[cellIndex] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(cellIndex), matRow, cellIndex, -1);
                    row[rowIndex] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(rowIndex + square), matRow, rowIndex + square, -1);
                    col[colIndex] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(colIndex + square*2), matRow, colIndex + square*2, -1);
                    grid[gridIndex] = new DLXNode(curRow, curCol, val+1, dlxHeaders.get(gridIndex + square*3), matRow, gridIndex + square*3, -1);

                    linkLR(cell[cellIndex], row[rowIndex]);
                    linkLR(row[rowIndex], col[colIndex]);
                    linkLR(col[colIndex], grid[gridIndex]);
                    linkLR(grid[gridIndex], cell[cellIndex]);

                    DLXNode[] constraints = new DLXNode[matrixCol];
                    System.arraycopy(cell, 0, constraints, 0, cell.length);
                    System.arraycopy(row, 0, constraints, square, row.length);
                    System.arraycopy(col, 0, constraints, square * 2, col.length);
                    System.arraycopy(grid, 0, constraints, square * 3, grid.length);

                    dlx.add(constraints);
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
                    linkUD(current, dlx.get(matRowIndex)[matColIndex]);
                    current = dlx.get(matRowIndex)[matColIndex];
                    instances++;
                }
                matRowIndex++;
            }
            linkUD(current, dlxHeaders.get(matColIndex));
        }
    }

    private void linkLR(@NotNull DLXNode posA, @NotNull DLXNode posB) { // posA is on left of posB, posB is on right of posA
        posA.setRight(posB);
        posB.setLeft(posA);
    }

    private void linkUD(@NotNull DLXNode posA, @NotNull DLXNode posB) { // posA is on top of posB, posB is below posA
        posA.setBottom(posB);
        posB.setTop(posA);
    }

    // Visualizing the DLX matrix
    public void printDLXMatrix() {
        if(!matrixFilled) System.out.println("Sparse Matrix has not been filled yet. Run the fillMatrix method first.");
        else {

            int colCount = 0, rowCount = 0;
            DLXNode header = rootNode.right;
            System.out.print("" + "\t");
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
                    System.out.print(i + "\t");
                    while(header != rootNode) {
                        DLXNode current = header.bottom;
                        while(current.matRow < i && current != header) current = current.bottom;

                        if(current.matRow == i) System.out.print(current.value + "\t");
                        else System.out.print("" + "\t");
                        header = header.right;
                    }
                }
            }

            System.out.println("Matrix of: " + rowCount + "x" + colCount);
        }
    }

    public void getSolved() {
        if(rootNode.right == rootNode) {
            saveSolution();
            numSolutions++;
            System.out.println("Solution " + numSolutions + ":");
            printBoard(solutions.get(numSolutions - 1));

            this.continueSolving = false;
//            boolean validInput = false;
//
//            while(!validInput) {
//                Scanner ask = new Scanner(System.in);
//                System.out.println("Continue searching? Enter Y/N:");
//                String input = ask.nextLine();
//
//                if(input.equals("Y")) {
//                    this.continueSolving = true;
//                    validInput = true;
//                }
//                else if(input.equals("N")) {
//                    this.continueSolving = false;
//                    validInput = true;
//                }
//                else System.out.println("Error input!");
//            }
        }
        else {
            DLXNode header = findLeastColumn();
            if(header == null) return;
            coverColumn(header);

            for(DLXNode node = header.bottom; node != header; node = node.bottom) {
                solution.add(node.puzzleValues());

                for(DLXNode nodeRight = node.right; nodeRight != node; nodeRight = nodeRight.right)
                    coverColumn(nodeRight);

                getSolved();

                if(this.continueSolving) {
                    solution.remove(solution.size() - 1);
                    for (DLXNode nodeLeft = node.left; nodeLeft != node; nodeLeft = nodeLeft.left)
                        uncoverColumn(nodeLeft);
                }
                else break;
            }
            if(this.continueSolving) uncoverColumn(header);

        }
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
                nodeLeft.top.setBottom(nodeLeft);
                nodeLeft.bottom.setTop(nodeLeft);
            }
        }
        header.left.setRight(header);
        header.right.setLeft(header);
    }

    public void printBoard(int[][] answer) {
        String div = "";
        int sqrt = (int) Math.sqrt(size);
        for(int i = 0; i < size + 3*(sqrt-1); i++) div += "--";
        for(int row = 0; row < size; row++) {
            if(row != 0 && row % sqrt == 0) System.out.println(div);
            for(int col = 0; col < size; col++) {
                if(col % sqrt == 0 && col != 0) System.out.print("|\t");
                System.out.print(answer[row][col] + "\t");
            }
            System.out.println("");
        }
    }

    private void saveSolution() {
        int[][] singleSolution = new int[size][size];
        for(int[] sol : solution) singleSolution[sol[0]][sol[1]] = sol[2];

        solutions.add(singleSolution);
    }

    public int[][] solve() {
        return solutions.get(0);
    }
}
