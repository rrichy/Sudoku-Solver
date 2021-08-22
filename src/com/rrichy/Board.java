package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Board extends JPanel {
    public final int nRows, nCols, gridRowSize, gridColSize;
    public final ArrayList<ArrayList<JButton>> rows;  // used for easy access
    private final ArrayList<ArrayList<JButton>> cols, grids;
    private int prevRow, prevCol, prevGrid;
    private char[] validChar = new char[2];

    Board(int nRows, int nCols) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.gridRowSize = (int) Math.sqrt(nRows);
        this.gridColSize = (int) Math.sqrt(nCols);

        if(nRows < 10 && nCols < 10) {
            validChar[0] = '1';
            validChar[1] = (char) ('1' + nRows);
        }
        else {
            validChar[0] = 'a';
            validChar[1] = (char) ('a' + nRows);
        }

        this.rows = new ArrayList<>();
        this.cols = new ArrayList<>();
        this.grids = new ArrayList<>();

        this.prevRow = 0;
        this.prevCol = 0;
        this.prevGrid = 0;

        int n = 0;
        while(n++ < nRows) { // fill the arraylist with another arraylist, so we can use add method
            rows.add(new ArrayList<>());
            cols.add(new ArrayList<>());
            grids.add(new ArrayList<>());
        }

        this.setPreferredSize(new Dimension(30 * nRows, 30* nCols));
        this.setLayout(new GridLayout(gridRowSize, gridColSize, 1, 1));
        this.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        int grid = 0;
        for(int i = 0; i < nRows; i += gridRowSize) {
            for(int j = 0; j < nCols; j += gridColSize) {
                this.add(createBoardGrid(i, j, grid));
                grid++;
            }
        }
    }

    private JPanel createBoardGrid(int r, int c, int g) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(gridRowSize, gridColSize));
        panel.setPreferredSize(new Dimension(30*r, 30*r));

        for(int i = r; i < r + gridRowSize; i++) {
            for(int j = c; j < c + gridColSize; j++) {
                JButton cell = createCellButton(i, j, g);

                rows.get(i).add(cell);
                cols.get(j).add(cell);
                grids.get(g).add(cell);
                panel.add(cell);
            }
        }

        return panel;
    }

    private JButton createCellButton(int r, int c, int g) {
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
                if(ch == '0') cell.setText("");
                else if(ch >= validChar[0] && ch <= validChar[1]) cell.setText(String.valueOf(Character.toUpperCase(ch)));
            }
        });

        // handles the highlighting of selected cell
        cell.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                prevRow = r;
                prevCol = c;
                prevGrid = g;

                grids.get(prevGrid).forEach(b -> b.setBackground(Color.decode("0xE0F3FE")));
                rows.get(prevRow).forEach(b -> b.setBackground(Color.decode("0xBBE4FD")));
                cols.get(prevCol).forEach(b -> b.setBackground(Color.decode("0xBBE4FD")));
                cell.setBackground(Color.decode("0x8CD1FB"));
            }

            @Override
            public void focusLost(FocusEvent e) {
                rows.get(prevRow).forEach(b -> b.setBackground(Color.white));
                cols.get(prevCol).forEach(b -> b.setBackground(Color.white));
                grids.get(prevGrid).forEach(b -> b.setBackground(Color.white));
            }
        });

        return cell;
    }
}
