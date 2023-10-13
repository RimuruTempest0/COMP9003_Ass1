package WizardTD;

import processing.core.PImage;

public abstract class Monster extends App {
    public int monster_x;
    public int monster_y;
    public double speed;
    public PImage sprite;
    public boolean isAlive;
    

    public Monster(int monster_x, int monster_y, double speed, boolean isAlive, PImage sprite) {
        this.monster_x = monster_x;
        this.monster_y = monster_y;
        this.speed = speed;
        this.isAlive = isAlive;
        this.sprite = sprite;
    }

    public void setMonsterImage(PImage sprite) {
        this.sprite = sprite;
    }

    public int getX() {
        return this.monster_x;
    }

    public int getY() {
        return this.monster_y;
    }

    public abstract void tick();
   
    public abstract void draw(App app);
   
}
