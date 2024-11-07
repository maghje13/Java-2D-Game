package main;

import entity.Player;
import object.OBJ_Coin;
import object.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    public final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels


    // SETTINGS
    public final boolean debugMode = false;
    int FPS = 60;
    public final int maxWorldCol = maxScreenCol;
    public final int maxWorldRow = maxScreenRow;

    // COIN SCORE TRACKER
    private int score = 0;


    // SYSTEM
    KeyHandler keyH = new KeyHandler();
    TileManager tileM = new TileManager(this);
    Thread gameThread;

    // PLAYER AND OBJECT SPAWNER
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[10];


    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // Should give better rendering performance
        this.addKeyListener(keyH); // Add the key listener
        this.setFocusable(true);

    }

    private ArrayList<OBJ_Coin> coins = new ArrayList<>();
    private Random random = new Random();

    // Coin spawn interval in frames
    private int coinSpawnTimer = 0;
    private final int coinSpawnInterval = 120;

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public void run() {

        double drawInterval = 1000000000/FPS; // 0.01666... seconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000 && debugMode) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }

        }

    }

    public void update() {
        player.update();

        // Spawn coins periodically
        coinSpawnTimer++;
        if (coinSpawnTimer >= coinSpawnInterval) {
            int x = random.nextInt(screenWidth - 16); // Random X position within screen width
            coins.add(new OBJ_Coin(x, 0)); // Add new coin at the top
            coinSpawnTimer = 0;
        }

        // Update all coins
        for (int i = 0; i < coins.size(); i++) {
            OBJ_Coin coin = coins.get(i);
            coin.update();

            // Check for coin collection, remove the coin and increase the score
            if (playerCollisionWithCoin(coin)) {
                coins.remove(i);
                i--; // Adjust index due to removal
                score += 1; // Increment score by 1 for each coin collected
                if (debugMode) System.out.println("Coin collected!");
                continue; // Skip further checks for this coin
            }

            // Check if coin hits the platform (row 10)
            int platformY = 9 * 48; // Y-coordinate of the platform (row 10)
            if (coin.getY() >= platformY) {
                // Stop the game
                gameOver();
                return; // Exit update loop
            }


        }
    }

    // Helper method for checking collision
    private boolean playerCollisionWithCoin(OBJ_Coin coin) {
        Rectangle playerBounds = new Rectangle(player.worldX, player.worldY, tileSize, tileSize);
        return playerBounds.intersects(coin.getSolidArea());
    }

    // Method to stop the game
    private void gameOver() {
        if (debugMode) System.out.println("Game Over!");
        // Implement additional game over logic (e.g., stop thread, show "Game Over" text)
        gameThread = null;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);
        player.draw(g2);

        // Draw all coins
        for (OBJ_Coin coin : coins) {
            coin.draw(g2);
        }

        // Display "Game Over" if the game thread is null (game stopped)
        if (gameThread == null) {
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(Color.RED);
            String text = "Game Over";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int x = (screenWidth - textWidth) / 2;
            int y = screenHeight / 2;
            g2.drawString(text, x, y);
        }

        // Draw the score in the top-left corner
        g2.setColor(Color.WHITE); // Set the text color
        g2.setFont(new Font("Arial", Font.BOLD, 24)); // Set the font
        g2.drawString("Score: " + score, 20, 30); // Display the score at (20, 30)


        g2.dispose();
    }

}
