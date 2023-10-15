package WizardTD;


public class Sidebar extends App {
    public Sidebar() {

    }

    private final int sideBar_button_x = 650;
    private int sideBar_button_y = 50;
    
    
    public static Button ffButton = new Button(0,"FF", "2x Speed", 650, 50);
    public static Button pauseButton = new Button(0,"P", "PAUSE", 650, 100);
    public static Button buildTowerButton = new Button(initial_tower_cost,"T", "Build\nTower", 650, 150);
    private Button upgradeRangeButton = new Button(300,"U1", "Upgrade\nRange", sideBar_button_x, sideBar_button_y + 150);
    private Button upgradeSpeedButton = new Button(300,"U2", "Upgrade\nSpeed", sideBar_button_x, sideBar_button_y + 200);
    private Button upgradeDamageButton = new Button(300,"U3", "Upgrade\nDamage", sideBar_button_x, sideBar_button_y + 250);
    private Button manaPoolButton = new Button(300,"M", "Mana Pool\nCost: ", sideBar_button_x, sideBar_button_y + 300);

    
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

        if(Build_tower_condition)
        {
            buildTowerButton.drawYellow(app);
        }

        

        if (current_mana_value > 208) {
            
            upgradeRangeButton.drawYellow(app);
            upgradeSpeedButton.drawYellow(app);
            upgradeDamageButton.drawYellow(app);
        }
        

        if (buildTowerButton.mouse_on_button(app)) {
            buildTowerButton.drawCost(app);
        }

        if (upgradeRangeButton.mouse_on_button(app)) {
            upgradeRangeButton.drawCost(app);
        }

        if (upgradeSpeedButton.mouse_on_button(app)) {
            upgradeSpeedButton.drawCost(app);
        }

        if (upgradeDamageButton.mouse_on_button(app)) {
            upgradeDamageButton.drawCost(app);
        }

        if (manaPoolButton.mouse_on_button(app)) {
            manaPoolButton.drawCost(app);
        }

        

    }
}
