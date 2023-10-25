package WizardTD;
import processing.core.PImage;

public class Bullet extends App{
    private int bullet_x;
    private int bullet_y;
    private final int move_distance = 5; //5 Pixels per frame
    private PImage sprite;
    private double bullet_damage;
    public double lastReturnTime;

    public Bullet (int bullet_x, int bullet_y, double bullet_damage, PImage sprite){
        this.bullet_x = bullet_x;
        this.bullet_y = bullet_y;
        this.bullet_damage = bullet_damage;
        this.sprite = sprite;
    }

    public int getX(){
        return this.bullet_x;
    }

    public int getY(){
        return this.bullet_y;
    }

    public double getDamage() {
        return this.bullet_damage;
    }

    public void setBulletXY(int x, int y){
        this.bullet_x = x;
        this.bullet_y = y;
    }

    public void update_damage(double damage) {
        this.bullet_damage = damage;
    }

    /**
     * Control the bullet move to a target monster
     * @param gremlin targe monster object
     * @param tower_x current tower x coordinate
     * @param tower_y current tower y coordinate
     */
    public void moveToMonster(Gremlin gremlin, int tower_x, int tower_y) {
        // Calculate the monster central point
        int monsterCenterX = gremlin.monster_x + gremlin.get_sprite_width() / 2;
        int monsterCenterY = gremlin.monster_y + gremlin.get_sprite_height() / 2;
    
        double distanceX = monsterCenterX - this.bullet_x;
        double distanceY = monsterCenterY - this.bullet_y;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        
        if (distance <= move_distance) { // 如果子弹非常靠近怪物
            gremlin.hitted(this.bullet_damage);
            // 重置子弹位置到塔
            // reset the bullet to tower
            this.bullet_x = tower_x;
            this.bullet_y = tower_y;
            this.lastReturnTime = System.currentTimeMillis();
        } else {
            double moveX = move_distance * (distanceX / distance);
            double moveY = move_distance * (distanceY / distance);
            
            this.bullet_x += moveX;
            this.bullet_y += moveY;
            
        }
    }
    

    public void draw(App app) {
        app.image(this.sprite, this.bullet_x, this.bullet_y);
    }
}
