package WizardTD;

import processing.core.PImage;

public abstract class Monster extends App {
    public int monster_x;
    public int monster_y;
    public double speed;
    public PImage sprite;
    public boolean isAlive;
    public boolean isReachTheWizardhouse;
    public double hp;
    public double armour;
    

    public Monster(int monster_x, int monster_y, double speed, double hp, double armour, boolean isAlive, boolean isReachTheWizardhouse, PImage sprite) {
        this.monster_x = monster_x;
        this.monster_y = monster_y;
        this.speed = speed;
        this.hp = hp;
        this.armour = armour;
        this.isAlive = isAlive;
        this.isReachTheWizardhouse = isReachTheWizardhouse;
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

    
    /**
     * abstract tick for different type of monsters
     */
    public abstract void tick();
   
    /**
     * abstract draw for different type of monsters
     * @param app
     */
    public abstract void draw(App app);
   
}
