package com.personal.gameOfLife;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.awt.Color.black;
import static java.awt.Color.white;

/**
 * @author Parisana
 * on 23/03/20
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        new ButtonGrid(50, 50);// makes new ButtonGrid with 2 parameters
    }

}

class ButtonGrid {

    JFrame frame = new JFrame(); // creates frame
    private final JButton[][] grid; // names the grid of buttons
    Board board;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    final int rows;
    final int columns;

    public ButtonGrid(int rows, int columns) {
        final JMenuBar jMenuBar = new JMenuBar();
        this.rows = rows;
        this.columns = columns;
        this.board = new Board(rows, columns);

        final JMenu menu = new JMenu("Menu");
        final JMenu speedMenu = new JMenu("Speed");
        final GridLayout gridLayout = new GridLayout(rows, columns);
        frame.setLayout(gridLayout); // set layout
        grid = new JButton[rows][columns]; // allocate the size of grid
        // main menu
        final JMenuItem start = new JMenuItem("Start");
        menu.add(start);
        final JMenuItem stop = new JMenuItem("Stop");
        menu.add(stop);
        final JMenuItem glider = new JMenuItem("Glider");
        menu.add(glider);
        // speed menu
        final JMenuItem slow = new JMenuItem("Slow");
        final JMenuItem normal = new JMenuItem("Normal");
        final JMenuItem fast = new JMenuItem("Fast");
        speedMenu.add(slow);
        speedMenu.add(normal);
        speedMenu.add(fast);
        final GameOfLife gameOfLife = new GameOfLife(board);
        start.addActionListener(e-> {
            System.out.println("Start clicked");
            MenuSelectionManager.defaultManager().clearSelectedPath();

            CompletableFuture.runAsync(()-> {
                try {
                    if (!isRunning.get()) {
                        isRunning.set(true);
                        board.print();
                        gameOfLife.setStop(false);
                        gameOfLife.start(grid);
                    } else System.out.println("Running already");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).thenRun(()-> isRunning.set(false));
        });
        stop.addActionListener(e-> {
            System.out.println("Stop clicked");
            MenuSelectionManager.defaultManager().clearSelectedPath();
            gameOfLife.setStop(true);
            gameOfLife.getBoard().displayBoard(grid);
            isRunning.set(false);
            board.resetBoard();
        });
        glider.addActionListener(e->{
            stop.doClick();
            board.initializeBoard(new Point(6, 1), new Point(7, 1),
                    new Point(6, 2), new Point(7,2),
                    new Point(6, 11), new Point(7, 11), new Point(8, 11),
                    new Point(5, 12), new Point(9, 12),
                    new Point(4, 13), new Point(10, 13),
                    new Point(4, 14), new Point(10, 14),
                    new Point(7, 15),
                    new Point(5, 16), new Point(9, 16),
                    new Point(6, 17), new Point(7, 17), new Point(8, 17),
                    new Point(7, 18),
                    new Point(4, 21), new Point(5, 21), new Point(6, 21),
                    new Point(4, 22), new Point(5, 22), new Point(6, 22),
                    new Point(3, 23), new Point(7, 23),
                    new Point(3, 23), new Point(7, 23),
                    new Point(2, 25), new Point(3, 25), new Point(7, 25), new Point(8, 25),
                    new Point(4, 35), new Point(5, 35),
                    new Point(4, 36), new Point(5, 36));
            start.doClick();
        });
        slow.addActionListener(e-> gameOfLife.setSleepTime(500));
        normal.addActionListener(e-> gameOfLife.setSleepTime(300));
        fast.addActionListener(e-> gameOfLife.setSleepTime(50));
        jMenuBar.add(menu);
        jMenuBar.add(speedMenu);
        frame.setJMenuBar(jMenuBar);

        final Color white = new Color(255, 255, 255);
        final Color black = new Color(0, 0, 0);
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                final int row = y;
                final int column = x;
                final JButton jButton = new JButton();
                jButton.setPreferredSize(new Dimension(40, 40));
                jButton.addActionListener(e -> {
                    if (this.board.getBoard()[row][column]) {
                        jButton.setBackground(white);
                        this.board.setCell(row, column, false);
                    } else {
                        jButton.setBackground(black);
                        this.board.setCell(row, column, true);
                    }
                });
                grid[x][y] = jButton; // creates new button
                frame.add(grid[x][y]); // adds button to grid
                grid[x][y].setBackground(Color.white);

            }

        }
//        grid[0][1].setBackground(new Color(0, 0, 0));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // sets appropriate size for frame
        frame.setVisible(true); // makes frame visible
    }

}

class Board {
    private final boolean[][] boardArray;
    private final int rowSize;
    private final int columnSize;
    Board(int m, int n) {
        rowSize = m;
        columnSize = n;
        this.boardArray = new boolean[m][n];
    }

    void resetBoard() {
        for (boolean[] rows : boardArray) {
            for (boolean cell : rows)
                cell = false;
        }
    }

    boolean[][] getBoard() {
        return this.boardArray;
    }

    boolean setCell(int row, int column, boolean value) {
        boardArray[row][column] = value;
        return boardArray[row][column];
    }

    void initializeBoard(Point... points) {
        for (Point point : points) {
            this.boardArray[(int)point.getX()][(int) point.getY()] = true;
        }
    }
    void displayBoard(JButton[][] grid) {
        for (int row = 0; row<grid.length; row++)
            for (int column = 0; column<grid.length; column++) {
                if (this.boardArray[row][column]) {
                    grid[column][row].setBackground(black);
//                    this.boardArray[row][column]= false;
                } else {
                    grid[column][row].setBackground(white);
//                    this.boardArray[row][column]= true;
                }
            }
    }

    public void print() {
        for (int i = 0; i< boardArray.length; i++) {
            for (int j= 0; j< boardArray.length; j++) {
                if (boardArray[j][i])
                    System.out.print("[" +j + ", " + i + "]\t");
            }
        }
        System.out.println();
    }
}

class GameOfLife {
    private final Board board;
    private boolean[][] previousStage;
    private final ArrayList<Point> changedPoints = new ArrayList<>();
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private int sleepTime = 300;

    public Board getBoard() {
        return board;
    }

    GameOfLife(Board board) {
        this.board = board;
        this.previousStage = Arrays.stream(board.getBoard()).map(boolean[]::clone).toArray(boolean[][]::new);
    }

    void setStop(boolean val) {
        this.stop.set(val);
        if (val) {
            for (int i=0; i< previousStage.length; i++) {
                for (int j=0; j<previousStage.length; j++) {
                    board.getBoard()[i][j] = false;
                }
            }
            this.previousStage = Arrays.stream(board.getBoard()).map(boolean[]::clone).toArray(boolean[][]::new);
        }
    }

    void start(JButton[][] grid) throws InterruptedException {
        if (stop.get()) return;
        previousStage = Arrays.stream(board.getBoard()).map(boolean[]::clone).toArray(boolean[][]::new);
        board.displayBoard(grid);
        changedPoints.clear();
        for (int row=0; row< previousStage.length; row++) {
            for (int column=0; column< previousStage.length; column++) {
                final boolean value = previousStage[row][column];
                final int topRowVal = getVal(row - 1, column) + getVal(row - 1, column + 1) + getVal(row - 1, column - 1);
                final int rightVal = getVal(row, column + 1);
                final int leftVal = getVal(row, column - 1);
                final int sameRowVal = rightVal + leftVal;
                final int botRowVal = getVal(row + 1, column) + getVal(row + 1, column + 1) + getVal(row + 1, column - 1);
                int sum = topRowVal
                        + sameRowVal
                        + botRowVal;
//                System.out.println("["+row + "," + column + "] = " + sum);
                if (previousStage[row][column]) {
                    if (sum<2 || sum>3) {
                        board.setCell(row, column, false);
                        changedPoints.add(new Point(column, row));
                    }
                } else {
                    if (sum == 3) {
                        board.setCell(row, column, true);
                        changedPoints.add(new Point(column, row));
                    }
                }
            }
        }
        Thread.sleep(sleepTime);
//        changedPoints.forEach(point -> previousStage[(int)point.getX()][(int) point.getY()] = !previousStage[(int)point.getX()][(int) point.getY()]);

        start(grid);
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    private int getVal(int row, int column) {
        try {
            return previousStage[row][column] ? 1 : 0;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }


}
