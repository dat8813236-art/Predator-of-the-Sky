package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import quiz.Question;
import quiz.QuestionManager;

public class GamePanel extends JPanel implements ActionListener {
    // Cửa sổ của màn hình game
    static final int WIDTH =1200;
    static final int HEIGHT = 600;

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

    static final int WALL_SIZE = UNIT_SIZE * 2;

    // Nanh rắn ( đã làm )
    boolean fangItemActive = false;
    int fangItemX, fangItemY;

    // Trạng thái cường hóa rắn
    boolean fangMode = false;

    // Thuốc tăng cường Đỏ và Xanh ( Đã làm )
    boolean redPotionActive = false;
    boolean bluePotionActive = false;

    int redPotionX, redPotionY;
    int bluePotionX, bluePotionY;

    // Trạng thái Buff khi ăn thuốc Đỏ
    boolean speedBoost = false;
    boolean wallPass = false;

    // Kho báu
    static final int TREASURE_SIZE = UNIT_SIZE * 4;
    int treasureX;
    int treasureY;
    boolean treasureActive = false;
    int wrongCount = 0;
    QuestionManager questionManager = new QuestionManager();

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
    int bossDashCounter = 0;
    long redPotionSpawnTime;
    long bluePotionSpawnTime;
    long fangItemSpawnTime;

    // Các lever của game
    int level = 1;
    ArrayList<Rectangle> walls = new ArrayList<>();

    // Ảnh và các thông số khác
    Timer timer;
    Random random;
    Image appleImg;
    Image wallImg;
    Image predatorImg;
    Image predator2Img;
    Image treasureImg;
    Image bossImg;
    Image snakeHeadDownImg;
    Image redPotionImg;
    Image bluePotionImg;
    Image fangItemImg;
    Image snakeHeadUpImg;
    Image snakeHeadRightImg;
    Image snakeHeadLeftImg;
    Image head;
    public GamePanel() {
        random = new Random();
        snakeHeadUpImg = new ImageIcon(getClass().getResource("/assets/snakeHeadUp.png")).getImage();
        snakeHeadRightImg = new ImageIcon(getClass().getResource("/assets/snakeHeadRight.png")).getImage();
        snakeHeadLeftImg = new ImageIcon(getClass().getResource("/assets/snakeHeadLeft.png")).getImage();
        redPotionImg = new ImageIcon(getClass().getResource("/assets/redPotion.png")).getImage();
        bluePotionImg = new ImageIcon(getClass().getResource("/assets/bluePotion.png")).getImage();
        snakeHeadDownImg = new ImageIcon(getClass().getResource("/assets/snakeHeadDown.png")).getImage();
        appleImg = new ImageIcon(getClass().getResource("/assets/apple.png")).getImage();
        wallImg = new ImageIcon(getClass().getResource("/assets/wall.png")).getImage();
        predatorImg = new ImageIcon(getClass().getResource("/assets/predator.png")).getImage();
        predator2Img = new ImageIcon(getClass().getResource("/assets/predator2.png")).getImage();
        treasureImg = new ImageIcon(getClass().getResource("/assets/treasure.png")).getImage();
        bossImg = new ImageIcon(getClass().getResource("/assets/boss.png")).getImage();
        fangItemImg = new ImageIcon(getClass().getResource("/assets/fangItemImg.png")).getImage();
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
        for (int i = 0 ; i < 20 ; i++) {
            int wx, wy;
            boolean overlap;
            do {
                overlap = false;
                wx = random.nextInt((WIDTH - WALL_SIZE) / UNIT_SIZE) * UNIT_SIZE;
                wy = random.nextInt((HEIGHT - WALL_SIZE) / UNIT_SIZE) * UNIT_SIZE;
                for (int j = 0; j < bodyParts; j++) {
                    if (x[j] == wx && y[j] == wy) {
                        overlap = true;
                        break;
                    }
                }
            } while (overlap);
            walls.add(new Rectangle(wx, wy, WALL_SIZE, WALL_SIZE));
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

    public void newtreasure() {
        treasureX = random.nextInt(WIDTH / TREASURE_SIZE) * UNIT_SIZE;
        treasureY = random.nextInt(HEIGHT / TREASURE_SIZE) * UNIT_SIZE;
        treasureActive = true;
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
    // Hàm Spawm Thuốc
    public void spawnRedPotion() {
        redPotionX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        redPotionY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        redPotionActive = true;
        redPotionSpawnTime = System.currentTimeMillis();
    }

    public void spawnBluePotion() {
        bluePotionX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        bluePotionY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        bluePotionActive = true;
    }

    // Mỗi lever nên spawm 1 predator mới
    public void spawnBoss() {
        bossX = random.nextInt((WIDTH - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        bossY = random.nextInt((HEIGHT - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        bossActive = true;
    }
    public void spawnFangItem() {
        fangItemX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        fangItemY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        fangItemActive = true;
        fangItemSpawnTime = System.currentTimeMillis();
    }
    private Color getBaseColor() {
        switch (level % 5) {
            case 1: return new Color(20, 20, 20);      // xám tối
            case 2: return new Color(10, 20, 60);      // xanh navy
            case 3: return new Color(0, 50, 30);       // xanh rừng
            case 4: return new Color(60, 20, 20);      // đỏ tối
            case 0: return new Color(40, 0, 60);       // tím tối
            default: return Color.BLACK;
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            Color base = getBaseColor();

            for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {
                for (int j = 0; j < HEIGHT / UNIT_SIZE; j++) {

                    if ((i + j) % 2 == 0)
                        g.setColor(base);
                    else
                        g.setColor(base.brighter());

                    g.fillRect(i * UNIT_SIZE,
                            j * UNIT_SIZE,
                            UNIT_SIZE,
                            UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 20, 30);
            g.drawString("Level: " + level, 20, 60);
            // Vẽ thanh máu của Rắn : ( chưa làm )


            if (bossActive) {
                g.drawImage(bossImg, bossX, bossY, BOSS_SIZE, BOSS_SIZE, null);
            }
            if(redPotionActive){
                long timeLeft = 8000 - (System.currentTimeMillis() - redPotionSpawnTime);

                if(timeLeft > 2000 || (System.currentTimeMillis()/200)%2==0){
                    g.drawImage(redPotionImg, redPotionX, redPotionY, UNIT_SIZE, UNIT_SIZE, null);
                }
            }

            if(bluePotionActive){
                long timeLeft = 8000 - (System.currentTimeMillis() - bluePotionSpawnTime);

                if(timeLeft > 2000 || (System.currentTimeMillis()/200)%2==0){
                    g.drawImage(bluePotionImg, bluePotionX, bluePotionY, UNIT_SIZE, UNIT_SIZE, null);
                }
            }

            if (predator2Active) {
                g.drawImage(predator2Img, predator2X, predator2Y,
                        PREDATOR_SIZE, PREDATOR_SIZE, null);
            }
            if (predatorActive) {
                g.drawImage(predatorImg, predatorX, predatorY, PREDATOR_SIZE, PREDATOR_SIZE, null);
            }

            if (treasureActive) {
                g.drawImage(treasureImg, treasureX, treasureY, TREASURE_SIZE, TREASURE_SIZE, null);
            }
            g.drawImage(appleImg, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);
            for (Rectangle wall : walls) {
                g.drawImage(wallImg, wall.x, wall.y, WALL_SIZE, WALL_SIZE, null);
            }
            if (fangItemActive) {
                g.drawImage(fangItemImg, fangItemX, fangItemY, UNIT_SIZE, UNIT_SIZE, null);
            }

            // Vẽ rắn
            for (int i = 0; i < bodyParts; i++) {
                // Thêm các chức năng để đầu rắn di chuyển theo hướng điều
                if (i == 0) {
                    if(direction == 'U') head = snakeHeadUpImg;
                    if(direction == 'D') head = snakeHeadDownImg;
                    if(direction == 'L') head = snakeHeadLeftImg;
                    if(direction == 'R') head = snakeHeadRightImg;

                    // Đầu rắn
                    g.drawImage(head, x[0], y[0], UNIT_SIZE, UNIT_SIZE, this);
                } else {
                    // THÂN RẮN
                    if (i % 2 == 0)
                        g.setColor(new Color(93, 140, 63));
                    else
                        g.setColor(new Color(166, 214, 58));

                    g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

                    g.setColor(Color.BLACK);
                    g.drawRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
    }

    // Cấp độ ( 1-10 )
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

        if (level == 4) {
            generateWalls();
        }
        if (level == 5) {
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
            // 40% spawn thuốc đỏ
            if (random.nextInt(100) < 40 && !redPotionActive) {
                spawnRedPotion();
            }
            // 30% spawn thuốc xanh
            if (random.nextInt(100) < 30 && !bluePotionActive) {
                spawnBluePotion();
            }
            // 25% rơi nanh
            if (random.nextInt(100) < 25 && !fangItemActive) {
                spawnFangItem();
            }

            // 30% xác suất spawn rương
            if (random.nextInt(100) < 99 && !treasureActive) {
                newtreasure();
            }

        }
        // Rương báu
        if (treasureActive) {
            Rectangle treasureRect = new Rectangle(
                    treasureX,
                    treasureY,
                    TREASURE_SIZE,
                    TREASURE_SIZE
            );
            int headCenterX = x[0] + UNIT_SIZE / 2;
            int headCenterY = y[0] + UNIT_SIZE / 2;
            if (treasureRect.contains(headCenterX, headCenterY)) {
                timer.stop();
                showQuestion();
                treasureActive = false;
                newApple();
                timer.start();
            }
        }
        // Nanh rand
        if (fangItemActive && x[0] == fangItemX && y[0] == fangItemY) {

            fangItemActive = false;
            fangMode = true;

            Timer fangTimer = new Timer(5000, e -> {
                fangMode = false;
            });

            fangTimer.setRepeats(false);
            fangTimer.start();
        }
        // Ăn thuốc đỏ Tăng Tốc
        if (redPotionActive && x[0] == redPotionX && y[0] == redPotionY) {

            redPotionActive = false;

            speedBoost = true;

            snakeSpeed = 60;
            timer.setDelay(snakeSpeed);

            Timer speedTimer = new Timer(5000, e -> {
                speedBoost = false;
                snakeSpeed = 120;
                timer.setDelay(snakeSpeed);
            });

            speedTimer.setRepeats(false);
            speedTimer.start();
        }
        // Ăn thuốc Xanh xuyên đá
        if (bluePotionActive && x[0] == bluePotionX && y[0] == bluePotionY) {

            bluePotionActive = false;

            wallPass = true;

            Timer wallTimer = new Timer(8000, e -> {
                wallPass = false;
            });

            wallTimer.setRepeats(false);
            wallTimer.start();
        }
    }
    public void checkCollision() {
        // Sửa lại để Rắn ăn thuốc Xanh không bị đâm vào đá
        Rectangle head = new Rectangle(x[0], y[0], UNIT_SIZE , UNIT_SIZE );

        if(!wallPass) {
            for (Rectangle wall : walls) {
                if (head.intersects(wall)) {
                    gameOver("Đập đầu vào tường!");
                }
            }
        }
        // Tự cắn thân
        for (int i = bodyParts - 1; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i])  {
                gameOver("Bạn tự cắn mình!");
            }
        }

        // Bị bắt
        Rectangle snakeHead = new Rectangle(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
        checkEnemyCollision(snakeHead, predatorActive, predatorX, predatorY, PREDATOR_SIZE, "Predator 1");
        checkEnemyCollision(snakeHead, predator2Active, predator2X, predator2Y, PREDATOR_SIZE, "Predator 2");
        checkEnemyCollision(snakeHead, bossActive, bossX, bossY, BOSS_SIZE, "Boss");
        // Đụng tường
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            gameOver("Bạn đã ra khỏi bản đồ!");
        }else {

            if (x[0] < 0) x[0] = WIDTH - UNIT_SIZE;
            if (x[0] >= WIDTH) x[0] = 0;

            if (y[0] < 0) y[0] = HEIGHT - UNIT_SIZE;
            if (y[0] >= HEIGHT) y[0] = 0;

        }
    }


private void checkEnemyCollision(Rectangle head, boolean active,
                                 int ex, int ey, int size, String name) {
    if (!active) return;

    Rectangle enemy = new Rectangle(ex, ey, size, size);

    if (head.intersects(enemy)) {

        if (fangMode) {
            // Ăn predator thay vì chết
            if (name.equals("Predator 1")) predatorActive = false;
            if (name.equals("Predator 2")) predator2Active = false;
            if (name.equals("Boss")) bossActive = false;

            applesEaten += 2; // thưởng điểm
        } else {
            gameOver("Bạn đã bị " + name + " săn!");
        }
    }
}
    public void gameOver(String message) {
        running = false;
        timer.stop();

        int choice = JOptionPane.showOptionDialog(
                this,
                message + "\nBạn có muốn chơi lại không?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Chơi lại", "Thoát"},
                "Chơi lại"
        );

        if (choice == 0) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    public void restartGame() {
        bodyParts = 3;
        applesEaten = 0;
        level = 1;
        direction = 'R';
        snakeSpeed = 120;
        predatorSpeed = 5;

        predatorActive = false;
        predator2Active = false;
        bossActive = false;
        bossMode = false;
        treasureActive = false;

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        generateWalls();
        newApple();
        spawnPredator();

        running = true;
        timer.setDelay(snakeSpeed);
        timer.start();
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
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

        if (Math.abs(dy) > Math.abs(dx)) {
            predator2Y += (dy > 0) ? UNIT_SIZE : -UNIT_SIZE;
        } else {
            predator2X += (dx > 0) ? UNIT_SIZE : -UNIT_SIZE;
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

        Question q = questionManager.getRandomQuestion();
        if (q == null) {
            JOptionPane.showMessageDialog(this, "Không có câu hỏi!");
            return;
        }

        int answer = JOptionPane.showOptionDialog(
                this,
                q.getContent(),
                "Quiz",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                q.getOptions(),
                null
        );

        // Nếu người chơi đóng cửa sổ
        if (answer == -1) {
            return;
        }

        // Trả lời đúng
        if (answer == q.getCorrectIndex()) {

            snakeSpeed = 80;
            timer.setDelay(snakeSpeed);

            Timer speedTimer = new Timer(5000, e -> {
                snakeSpeed = 120;
                timer.setDelay(snakeSpeed);
            });
            speedTimer.setRepeats(false);
            speedTimer.start();

        }
        // Trả lời sai
        else {

            wrongCount++;

            if (!predatorActive) {
                spawnPredator();
            }

            if (predatorSpeed > 1) {
                predatorSpeed--;
            }
        }
    }
    public void moveBossFast() {

        int step = UNIT_SIZE * 3; // dash cực mạnh

        int dx = x[0] - bossX;
        int dy = y[0] - bossY;

        if (Math.abs(dx) > Math.abs(dy)) {
            bossX += (dx > 0) ? step : -step;
        } else {
            bossY += (dy > 0) ? step : -step;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            if (bossActive) {

                bossDashCounter++;

                if (bossDashCounter % 20 == 0) {
                    moveBossFast(); // dash
                } else {
                    moveBoss(); // bình thường
                }
            }
            predatorDelayCounter++;
            if (predatorDelayCounter % predatorSpeed == 0) {
                movePredator();
                movePredator2();
            }

            checkCollision();
            // item tự biến mất sau 8 giây

            if(redPotionActive &&
                    System.currentTimeMillis() - redPotionSpawnTime > 8000){
                redPotionActive = false;
            }

            if(bluePotionActive &&
                    System.currentTimeMillis() - bluePotionSpawnTime > 8000){
                bluePotionActive = false;
            }

            if(fangItemActive &&
                    System.currentTimeMillis() - fangItemSpawnTime > 8000){
                fangItemActive = false;
            }
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