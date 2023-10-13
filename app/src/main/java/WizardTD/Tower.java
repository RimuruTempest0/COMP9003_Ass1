package WizardTD;
import processing.core.PApplet;
import processing.core.PImage;

public class Tower extends App{

    private int tower_x;
    private int tower_y;


    private double initial_tower_range;
    private double initial_tower_firing_speed;
    private int initial_tower_damage;
    private int tower_damage;

    private int range_level;
    private int speed_level;
    private int damage_level;
    private int upgrade_count;

    private int tower_cost;
    private PImage sprite;

    
    public Tower(double initial_tower_range, double initial_tower_firing_speed,
            int initial_tower_damage, int initial_tower_cost) {
        
        this.initial_tower_range = initial_tower_range;
        this.initial_tower_firing_speed = initial_tower_firing_speed;
        this.tower_damage = initial_tower_damage;
        this.range_level=0;
        this.speed_level=0;
        this.damage_level=0;
        this.upgrade_count=0;
        this.tower_cost = initial_tower_cost;
        
    }

    public void setTower(int tower_x, int tower_y, PImage sprite){
        this.tower_x = tower_x;
        this.tower_y = tower_y;
        this.sprite = sprite;
    }

    public void upgrade_cost(){
        this.upgrade_count++;
        this.tower_cost += upgrade_count*upgrade_count;
    }

    public void upgrade_range(){
        this.initial_tower_range += App.CELLSIZE;
        this.range_level++;
        upgrade_cost();
    }

    public void upgrade_speed(){
        this.initial_tower_firing_speed += 0.5;
        this.speed_level++;
        upgrade_cost();
    }

    public void upgrade_damage(){
        this.tower_damage += (initial_tower_damage/2);
        this.damage_level++;
        upgrade_cost();
    }


    public void draw_O(PApplet app, int level){
        app.stroke(255, 0, 0);
        app.strokeWeight(1);
        app.textSize(12);
        for(int i=0; i<level; i++){
            app.text("O",this.tower_x+(i*2),this.tower_y);
        }
    }

    public void draw_X(PApplet app, int level){
        app.stroke(255, 0, 0);
        app.strokeWeight(1);
        app.textSize(12);
        for(int i=0; i<level; i++){
            app.text("X",this.tower_x+(i*2),this.tower_y+this.sprite.height);
        }
    }

    public void draw_square(PApplet app, int level){
        app.noFill(); // 不填充颜色
        app.stroke(10, 161, 243); // 设置边框颜色为蓝色
        app.strokeWeight(level);
        app.rect(this.tower_x+2, this.tower_y+2, 28, 28);
    }

    public void draw_range(PApplet app){
        app.noFill(); // 不填充颜色
        app.stroke(235, 240, 10); // 设置边框颜色为蓝色
        app.strokeWeight(1);
        app.ellipse((float)this.tower_x, (float)this.tower_y, (float)this.initial_tower_range, (float)this.initial_tower_range);
    }

    public boolean mouse_on_tower(PApplet app) {
        int mouseX = app.mouseX;
        int mouseY = app.mouseY;
        if (mouseX <= this.tower_x + this.sprite.width &&
                mouseX >= this.tower_x &&
                mouseY >= this.tower_y &&
                mouseY <= this.tower_y + this.sprite.height) {
            return true;

        }
        return false;
    }

    public void draw(PApplet app){
        if(mouse_on_tower(app))
        {
            draw_range(app);
        }
        
        app.image(this.sprite, this.tower_x,this.tower_y);
    }
}
