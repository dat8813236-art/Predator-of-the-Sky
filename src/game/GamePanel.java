package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // Cửa sổ của màn hình game
    static final int WIDTH = 1200;
    static final int HEIGHT = 800;
    // Kích cỡ mặc định của Rắn , Predator và boss
    static final int UNIT_SIZE = 18;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    // Toạ độ bắt đầu game ( góc trên bên trái )
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int snakeSpeed = 120; // Tốc độ mặc định của Rắn
    int bodyParts = 3; // Độ dài ban đầu của Rắn
    char direction = 'R'; // Hướng ban đầu của Rắn
    boolean running = false;
    // Táo
    int appleX;
    int appleY;
    int applesEaten;
    static final int TREASURE_SIZE = UNIT_SIZE * 4;
    int bigAppleX;
    int bigAppleY;
    boolean bigAppleActive = false;
    int wrongCount = 0;
    // Predator và Boss
    static final int PREDATOR_SIZE = UNIT_SIZE * 2;
    int predatorX;
    int predatorY;
    boolean predatorActive = false;
    int predator2X;
    int predator2Y;
    boolean predator2Active = false;
    int predatorDelayCounter = 0;
    int predatorSpeed = 5;// số càng lớn = càng chậm
    static final int BOSS_SIZE = UNIT_SIZE * 3;
    boolean bossMode = false;
    int bossX;
    int bossY;
    boolean bossActive = false;
    // Các lever của game
    int level = 1;
    ArrayList<Rectangle> walls = new ArrayList<>();
    // Ảnh và các thông số khác
    Timer timer;
    Random random;
    Image appleImg;
    Image predatorImg;
    Image treasureImg ;
    Image bossImg;



    public GamePanel() {
        random = new Random();
        appleImg = new ImageIcon(getClass().getResource("/assets/apple.png")).getImage();
        predatorImg = new ImageIcon(getClass().getResource("/assets/predator.png")).getImage();
        treasureImg = new ImageIcon(getClass().getResource("/assets/treasure.png")).getImage();
        bossImg = new ImageIcon(getClass().getResource("/assets/boss.png")).getImage();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        generateWalls();
        running = true;
        spawnPredator();
        timer = new Timer(snakeSpeed, this);
        timer.start();
    }
    public void generateWalls() {
        walls.clear();
        for (int i = 0; i < 10; i++) {
            int wx, wy;
            boolean overlap;
            do {
                overlap = false;
                wx = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
                wy = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
                for (int j = 0; j < bodyParts; j++) {
                    if (x[j] == wx && y[j] == wy) {
                        overlap = true;
                        break;
                    }
                }
            } while (overlap);
            walls.add(new Rectangle(wx, wy, UNIT_SIZE, UNIT_SIZE));
        }
    }
    public void newApple() {
        appleImg = new ImageIcon(getClass().getResource("/assets/apple.png")).getImage();
        boolean onSnake;
        do {
            onSnake = false;
            appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    onSnake = true;
                    break;
                }
            }
        } while (onSnake);
    }

    public void newBigApple() {
        bigAppleX = random.nextInt(WIDTH / TREASURE_SIZE) * UNIT_SIZE;
        bigAppleY = random.nextInt(HEIGHT /TREASURE_SIZE) * UNIT_SIZE;
        bigAppleActive = true;
    }
    public void spawnPredator() {
        predatorX = random.nextInt((WIDTH - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predatorY = random.nextInt((HEIGHT - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predatorActive = true;
    }
    public void spawnPredator2() {
        predator2X = random.nextInt((WIDTH - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predator2Y = random.nextInt((HEIGHT - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predator2Active = true;
    }
    public void spawnBoss() {

        bossX = random.nextInt((WIDTH - BOSS_SIZE ) / UNIT_SIZE) * UNIT_SIZE;
        bossY = random.nextInt((HEIGHT - BOSS_SIZE ) / UNIT_SIZE) * UNIT_SIZE;
        bossActive = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 20, 30);
            g.drawString("Level: " + level, 20, 60);
            // Đổi màu nền theo level
            switch (level % 5) {
                case 1 -> setBackground(Color.black);
                case 2 -> setBackground(new Color(20, 20, 60));
                case 3 -> setBackground(new Color(0, 60, 30));
                case 4 -> setBackground(new Color(60, 0, 0));
                case 0 -> setBackground(new Color(40, 0, 60));
            }

            if (bossMode) {
                g.drawImage(bossImg ,bossX, bossY, BOSS_SIZE , BOSS_SIZE  , null);
            }

            if (predator2Active) {
                g.drawImage(predatorImg, predator2X, predator2Y,
                        PREDATOR_SIZE, PREDATOR_SIZE, null);
            }
            if (predatorActive) {
                g.drawImage(predatorImg, predatorX, predatorY,PREDATOR_SIZE , PREDATOR_SIZE , null);
            }

            // Vẽ táo to
            if (bigAppleActive) {
                g.drawImage(treasureImg ,bigAppleX, bigAppleY, TREASURE_SIZE ,TREASURE_SIZE , null);
            }
            g.drawImage(appleImg, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);
            g.setColor(Color.gray);
            for (Rectangle wall : walls) {
                g.fillRect(wall.x, wall.y, wall.width, wall.height);
            }
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
    public void levelUp() {
        level++;

        if (predatorSpeed > 1) predatorSpeed--;

        if (snakeSpeed > 60) {
            snakeSpeed -= 5;
            timer.setDelay(snakeSpeed);
        }

        if (level == 3) {
            spawnPredator2();
        }

        if (level == 7) {
            generateWalls();
        }
        if (level == 10) {
            bossMode = true;
            spawnBoss();
        }

    }

    public void checkApple() {
        // Táo thường
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
            if (applesEaten % 5 == 0) {
                levelUp();
            }

            // 30% xác suất spawn rương
            if (random.nextInt(100) < 99 && !bigAppleActive) {
                newBigApple();
            }
        }
        // Táo to
        if (bigAppleActive) {
            Rectangle treasureRect = new Rectangle(
                    bigAppleX,
                    bigAppleY,
                    TREASURE_SIZE,
                    TREASURE_SIZE
            );
            int headCenterX = x[0] + UNIT_SIZE / 2;
            int headCenterY = y[0] + UNIT_SIZE / 2;
            if (treasureRect.contains(headCenterX, headCenterY)) {
                timer.stop();
                showQuestion();
                bigAppleActive = false;
                newApple();
                timer.start();
            }
        }
    }
    public void checkCollision() {
        Rectangle head = new Rectangle(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
        for (Rectangle wall : walls) {
            if (head.intersects(wall)) {
                running = false;
                timer.stop();
                JOptionPane.showMessageDialog(this, "Đập đầu vào tường!");
                return;
            }
        }
        // Tự cắn thân
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                timer.stop();
                JOptionPane.showMessageDialog(this, "Bạn tự cắn mình!");
                return;
            }
        }

        // Bị bắt
        Rectangle snakeHead = new Rectangle(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
        checkEnemyCollision(snakeHead, predatorActive, predatorX, predatorY, PREDATOR_SIZE, "Predator 1");
        checkEnemyCollision(snakeHead, predator2Active, predator2X, predator2Y, PREDATOR_SIZE, "Predator 2");
        checkEnemyCollision(snakeHead, bossActive, bossX, bossY, BOSS_SIZE, "Boss");


        // Đụng tường
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
        }
    }
    private void checkEnemyCollision(Rectangle head, boolean active,
                                     int ex, int ey, int size, String name) {
        if (!active) return;
        Rectangle enemy = new Rectangle(ex, ey, size, size);
        if (head.intersects(enemy)) {
            running = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Bạn đã bị " + name + " săn!");
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

        int step = UNIT_SIZE;

        int dx = x[0] - predatorX;
        int dy = y[0] - predatorY;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) predatorX += step;
            else predatorX -= step;
        } else {
            if (dy > 0) predatorY += step;
            else predatorY -= step;
        }
    }
    public void movePredator2() {

        if (!predator2Active) return;

        int dx = x[0] - predator2X;
        int dy = y[0] - predator2Y;

        if (Math.abs(dx) > Math.abs(dy)) {
            predator2X += (dx > 0) ? UNIT_SIZE : -UNIT_SIZE;
        } else {
            predator2Y += (dy > 0) ? UNIT_SIZE : -UNIT_SIZE;
        }
    }
    public void moveBoss() {

        int step = UNIT_SIZE;

        int dx = x[0] - bossX;
        int dy = y[0] - bossY;

        if (Math.abs(dx) > Math.abs(dy)) {
            bossX += (dx > 0) ? step : -step;
        } else {
            bossY += (dy > 0) ? step : -step;
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

            Timer speedTimer = new Timer(5000, e -> {
                snakeSpeed = 120;
                timer.setDelay(snakeSpeed);
            });
            speedTimer.setRepeats(false);
            speedTimer.start();
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
            if (bossActive) {
                moveBoss();
            }
            predatorDelayCounter++;
            if (predatorDelayCounter % predatorSpeed == 0) {
                movePredator();
                movePredator2();
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