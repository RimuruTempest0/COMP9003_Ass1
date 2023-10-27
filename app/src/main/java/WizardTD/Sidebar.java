package WizardTD;

import java.util.ArrayList;
import java.util.HashMap;

public class Sidebar extends App {
    public Sidebar() {

    }

    private static final int sideBar_button_x = 650;
    private static int sideBar_button_y = 50;
    private static int rangeCost = 10;
    private static int speedCost = 10;
    private static int damageCost = 10;
    private static int manaPoolCost = mana_pool_spell_initial_cost;
    public static Button ffButton = new Button(0,"FF", "2x Speed", 650, 50);
    public static Button pauseButton = new Button(0,"P", "PAUSE", 650, 100);
    public static Button buildTowerButton = new Button(initial_tower_cost,"T", "Build\nTower", 650, 150);
    public static Button upgradeRangeButton = new Button(rangeCost,"U1", "Upgrade\nRange", sideBar_button_x, sideBar_button_y + 150);
    public static Button upgradeSpeedButton = new Button(speedCost,"U2", "Upgrade\nSpeed", sideBar_button_x, sideBar_button_y + 200);
    public static Button upgradeDamageButton = new Button(damageCost,"U3", "Upgrade\nDamage", sideBar_button_x, sideBar_button_y + 250);
    public static Button manaPoolButton = new Button(manaPoolCost,"M", "Mana Pool\nCost: "+manaPoolCost, sideBar_button_x, sideBar_button_y + 300);


    private ArrayList<Tower> tower_list = new ArrayList<>();
    private HashMap<Tower, MyPoint> tower_to_point = new HashMap<>();
    public static HashMap<MyPoint, Tower> point_to_tower = new HashMap<>();
    private Tower tower;
    
    private ArrayList<String> costs_board = new ArrayList<String>();
    public static int total_cost = 0;
    public void draw(App app) {
        // 先画一个大的矩形，要盖住右侧出生的小兵, Right bar
        int sideBar_Cover_Height = CELLSIZE * BOARD_WIDTH;
        app.stroke(background_R, background_G, background_B);
        app.fill(background_R, background_G, background_B);
        app.rect(sideBar_Cover_Height, TOPBAR, SIDEBAR, sideBar_Cover_Height);

        /**
         * Draw all buttons
         */
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
            ffButton.drawYellow(app);
        }

        if (Pause_press_condition) 
        {
            pauseButton.drawYellow(app);
        }

        /**
         * Draw or upgrade towers corresponding to button conditions
         */
        
        //------------------------------Tower Draw START---------------------------------
        if (Build_tower_condition) {
            buildTowerButton.drawYellow(app);
            tower = new Tower(initial_tower_range, initial_tower_firing_speed, initial_tower_damage,
                    initial_tower_cost);
            tower.setTowerImage(tower_0_image);
            tower.setTowerWithMouse(app);
            if (tower.valid_place_tower(app)) {
                tower.setTowerXY((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
                if (Place_tower_on_map ) {
                    MyPoint tower_Point = new MyPoint((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
                    if((current_mana_value >= initial_tower_cost))
                    {
                        tower_to_point.put(tower, tower_Point);
                        point_to_tower.put(tower_Point, tower);
                        tower_list.add(tower);
                        tower.set_bullet();
                        tower.mana_subtractor(initial_tower_cost);
                        Place_tower_on_map = !Place_tower_on_map;
                        Build_tower_condition = !Build_tower_condition;
                    } else {
                        System.out.println("Insufficient Mana !!!");
                        Place_tower_on_map = !Place_tower_on_map;
                    }
                }
            }
            tower.draw(app);
        }

        if (Upgrade_range_condition) {
            upgradeRangeButton.drawYellow(app);
            MyPoint tower_Point = new MyPoint((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
            if(point_to_tower.containsKey(tower_Point)) {
                Tower tower = point_to_tower.get(tower_Point);
                // Integer cost = (rangeCost);
                Integer cost = tower.getRangeCost();
                if(!costs_board.contains("Range cost: "+cost.toString())){
                    total_cost+=cost;
                    costs_board.add("Range cost: "+cost.toString());
                }
                tower.drawUpgradePanel(650, 450, 100, 150, costs_board, app);

                
                if(Deploy_range_upgrade) {
                    if(tower.getRangeLevel()<3){
                        tower.upgrade_range();
                        if(rangeCost<40) {rangeCost += 10;}
                        tower.mana_subtractor(cost);
                        costs_board.remove("Range cost: "+cost.toString());
                        total_cost=0;
                    }else{
                        System.out.println("Tower Range Level Max !!!");
                    }
                    Deploy_range_upgrade = !Deploy_range_upgrade;
                    Upgrade_range_condition = !Upgrade_range_condition;
                }
            }
        }

        if (Upgrade_speed_condition) {
            upgradeSpeedButton.drawYellow(app);
            MyPoint tower_Point = new MyPoint((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
            if(point_to_tower.containsKey(tower_Point)) {
                Tower tower = point_to_tower.get(tower_Point);
                Integer cost = (tower.getSpeedCost());
                if(!costs_board.contains("Speed cost: "+cost.toString())){
                    total_cost+=cost;
                    costs_board.add("Speed cost: "+cost.toString());
                }
                tower.drawUpgradePanel(650, 450, 100, 150, costs_board, app);
                
                
                
                if(Deploy_speed_upgrade) {
                    if(tower.getSpeedLevel()<3){
                        tower.upgrade_speed();
                        if(speedCost<30) {speedCost += 10;}
                        tower.mana_subtractor(cost);
                        costs_board.remove("Speed cost: "+cost.toString());
                        total_cost = 0;
                    }else{
                        System.out.println("Tower Speed Level Max !!!");
                    }
                    Deploy_speed_upgrade = !Deploy_speed_upgrade;
                    Upgrade_speed_condition = !Upgrade_speed_condition;
                }
            }
        }

        if (Upgrade_damage_condition) {
            upgradeDamageButton.drawYellow(app);
            MyPoint tower_Point = new MyPoint((app.mouseX / 32) * 32, ((app.mouseY / 32) * 32) - 32 + TOPBAR);
            if(point_to_tower.containsKey(tower_Point)) {
                Tower tower = point_to_tower.get(tower_Point);
                Integer cost = (tower.getDamageCost());
                if(!costs_board.contains("Damage cost: "+cost.toString())){
                    total_cost+=cost;
                    costs_board.add("Damage cost: "+cost.toString());
                }
                tower.drawUpgradePanel(650, 450, 100, 150, costs_board, app);
                
                if(Deploy_damage_upgrade) {
                    if(tower.getDamageLevel()<3){
                        tower.upgrade_damage();
                        if(damageCost<30) {damageCost += 10;}
                        tower.mana_subtractor(cost);
                        costs_board.remove("Damage cost: "+cost.toString());
                        total_cost = 0;
                    }else{
                        System.out.println("Tower Damage Level Max !!!");
                    }
                    Deploy_damage_upgrade = !Deploy_damage_upgrade;
                    Upgrade_damage_condition = !Upgrade_damage_condition;
                }
            }
        }

        for (Tower tower : tower_list) {
            if (tower_to_point.size() != 0 && point_to_tower.size() != 0) {
                int x = tower_to_point.get(tower).getX();
                int y = tower_to_point.get(tower).getY();
                // MyPoint curTowerTpoint = new MyPoint(x, y);
                
                
                if(tower.getDamageLevel() >= 2 && tower.getRangeLevel() >= 2 && tower.getSpeedLevel() >= 2)
                {
                    tower.setTowerImage(tower_2_image);
                    tower.setTowerXY(x, y);
                    tower.fire_bullet(app);
                    tower.draw(app);
                    
                    tower.draw_square(app, tower.getSpeedLevel()-2);
                    tower.draw_O(app, tower.getRangeLevel()-2);
                    tower.draw_X(app, tower.getDamageLevel()-2);
                } else if(tower.getDamageLevel() >= 1 && tower.getRangeLevel() >= 1 && tower.getSpeedLevel() >= 1) {
                    tower.setTowerImage(tower_1_image);
                    tower.setTowerXY(x, y);
                    tower.fire_bullet(app);
                    tower.draw(app);
                    
                    tower.draw_square(app, tower.getSpeedLevel()-1);
                    tower.draw_O(app, tower.getRangeLevel()-1);
                    tower.draw_X(app, tower.getDamageLevel()-1);
                } else {
                    tower.setTowerXY(x, y);
                    tower.fire_bullet(app);
                    tower.draw(app);
                    
                    tower.draw_square(app, tower.getSpeedLevel());
                    tower.draw_O(app, tower.getRangeLevel());
                    tower.draw_X(app, tower.getDamageLevel());
                }
                
                
            }
        }
        //------------------------------Tower Draw END---------------------------------

        if(Upgrade_mana_pool_condition) {
             
            if(current_mana_value-manaPoolCost >=0) {
                current_mana_value -= manaPoolCost;
                initial_mana_cap = (int) (initial_mana_cap*mana_pool_spell_cap_multiplier);
                manaPoolCost += mana_pool_spell_cost_increase_per_use;
                manaPoolButton.set_button_cost_and_content(manaPoolCost, "Mana Pool\nCost: "+manaPoolCost);
                mana_gained_on_kill += mana_gained_on_kill*mana_pool_spell_mana_gained_multiplier;
                Upgrade_mana_pool_condition = !Upgrade_mana_pool_condition;
            } else {
                System.out.println("Insufficient Mana !!!");
                Upgrade_mana_pool_condition = !Upgrade_mana_pool_condition;
            }
        }
        
        

        if (buildTowerButton.mouse_on_button(app)) {
            buildTowerButton.drawCost(app);
        }

        if (manaPoolButton.mouse_on_button(app)) {
            manaPoolButton.drawCost(app);
        }

    }
}
