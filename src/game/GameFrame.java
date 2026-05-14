package game;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame(String username) {
        setTitle("Snake Hunter Game - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(new GamePanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}