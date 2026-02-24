package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int WIDTH = 1200;
    static final int HEIGHT = 800;
    static final int UNIT_SIZE = 18;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    int snakeSpeed = 120;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int bodyParts = 3;
    char direction = 'R';
    boolean running = false;
    int appleX;
    int appleY;
    int applesEaten;
    int bigAppleX;
    int bigAppleY;
    boolean bigAppleActive = false;
    int wrongCount = 0;
    int predatorX;
    int predatorY;
    boolean predatorActive = false;
    int predatorDelayCounter = 0;
    int predatorSpeed = 5;   // số càng lớn = càng chậm

    Timer timer;
    Random random;
    Image appleImg;
    Image predatorImg;

    public GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        spawnPredator();
        timer = new Timer(snakeSpeed, this);
        timer.start();
    }
    public void newApple() {
        appleImg = new ImageIcon(getClass().getResource("/assets/apple.png")).getImage();
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }
    public void newBigApple() {
        bigAppleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE ;
        bigAppleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        bigAppleActive = true;
    }
    public void spawnPredator() {
        predatorImg = new ImageIcon(getClass().getResource("/assets/predator.png")).getImage();
        predatorX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        predatorY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        predatorActive = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            if (predatorActive) {
                g.drawImage(predatorImg, predatorX, predatorY,UNIT_SIZE * 2, UNIT_SIZE * 2, null);
            }
            // Vẽ táo to
            if (bigAppleActive) {
                g.setColor(Color.MAGENTA);
                g.fillOval(bigAppleX, bigAppleY, 2 * UNIT_SIZE ,2 * UNIT_SIZE);
            }


            g.drawImage(appleImg, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0)); // thân
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }
    public void checkApple() {
        // Táo thường
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();

            // 30% xác suất spawn táo to
            if (random.nextInt(100) < 30 && !bigAppleActive) {
                newBigApple();
            }
        }

        // Táo to
        if (bigAppleActive && x[0] == bigAppleX && y[0] == bigAppleY) {
            timer.stop(); // pause game
            showQuestion(); // mở câu hỏi
            bigAppleActive = false;
            newApple();
            timer.start(); // resume game
        }
    }
    public void checkCollision() {

        // Predator bắt được
        if (predatorActive && x[0] == predatorX && y[0] == predatorY) {
            running = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Bạn đã bị săn!");
        }

        // Đụng tường
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= UNIT_SIZE; break;
            case 'D': y[0] += UNIT_SIZE; break;
            case 'L': x[0] -= UNIT_SIZE; break;
            case 'R': x[0] += UNIT_SIZE; break;
        }
    }
    public void movePredator() {

        if (!predatorActive) return;

        int dx = x[0] - predatorX;
        int dy = y[0] - predatorY;

        // Ưu tiên trục có khoảng cách lớn hơn
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) predatorX += UNIT_SIZE;
            else predatorX -= UNIT_SIZE;
        } else {
            if (dy > 0) predatorY += UNIT_SIZE;
            else predatorY -= UNIT_SIZE;
        }
    }
    public void showQuestion() {

        String question = "Java dùng từ khóa nào để tạo class?";
        String[] options = {"class", "define", "new", "object"};

        int answer = JOptionPane.showOptionDialog(
                this,
                question,
                "Quiz",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (answer == 0) {

            snakeSpeed = 80;
            timer.setDelay(snakeSpeed);

//            JOptionPane.showMessageDialog(this, "Đúng! Tăng tốc trong 5 giây!");

            new Timer(5000, e -> {
                snakeSpeed = 120;
                timer.setDelay(snakeSpeed);
            }).start();
        } else {
            wrongCount++;
//            JOptionPane.showMessageDialog(this, "Sai! Kẻ săn mồi được tăng tốc chạy");

            if (!predatorActive) {
                spawnPredator();
            }
            if (predatorSpeed > 1) {
                predatorSpeed--;   // càng sai càng nhanh
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {

            move();
            checkApple();

            predatorDelayCounter++;
            if (predatorDelayCounter % predatorSpeed == 0) {
                movePredator();
            }

            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
}