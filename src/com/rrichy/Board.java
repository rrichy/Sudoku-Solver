package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Board extends JPanel {
    public final int nRows, nCols, gridRowSize, gridColSize;
    public int[][] values;
    public final ArrayList<ArrayList<JButton>> cells;
    private final char[] validChar = new char[2];

    Board(int nRows, int nCols) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.gridRowSize = (int) Math.sqrt(nRows);
        this.gridColSize = (int) Math.sqrt(nCols);
        this.values = new int[nRows][nCols];

        if(nRows < 10 && nCols < 10) {
            validChar[0] = '1';
            validChar[1] = (char) ('1' + nRows);
        }
        else {
            validChar[0] = 'a';
            validChar[1] = (char) ('a' + nRows);
        }

        this.cells = new ArrayList<>();

        // fill the arraylist with another arraylist, so we can use add method
        int n = 0;
        while(n++ < nRows) cells.add(new ArrayList<>());

        this.setPreferredSize(new Dimension(30 * nRows, 30* nCols));
        this.setLayout(new GridLayout(gridRowSize, gridColSize, 1, 1));
        this.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        for(int i = 0; i < nRows; i += gridRowSize) {
            for(int j = 0; j < nCols; j += gridColSize) {
                this.add(createBoardGrid(i, j));
            }
        }
    }

    private JPanel createBoardGrid(int r, int c) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(gridRowSize, gridColSize));
        panel.setPreferredSize(new Dimension(30*r, 30*r));

        for(int i = r; i < r + gridRowSize; i++) {
            for(int j = c; j < c + gridColSize; j++) {
                JButton cell = createCellButton(i, j);
                panel.add(cell);
            }
        }

        return panel;
    }

    private JButton createCellButton(int r, int c) {
        JButton cell = new JButton("");

        cell.setMargin(new Insets(0,0,0,0));
        cell.setBackground(Color.white);
//        cell.setFont(new Font("Arial", Font.PLAIN, 10));
//        cell.setIcon(icon);

        // handles the input value of a cell
        cell.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                char ch = e.getKeyChar();
                if(ch == '0') {
                    cell.setText("");
                    values[r][c] = 0;
                }
                else if(ch >= validChar[0] && ch <= validChar[1]) {
                    cell.setText(String.valueOf(Character.toUpperCase(ch)));
                    values[r][c] = ch - validChar[0] + 1;
                }
                cell.setForeground(Color.black);

                checkConflict(r, c, values[r][c]);
            }
        });

        // handles the highlighting of selected cell
        cell.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                int[] g = getGridOrigin(r, c);

                for(int i = g[0]; i < g[0] + gridRowSize; i++) {
                    for(int j = g[1]; j < g[1] + gridColSize; j++) {
                        cells.get(i).get(j).setBackground(Color.decode("0xE0F3FE"));
                    }
                }
                cells.get(r).forEach(b -> b.setBackground(Color.decode("0xBBE4FD")));
                IntStream.range(0, nRows).forEach(rowIndex -> cells.get(rowIndex).get(c).setBackground(Color.decode("0xBBE4FD")));
                cell.setBackground(Color.decode("0x8CD1FB"));
            }

            @Override
            public void focusLost(FocusEvent e) {
                int[] g = getGridOrigin(r, c);

                for(int i = g[0]; i < g[0] + gridRowSize; i++) {
                    for(int j = g[1]; j < g[1] + gridColSize; j++) {
                        cells.get(i).get(j).setBackground(Color.white);
                    }
                }
                cells.get(r).forEach(b -> b.setBackground(Color.white));
                IntStream.range(0, nRows).forEach(rowIndex -> cells.get(rowIndex).get(c).setBackground(Color.white));
            }
        });

        cells.get(r).add(cell);

        return cell;
    }

    private void checkConflict(int r, int c, int v) {
        // check row
        AtomicBoolean conflicted = new AtomicBoolean(false);
        IntStream.range(0, nRows).forEach(col -> {
            if(values[r][col] == v && col != c) {
                cells.get(r).get(col).setForeground(Color.red);
                conflicted.set(true);
            }
        });

        // check column
        IntStream.range(0, nCols).forEach(row -> {
            if(values[row][c] == v && row != r) {
                cells.get(row).get(c).setForeground(Color.red);
                conflicted.set(true);
            }
        });

        // check grid
        int[] origin = getGridOrigin(r, c);
        int rowLim = origin[0] + gridRowSize;
        int colLim = origin[1] + gridColSize;

        for(int row = origin[0]; row < rowLim; row++) {
            for(int col = origin[1]; col < colLim; col++) {
                if(row == r && col == c) continue;
                if(values[row][col] == v) {
                    cells.get(row).get(col).setForeground(Color.red);
                    conflicted.set(true);
                }
            }
        }

        if(conflicted.get()) cells.get(r).get(c).setForeground(Color.red);
    }

    private int[] getGridOrigin(int r, int c) {
        return new int[] {Math.floorDiv(r, gridRowSize) * gridRowSize, Math.floorDiv(c, gridColSize) * gridColSize};
    }

    public void solveBoard() {
        SudokuSolver sudoku = new SudokuSolver(values);
        values = sudoku.solve();

        boolean numerical = nRows < 10;
        IntStream.range(0, nRows).forEach(rowIndex -> {
            ArrayList<JButton> currentRow = cells.get(rowIndex);

            IntStream.range(0, nCols).forEach(colIndex -> {
                JButton cell = currentRow.get(colIndex);
                if(cell.getText().equals("")) {
                    if(numerical) cell.setText(String.valueOf(values[rowIndex][colIndex]));
                    else cell.setText(String.valueOf((char) (values[rowIndex][colIndex] + 64)));
//                        cell.setEnabled(false);
                    cell.setForeground(Color.decode("0x21a5f9"));
                }
            });
        });
    }
}
