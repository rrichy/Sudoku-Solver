package com.rrichy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WindowFrame extends JFrame implements ActionListener {
    private JButton btn1 = createBoardSizeButton("4x4");
    private JButton btn2 = createBoardSizeButton("9x9");
    private JButton btn3 = createBoardSizeButton("16x16");
    private JButton btn4 = createBoardSizeButton("25x25");
    private JPanel boardSizePanel, boardCellPanel;

    ImageIcon icon = new ImageIcon("cell.jpg");

    WindowFrame() {
        boardSizePanel = new JPanel();
        boardSizePanel.setLayout(new FlowLayout());

        boardSizePanel.add(btn1);
        boardSizePanel.add(btn2);
        boardSizePanel.add(btn3);
        boardSizePanel.add(btn4);

        boardCellPanel = new JPanel();
        createBoard(4,4);
//        ImageIcon cellBg = new ImageIcon("cell.jpg");


        this.add(boardSizePanel);
        this.add(boardCellPanel);

        this.setLayout(new GridLayout(0, 1));
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Sudoku Solver by rrichy");
        this.setVisible(true);
    }

    private JButton createBoardSizeButton(String label) {
        JButton button = new JButton(label);
        button.setSize(80, 25);
        button.addActionListener(this);

        return button;
    }

    private void createBoard(int r, int c) {
        boardCellPanel.removeAll();
        boardCellPanel.setLayout(new GridLayout(r, c));
        boardCellPanel.setSize(45*r, 45*r);

        for(int i = 0; i < r; i++) {
            for(int j = 0; j < c; j++) {
                boardCellPanel.add(createCellButton());
            }
        }
        validate();
        repaint();
    }

    private JButton createCellButton() {
        JButton cell = new JButton("");
        cell.setIcon(icon);
        cell.setPreferredSize(new Dimension(45, 45));
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
        if(e.getSource() == btn1) System.out.println("4x4");
        if(e.getSource() == btn2) System.out.println("9x9");
        if(e.getSource() == btn3) System.out.println("16x16");
        if(e.getSource() == btn4) {
            System.out.println("25x25");
            createBoard(25,25);
            repaint();
        }
    }

}
