package WizardTD;

import java.util.HashMap;

import processing.core.PImage;

public class Gremlin extends Monster {
    
    public boolean animation_start;
    public int current_frame = 0;
    public int number_frames = 5;
    MyPoint spawn_p;

    public Gremlin(int monster_x, int monster_y, double speed, boolean isAlive, boolean animation_start, PImage sprite) {
        super(monster_x, monster_y, speed, isAlive, sprite);
        this.animation_start = animation_start;
        spawn_p = new MyPoint(((monster_y-6-TOPBAR)/CELLSIZE),(monster_x-6)/CELLSIZE);
    }

    

    public void drawGremlinDeath(int x, int y, App app) {
        double speed = (double) number_frames / (double) FPS;
        if (current_frame == gremlin_dead.length - 1) {
            animation_start = false;
        }

        if (animation_start) {
            app.image(gremlin_dead[current_frame], x, y);
            if (app.frameCount % (FPS * speed) == 0) {
                current_frame = (current_frame + 1) % number_frames;
            }
        }
    }

    public void setSpawnPosition(int x, int y)
    {
        this.monster_x = x;
        this.monster_y = y;
    }
    
    MyPoint next_point = new MyPoint();
    // 每个Monster存储对应的路线迭代index
    HashMap<MyPoint, Integer> indexMap = new HashMap<>();
    public void tick() {
        // TODO 这里写死亡状态检测
        if (this.monster_x == wizard_house_y*CELLSIZE + 6 && this.monster_y == wizard_house_x*CELLSIZE+TOPBAR+6) {
            this.isAlive = false;
            this.animation_start = true;
        } else {
            // 判断上下左右走，用当前Point.get(x,y)于下一个Point.get(x,y)判断
            // 判断下一个Point的x还是y相等，然后判断x y的大小
            for(int x=0; x<startPoints.size(); x++){
                MyPoint spawn = startPoints.get(x);
                if(!indexMap.containsKey(spawn)){
                    indexMap.put(spawn, 1);
                }
            }
            int shortest_index = indexMap.get(spawn_p);
            next_point = shortest_Paths.get(spawn_p).get(shortest_index);
            
            // Move Right
            if(this.monster_x < next_point.getY()*CELLSIZE + 6)
            {
                this.monster_x += this.speed;
                if(this.monster_x == next_point.getY()*CELLSIZE + 6){
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }

            // Move Left
            if(this.monster_x > next_point.getY()*CELLSIZE + 6)
            {
                this.monster_x -= this.speed;
                if(this.monster_x == next_point.getY()*CELLSIZE + 6){
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }


            // Move Down
            if(this.monster_y < next_point.getX()*CELLSIZE+ TOPBAR + 6)
            {
                this.monster_y += this.speed;
                if(this.monster_y == next_point.getX()*CELLSIZE+ TOPBAR+6){
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }

            // Move Up
            if(this.monster_y > next_point.getX()*CELLSIZE+ TOPBAR + 6)
            {
                this.monster_y -= this.speed;
                if(this.monster_y == next_point.getX()*CELLSIZE+ TOPBAR+6){
                    shortest_index++;
                    indexMap.put(spawn_p, shortest_index);
                }
            }
        }
    }

    public void draw(App app)
     {

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
            app.rect(this.monster_x, this.monster_y-10, 20, 3);

            // Gremlin green life bar
            app.noFill();
            app.stroke(10, 240, 60); // 设置边框颜色为绿色
            app.strokeWeight(2);
            app.rect(this.monster_x, this.monster_y-10, 20, 3);

            app.image(this.sprite, this.monster_x, this.monster_y);
        } else {      
            drawGremlinDeath(monster_x, monster_y, app);
        }

    }
}
