package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BulletTest {

    @Test
    public void testBulletCreation() {
        Bullet bullet = new Bullet(100, 100, 50, null); 
        assertEquals(100, bullet.getX(), "Bullet x-coordinate should be initialized to 100");
        assertEquals(100, bullet.getY(), "Bullet y-coordinate should be initialized to 100");
        assertEquals(50, bullet.getDamage(), "Bullet damage should be initialized to 50");
    }

    @Test
    public void testSetBulletXY() {
        Bullet bullet = new Bullet(100, 100, 50, null);
        bullet.setBulletXY(150, 250);
        assertEquals(150, bullet.getX(), "Bullet x-coordinate should be updated to 150");
        assertEquals(250, bullet.getY(), "Bullet y-coordinate should be updated to 250");
    }

    @Test
    public void testGetDamage() {
        PImage sprite = new PImage();
        Bullet bullet = new Bullet(100, 100, 50, sprite);
        
        assertEquals(50, bullet.getDamage(), "Bullet damage should be 50");
    }

}
