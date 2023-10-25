package WizardTD;

import java.util.HashMap;

import processing.core.PImage;

public class Gremlin extends Monster {

    public boolean animation_start;
    public boolean animation_finished;
    public int current_frame = 0;
    public int number_frames = 5;
    private int life_bar_width = 20;
    MyPoint next_point = new MyPoint();
    // 每个Monster存储对应的路线迭代index
    HashMap<MyPoint, Integer> indexMap = new HashMap<>();
    
    MyPoint spawn_p;

    public Gremlin(int monster_x, int monster_y, double speed, double hp, double armour, boolean isAlive, boolean isReachTheWizardhouse, boolean animation_start,
            PImage sprite) {
        super(monster_x, monster_y, speed, hp, armour, isAlive, isReachTheWizardhouse, sprite);
        this.animation_start = animation_start;
        spawn_p = new MyPoint(((monster_y - 6 - TOPBAR) / CELLSIZE), (monster_x - 6) / CELLSIZE);
        
    }

    private double current_speed = this.speed;

    public void set_gremlin_XY (int x, int y){
        this.monster_x = x;
        this.monster_y = y;
    }
    
    /**
     * Method for drawing gremlin death animation
     * @param x animation x coordinate
     * @param y animation y coordinate
     * @param app
     */
    public void drawGremlinDeath(int x, int y, App app) {
        double speed = (double) number_frames / (double) FPS;
        if (current_frame == gremlin_dead.length - 1) {
            this.animation_start = false;
            this.animation_finished = true;
        }

        if (this.animation_start) {
            app.image(gremlin_dead[current_frame], x, y);
            if (app.frameCount % (FPS * speed) == 0) {
                current_frame = (current_frame + 1) % number_frames;
            }
        }
    }
    
    /**
     * Set monster spawn position
     * @param x spawn coordinate x
     * @param y spawn coordinate y
     */
    public void setSpawnPosition(int x, int y) {
        this.monster_x = x;
        this.monster_y = y;
    }

    /**
     * Resume gremlin move speed
     */
    public void resume_speed() {
        current_speed = this.speed;
    }

    /**
     * Double gremlin move speed
     */
    public void FF_speed() {
        resume_speed();
        current_speed += current_speed;
    }

    /**
     * Detect whether gremlin is hitted by bullet
     * @param damage_take how many damage token
     */
    public void hitted(double damage_take){
        double current_hp = Math.abs(this.hp-damage_take*this.armour);
        double ratio = current_hp/this.hp*20;
        this.hp=current_hp;
        // double sub_width = this.hp / this.hp * life_bar_width;
        // life_bar_width-= (int) sub_width;
        life_bar_width-=ratio;
    }

    

    /**
     * Gremlin moving logic
     */
    public void tick() {
        // TODO 这里写死亡状态检测
        if (this.monster_x == wizard_house_y * CELLSIZE + 6
                && this.monster_y == wizard_house_x * CELLSIZE + TOPBAR + 6 ) {
            this.isAlive = false;
            this.isReachTheWizardhouse = true;
            this.animation_start = true;
        } else if (life_bar_width<=0) {
            this.isAlive = false;
            this.animation_start = true;
        } else{
            // 判断上下左右走，用当前Point.get(x,y)于下一个Point.get(x,y)判断
            // 判断下一个Point的x还是y相等，然后判断x y的大小
            for (int x = 0; x < startPoints.size(); x++) {
                MyPoint spawn = startPoints.get(x);
                if (!indexMap.containsKey(spawn)) {
                    indexMap.put(spawn, 1);
                }
            }
            int shortest_index = indexMap.get(spawn_p);
            next_point = shortest_Paths.get(spawn_p).get(shortest_index);

            // Move Right
            if (this.monster_x < next_point.getY() * CELLSIZE + 6) {
                this.monster_x += current_speed;
                if (this.monster_x >= next_point.getY() * CELLSIZE + 6) {
                    this.monster_x = next_point.getY() * CELLSIZE + 6;
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }

            // Move Left
            if (this.monster_x > next_point.getY() * CELLSIZE + 6) {
                this.monster_x -= current_speed;
                if (this.monster_x <= next_point.getY() * CELLSIZE + 6) {
                    this.monster_x = next_point.getY() * CELLSIZE + 6;
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }

            // Move Down
            if (this.monster_y < next_point.getX() * CELLSIZE + TOPBAR + 6) {
                this.monster_y += current_speed;
                if (this.monster_y >= next_point.getX() * CELLSIZE + TOPBAR + 6) {
                    this.monster_y = next_point.getX() * CELLSIZE + TOPBAR + 6;
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }

            // Move Up
            if (this.monster_y > next_point.getX() * CELLSIZE + TOPBAR + 6) {
                this.monster_y -= current_speed;
                if (this.monster_y <= next_point.getX() * CELLSIZE + TOPBAR + 6) {
                    this.monster_y = next_point.getX() * CELLSIZE + TOPBAR + 6;
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }

            // Double the monster speed
            if (FF_press_condition) {
                /*
                 * 人物运动加速
                 * wave倒计时加速
                 * mana条加速
                 */
                for (Gremlin g : gremlinList) {
                    g.FF_speed();
                }

            } else {
                for (Gremlin g : gremlinList) {
                    g.resume_speed();
                }
            }
        }
    }

    public int get_sprite_width(){
        return this.sprite.width;
    }

    public int get_sprite_height(){
        return this.sprite.height;
    }

    public void draw(App app) {

        /*
         * 要想让monster在path上走，
         * 我想需要在处理map的时候，存储了'X'的坐标，
         * 然后monster移动的时候就跟随存储的‘X’的x和y
         */
        if (isAlive) {
            // Gremlin red life bar
            app.noFill();
            app.stroke(255, 0, 0); // 设置边框颜色为红色
            app.strokeWeight(2);
            app.rect(this.monster_x, this.monster_y - 10, 20, 3);

            // Gremlin green life bar
            app.noFill();
            app.stroke(10, 240, 60); // 设置边框颜色为绿色
            app.strokeWeight(2);
            app.rect(this.monster_x, this.monster_y - 10, life_bar_width, 3);

            app.image(this.sprite, this.monster_x, this.monster_y);

            
        } else {

            drawGremlinDeath(monster_x, monster_y, app);
           
        }

    }
}
