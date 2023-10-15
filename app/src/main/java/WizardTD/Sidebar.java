package WizardTD;

import java.util.ArrayList;
import java.util.HashMap;

public class Sidebar extends App {
    public Sidebar() {

    }

    private static final int sideBar_button_x = 650;
    private static int sideBar_button_y = 50;
    
    
    public static Button ffButton = new Button(0,"FF", "2x Speed", 650, 50);
    public static Button pauseButton = new Button(0,"P", "PAUSE", 650, 100);
    public static Button buildTowerButton = new Button(initial_tower_cost,"T", "Build\nTower", 650, 150);
    public static Button upgradeRangeButton = new Button(300,"U1", "Upgrade\nRange", sideBar_button_x, sideBar_button_y + 150);
    private Button upgradeSpeedButton = new Button(300,"U2", "Upgrade\nSpeed", sideBar_button_x, sideBar_button_y + 200);
    private Button upgradeDamageButton = new Button(300,"U3", "Upgrade\nDamage", sideBar_button_x, sideBar_button_y + 250);
    private Button manaPoolButton = new Button(300,"M", "Mana Pool\nCost: ", sideBar_button_x, sideBar_button_y + 300);


    private ArrayList<Tower> tower_list = new ArrayList<>();
    private HashMap<Tower, MyPoint> tower_to_point = new HashMap<>();
    public static HashMap<MyPoint, Tower> point_to_tower = new HashMap<>();
    private Tower tower;
    
    public void draw(App app) {
        // 先画一个大的矩形，要盖住右侧出生的小兵, Right bar
        int sideBar_Cover_Height = CELLSIZE * BOARD_WIDTH;
        app.stroke(background_R, background_G, background_B);
        app.fill(background_R, background_G, background_B);
        app.rect(sideBar_Cover_Height, TOPBAR, SIDEBAR, sideBar_Cover_Height);

        ffButton.draw(app);
        pauseButton.draw(app);
        buildTowerButton.draw(app);
        upgradeRangeButton.draw(app);
        upgradeSpeedButton.draw(app);
        upgradeDamageButton.draw(app);
        manaPoolButton.draw(app);

        // 当鼠标在按钮上，按下mousePressed后，变为黄色
        if (FF_press_condition) 
        {
            /*
             * 人物运动加速
             * wave倒计时加速
             * mana条加速
             */
            ffButton.drawYellow(app);
        }

        if (Pause_press_condition) 
        {
            pauseButton.drawYellow(app);
            
        }

            //------------------------------Tower Draw START---------------------------------
        if (Build_tower_condition) {
            buildTowerButton.drawYellow(app);
            tower = new Tower(initial_tower_range, initial_tower_firing_speed, initial_tower_damage,
                    initial_tower_cost);
            tower.setTowerImage(tower_0_image);
            tower.setTowerWithMouse(app);
            if (tower.valid_place_tower(app)) {
                tower.setTowerXY((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
                if (Place_tower_on_map && (initial_mana > 0)) {
                    MyPoint tower_Point = new MyPoint((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
                    // System.out.println(tower_Point.toString());
                    tower_to_point.put(tower, tower_Point);
                    point_to_tower.put(tower_Point, tower);
                    tower_list.add(tower);

                    initial_mana-=initial_tower_cost;
                    Place_tower_on_map = !Place_tower_on_map;
                    Build_tower_condition = !Build_tower_condition;
                }
            }
            tower.draw(app);
        }

        if (Upgrade_range_condition) {
            upgradeRangeButton.drawYellow(app);
            MyPoint tower_Point = new MyPoint((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
            if(point_to_tower.containsKey(tower_Point)) {
                Tower tower = point_to_tower.get(tower_Point);
                if(Deploy_upgrade) {
                    if(tower.getRangeLevel()<3){
                        tower.upgrade_range();
                        initial_mana -= tower.getRangeLevel()*20;
                    }else{
                        System.out.println("Tow Range Level Max !!!");
                    }
                    Deploy_upgrade = !Deploy_upgrade;
                    Upgrade_range_condition = !Upgrade_range_condition;
                }
            }
        }

        for (Tower tower : tower_list) {
            if (tower_to_point.size() != 0 && point_to_tower.size() != 0) {
                int x = tower_to_point.get(tower).getX();
                int y = tower_to_point.get(tower).getY();
                // MyPoint curTowerTpoint = new MyPoint(x, y);

                tower.setTowerXY(x, y);
                tower.draw(app);
                tower.draw_O(app, tower.getRangeLevel());
            }
        }
        //------------------------------Tower Draw END---------------------------------


        

        if (current_mana_value > 208) {
            
            
            upgradeSpeedButton.drawYellow(app);
            upgradeDamageButton.drawYellow(app);
        }
        

        if (buildTowerButton.mouse_on_button(app)) {
            buildTowerButton.drawCost(app);
        }

        // if (upgradeRangeButton.mouse_on_button(app)) {
        //     upgradeRangeButton.drawCost(app);
        // }

        // if (upgradeSpeedButton.mouse_on_button(app)) {
        //     upgradeSpeedButton.drawCost(app);
        // }

        // if (upgradeDamageButton.mouse_on_button(app)) {
        //     upgradeDamageButton.drawCost(app);
        // }

        if (manaPoolButton.mouse_on_button(app)) {
            manaPoolButton.drawCost(app);
        }

        

    }
}
