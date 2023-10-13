package WizardTD;
import processing.core.PImage;

public class Gremlin extends Monster {
    
    public boolean animation_start;
    public int current_frame = 0;
    public int number_frames = 5;

    public Gremlin(int monster_x, int monster_y, double speed, boolean isAlive, boolean animation_start, PImage sprite) {
        super(monster_x, monster_y, speed, isAlive, sprite);
        this.animation_start = animation_start;
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
    int shortest_index = 1;
    public void tick() {
        // System.out.println(wizard_house_x +" " + wizard_house_y); (8, 9)
        // TODO 这里写死亡状态检测
        if (this.monster_x == wizard_house_y*CELLSIZE + 6 && this.monster_y == wizard_house_x*CELLSIZE+TOPBAR+6) {
            this.isAlive = false;
            this.animation_start = true;
        } else {
            // 判断上下左右走，用当前Point.get(x,y)于下一个Point.get(x,y)判断
            // 判断下一个Point的x还是y相等，然后判断x y的大小
            
            // System.out.println(current_point.getX()+" "+current_point.getY());
            next_point = shortest_Paths.get(0).get(shortest_index);


            // Move Right
            if(this.monster_x < next_point.getY()*CELLSIZE + 6)
            {
                this.monster_x += monster_speed;
                if(this.monster_x == next_point.getY()*CELLSIZE + 6){
                    shortest_index++;
                }
            }

            // Move Left
            if(this.monster_x > next_point.getY()*CELLSIZE + 6)
            {
                this.monster_x -= monster_speed;
                if(this.monster_x == next_point.getY()*CELLSIZE + 6){
                    shortest_index++;
                }
            }


            // Move Down
            if(this.monster_y < next_point.getX()*CELLSIZE+ TOPBAR + 6)
            {
                this.monster_y += monster_speed;
                if(this.monster_y == next_point.getX()*CELLSIZE+ TOPBAR+6){
                    shortest_index++;
                }
            }

            // Move Up
            if(this.monster_y > next_point.getX()*CELLSIZE+ TOPBAR + 6)
            {
                this.monster_y -= monster_speed;
                if(this.monster_y == next_point.getX()*CELLSIZE+ TOPBAR+6){
                    shortest_index++;
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
            // app.Fill(); // 不填充颜色
            // app.stroke(255, 0, 0); // 设置边框颜色为红色
            // app.strokeWeight(2);
            // app.rect(i * 32, j * 32 + 6, 32, 32);
            app.image(this.sprite, this.monster_x, this.monster_y);
        } else {      
            drawGremlinDeath(monster_x, monster_y, app);
        }

    }
}
