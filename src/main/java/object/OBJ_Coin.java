package object;

import java.awt.*;

public class OBJ_Coin extends SuperObject {
    private int x, y;
    private int speed = 2; // Falling speed

    public OBJ_Coin(int x, int y) {
        this.x = x;
        this.y = y;
        name = "Coin";
        // Define solidArea for collision detection (e.g., 16x16 size)
        solidArea = new Rectangle(x, y, 16, 16);
    }

    public void update() {
        // Move coin down
        y += speed;
        solidArea.y = y; // Update the collision box position
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW); // Placeholder color for coin; use an image if available
        g2.fillOval(x, y, 16, 16); // Draw a simple circle as a coin
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getSolidArea() {
        return solidArea;
    }
}
