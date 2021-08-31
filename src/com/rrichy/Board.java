package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.IntStream;


public class Board extends JPanel {
    private Color CellBackground, Highlight, SubHighlight, DimHighlight, FixedColor, SolutionColor, ConflictedColor;
    public final int nRows, nCols, gridRowNum, gridColNum;
    public int[][] values;
    private HashSet<Integer> fixed, conflicts;
    private final boolean charNumerical;
    private final char[] validChar = new char[2];
    private int cellSize = 30;
    private int rowSelect, colSelect;
    private Font font;

    Board(int nRows, int nCols) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.gridRowNum = (int) Math.sqrt(nRows);
        this.gridColNum = (int) Math.sqrt(nCols);
        this.values = new int[nRows][nCols];

        this.charNumerical = nRows < 10;
        this.rowSelect = this.colSelect = -1;

        if(charNumerical) {
            validChar[0] = '1';
            validChar[1] = (char) ('1' + nRows - 1);
        }
        else {
            validChar[0] = 'a';
            validChar[1] = (char) ('a' + nRows - 1);
        }

        font = new Font("Segoi UI", Font.PLAIN, 20);

        setFocusable(true);

        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                rowSelect = (int) Math.floor(e.getY() / cellSize);
                colSelect = (int) Math.floor(e.getX() / cellSize);

                System.out.println(String.format("Row: %s, Column: %s", rowSelect, colSelect));
                repaint();
                requestFocusInWindow();
            }
        });

        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                System.out.println("drawing");
                if(rowSelect != -1 && colSelect != -1) {

                    char ch = e.getKeyChar();
                    int id = generateHash(rowSelect, colSelect);
                    if(ch == '0') {
                        values[rowSelect][colSelect] = 0;
                        fixed.remove(id);
                    }
                    else if(ch >= validChar[0] && ch <= validChar[1]) {
                        values[rowSelect][colSelect] = ch - validChar[0] + 1;
                        fixed.add(id);
                    }

                    checkConflict(rowSelect, colSelect, values[rowSelect][colSelect]);
                    repaint();
                }
            }
        });

        initializeColors();
        setBackground(CellBackground);

        this.fixed = new HashSet<>();
        this.conflicts = new HashSet<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(font);
        highlightCell(g2);
        drawInput(g2);

        g2.setColor(Color.black);
        for(int c = 0; c < nCols + 1; c++) {
            if(c % gridColNum == 0) g2.setStroke(new BasicStroke(2));
            else g2.setStroke(new BasicStroke(1));
            g2.drawLine(c * cellSize, 0, c * cellSize, cellSize * nCols);
        }

        for(int r = 0; r < nRows + 1; r++) {
            if(r % gridRowNum == 0) g2.setStroke(new BasicStroke(2));
            else g2.setStroke(new BasicStroke(1));
            g2.drawLine(0, r * cellSize, cellSize * nRows, r * cellSize);
        }

    }

    public Dimension getPreferredSize() {
        return new Dimension(cellSize * nRows, cellSize * nCols);
    }

    private void highlightCell(Graphics2D g) {
        if(rowSelect != -1 && colSelect != -1) {
            int[] origin = getGridOrigin(rowSelect, colSelect);
            g.setColor(DimHighlight);
            g.fillRect(origin[1] * cellSize, origin[0] * cellSize, gridColNum * cellSize, gridRowNum * cellSize);

            g.setColor(SubHighlight);
            g.fillRect(0, rowSelect * cellSize, cellSize * nCols, cellSize);
            g.fillRect(colSelect * cellSize, 0, cellSize, cellSize * nRows);

            g.setColor(Highlight);
            g.fillRect(colSelect * cellSize, rowSelect * cellSize, cellSize, cellSize);
        }
    }

    private void drawInput(Graphics2D g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textHeight = 20;

        for(int row = 0; row < nRows; row++) {
            for(int col = 0; col < nCols; col++) {
                int value = values[row][col];
                if(value != 0) {
                    String text;
                    int id = generateHash(row, col);

                    if(fixed.contains(id)) g.setColor(FixedColor);
                    else g.setColor(SolutionColor);

                    if(conflicts.contains(id)) g.setColor(ConflictedColor);

                    if(charNumerical) text = String.valueOf(value);
                    else text = String.valueOf((char) (value + 64));

                    int textWidth = metrics.stringWidth(text);

//                    g.setColor(ConflictedColor);
//                    g.fillRect(col * cellSize + (cellSize - textWidth + 2) / 2, row * cellSize + (cellSize - textHeight) /2, textWidth, textHeight);

                    g.drawString(text, col * cellSize + (cellSize - textWidth + 2) / 2, (row + 1) * cellSize - (cellSize - textHeight + 4) / 2);
                }
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

    private int[] getGridOrigin(int r, int c) {
        return new int[] {Math.floorDiv(r, gridRowNum) * gridRowNum, Math.floorDiv(c, gridColNum) * gridColNum};
    }

    private int generateHash(int r, int c) { // generateHash is used as ids in hashset
        return (r + 1) * 100 + c;
    }

    private void checkConflict(int r, int c, int v) {
        HashSet<Integer> localConflict = new HashSet<>();
        int cellId = generateHash(r, c);
        // check current row
        IntStream.range(0, nCols).forEach(col -> {
            if(col != c) {
                if(values[r][col] == v) {
                    localConflict.add(cellId);
                    localConflict.add(generateHash(r, col));
                }
                else conflicts.remove(generateHash(r, col));
            }
        });

        // check current column
        IntStream.range(0, nRows).forEach(row -> {
            if(row != r) {
                if(values[row][c] == v) {
                    localConflict.add(cellId);
                    localConflict.add(generateHash(row, c));
                }
                else conflicts.remove(generateHash(row, c));
            }
        });

        // check current grid
        int[] origin = getGridOrigin(r, c);
        int rowLim = origin[0] + gridRowNum;
        int colLim = origin[1] + gridColNum;

        for(int row = origin[0]; row < rowLim; row++) {
            for(int col = origin[1]; col < colLim; col++) {
                if(row == r && col == c) continue;
                if(values[row][col] == v) {
                    localConflict.add(cellId);
                    localConflict.add(generateHash(row, col));
                }
                else conflicts.remove(generateHash(row, col));
            }
        }

        if(localConflict.size() > 0) conflicts.addAll(localConflict);
        else conflicts.remove(cellId);
    }

    public void solveBoard() {
        long t0 = new Date().getTime();

        try {
            SudokuSolver sudoku = new SudokuSolver(values);
            values = sudoku.solve();
            repaint();
        }
        catch (Exception e) {
            System.out.println("invalid caught!");
            JOptionPane.showMessageDialog(this, "Make sure the puzzle is valid.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }

        System.out.print("Finished in " + (new Date().getTime() - t0) + "ms.");
    }
}
