package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public int background_R = 132;
    public int background_G = 115;
    public int background_B = 74;

    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();

    public PImage[][] mapTile = new PImage[20][20];;
    public static char[][] Map_Grid;

    private static int wizard_house_display_x;
    private static int wizard_house_display_y;
    public static int wizard_house_x;
    public static int wizard_house_y;
    private static PImage wizardImage;

    private Topbar topbar;
    private Sidebar sidebar;

    public static HashMap<MyPoint, ArrayList<MyPoint>> shortest_Paths = new HashMap<>();
    public static ArrayList<MyPoint> startPoints = new ArrayList<>();


    /**
     * Read the layout object from json file and store data in a 2D char array
     * @param levelAddress levelx.txt file address
     * @return A 20x20 2D char array which store the level characters
     */
    public char[][] readLevel(String levelAddress) {
        char[][] data = new char[20][20];

        // Fill the array with grass
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                data[i][j] = ' ';
            }
        }
        try {
            File file = new File(levelAddress);
            Scanner sc = new Scanner(file);
            for (int x = 0; x < 20; x++) {
                String row = sc.nextLine();
                for (int y = 0; y < row.length(); y++) {
                    data[x][y] = row.charAt(y);
                }
            }
            sc.close();
            return data;

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }

    }

    /**
     * Method to choose right direction of path tile 
     * This method is called in loadBackGround(char[][] backGroundArray) method
     * @param x current tile x
     * @param y current tile y
     * @param backGroundArray 2D map grid char array
     * @return corresponding direction tile code
     */
    public int choosePath(int x, int y, char[][] backGroundArray) {
        int upX = x - 1;
        int downX = x + 1;
        int leftY = y - 1;
        int rightY = y + 1;

        char up = ' ';
        char down = ' ';
        char left = ' ';
        char right = ' ';

        // The following try catch detects the boundary
        try {
            up = backGroundArray[upX][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("上边界出界: " + upX + " " + y);
            startPoints.add(new MyPoint(upX + 1, y));
            up = 'X';
        }
        try {
            down = backGroundArray[downX][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("下边界出界： " + downX + " " + y);
            startPoints.add(new MyPoint(downX, y));
            down = 'X';

        }
        try {
            left = backGroundArray[x][leftY];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("左边界出界: " + x + " " + leftY);
            startPoints.add(new MyPoint(x, leftY + 1));
            left = 'X';
        }
        try {
            right = backGroundArray[x][rightY];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("右边界出界： " + x + " " + rightY);
            startPoints.add(new MyPoint(x, rightY));
            right = 'X';
        }


        // 计算上下左右总值
        int fourValue = (int) up + (int) down + (int) left + (int) right;

        // 上草下路用右拐
        if (down == 'X' && left == 'X' && up != 'X' && right != 'X') {
            return 1;
        }
        if (up == 'X' && left == 'X' && down != 'X' && right != 'X') {
            return 11;
        }
        if (up == 'X' && right == 'X' && down != 'X' && left != 'X') {
            return 12;
        }
        if (down == 'X' && right == 'X' && up != 'X' && left != 'X') {
            return 13;
        }

        // T字路：单面为Shrub或者Grass
        int T_road1 = 296;
        int T_road2 = 347;
        if (fourValue == T_road1 || fourValue == T_road2) {
            if (up != 'X') {
                return 2;
            }
            if (right != 'X') {
                return 21;
            }
            if (down != 'X') {
                return 22;
            }
            if (left != 'X') {
                return 23;
            }

        }
        // 四周是草用十字路
        int cross_road = 352;
        if (fourValue == cross_road) {
            return 3;
        }

        // 上下为路，左右不为路的时候，转path0 90度
        if ((up == 'X' || down == 'X') && left != 'X' && right != 'X') {
            return 101;
        }
        return 0;
    }

    /**
     * Replace the corresponding character with PImage into a PIamge map 2D array
     * @param backGroundArray the 2D array store the levelx.txt data
     */
    public void loadBackGround(char[][] backGroundArray) {
        PImage grass = loadImage("src/main/resources/WizardTD/grass.png");
        PImage shrub = loadImage("src/main/resources/WizardTD/shrub.png");

        PImage path0 = loadImage("src/main/resources/WizardTD/path0.png");
        PImage path0_rotate = rotateImageByDegrees(path0, 90);

        PImage path1 = loadImage("src/main/resources/WizardTD/path1.png");
        PImage path1_rotate_1 = rotateImageByDegrees(path1, 90);
        PImage path1_rotate_2 = rotateImageByDegrees(path1, 180);
        PImage path1_rotate_3 = rotateImageByDegrees(path1, 270);

        PImage path2 = loadImage("src/main/resources/WizardTD/path2.png");
        PImage path2_rotate_1 = rotateImageByDegrees(path2, 90);
        PImage path2_rotate_2 = rotateImageByDegrees(path2, 180);
        PImage path2_rotate_3 = rotateImageByDegrees(path2, 270);

        PImage path3 = loadImage("src/main/resources/WizardTD/path3.png");
        wizardImage = loadImage("src/main/resources/WizardTD/wizard_house.png");

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                switch (backGroundArray[i][j]) {
                    case ' ':
                        mapTile[i][j] = grass;
                        break;
                    case 'S':
                        mapTile[i][j] = shrub;
                        break;
                    case 'X':
                        int pathNum = choosePath(i, j, backGroundArray);
                        switch (pathNum) {
                            case 0:
                                mapTile[i][j] = path0;
                                break;
                            case 1:
                                mapTile[i][j] = path1;
                                break;
                            case 11:
                                mapTile[i][j] = path1_rotate_1;
                                break;
                            case 12:
                                mapTile[i][j] = path1_rotate_2;
                                break;
                            case 13:
                                mapTile[i][j] = path1_rotate_3;
                                break;
                            case 2:
                                mapTile[i][j] = path2;
                                break;
                            case 21:
                                mapTile[i][j] = path2_rotate_1;
                                break;
                            case 22:
                                mapTile[i][j] = path2_rotate_2;
                                break;
                            case 23:
                                mapTile[i][j] = path2_rotate_3;
                                break;
                            case 3:
                                mapTile[i][j] = path3;
                                break;
                            case 101:
                                mapTile[i][j] = path0_rotate;
                                break;
                        }
                        break;
                    case 'W':
                        // Draw the grass at the 'W' place and store the position
                        mapTile[i][j] = loadImage("src/main/resources/WizardTD/grass.png");
                        wizard_house_x = i;
                        wizard_house_y = j;
                        // Adjust the wizard house entrence with face to the path tile
                        if (backGroundArray[i - 1][j] == 'X') {
                            wizardImage = rotateImageByDegrees(wizardImage, 90);
                            wizard_house_display_x = i * 32 + 40;
                            wizard_house_display_y = j * 32 - 8;
                        }
                        if (backGroundArray[i][j + 1] == 'X') {
                            wizardImage = rotateImageByDegrees(wizardImage, 180);
                            wizard_house_display_x = i * 32 + 40 - 8;
                            wizard_house_display_y = j * 32;
                        }
                        if (backGroundArray[i + 1][j] == 'X') {
                            wizardImage = rotateImageByDegrees(wizardImage, 270);
                            wizard_house_display_x = i * 32 + 40;
                            wizard_house_display_y = j * 32 - 8;
                        }
                        break;
                }
            }
        }
    }

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the
     * player, enemies and map elements.
     */

    public static int initial_mana;
    public static int initial_mana_cap;
    public static int initial_mana_gained_per_second;
    public static int current_mana_value; // 这个变量在Topbar里赋值

    public static double initial_tower_range;
    public static double initial_tower_firing_speed;
    public static int initial_tower_damage;
    public static int initial_tower_cost;

    public static JSONArray waves;

    private PImage gremlin_image;
    protected static PImage tower_0_image;
    protected static PImage tower_1_image;
    protected static PImage tower_2_image;
    protected static PImage bullet_image;
    private HashMap<Integer, PImage> tower_image_map = new HashMap<>();

    public static PImage[] gremlin_dead = new PImage[5];

    /**
     * Back trace to find the shortest path
     * @param pair father nodes and children nodes
     * @param start path start point
     * @param destination path end point
     * @return a list of points
     */
    private ArrayList<MyPoint> findShortestPath(HashMap<MyPoint, MyPoint> pair, MyPoint start, MyPoint destination) {
        ArrayList<MyPoint> path = new ArrayList<>();
        MyPoint current = destination;
        while (!current.equals(start)) {
            path.add(0, current);
            current = pair.get(current);
        }
        path.add(0, start);
        return path;
    }

    /**
     * The BFS Algorithm
     * @param start path start point
     * @param destination path end point
     * @return a list of points with visited
     */
    // ------------------------------------BFS
    // start------------------------------------
    private ArrayList<MyPoint> BreadthFirstSearch(MyPoint start, MyPoint destination) {
        
        LinkedList<MyPoint> queue = new LinkedList<>();
        ArrayList<MyPoint> visited = new ArrayList<>();

        boolean route_found = false;

        // (children, parent)
        HashMap<MyPoint, MyPoint> pair = new HashMap<>();

        queue.addLast(start);
        while (!queue.isEmpty()) {
            MyPoint current = queue.removeFirst();
            visited.add(current);

            if (current.equals(destination)) {
                route_found = true;
                break;
            }

            int x = current.getX();
            int y = current.getY();

            try {
                MyPoint up = new MyPoint(x - 1, y);
                if (Map_Grid[x - 1][y] >= 87 && !visited.contains(up) && !queue.contains(up)) {
                    queue.addLast(up);
                    pair.put(up, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {}

            try {
                MyPoint down = new MyPoint(x + 1, y);
                if (Map_Grid[x + 1][y] >= 87 && !visited.contains(down) && !queue.contains(down)) {
                    queue.addLast(down);
                    pair.put(down, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {}

            try {
                MyPoint left = new MyPoint(x, y - 1);
                if (Map_Grid[x][y - 1] >= 87 && !visited.contains(left) && !queue.contains(left)) {
                    queue.addLast(left);
                    pair.put(left, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {}

            try {
                MyPoint right = new MyPoint(x, y + 1);
                if (Map_Grid[x][y + 1] >= 87 && !visited.contains(right) && !queue.contains(right)) {
                    queue.addLast(right);
                    pair.put(right, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {}
        }

        if (!route_found) {
            System.out.println("Route not found");
            return null;
        } else {
            System.out.println("Route found");
        }

        return findShortestPath(pair, start, destination);
    }
    // ------------------------------------BFS
    // end------------------------------------

    @Override
    public void setup() {
        frameRate(FPS);

        // read JSON file
        JSONObject gameJson = loadJSONObject(configPath);

        // --------------Initialize the map grid START----------
        Map_Grid = readLevel(gameJson.getString("layout"));
        loadBackGround(Map_Grid);
        // --------------Initialize the map grid END----------

        initial_tower_range = gameJson.getDouble("initial_tower_range");
        initial_tower_firing_speed = gameJson.getDouble("initial_tower_firing_speed");
        initial_tower_damage = gameJson.getInt("initial_tower_damage");
        initial_tower_cost = gameJson.getInt("tower_cost");
        initial_mana = gameJson.getInt("initial_mana");
        initial_mana_gained_per_second = gameJson.getInt("initial_mana_gained_per_second");
        initial_mana_cap = gameJson.getInt("initial_mana_cap");

        current_mana_value = initial_mana;
        // 读取wave内容
        waves = gameJson.getJSONArray("waves");

        topbar = new Topbar();
        sidebar = new Sidebar();

        // setup gremlin death animation sequence
        for (int x = 0; x < 5; x++) {
            gremlin_dead[x] = loadImage("src/main/resources/WizardTD/gremlin" + (x + 1) + ".png");
        }

        // 存储最短路径
        MyPoint end = new MyPoint(wizard_house_x, wizard_house_y);
        for (int x = 0; x < startPoints.size(); x++) {
            MyPoint start = startPoints.get(x);
            shortest_Paths.put(start, BreadthFirstSearch(start, end));

            // 如果是左右出生，需要加TOPBAR+上下6
            // 如果上下出生，只需要左右+6
            // monsterList.add(new
            // Gremlin(start.getY()*CELLSIZE,start.getX()*CELLSIZE+TOPBAR+6,true,false));
            System.out.println("Monster adds at: " + (start.getY() * CELLSIZE) + " " + (start.getX() * CELLSIZE));
        }

        for (int x = 0; x < startPoints.size(); x++) {
            System.out.println("shortestPath: " + shortest_Paths.get(startPoints.get(x)).toString());
        }

        System.out.println("startPoints: " + startPoints.toString());

        gremlin_image = this.loadImage("src/main/resources/WizardTD/gremlin.png");

        tower_0_image = this.loadImage("src/main/resources/WizardTD/tower0.png");
        tower_1_image = this.loadImage("src/main/resources/WizardTD/tower1.png");
        tower_2_image = this.loadImage("src/main/resources/WizardTD/tower2.png");
        tower_image_map.put(0, tower_0_image);
        tower_image_map.put(1, tower_1_image);
        tower_image_map.put(2, tower_2_image);

        bullet_image = this.loadImage("src/main/resources/WizardTD/fireball.png");

    }

    public static boolean FF_press_condition = false;
    public static boolean Pause_press_condition = false;
    public static boolean Build_tower_condition = false;
    public static boolean Place_tower_on_map = false;

    public static boolean Upgrade_range_condition = false;
    public static boolean Upgrade_speed_condition = false;
    public static boolean Upgrade_damage_condition = false;

    public static boolean Deploy_range_upgrade = false;
    public static boolean Deploy_speed_upgrade = false;
    public static boolean Deploy_damage_upgrade = false;
    public static boolean Lose_condition = false;
    public static String mouse_on_which_button;

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed() {
        if (key == 'f' || key == 'F') {
            FF_press_condition = !FF_press_condition;
        }

        if (key == 'p' || key == 'P') {
            Pause_press_condition = !Pause_press_condition;
        }
        
        if (key == 't' || key == 'T') {
            Build_tower_condition = !Build_tower_condition;
        }

        if (key == '1') {
            Upgrade_range_condition = !Upgrade_range_condition;
        }

        if (key == '2') {
            Upgrade_speed_condition = !Upgrade_speed_condition;
        }

        if (key == '3') {
            Upgrade_damage_condition = !Upgrade_damage_condition;
        }

        if (Lose_condition) {
            if (key == 'r' || key == 'R') {
                gremlinList.removeAll(gremlinList);
                Lose_condition = false;
                isLastWave = false;
                setup();
                draw();
                
                loop();
            }
        }
        
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    
    /**
     * Receive mouse pressed signal
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (Sidebar.ffButton.mouse_on_button(this)) {
            FF_press_condition = !FF_press_condition;
        }

        if (Sidebar.pauseButton.mouse_on_button(this)) {
            Pause_press_condition = !Pause_press_condition;
        }

        if (Sidebar.buildTowerButton.mouse_on_button(this)) {
            Build_tower_condition = !Build_tower_condition;
        }

        if (mouseX >= 0 && mouseX <= 640 && mouseY >= TOPBAR && mouseY <= 680 && Build_tower_condition) {
            Place_tower_on_map = !Place_tower_on_map;
        }

        if (Sidebar.upgradeRangeButton.mouse_on_button(this)) {
            Upgrade_range_condition = !Upgrade_range_condition;
        }

        if (Upgrade_range_condition) {
            MyPoint tower_Point = new MyPoint((mouseX / 32) * 32, ((mouseY / 32) * 32) - 32 + TOPBAR);
            if (Sidebar.point_to_tower.containsKey(tower_Point)) {
                Tower tower = Sidebar.point_to_tower.get(tower_Point);
                if (tower.mouse_on_tower(this)) {
                    Deploy_range_upgrade = !Deploy_range_upgrade;
                }
            }
        }

        if (Sidebar.upgradeSpeedButton.mouse_on_button(this)) {
            Upgrade_speed_condition = !Upgrade_speed_condition;
        }

        if (Upgrade_speed_condition) {
            MyPoint tower_Point = new MyPoint((mouseX / 32) * 32, ((mouseY / 32) * 32) - 32 + TOPBAR);
            if (Sidebar.point_to_tower.containsKey(tower_Point)) {
                Tower tower = Sidebar.point_to_tower.get(tower_Point);
                if (tower.mouse_on_tower(this)) {
                    Deploy_speed_upgrade = !Deploy_speed_upgrade;
                }
            }
        }

        if (Sidebar.upgradeDamageButton.mouse_on_button(this)) {
            Upgrade_damage_condition = !Upgrade_damage_condition;
        }

        if (Upgrade_damage_condition) {
            MyPoint tower_Point = new MyPoint((mouseX / 32) * 32, ((mouseY / 32) * 32) - 32 + TOPBAR);
            if (Sidebar.point_to_tower.containsKey(tower_Point)) {
                Tower tower = Sidebar.point_to_tower.get(tower_Point);
                if (tower.mouse_on_tower(this)) {
                    Deploy_damage_upgrade = !Deploy_damage_upgrade;
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }


    /**
     * 
     * initialize wave variables
     * 提取wave信息
     */
    private double next_wave_pause;

    public static double wave_duration;
    public static double pre_wave_pause;

    public static ArrayList<Gremlin> gremlinList = new ArrayList<>();
    public static ArrayList<Gremlin> death_gremlin_index = new ArrayList<>();
    private ArrayList<ArrayList<Monster>> big_monsterList = new ArrayList<>();

    public static String monster_type;
    public static double monster_hp;
    public static double monster_speed;
    public static double monster_armour;
    public static int mana_gained_on_kill;
    public static double monster_quantity;
    public double monster_count;

    private double Monster_timer;
    // 更换wave的时机就是duration(startsIn)减为0
    private int startTime;
    private int startsIn;

    public int getStartIn() {
        return startsIn;
    }

    private int current_wave = 0;
    private int next_wave_index = current_wave + 1;
    // 这里的next_wave_display别改成static，我在Topbar里面用"App."调用了
    public int next_wave_display = next_wave_index + 1;

    private boolean processWave = true;
    private boolean isLastWave = false;
    
    /**
     * Control wave state change
     */
    public void wave_change() {
        // int current_wave=0; 从0 wave 开始

        /*
         * 这里顺序不可变，先提取current_wave=0的信息，
         * 然后，next_wave_duration里面才有值，
         * 进而，才能计算startsIn
         * 之后，才能判断是否要change_wave
         */

        // 先提取 current_wave=0 的信息
        if (processWave) {
            process_wave(current_wave);

            this.Monster_timer = (wave_duration / monster_quantity) * FPS;
            // for(int x=0; x<=monster_quantity; x++){
            // monsterList.add(gremlin);
            // }
            monster_count = monster_quantity;
            System.out.println("Monster_timer to draw: " + Monster_timer + "-----\n");
            processWave = false;
        }

        // 计算 startsIn 时间
        int counter_speed = 1000;
        int sec = 0; // 从 startTime 以来经过的秒数
        // TODO 倒计时无法衔接起来
        if (FF_press_condition) {
            counter_speed = 500;
            sec = (int) ((millis() - startTime) / counter_speed);
        } else {
           counter_speed = 1000;
           sec = (int) ((millis() - startTime) / counter_speed);
        }
        
        
        
        startsIn = (int) (next_wave_pause - sec); // 计算倒计时
        if (startsIn <= 0) {
            // 重置 startTime
            startTime = millis();
        }

        // 达到要求，更换wave
        if (current_wave < waves.size() - 1 && startsIn <= 0) {
            current_wave++;
            next_wave_index++;
            next_wave_display++;
            processWave = true;
            // System.out.println(monsterList.size());

        }

    }

    private HashMap<Integer, ArrayList<Object>> monster_info_map = new HashMap<>();

    /**
     * Extract data from wave
     * @param cur_wave current wave index
     */
    public void process_wave(int cur_wave) {
        JSONObject wave = waves.getJSONObject(cur_wave);
        wave_duration = wave.getInt("duration");
        pre_wave_pause = wave.getDouble("pre_wave_pause");

        // 如果当前wave+1超过size，也就是最后一个wave
        if (cur_wave + 1 >= waves.size()) {
            // TODO 在这里处理最后一个wave的逻辑，不显示wave和startsIn
            next_wave_pause = 5;
            isLastWave = true;
        } else {
            // 提取下一波wave的duration
            JSONObject next_wave = waves.getJSONObject(next_wave_index);
            next_wave_pause = next_wave.getInt("pre_wave_pause");
        }

        // 提取monsters信息
        JSONArray monsters = wave.getJSONArray("monsters");
        // monsters.size(): 第三波正常：2
        ArrayList<Object> one_wave_monster = new ArrayList<>();
        for (Integer j = 0; j < monsters.size(); j++) {
            JSONObject monster = monsters.getJSONObject(j);

            monster_type = monster.getString("type");
            monster_hp = monster.getDouble("hp");
            monster_speed = monster.getDouble("speed");
            monster_armour = monster.getDouble("armour");
            mana_gained_on_kill = monster.getInt("mana_gained_on_kill");
            monster_quantity = monster.getInt("quantity");

            one_wave_monster.add(monster_type);
            one_wave_monster.add(monster_hp);
            one_wave_monster.add(monster_speed);
            one_wave_monster.add(monster_armour);
            one_wave_monster.add(mana_gained_on_kill);
            one_wave_monster.add(monster_quantity);
        }
        monster_info_map.put(cur_wave, one_wave_monster);
    }

    /**
     * Draw all elements in the game by current frame.
     * draw() will be called once every second.
     */
    public static MyPoint spawn_point;

    @Override
    public void draw() {
        background(background_R, background_G, background_B);

        // 如果在setUp设置fill，那么就意味着在整个程序运行后，他都只能执行这个fill

        // 画地图和wizard_house
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                image(mapTile[i][j], j * 32, i * 32 + 40); // 在对应的位置绘制图片
            }
        }
        image(wizardImage, wizard_house_display_y, wizard_house_display_x);
        // 在wave_change()里，每次wave更变，处理monster的更变
        wave_change();
        
        for (int x=0; x<gremlinList.size(); x++) {
            Gremlin g = gremlinList.get(x);
            
            // 被打死
            if (!g.isAlive && g.animation_finished) { // 检查怪物是否死亡且动画是否完成
                death_gremlin_index.add(g);
                
                if (!g.isReachTheWizardhouse) { // 如果怪物没有到达巫师的房子，则增加 mana
                    current_mana_value += mana_gained_on_kill;
                    g.animation_finished = false;
                }
            }
            
            // 撞到house
            if (g.isReachTheWizardhouse && g.animation_finished) { // 如果怪物到达了巫师的房子且动画完成
                death_gremlin_index.add(g);
                current_mana_value -= g.hp;
                g.animation_finished = false;

                if(current_mana_value <= 0) {
                    current_mana_value = 0;
                    textSize(50);
                    text("You Lose!",380,340);
                    noLoop();
                    Lose_condition = true;
                }
                
            }
            
            g.tick();
            g.draw(this);
        }
        
        

        this.Monster_timer--;

        // 生成Monster List
        spawn_point = startPoints.get(random.nextInt(startPoints.size()));
        int gremlin_adjust = (CELLSIZE - gremlin_image.width) / 2; // 这里是6
        // TODO 目前开始是从0出生的，还不是地图外

        // 这里这个map是控制wave里monsters里面的信息的
        if (monster_info_map.containsKey(current_wave)) {
            double hp = (double) (monster_info_map.get(current_wave).get(1));
            double speed = (double) (monster_info_map.get(current_wave).get(2));
            double armour = (double) (monster_info_map.get(current_wave).get(3));
            if (this.Monster_timer < 0 && monster_count != 0) {
                gremlinList.add(new Gremlin(spawn_point.getY() * CELLSIZE + gremlin_adjust,
                        spawn_point.getX() * CELLSIZE + TOPBAR + gremlin_adjust, speed, hp, armour, true, false, false,
                        this.gremlin_image));
                monster_count--;

                this.Monster_timer = (wave_duration / monster_quantity) * FPS;
            }
        }

        

        topbar.draw(this);
        sidebar.draw(this);

        
        for(Gremlin gremlin : death_gremlin_index) {
                gremlinList.remove(gremlin);
        }

        if(isLastWave && gremlinList.size()==0) {
            textSize(50);
            text("You Win!",380,340);
            noLoop();
        }
    }

    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source:
     * https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * 
     * @param pimg  The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        // BufferedImage rotated = new BufferedImage(newWidth, newHeight,
        // BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
