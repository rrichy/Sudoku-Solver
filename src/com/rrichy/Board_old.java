package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;


public class Board_old extends JPanel {
    private Color CellBackground, Highlight, SubHighlight, DimHighlight, FixedColor, SolutionColor, ConflictedColor;
    public final int nRows, nCols, gridRowSize, gridColSize;
    public int[][] values;
    private HashMap<Integer, JPanel> grids;
    public HashMap<Integer, JButton> cells;
    private final boolean charNumerical;
    private final char[] validChar = new char[2];

    Board_old(int nRows, int nCols) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.gridRowSize = (int) Math.sqrt(nRows);
        this.gridColSize = (int) Math.sqrt(nCols);
        this.values = new int[nRows][nCols];

        this.charNumerical = nRows < 10;

        if(charNumerical) {
            validChar[0] = '1';
            validChar[1] = (char) ('1' + nRows);
        }
        else {
            validChar[0] = 'a';
            validChar[1] = (char) ('a' + nRows);
        }

        initializeColors();

        this.grids = new HashMap<>();
        this.cells = new HashMap<>();

//        this.setPreferredSize(new Dimension(30 * nRows, 30* nCols));
        this.setLayout(new GridLayout(gridRowSize, gridColSize, 2, 2));
        this.setBorder(BorderFactory.createLineBorder(FixedColor, 2));
        this.setBackground(Color.black);

        for(int i = 0; i < nRows; i += gridRowSize) {
            for(int j = 0; j < nCols; j += gridColSize) {
                JPanel gridPanel = createGridPanel();
                grids.put(generateHash(i, j), gridPanel);

                this.add(gridPanel);
            }
        }

        for(int r = 0; r < nRows; r++) {
            for(int c = 0; c < nCols; c++) {
                JButton cell = createCellButton(r, c);

                int[] origin = getGridOrigin(r, c);
                grids.get(generateHash(origin[0], origin[1])).add(cell);
            }
        }
    }

    private void initializeColors() {
        CellBackground = Color.white;
        Highlight = Color.decode("0x8CD1FB");
        SubHighlight = Color.decode("0xBBE4FD");
        DimHighlight = Color.decode("0xE0F3FE");
        FixedColor = Color.black;
        SolutionColor = Color.decode("0x21a5f9");
        ConflictedColor = Color.red;
    }

    private JPanel createGridPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(gridRowSize, gridColSize));
        panel.setPreferredSize(new Dimension(30*gridRowSize, 30*gridColSize));

        return panel;
    }

    private JButton createCellButton(int r, int c) {
        JButton cell = new JButton("");

        cell.setMargin(new Insets(0,0,0,0));
        cell.setBackground(CellBackground);
        cell.putClientProperty("fixed", false);
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
                    cell.putClientProperty("fixed", false);
                }
                else if(ch >= validChar[0] && ch <= validChar[1]) {
                    cell.setText(String.valueOf(Character.toUpperCase(ch)));
                    values[r][c] = ch - validChar[0] + 1;
                    cell.putClientProperty("fixed", true);
                }
//                cell.setForeground(FixedColor);

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
                        int key = generateHash(i, j);
                        cells.get(key).setBackground(DimHighlight);
                    }
                }
                IntStream.range(0, nCols).forEach(colIndex -> {
                    int key = generateHash(r, colIndex);
                    cells.get(key).setBackground(SubHighlight);
                });
                IntStream.range(0, nRows).forEach(rowIndex -> {
                    int key = generateHash(rowIndex, c);
                    cells.get(key).setBackground(SubHighlight);
                });

                cell.setBackground(Highlight);
            }

            @Override
            public void focusLost(FocusEvent e) {
                int[] g = getGridOrigin(r, c);

                for(int i = g[0]; i < g[0] + gridRowSize; i++) {
                    for(int j = g[1]; j < g[1] + gridColSize; j++) {
                        int key = generateHash(i, j);
                        cells.get(key).setBackground(CellBackground);
                    }
                }
                IntStream.range(0, nCols).forEach(colIndex -> {
                    int key = generateHash(r, colIndex);
                    cells.get(key).setBackground(CellBackground);
                });
                IntStream.range(0, nRows).forEach(rowIndex -> {
                    int key = generateHash(rowIndex, c);
                    cells.get(key).setBackground(CellBackground);
                });
            }
        });

        cells.put(generateHash(r, c), cell);

        return cell;
    }

    private void checkConflict(int r, int c, int v) {
        // check current row
        AtomicBoolean conflicted = new AtomicBoolean(false);
        IntStream.range(0, nCols).forEach(col -> {
            JButton cell = cells.get(generateHash(r, col));
            boolean fixed = (boolean) cell.getClientProperty("fixed");

            if(values[r][col] == v && col != c) {
                cell.setForeground(ConflictedColor);
                conflicted.set(true);
            }
            else if(fixed) cell.setForeground(FixedColor);
            else cell.setForeground(SolutionColor);
        });

        // check current column
        IntStream.range(0, nRows).forEach(row -> {
            JButton cell = cells.get(generateHash(row, c));
            boolean fixed = (boolean) cell.getClientProperty("fixed");

            if(values[row][c] == v && row != r) {
                cell.setForeground(ConflictedColor);
                conflicted.set(true);
            }
            else if(fixed) cell.setForeground(FixedColor);
            else cell.setForeground(SolutionColor);
        });

        // check current grid
        int[] origin = getGridOrigin(r, c);
        int rowLim = origin[0] + gridRowSize;
        int colLim = origin[1] + gridColSize;

        for(int row = origin[0]; row < rowLim; row++) {
            for(int col = origin[1]; col < colLim; col++) {
                JButton cell = cells.get(generateHash(row, col));
                boolean fixed = (boolean) cell.getClientProperty("fixed");

                if(row == r && col == c) continue;
                if(values[row][col] == v) {
                    cell.setForeground(ConflictedColor);
                    conflicted.set(true);
                }
                else if(fixed) cell.setForeground(FixedColor);
                else cell.setForeground(SolutionColor);
            }
        }

        if(conflicted.get()) cells.get(generateHash(r, c)).setForeground(ConflictedColor);
    }

    private int[] getGridOrigin(int r, int c) {
        return new int[] {Math.floorDiv(r, gridRowSize) * gridRowSize, Math.floorDiv(c, gridColSize) * gridColSize};
    }

    private int generateHash(int r, int c) { // generateHash is used as a key in a hashmap of each cell(r, c).
        return (r + 1) * 100 + c;
    }

    public void solveBoard() {
        long t0 = new Date().getTime();

        try {
            SudokuSolver sudoku = new SudokuSolver(values);

            values = sudoku.solve();

            IntStream.range(0, nRows).forEach(rowIndex -> {
                IntStream.range(0, nCols).forEach(colIndex -> {
                    JButton cell = cells.get(generateHash(rowIndex, colIndex));
                    if(cell.getText().equals("")) {
                        if(charNumerical) cell.setText(String.valueOf(values[rowIndex][colIndex]));
                        else cell.setText(String.valueOf((char) (values[rowIndex][colIndex] + 64)));
//                        cell.setEnabled(false);
                        cell.setForeground(SolutionColor);
                    }
                });
            });
        }
        catch (Exception e) {
            System.out.println("invalid caught!");
            JOptionPane.showMessageDialog(this, "Make sure the puzzle is valid.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }

        System.out.print("Finished in " + (new Date().getTime() - t0) + "ms.");
    }
}
