package WizardTD;

import java.util.List;

import processing.core.PImage;

public class Tower extends App {

    private int tower_x;
    private int tower_y;

    private double initial_tower_range;
    private double initial_tower_firing_speed;
    private int initial_tower_damage;
    private int tower_damage;

    private int range_level;
    private int speed_level;
    private int damage_level;
    private int upgrade_count = 0;

    private int tower_cost;
    // private int range_cost;
    // private int speed_cost;

    private PImage tower_sprite;
    private Bullet bullet;

    public Tower(double initial_tower_range, double initial_tower_firing_speed,
            int initial_tower_damage, int initial_tower_cost) {

        this.initial_tower_range = initial_tower_range;
        this.initial_tower_firing_speed = initial_tower_firing_speed;
        this.tower_damage = initial_tower_damage;
        this.range_level = 0;
        this.speed_level = 0;
        this.damage_level = 0;
        this.upgrade_count = 0;
        this.tower_cost = initial_tower_cost;
        this.bullet = new Bullet(0, 0, bullet_image);
        

    }

    public void mana_subtractor(int subtract_Cost) {
        if ((current_mana_value - subtract_Cost) < 0) {
            System.out.println("Insufficient Mana !!!");
        } else {
            current_mana_value -= subtract_Cost;
        }
    }

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

    public void setTowerImage(PImage sprite) {
        this.tower_sprite = sprite;
    }

    public void setTowerXY(int tower_x, int tower_y) {
        this.tower_x = tower_x;
        this.tower_y = tower_y;

    }

    public void setTowerWithMouse(App app) {
        this.tower_x = app.mouseX;
        this.tower_y = app.mouseY;
    }

    public void upgrade_cost() {
        this.upgrade_count++;
        this.tower_cost += upgrade_count * 20 + initial_tower_cost;
    }

    public void upgrade_range() {
        this.initial_tower_range += App.CELLSIZE;
        this.range_level++;
        // this.range_cost += 10;
        upgrade_cost();
    }

    public int getRangeLevel() {
        return this.range_level;
    }

    public void upgrade_speed() {
        this.initial_tower_firing_speed += 0.5;
        this.speed_level++;
        // this.speed_cost += 10;
        upgrade_cost();
    }

    public int getSpeedLevel() {
        return this.speed_level;
    }

    public void upgrade_damage() {
        this.tower_damage += (initial_tower_damage / 2);
        this.damage_level++;
        upgrade_cost();
    }

    public int getDamageLevel() {
        return this.damage_level;
    }

    public void draw_O(App app, int level) {
        app.fill(243, 10, 169);
        app.textSize(10);
        for (int i = 0; i < level; i++) {
            app.text("O", this.tower_x + (i * 10), this.tower_y + 5);
        }
    }

    public void draw_X(App app, int level) {
        app.fill(243, 10, 169);
        app.textSize(10);
        for (int i = 0; i < level; i++) {
            app.text("X", this.tower_x + (i * 10), this.tower_y + this.tower_sprite.height);
        }
    }

    public void draw_square(App app, int level) {
        app.noFill(); // 不填充颜色
        app.stroke(10, 161, 243); // 设置边框颜色为蓝色
        app.strokeWeight(level);
        app.rect(this.tower_x + 5, this.tower_y + 5, 20, 20);
        app.strokeWeight(1);
    }

    public void draw_range(App app) {
        app.noFill(); // 不填充颜色
        app.stroke(10, 240, 60); // 设置边框颜色为蓝色
        app.strokeWeight(1);
        app.ellipse((float) (this.tower_x + this.tower_sprite.width / 2),
                (float) (this.tower_y + this.tower_sprite.height / 2),
                (float) this.initial_tower_range, (float) this.initial_tower_range);
    }

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

    public Gremlin getClosestMonsterInRange(List<Gremlin> gremlinList) {
        Gremlin closestMonster = null;
        double closestDistance = Double.MAX_VALUE;

        for (Gremlin m : gremlinList) {
            // 获取塔和怪物的位置
            int towerX = this.tower_x;
            int towerY = this.tower_y;
            int monsterX = m.monster_x;
            int monsterY = m.monster_y;

            // 计算塔和怪物之间的距离
            double distance = Math.sqrt(Math.pow(monsterX - towerX, 2) + Math.pow(monsterY - towerY, 2));

            // 如果怪物在塔的射程内并且距离比当前找到的距离更近，那么更新最近的怪物和距离
            if (distance <= this.initial_tower_range && distance < closestDistance) {
                closestMonster = m;
                closestDistance = distance;
            }
        }

        return closestMonster;
    }

   

    public void draw(App app) {

        if (mouse_on_tower(app)) {
            draw_range(app);
        }

        app.image(this.tower_sprite, this.tower_x, this.tower_y);
        // bullet = new Bullet(this.tower_x, this.tower_y, bullet_image);
        int tower_width = this.tower_sprite.width;
        int tower_height = this.tower_sprite.height;

        
        
        Gremlin closestGremlin = getClosestMonsterInRange(gremlinList);
        if (closestGremlin != null) {
            // System.out.println(closestMonster.monster_x + " "+closestMonster.monster_y);
            bullet.moveToMonster(closestGremlin, this.tower_x+ 13, this.tower_y+ 13);
        }

        bullet.draw(app);

    }
}
