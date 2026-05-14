package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import quiz.Question;
import quiz.QuestionManager;

public class GamePanel extends JPanel implements ActionListener {

    // ──────────────────────────────────────────────
    // KÍCH THƯỚC MÀN HÌNH
    // ──────────────────────────────────────────────
    static final int WIDTH      = 1200;
    static final int HEIGHT     = 600;
    static final int UNIT_SIZE  = 18;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int WALL_SIZE  = UNIT_SIZE * 2;

    // ──────────────────────────────────────────────
    // RẮN
    // ──────────────────────────────────────────────
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int  snakeSpeed = 120;
    int  bodyParts  = 3;
    char direction  = 'R';
    boolean running = false;

    // ──────────────────────────────────────────────
    // TÁO
    // ──────────────────────────────────────────────
    int appleX, appleY;
    int applesEaten;

    // ──────────────────────────────────────────────
    // NANH RẮN
    // ──────────────────────────────────────────────
    boolean fangItemActive = false;
    int     fangItemX, fangItemY;
    boolean fangMode       = false;
    long    fangItemSpawnTime;

    // ──────────────────────────────────────────────
    // THUỐC
    // ──────────────────────────────────────────────
    boolean redPotionActive  = false;
    boolean bluePotionActive = false;
    int     redPotionX,  redPotionY;
    int     bluePotionX, bluePotionY;
    boolean speedBoost = false;
    boolean wallPass   = false;
    long    redPotionSpawnTime;
    long    bluePotionSpawnTime;

    // ──────────────────────────────────────────────
    // ITEM KIẾM (chỉ xuất hiện ở level boss)
    // ──────────────────────────────────────────────
    boolean knifeItemActive = false;
    int     knifeItemX, knifeItemY;
    long    knifeItemSpawnTime;
    static final int KNIFE_DAMAGE        = 10;   // mỗi lần ăn kiếm gây dame này
    static final int ITEM_LIFETIME_MS    = 8000;

    // Trạng thái đang cầm knife
    boolean knifeMode = false;
    // Cooldown tránh damage boss liên tục trong 1 lần chạm
    boolean bossHitCooldown = false;

    // ──────────────────────────────────────────────
    // PAUSE / TRẠNG THÁI
    // ──────────────────────────────────────────────
    boolean paused  = false;
    boolean victory = false;

    // ──────────────────────────────────────────────
    // BOSS HP
    // ──────────────────────────────────────────────
    int bossMaxHP = 20;
    int bossHP    = bossMaxHP;

    // ──────────────────────────────────────────────
    // BOSS CHARGE (cơ chế mới)
    //   IDLE  → đứng yên, sau IDLE_TICKS ticks → WARN
    //   WARN  → vẽ đường cảnh báo, sau WARN_TICKS → CHARGE
    //   CHARGE→ lao thẳng theo hướng đã chọn cho đến khi ra biên → IDLE
    // ──────────────────────────────────────────────
    enum BossState { IDLE, WARN, CHARGE }
    BossState bossState      = BossState.IDLE;
    int       bossStateTick  = 0;
    char      bossChargeDir  = 'R';          // hướng sẽ lao
    static final int IDLE_TICKS  = 20;       // ~số frame đứng yên
    static final int WARN_TICKS  = 20;       // ~số frame hiện cảnh báo
    static final int BOSS_CHARGE_STEP = UNIT_SIZE * 10; // tốc độ lao

    // ──────────────────────────────────────────────
    // KÍCH THƯỚC PREDATOR / BOSS
    // ──────────────────────────────────────────────
    static final int PREDATOR_SIZE = UNIT_SIZE * 6;
    static final int BOSS_SIZE     = UNIT_SIZE * 10;
    static final int TREASURE_SIZE = UNIT_SIZE * 5;

    int     predatorX, predatorY;
    boolean predatorActive = false;
    int     predator2X, predator2Y;
    boolean predator2Active = false;
    int     predatorDelayCounter = 0;
    int     predatorSpeed = 5;

    boolean bossMode   = false;
    int     bossX, bossY;
    boolean bossActive = false;

    // ──────────────────────────────────────────────
    // RƯƠNG BÁU
    // ──────────────────────────────────────────────
    int     treasureX, treasureY;
    boolean treasureActive = false;
    int     wrongCount     = 0;
    QuestionManager questionManager = new QuestionManager();

    // ──────────────────────────────────────────────
    // CẤP ĐỘ & TƯỜNG
    // ──────────────────────────────────────────────
    int level = 1;
    ArrayList<Rectangle> walls = new ArrayList<>();



    // ──────────────────────────────────────────────
    // ẢNH & TIMER
    // ──────────────────────────────────────────────
    Timer  timer;
    Random random;
    Image appleImg, wallImg, predatorImg, predator2Img;
    Image treasureImg, bossImg, fangItemImg;
    Image snakeHeadDownImg, snakeHeadUpImg, snakeHeadRightImg, snakeHeadLeftImg;
    Image redPotionImg, bluePotionImg;
    Image knifeItemImg;
    Image head;

    // ══════════════════════════════════════════════
    // CONSTRUCTOR
    // ══════════════════════════════════════════════
    public GamePanel() {
        random = new Random();

        snakeHeadUpImg    = loadImg("/assets/snakeHeadUp.png");
        snakeHeadRightImg = loadImg("/assets/snakeHeadRight.png");
        snakeHeadLeftImg  = loadImg("/assets/snakeHeadLeft.png");
        snakeHeadDownImg  = loadImg("/assets/snakeHeadDown.png");
        redPotionImg      = loadImg("/assets/redPotion.png");
        bluePotionImg     = loadImg("/assets/bluePotion.png");
        appleImg          = loadImg("/assets/apple.png");
        wallImg           = loadImg("/assets/wall.png");
        predatorImg       = loadImg("/assets/predator.png");
        predator2Img      = loadImg("/assets/predator2.png");
        treasureImg       = loadImg("/assets/treasure.png");
        bossImg           = loadImg("/assets/boss.png");
        fangItemImg       = loadImg("/assets/fangItemImg.png");
        knifeItemImg      = loadImg("/assets/knife.png");

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    /** Helper load ảnh không crash khi file không tồn tại */
    private Image loadImg(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            return null;
        }
    }

    // ══════════════════════════════════════════════
    // KHỞI ĐỘNG
    // ══════════════════════════════════════════════
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
        for (int i = 0; i < 20; i++) {
            int wx, wy;
            boolean overlap;
            do {
                overlap = false;
                wx = random.nextInt((WIDTH  - WALL_SIZE) / UNIT_SIZE) * UNIT_SIZE;
                wy = random.nextInt((HEIGHT - WALL_SIZE) / UNIT_SIZE) * UNIT_SIZE;
                for (int j = 0; j < bodyParts; j++) {
                    if (x[j] == wx && y[j] == wy) { overlap = true; break; }
                }
            } while (overlap);
            walls.add(new Rectangle(wx, wy, WALL_SIZE, WALL_SIZE));
        }
    }

    public boolean isPositionValid(int px, int py, int size) {
        Rectangle obj = new Rectangle(px, py, size, size);
        for (Rectangle wall : walls) {
            if (obj.intersects(wall)) return false;
        }
        return true;
    }

    // ══════════════════════════════════════════════
    // SPAWN CÁC VẬT THỂ
    // ══════════════════════════════════════════════
    public void newApple() {
        do {
            appleX = random.nextInt(WIDTH  / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (!isPositionValid(appleX, appleY, UNIT_SIZE));
    }

    public void newTreasure() {
        do {
            treasureX = random.nextInt(WIDTH  / UNIT_SIZE) * UNIT_SIZE;
            treasureY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (!isPositionValid(treasureX, treasureY, TREASURE_SIZE));
        treasureActive = true;
    }

    public void spawnPredator() {
        predatorX = random.nextInt((WIDTH  - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predatorY = random.nextInt((HEIGHT - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predatorActive = true;
    }

    public void spawnPredator2() {
        predator2X = random.nextInt((WIDTH  - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predator2Y = random.nextInt((HEIGHT - PREDATOR_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        predator2Active = true;
    }

    public void spawnBoss() {
        bossX = random.nextInt((WIDTH  - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        bossY = random.nextInt((HEIGHT - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        bossActive = true;
        bossState  = BossState.IDLE;
        bossStateTick = 0;
    }

    /** Boss bị đánh → teleport về vị trí random (bỏ chạy) */
    public void bossTeleport() {
        bossX = random.nextInt((WIDTH  - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        bossY = random.nextInt((HEIGHT - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
        bossState     = BossState.IDLE;
        bossStateTick = 0;
    }

    public void spawnRedPotion() {
        do {
            redPotionX = random.nextInt(WIDTH  / UNIT_SIZE) * UNIT_SIZE;
            redPotionY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (!isPositionValid(redPotionX, redPotionY, UNIT_SIZE));
        redPotionActive    = true;
        redPotionSpawnTime = System.currentTimeMillis();
    }

    public void spawnBluePotion() {
        do {
            bluePotionX = random.nextInt(WIDTH  / UNIT_SIZE) * UNIT_SIZE;
            bluePotionY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (!isPositionValid(bluePotionX, bluePotionY, UNIT_SIZE));
        bluePotionActive    = true;
        bluePotionSpawnTime = System.currentTimeMillis();
    }

    public void spawnFangItem() {
        do {
            fangItemX = random.nextInt(WIDTH  / UNIT_SIZE) * UNIT_SIZE;
            fangItemY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (!isPositionValid(fangItemX, fangItemY, UNIT_SIZE));
        fangItemActive    = true;
        fangItemSpawnTime = System.currentTimeMillis();
    }

    /** Spawn kiếm – chỉ gọi khi bossActive */
    public void spawnKnifeItem() {
        do {
            knifeItemX = random.nextInt(WIDTH  / UNIT_SIZE) * UNIT_SIZE;
            knifeItemY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (!isPositionValid(knifeItemX, knifeItemY, UNIT_SIZE));
        knifeItemActive    = true;
        knifeItemSpawnTime = System.currentTimeMillis();
    }

    // ══════════════════════════════════════════════
    // CẤP ĐỘ
    // ══════════════════════════════════════════════
    public void levelUp() {
        level++;
        if (predatorSpeed > 1) predatorSpeed--;
        if (snakeSpeed > 60) {
            snakeSpeed -= 5;
            timer.setDelay(snakeSpeed);
        }
        if (level == 3) spawnPredator2();
        if (level == 4) generateWalls();
        if (level == 5) {
            bossMode = true;
            spawnBoss();
        }
    }

    // ══════════════════════════════════════════════
    // MÀU NỀN THEO LEVEL
    // ══════════════════════════════════════════════
    private Color getBaseColor() {
        switch (level % 5) {
            case 1: return new Color(20, 20, 20);
            case 2: return new Color(10, 20, 60);
            case 3: return new Color( 0, 50, 30);
            case 4: return new Color(60, 20, 20);
            case 0: return new Color(40,  0, 60);
            default: return Color.BLACK;
        }
    }

    // ══════════════════════════════════════════════
    // VẼ
    // ══════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!running) return;

        // --- Nền ô cờ ---
        Color base = getBaseColor();
        for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {
            for (int j = 0; j < HEIGHT / UNIT_SIZE; j++) {
                g.setColor((i + j) % 2 == 0 ? base : base.brighter());
                g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }

        // --- HUD ---
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + applesEaten, 20, 30);
        g.drawString("Level: " + level,       20, 60);

        // --- Hiện trạng thái knife ---
        if (knifeMode) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("KNIFE MODE!", 20, 90);
        }

        // --- PAUSE ---
        if (paused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("PAUSED", WIDTH / 2 - 150, HEIGHT / 2);
        }

        // --- BOSS CẢNH BÁO (đường xanh) ---
        if (bossActive && bossState == BossState.WARN) {
            drawBossWarning(g);
        }

        // --- BOSS ---
        if (bossActive && bossImg != null) {
            g.drawImage(bossImg, bossX, bossY, BOSS_SIZE, BOSS_SIZE, null);
        }

        // --- THANH MÁU BOSS ---
        if (bossActive) {
            drawBossHP(g);
        }

        // --- KIẾM ---
        if (knifeItemActive) {
            long timeLeft = ITEM_LIFETIME_MS - (System.currentTimeMillis() - knifeItemSpawnTime);
            if (timeLeft > 2000 || (System.currentTimeMillis() / 200) % 2 == 0) {
                if (knifeItemImg != null)
                    g.drawImage(knifeItemImg, knifeItemX, knifeItemY, UNIT_SIZE, UNIT_SIZE, null);
                else {
                    // fallback nếu không có ảnh: vẽ hình thoi vàng
                    g.setColor(Color.YELLOW);
                    int cx = knifeItemX + UNIT_SIZE / 2;
                    int cy = knifeItemY + UNIT_SIZE / 2;
                    int[] xp = {cx, cx + 7, cx, cx - 7};
                    int[] yp = {cy - 9, cy, cy + 9, cy};
                    g.fillPolygon(xp, yp, 4);
                    g.setColor(Color.ORANGE);
                    g.drawPolygon(xp, yp, 4);
                }
            }
        }

        // --- THUỐC ---
        drawPotionIfActive(g, redPotionActive,  redPotionImg,  redPotionX,  redPotionY,  redPotionSpawnTime);
        drawPotionIfActive(g, bluePotionActive, bluePotionImg, bluePotionX, bluePotionY, bluePotionSpawnTime);

        // --- PREDATOR ---
        if (predator2Active && predator2Img != null)
            g.drawImage(predator2Img, predator2X, predator2Y, PREDATOR_SIZE, PREDATOR_SIZE, null);
        if (predatorActive && predatorImg != null)
            g.drawImage(predatorImg, predatorX, predatorY, PREDATOR_SIZE, PREDATOR_SIZE, null);

        // --- RƯƠNG ---
        if (treasureActive && treasureImg != null)
            g.drawImage(treasureImg, treasureX, treasureY, TREASURE_SIZE, TREASURE_SIZE, null);

        // --- TÁO ---
        if (appleImg != null)
            g.drawImage(appleImg, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);

        // --- TƯỜNG ---
        for (Rectangle wall : walls) {
            if (wallImg != null)
                g.drawImage(wallImg, wall.x, wall.y, WALL_SIZE, WALL_SIZE, null);
            else {
                g.setColor(Color.GRAY);
                g.fillRect(wall.x, wall.y, WALL_SIZE, WALL_SIZE);
            }
        }

        // --- NANH ---
        if (fangItemActive && fangItemImg != null)
            g.drawImage(fangItemImg, fangItemX, fangItemY, UNIT_SIZE, UNIT_SIZE, null);

        // --- VICTORY ---
        if (victory) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("VICTORY!", WIDTH / 2 - 220, HEIGHT / 2);
        }

        // --- RẮN ---
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                if      (direction == 'U') head = snakeHeadUpImg;
                else if (direction == 'D') head = snakeHeadDownImg;
                else if (direction == 'L') head = snakeHeadLeftImg;
                else                       head = snakeHeadRightImg;
                if (head != null)
                    g.drawImage(head, x[0], y[0], UNIT_SIZE, UNIT_SIZE, this);
            } else {
                // Thân rắn đổi màu vàng khi đang knifeMode
                if (knifeMode) {
                    g.setColor(i % 2 == 0 ? new Color(255, 200, 0) : new Color(255, 140, 0));
                } else {
                    g.setColor(i % 2 == 0 ? new Color(93, 140, 63) : new Color(166, 214, 58));
                }
                g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    /** Vẽ đường cảnh báo đỏ theo hướng boss sắp lao */
    private void drawBossWarning(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Nhấp nháy: đỏ tươi nếu tick chẵn, đỏ tối nếu tick lẻ
        boolean blink = (bossStateTick / 5) % 2 == 0;
        if (blink) {
            g2.setColor(new Color(0, 255, 0, 180)); // đỏ tươi
        } else {
            g2.setColor(new Color(0, 205, 0, 100));   // đỏ tối
        }
        g2.setStroke(new BasicStroke(UNIT_SIZE * 4));

        switch (bossChargeDir) {
            case 'L': g2.drawLine(0,      bossY + BOSS_SIZE / 2, bossX, bossY + BOSS_SIZE / 2); break;
            case 'R': g2.drawLine(bossX + BOSS_SIZE, bossY + BOSS_SIZE / 2, WIDTH,  bossY + BOSS_SIZE / 2); break;
            case 'U': g2.drawLine(bossX + BOSS_SIZE / 2, 0,      bossX + BOSS_SIZE / 2, bossY); break;
            case 'D': g2.drawLine(bossX + BOSS_SIZE / 2, bossY + BOSS_SIZE, bossX + BOSS_SIZE / 2, HEIGHT); break;
        }
        g2.setStroke(new BasicStroke(1)); // reset
    }

    private void drawBossHP(Graphics g) {
        int barW = 300, barH = 20;
        int xBar = WIDTH / 2 - barW / 2, yBar = 20;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(xBar, yBar, barW, barH);
        int curW = (int) ((double) bossHP / bossMaxHP * barW);
        g.setColor(Color.RED);
        g.fillRect(xBar, yBar, curW, barH);
        g.setColor(Color.WHITE);
        g.drawRect(xBar, yBar, barW, barH);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("BOSS HP: " + bossHP + "/" + bossMaxHP, xBar + 70, yBar + 15);
    }

    private void drawPotionIfActive(Graphics g, boolean active, Image img,
                                    int px, int py, long spawnTime) {
        if (!active) return;
        long timeLeft = ITEM_LIFETIME_MS - (System.currentTimeMillis() - spawnTime);
        if (timeLeft > 2000 || (System.currentTimeMillis() / 200) % 2 == 0) {
            if (img != null) g.drawImage(img, px, py, UNIT_SIZE, UNIT_SIZE, null);
        }
    }

    // ══════════════════════════════════════════════
    // LOGIC UPDATE (gọi mỗi tick)
    // ══════════════════════════════════════════════
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running || paused) { repaint(); return; }

        move();
        checkApple();

        // --- Boss AI ---
        if (bossActive) updateBoss();

        // --- Predator ---
        predatorDelayCounter++;
        if (predatorDelayCounter % predatorSpeed == 0) {
            movePredator();
            movePredator2();
        }

        checkCollision();
        expireItems();

        // --- Spawn kiếm định kỳ khi boss còn sống ---
        if (bossActive && !knifeItemActive && random.nextInt(100) < 100) {
            spawnKnifeItem();
        }

        repaint();
    }

    /** Hết thời gian → xóa item */
    private void expireItems() {
        long now = System.currentTimeMillis();
        if (redPotionActive  && now - redPotionSpawnTime  > ITEM_LIFETIME_MS) redPotionActive  = false;
        if (bluePotionActive && now - bluePotionSpawnTime > ITEM_LIFETIME_MS) bluePotionActive = false;
        if (fangItemActive   && now - fangItemSpawnTime   > ITEM_LIFETIME_MS) fangItemActive   = false;
        if (knifeItemActive  && now - knifeItemSpawnTime  > ITEM_LIFETIME_MS) knifeItemActive  = false;
    }

    // ══════════════════════════════════════════════
    // BOSS AI MỚI: IDLE → WARN → CHARGE
    // ══════════════════════════════════════════════
    private void updateBoss() {
        bossStateTick++;

        switch (bossState) {
            case IDLE:
                if (bossStateTick >= IDLE_TICKS) {
                    // Chọn hướng random
                    char[] dirs = {'L', 'R', 'U', 'D'};
                    bossChargeDir = dirs[random.nextInt(4)];
                    bossState     = BossState.WARN;
                    bossStateTick = 0;
                }
                break;

            case WARN:
                // Đứng yên, đã vẽ cảnh báo trong draw()
                if (bossStateTick >= WARN_TICKS) {
                    bossState     = BossState.CHARGE;
                    bossStateTick = 0;
                }
                break;

            case CHARGE:
                // Lao theo hướng đã chọn
                switch (bossChargeDir) {
                    case 'L': bossX -= BOSS_CHARGE_STEP; break;
                    case 'R': bossX += BOSS_CHARGE_STEP; break;
                    case 'U': bossY -= BOSS_CHARGE_STEP; break;
                    case 'D': bossY += BOSS_CHARGE_STEP; break;
                }
                // Khi ra khỏi màn hình → quay về IDLE, reset vị trí ngẫu nhiên
                if (bossX < -BOSS_SIZE || bossX > WIDTH || bossY < -BOSS_SIZE || bossY > HEIGHT) {
                    bossX = random.nextInt((WIDTH  - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
                    bossY = random.nextInt((HEIGHT - BOSS_SIZE) / UNIT_SIZE) * UNIT_SIZE;
                    bossState     = BossState.IDLE;
                    bossStateTick = 0;
                }
                break;
        }
    }
    // ══════════════════════════════════════════════
    // VICTORY
    // ══════════════════════════════════════════════
    public void triggerVictory() {
        victory = true;
        timer.stop();
        new Timer(5000, e -> System.exit(0)) {{ setRepeats(false); start(); }};
    }

    // ══════════════════════════════════════════════
    // DI CHUYỂN RẮN
    // ══════════════════════════════════════════════
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

    // ══════════════════════════════════════════════
    // DI CHUYỂN PREDATOR
    // ══════════════════════════════════════════════
    public void movePredator() {
        if (!predatorActive) return;
        int dx = x[0] - predatorX, dy = y[0] - predatorY;
        if (Math.abs(dx) > Math.abs(dy)) predatorX += (dx > 0) ? UNIT_SIZE : -UNIT_SIZE;
        else                              predatorY += (dy > 0) ? UNIT_SIZE : -UNIT_SIZE;
    }

    public void movePredator2() {
        if (!predator2Active) return;
        int dx = x[0] - predator2X, dy = y[0] - predator2Y;
        if (Math.abs(dy) > Math.abs(dx)) predator2Y += (dy > 0) ? UNIT_SIZE : -UNIT_SIZE;
        else                              predator2X += (dx > 0) ? UNIT_SIZE : -UNIT_SIZE;
    }

    // ══════════════════════════════════════════════
    // CHECK ĂN TÁO & ITEMS
    // ══════════════════════════════════════════════
    public void checkApple() {
        // Táo thường
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
            if (applesEaten % 5 == 0) levelUp();

            if (random.nextInt(100) < 10 && !redPotionActive)  spawnRedPotion();
            if (random.nextInt(100) < 10 && !bluePotionActive) spawnBluePotion();
            if (random.nextInt(100) < 25 && !fangItemActive)   spawnFangItem();
            if (random.nextInt(100) < 30 && !treasureActive)   newTreasure();
        }

        // Rương báu
        if (treasureActive) {
            Rectangle tr   = new Rectangle(treasureX, treasureY, TREASURE_SIZE, TREASURE_SIZE);
            int hcx = x[0] + UNIT_SIZE / 2, hcy = y[0] + UNIT_SIZE / 2;
            if (tr.contains(hcx, hcy)) {
                timer.stop();
                showQuestion();
                treasureActive = false;
                newApple();
                timer.start();
            }
        }

        // Nanh
        if (fangItemActive && x[0] == fangItemX && y[0] == fangItemY) {
            fangItemActive = false;
            fangMode = true;
            new Timer(5000, e -> fangMode = false) {{ setRepeats(false); start(); }};
        }

        // Thuốc đỏ – tăng tốc
        if (redPotionActive && x[0] == redPotionX && y[0] == redPotionY) {
            redPotionActive = false;
            speedBoost = true;
            snakeSpeed = 60; timer.setDelay(snakeSpeed);
            new Timer(5000, e -> { speedBoost = false; snakeSpeed = 120; timer.setDelay(snakeSpeed); })
            {{ setRepeats(false); start(); }};
        }

        // Thuốc xanh – xuyên tường
        if (bluePotionActive && x[0] == bluePotionX && y[0] == bluePotionY) {
            bluePotionActive = false;
            wallPass = true;
            new Timer(8000, e -> wallPass = false) {{ setRepeats(false); start(); }};
        }

        // Knife – bật knifeMode 5 giây
        if (knifeItemActive && x[0] == knifeItemX && y[0] == knifeItemY) {
            knifeItemActive = false;
            knifeMode = true;
            bossHitCooldown = false;
            new Timer(5000, e -> {
                knifeMode = false;
                bossHitCooldown = false;
            }) {{ setRepeats(false); start(); }};
        }
    }

    // ══════════════════════════════════════════════
    // KIỂM TRA VA CHẠM
    // ══════════════════════════════════════════════
    public void checkCollision() {
        Rectangle snakeHeadRect = new Rectangle(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

        // Tường
        if (!wallPass) {
            for (Rectangle wall : walls) {
                if (snakeHeadRect.intersects(wall)) gameOver("Đập đầu vào tường!");
            }
        }

        // Tự cắn
        for (int i = bodyParts - 1; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) gameOver("Bạn tự cắn mình!");
        }

        // Va chạm với predator
        checkEnemyCollision(snakeHeadRect, predatorActive,  predatorX,  predatorY,  PREDATOR_SIZE, "Predator 1");
        checkEnemyCollision(snakeHeadRect, predator2Active, predator2X, predator2Y, PREDATOR_SIZE, "Predator 2");

        // Va chạm Boss
        if (bossActive) {
            Rectangle bossRect = new Rectangle(bossX, bossY, BOSS_SIZE, BOSS_SIZE);

            if (knifeMode) {
                // ── Đang cầm knife: cả đầu lẫn thân đều an toàn ──
                // Chỉ đầu rắn mới gây dame boss
                if (snakeHeadRect.intersects(bossRect) && !bossHitCooldown) {
                    bossHP -= KNIFE_DAMAGE;
                    bossHitCooldown = true;
                    // Cooldown 500ms tránh trừ máu liên tục
                    new Timer(500, e -> bossHitCooldown = false) {{ setRepeats(false); start(); }};

                    if (bossHP <= 0) {
                        bossActive = false;
                        triggerVictory();
                    } else {
                        bossTeleport(); // boss bỏ chạy
                    }
                }
                // Thân chạm boss → an toàn, không làm gì

            } else {
                // ── Không có knife: cả đầu lẫn thân đều chết ──
                if (snakeHeadRect.intersects(bossRect)) {
                    gameOver("Boss đã nghiền nát bạn!");
                }
                for (int i = 1; i < bodyParts; i++) {
                    Rectangle bodyPart = new Rectangle(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    if (bodyPart.intersects(bossRect)) {
                        gameOver("Boss cắt đứt thân rắn bạn!");
                    }
                }
            }
        }

        // Ra ngoài bản đồ
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT)
            gameOver("Bạn đã ra khỏi bản đồ!");
    }

    private void checkEnemyCollision(Rectangle head, boolean active,
                                     int ex, int ey, int size, String name) {
        if (!active) return;
        if (head.intersects(new Rectangle(ex, ey, size, size))) {
            if (fangMode) {
                if (name.equals("Predator 1")) predatorActive  = false;
                if (name.equals("Predator 2")) predator2Active = false;
                applesEaten += 2;
            } else {
                gameOver("Bạn đã bị " + name + " săn!");
            }
        }
    }

    // ══════════════════════════════════════════════
    // GAME OVER / RESTART
    // ══════════════════════════════════════════════
    public void gameOver(String message) {
        running = false;
        timer.stop();
        int choice = JOptionPane.showOptionDialog(this,
                message + "\nScore: " + applesEaten + "\nLevel: " + level +
                        "\nBạn có muốn chơi lại không?",
                "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"Chơi lại", "Thoát"}, "Chơi lại");
        if (choice == 0) restartGame();
        else             System.exit(0);
    }

    public void restartGame() {
        bodyParts    = 3;
        applesEaten  = 0;
        level        = 1;
        direction    = 'R';
        snakeSpeed   = 120;
        predatorSpeed = 5;
        bossHP       = bossMaxHP;

        predatorActive  = false;
        predator2Active = false;
        bossActive      = false;
        bossMode        = false;
        treasureActive  = false;
        fangItemActive  = false;
        redPotionActive = false;
        bluePotionActive = false;
        knifeItemActive = false;
        wallPass  = false;
        speedBoost = false;
        knifeMode        = false;
        bossHitCooldown  = false;
        fangMode  = false;
        victory   = false;

        bossState     = BossState.IDLE;
        bossStateTick = 0;

        for (int i = 0; i < bodyParts; i++) { x[i] = 0; y[i] = 0; }

        generateWalls();
        newApple();
        spawnPredator();

        running = true;
        timer.setDelay(snakeSpeed);
        timer.start();
    }

    // ══════════════════════════════════════════════
    // QUIZ
    // ══════════════════════════════════════════════
    public void showQuestion() {
        Question q = questionManager.getRandomQuestion();
        if (q == null) { JOptionPane.showMessageDialog(this, "Không có câu hỏi!"); return; }

        int answer = JOptionPane.showOptionDialog(this, q.getContent(), "Quiz",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, q.getOptions(), null);
        if (answer == -1) return;

        if (answer == q.getCorrectIndex()) {
            snakeSpeed = 90; timer.setDelay(snakeSpeed);
            new Timer(5000, e -> { snakeSpeed = 150; timer.setDelay(snakeSpeed); })
            {{ setRepeats(false); start(); }};
        } else {
            wrongCount++;
            if (!predatorActive) spawnPredator();
            if (predatorSpeed > 1) predatorSpeed--;
        }
    }

    // ══════════════════════════════════════════════
    // INPUT
    // ══════════════════════════════════════════════
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_C:     paused    = !paused; break;
                case KeyEvent.VK_LEFT:  if (direction != 'R') direction = 'L'; break;
                case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                case KeyEvent.VK_UP:    if (direction != 'D') direction = 'U'; break;
                case KeyEvent.VK_DOWN:  if (direction != 'U') direction = 'D'; break;
            }
        }
    }
}
