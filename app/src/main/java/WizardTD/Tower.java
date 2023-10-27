package WizardTD;

import java.util.ArrayList;
import java.util.List;

import processing.core.PImage;

public class Tower extends App implements FireBullet{

    private int tower_x;
    private int tower_y;

    private double tower_range;
    private double tower_firing_speed;
    private double tower_damage;

    private int range_level;
    private int speed_level;
    private int damage_level;
    private int upgrade_count = 0;

    private int tower_cost;
    private int range_cost = 20;
    private int speed_cost = 20;
    private int damage_cost = 20;

    private PImage tower_sprite;
    private Bullet bullet;

   

    public Tower(double initial_tower_range, double initial_tower_firing_speed,
            int initial_tower_damage, int initial_tower_cost) {

        this.tower_range = initial_tower_range;
        this.tower_firing_speed = initial_tower_firing_speed;
        this.tower_damage = initial_tower_damage;
        this.range_level = 0;
        this.speed_level = 0;
        this.damage_level = 0;
        this.upgrade_count = 0;
        this.tower_cost = initial_tower_cost;
        
        

    }

    /**
     * Subtract mana value for each tower build
     * @param subtract_Cost subtract tower cost
     */
    public void mana_subtractor(int subtract_Cost) {
        if ((current_mana_value - subtract_Cost) >= 0) {
            current_mana_value -= subtract_Cost;
        }
    }

    /**
     * Set the tower cost
     * @param cost cost value
     */
    public void set_tower_cost(int cost) {
        this.tower_cost = cost;
    }

    public int get_tower_cost() {
        return this.tower_cost;
    }

    public int get_X() {
        return this.tower_x;
    }

    public int get_Y() {
        return this.tower_y;
    }

    /**
     * Set tower image
     * @param sprite
     */
    public void setTowerImage(PImage sprite) {
        this.tower_sprite = sprite;
    }

    public void setTowerXY(int tower_x, int tower_y) {
        this.tower_x = tower_x;
        this.tower_y = tower_y;

    }

    /**
     * Set tower with cursor position
     * @param app
     */
    public void setTowerWithMouse(App app) {
        this.tower_x = app.mouseX;
        this.tower_y = app.mouseY;
    }

    /**
     * Upgrade tower cost
     */
    public void upgrade_cost() {
        this.upgrade_count++;
        this.tower_cost += upgrade_count * 20 + initial_tower_cost;
    }

    /**
     * Upgrade tower range
     */
    public void upgrade_range() {
        this.tower_range += App.CELLSIZE;
        this.range_level++;
        this.range_cost += 10;
        upgrade_cost();
    }

    public int getRangeLevel() {
        return this.range_level;
    }

    public int getRangeCost() {
        return this.range_cost;
    }
    /**
     * Upgrade tower speed
     */
    public void upgrade_speed() {
        this.tower_firing_speed -= 0.5;
        this.speed_level++;
        this.speed_cost += 10;
        upgrade_cost();
    }

    public int getSpeedLevel() {
        return this.speed_level;
    }

    public int getSpeedCost() {
        return this.speed_cost;
    }

  
    /**
     * Upgrade tower damage
     */
    public void upgrade_damage() {
        this.tower_damage = this.tower_damage-(tower_damage / 2);
        this.damage_level++;
        this.bullet.update_damage(this.tower_damage);
        this.damage_cost += 10;
        upgrade_cost();
    }
   

    public int getDamageLevel() {
        return this.damage_level;
    }

    public int getDamageCost() {
        return this.damage_cost;
    }

    /**
     * Draw tower range upgrade mark
     * @param app
     * @param level current range level
     */
    public void draw_O(App app, int level) {
        app.fill(243, 10, 169);
        app.textSize(10);
        for (int i = 0; i < level; i++) {
            app.text("O", this.tower_x + (i * 10), this.tower_y + 5);
        }
    }

    /**
     * Draw tower damage upgrade mark
     * @param app
     * @param level current damage level
     */
    public void draw_X(App app, int level) {
        app.fill(243, 10, 169);
        app.textSize(10);
        for (int i = 0; i < level; i++) {
            app.text("X", this.tower_x + (i * 10), this.tower_y + this.tower_sprite.height);
        }
    }

    /**
     * Draw tower speed upgrade mark
     * @param app
     * @param level current speed level
     */
    public void draw_square(App app, int level) {
        app.noFill();
        app.stroke(10, 161, 243);
        app.strokeWeight(level);
        app.rect(this.tower_x + 5, this.tower_y + 5, 20, 20);
        app.strokeWeight(1);
    }

    /**
     * Draw tower firing range ellipse
     * @param app
     */
    public void draw_range(App app) {
        app.noFill();
        app.stroke(10, 240, 60);
        app.strokeWeight(1);
        app.ellipse((float) (this.tower_x + this.tower_sprite.width / 2),
                (float) (this.tower_y + this.tower_sprite.height / 2),
                (float) this.tower_range*2, (float) this.tower_range*2);
    }

    /**
     * Detect whether cursor on tower
     * @param app
     * @return Yes if cursor on tower, vice versa
     */
    public boolean mouse_on_tower(App app) {
        int mouse_X = app.mouseX;
        int mouse_Y = app.mouseY;
        if (mouse_X <= this.tower_x + this.tower_sprite.width &&
                mouse_X >= this.tower_x &&
                mouse_Y >= this.tower_y &&
                mouse_Y <= this.tower_y + this.tower_sprite.height) {
            return true;

        }
        return false;
    }

    /**
     * Detect whether tower on map area and on grass tile
     * @param app
     * @return True if on valid tile to place tower
     */
    public boolean valid_place_tower(App app) {
        int tower_grid_x = (this.tower_y - TOPBAR) / 32;
        int tower_grid_y = this.tower_x / 32;
        if (app.mouseX >= 0 && app.mouseX <= 640 && app.mouseY >= TOPBAR && app.mouseY <= 680) {
            try {
                if (Map_Grid[tower_grid_x][tower_grid_y] == ' ') {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        return false;
    }

    /**
     * Calculate the shortest distance between gremlin and tower
     * @param gremlinList A list store all generated gremlin
     * @return The shortest gremlin
     */
    public Gremlin getClosestMonsterInRange(List<Gremlin> gremlinList) {
        Gremlin closest_gremlin = null;
        double closest_distance = Double.MAX_VALUE;

        for (Gremlin g : gremlinList) {
            // store the distance of monster and tower
            int towerX = this.tower_x + this.tower_sprite.width / 2;
            int towerY = this.tower_y + this.tower_sprite.width / 2;
            int gremlinX = g.monster_x + g.get_sprite_width()/2;
            int gremlinY = g.monster_y + g.get_sprite_width()/2;

            // calculate the Euclidean Distance between gremlin and tower
            double distance = Math.sqrt(Math.pow(gremlinX - towerX, 2) + Math.pow(gremlinY - towerY, 2));

            // swap closest monster with shorter one  
            if (distance < this.tower_range && distance < closest_distance) {
                closest_gremlin = g;
                closest_distance = distance;
            }
        }

        return closest_gremlin;
    }

    /**
     * Generate bullet while tower been valid placecd
     */
    @Override
    public void set_bullet() {
        this.bullet = new Bullet(this.tower_x+this.tower_sprite.width/2, 
        this.tower_y+this.tower_sprite.width/2, this.tower_damage, bullet_image);
    }
    
    /**
     * Control firing rate
     * @param app
     */
    @Override
    public void fire_bullet(App app) {
        double currentTime = System.currentTimeMillis();
        double since_return_time = currentTime - bullet.return_time;

        // check if the timer passes 1.5 seconds
        if (since_return_time >= this.tower_firing_speed*1000) {
            Gremlin closest_gremlin = getClosestMonsterInRange(gremlinList);
            int towerX = this.tower_x + this.tower_sprite.width / 2;
            int towerY = this.tower_y + this.tower_sprite.width / 2;

            // if there is a closest gremlin, firing the bullet
            if (closest_gremlin != null) {
                bullet.moveToMonster(closest_gremlin, towerX, towerY);
            } else {
                bullet.setBulletXY(towerX, towerY);
            }

            bullet.draw(app);
        }
    }
    
    /**
     * Draw right down corner upgrade panel
     * @param x panel coordinate x
     * @param y panel coordinate y
     * @param width panel width
     * @param height panel height
     * @param costs list of seleceted upgrade
     * @param app
     */
    public void drawUpgradePanel(int x, int y, int width, int height, ArrayList<String> costs, App app) {
        // Draw the background rectangle
        app.fill(255); 
        app.stroke(0); 
        app.rect(x, y, width, height);
        
        // Draw the title
        app.fill(0); 
        app.textSize(12);
        app.text("Upgrade Cost", x+5, y +18);
        
        app.line(x, y+25, x+width, y+25);
        // Draw the costs
        app.textSize(18);
        
        for (int i = 0; i < costs.size(); i++) {
            app.textSize(10);
            app.text(costs.get(i), x + 5 , y + 50 + i * 25);
            if(i==costs.size()-1) {
                app.line(x, y + 55 + i * 30, x+width, y + 55 + i * 30);
                app.text("Total: " + Sidebar.total_cost, x + 5, y + 70 + i * 30);
            }
        }
      }

    public void draw(App app) {

        if (mouse_on_tower(app)) {
            draw_range(app);
        }
        app.image(this.tower_sprite, this.tower_x, this.tower_y);
    }
}
