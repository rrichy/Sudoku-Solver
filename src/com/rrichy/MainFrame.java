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
    private JButton clearBtn = createButton("Clear");
    private JPanel boardSizesPanel, boardPanel, commandPanel;
    private Board board;


    MainFrame() {
        boardSizesPanel = new JPanel();
//        boardSizesPanel.setBackground(Color.PINK);

        boardSizesPanel.add(btn1);
        boardSizesPanel.add(btn2);
        boardSizesPanel.add(btn3);
        boardSizesPanel.add(btn4);

        boardPanel = new JPanel();
        boardPanel.setBackground(Color.yellow);

        board = new Board(9, 9);

        boardPanel.add(board);

        btn2.setEnabled(false);

        commandPanel = new JPanel();
//        commandPanel.setBackground(Color.magenta);

        commandPanel.add(solveBtn);
        commandPanel.add(clearBtn);

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
//        else if(e.getSource() == solveBtn) board.solveBoard();
//        else if(e.getSource() == clearBtn) {
//            boardPanel.removeAll();
//            int nRows = board.nRows;
//            int nCols = board.nCols;
//
//            board = new Board(nRows, nCols);
//            boardPanel.add(board);
//
//            refresh();
//        }
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

        refresh();
    }

    private void refresh() {
        this.pack();
        validate();
        repaint();
    }

}
