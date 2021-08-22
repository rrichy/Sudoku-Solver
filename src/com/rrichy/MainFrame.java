package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class MainFrame extends JFrame implements ActionListener {
    private JButton btn1 = createButton("4x4");
    private JButton btn2 = createButton("9x9");
    private JButton btn3 = createButton("16x16");
    private JButton btn4 = createButton("25x25");
    private JButton solveBtn = createButton("Solve!");
    private JPanel boardSizesPanel, boardPanel, commandPanel;
    private Board board;


    MainFrame() {
        boardSizesPanel = new JPanel();
        boardSizesPanel.setBackground(Color.PINK);

        boardSizesPanel.add(btn1);
        boardSizesPanel.add(btn2);
        boardSizesPanel.add(btn3);
        boardSizesPanel.add(btn4);

        boardPanel = new JPanel();
        boardPanel.setBackground(Color.yellow);

        board = new Board(9,9);

        boardPanel.add(board);

        btn2.setEnabled(false);

        commandPanel = new JPanel();
        commandPanel.setBackground(Color.magenta);
        commandPanel.add(solveBtn);

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
            int[][] puzzle = new int[board.nRows][board.nCols];
            boolean numerical = board.nRows < 10;

            // storing all the values from the ArrayList rows to a type int[][] variable
            IntStream.range(0, board.nRows).forEach(rowIndex -> {
                puzzle[rowIndex] = board.rows.get(rowIndex).stream().mapToInt(cell -> {
                    String text = cell.getText();
                    if(text.equals("")) return 0;

                    if(numerical) return Integer.parseInt(text);
                    else return text.charAt(0) - 64;
                }).toArray();
            });

            for(int[] row : puzzle) System.out.println(Arrays.toString(row));

            // the type int[][] variable is passed to the sudoku solving algorithm
            SudokuSolver sudoku = new SudokuSolver(puzzle);
            int[][] solution = sudoku.solve();

            IntStream.range(0, board.nRows).forEach(rowIndex -> {
                ArrayList<JButton> currentRow = board.rows.get(rowIndex);

                IntStream.range(0, board.nCols).forEach(colIndex -> {
                    JButton cell = currentRow.get(colIndex);
                    if(cell.getText().equals("")) {
                        if(numerical) cell.setText(String.valueOf(solution[rowIndex][colIndex]));
                        else cell.setText(String.valueOf((char) (solution[rowIndex][colIndex] + 64)));
                        cell.setForeground(Color.red);
                    }
                });
            });

        }
        else {
//            handleHighlightSelection(button);
        }
    }

    private void handleBoardSizeChange(ActionEvent e) {
        boardPanel.removeAll();
        if(e.getSource() == btn1) board = new Board(4,4);
        if(e.getSource() == btn2) board = new Board(9,9);
        if(e.getSource() == btn3) board = new Board(16,16);
        if(e.getSource() == btn4) board = new Board(25,25);
        boardPanel.add(board);

        this.pack();
        validate();
        repaint();
    }

}
