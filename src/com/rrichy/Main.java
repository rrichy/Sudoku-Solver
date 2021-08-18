package com.rrichy;

import javax.swing.*;
import javax.swing.border.*;
//import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

public class Main implements ActionListener {
    private static JFrame frame = new JFrame();
    private static JButton btn1 = createBoardButton("4x4");
    private static JButton btn2 = createBoardButton("9x9");
    private static JButton btn3 = createBoardButton("16x16");
    private static JButton btn4 = createBoardButton("25x25");

    public static void main(String[] args) {
        JPanel buttonPanel = new JPanel();
        JPanel board = new JPanel();

        buttonPanel.setLayout(new FlowLayout());

        buttonPanel.add(btn1);
        buttonPanel.add(btn2);
        buttonPanel.add(btn3);
        buttonPanel.add(btn4);

        JButton cell = new JButton("1");
        cell.setBackground(Color.white);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 10, 5, 10);
        Border compound = new CompoundBorder(line, margin);
        cell.setBorder(compound);

        cell.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(cell.isFocusPainted()) {
                    System.out.println("cell key pressed" + e.getKeyCode());
                    int key = e.getKeyCode();
                    if(key >= 48 && key <= 57) cell.setText(String.valueOf(e.getKeyChar()));
                }

            }
        });

        board.add(cell);

        frame.add(buttonPanel);
        frame.add(board);

        frame.setLayout(new GridLayout(0, 1));
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Sudoku Solver by rrichy");
        frame.setVisible(true);

//        long t0 = new Date().getTime();
//
//
//        System.out.println("");
//        System.out.print("Finished in " + (new Date().getTime() - t0) + "ms.");
    }

    private static JButton createBoardButton(String label) {
        JButton button = new JButton(label);
        button.setSize(80, 25);
        button.addActionListener(new Main());

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn1) System.out.println("4x4");
        if(e.getSource() == btn2) System.out.println("9x9");
        if(e.getSource() == btn3) System.out.println("16x16");
        if(e.getSource() == btn4) System.out.println("25x25");
    }
}
