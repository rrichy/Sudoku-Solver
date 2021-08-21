package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class WindowFrame extends JFrame implements ActionListener {
    private JButton btn1 = createButton("4x4");
    private JButton btn2 = createButton("9x9");
    private JButton btn3 = createButton("16x16");
    private JButton btn4 = createButton("25x25");
    private JButton solveBtn = createButton("Solve!");
    private JPanel boardSizesPanel, boardPanel, board, commandPanel;
    private ArrayList<ArrayList<JButton>> rows, cols, grids; // used for easy access
    private int prevRow, prevCol, prevGrid;
    private JButton prevCell;

    int gridRow, gridCol;

    WindowFrame() {
        boardSizesPanel = new JPanel();
        boardSizesPanel.setBackground(Color.PINK);

        boardSizesPanel.add(btn1);
        boardSizesPanel.add(btn2);
        boardSizesPanel.add(btn3);
        boardSizesPanel.add(btn4);

        boardPanel = new JPanel();
        boardPanel.setBackground(Color.yellow);

        board = new JPanel();
        board.setBackground(Color.black);

        boardPanel.add(board);

        createBoard(9,9);
        btn2.setEnabled(false);

        commandPanel = new JPanel();
        commandPanel.setBackground(Color.magenta);
        commandPanel.add(solveBtn);

//        this.setLayout(new GridLayout(0, 1));
//        this.setLayout(new BorderLayout());
//        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Sudoku Solver by rrichy");
        this.setVisible(true);
        this.setResizable(false);

        this.add(boardSizesPanel, BorderLayout.NORTH);
        this.add(boardPanel, BorderLayout.CENTER);
        this.add(commandPanel, BorderLayout.SOUTH);

        this.pack();
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.addActionListener(this);

        return button;
    }

    private void createBoard(int r, int c) {
        rows = new ArrayList<>();
        cols = new ArrayList<>();
        grids = new ArrayList<>();

        prevRow = 0;
        prevCol = 0;
        prevGrid = 0;

        int n = r;
        while(n-- > 0) { // fill the arraylist with null so we can use arraylist.set method
            rows.add(new ArrayList<>());
            cols.add(new ArrayList<>());
            grids.add(new ArrayList<>());
        }

        board.removeAll();
        board.setPreferredSize(new Dimension(30*r, 30*r));

        gridRow = (int) Math.sqrt(r);
        gridCol = (int) Math.sqrt(c);
        int grid = 0;

        board.setLayout(new GridLayout(gridRow, gridCol, 1, 1));
        board.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        for(int i = 0; i < r; i += gridRow) {
            for(int j = 0; j < c; j += gridCol) {
                board.add(createBoardGrid(i, j, grid));
                grid++;
            }
        }

        prevCell = rows.get(0).get(0);

        this.pack();
        validate();
        repaint();
    }

    private JPanel createBoardGrid(int r, int c, int g) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(gridRow, gridCol));
        panel.setPreferredSize(new Dimension(30*r, 30*r));

        for(int i = r; i < r + gridRow; i++) {
            for(int j = c; j < c + gridCol; j++) {
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
        JButton cell = createButton("");
        cell.putClientProperty("row", r);
        cell.putClientProperty("col", c);
        cell.putClientProperty("grid", g);

//        cell.setIcon(icon);
        cell.setMargin(new Insets(0,0,0,0));
        cell.setBackground(Color.white);
//        cell.setFont(new Font("Arial", Font.PLAIN, 10));

        cell.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() >= 49 && e.getKeyCode() <= 57) cell.setText(String.valueOf(e.getKeyChar()));
                else if(e.getKeyCode() == 48) cell.setText("");
                System.out.println("Key pressed!" + e.getKeyCode());
            }
        });

        return cell;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        if(e.getSource() == btn1 || e.getSource() == btn2 || e.getSource() == btn3 || e.getSource() == btn4) {
            btn1.setEnabled(true);
            btn2.setEnabled(true);
            btn3.setEnabled(true);
            btn4.setEnabled(true);
            button.setEnabled(false);
            handleBoardSizeChange(e);
        }
        else if(e.getSource() == solveBtn) {
            ArrayList<ArrayList<Integer>> values = new ArrayList<>();
            rows.stream().forEach(row -> {
                ArrayList<Integer> rowVals = new ArrayList<>();
                row.stream().forEach(cell -> {
                    String text = cell.getText();
                    if(text == "") rowVals.add(0);
                    else rowVals.add(Integer.parseInt(text));
                });
                values.add(rowVals);
            });

            values.forEach(row -> System.out.println(row));
        }
        else {
            handleHighlightSelection(button);
        }
    }

    private void handleBoardSizeChange(ActionEvent e) {
        if(e.getSource() == btn1) createBoard(4,4);
        if(e.getSource() == btn2) createBoard(9,9);
        if(e.getSource() == btn3) createBoard(16,16);
        if(e.getSource() == btn4) createBoard(25,25);
    }

    private void handleHighlightSelection(JButton button) {
        rows.get(prevRow).forEach(b -> b.setBackground(Color.white));
        cols.get(prevCol).forEach(b -> b.setBackground(Color.white));
        grids.get(prevGrid).forEach(b -> b.setBackground(Color.white));
        prevCell.setBackground(Color.white);

        prevRow = (int) button.getClientProperty("row");
        prevCol = (int) button.getClientProperty("col");
        prevGrid = (int) button.getClientProperty("grid");
        prevCell = button;

        rows.get(prevRow).forEach(b -> b.setBackground(Color.decode("0xC8D5E6")));
        cols.get(prevCol).forEach(b -> b.setBackground(Color.decode("0xC8D5E6")));
        grids.get(prevGrid).forEach(b -> b.setBackground(Color.decode("0x8EA9CC")));
        prevCell.setBackground(Color.decode("0x5B82B5"));

//            System.out.println("row: " + button.getClientProperty("row"));
//            System.out.println("column: " + button.getClientProperty("col"));
//            System.out.println("grid: " + button.getClientProperty("grid"));
    }

}
